package net.reyadeyat.api.library.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import jakarta.servlet.ServletException;
import java.io.BufferedInputStream;
import java.io.FileReader;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Random;
import java.util.TreeMap;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class TLS {
    final private static Object load_lock = new Object();
    private static SSLSocketFactory ssf;
    private static SSLSocketFactory untrusted_ssf;
    private static KeyStore key_store;
    final private static char[] base_password = "changeit".toCharArray();//Server or Android null
    private static CertificateFactory certificate_factory;
    private static KeyPairGenerator generator;
    final private static TreeMap<String, HostCertificate> cached_host_map = new TreeMap<>();
    final private static String protocol = "TLSv1.3";
    
    
    static public Boolean initialized() throws ServletException {
        return ssf != null;
    }
    
    
    public static SSLSocketFactory getSocketFactory() throws Exception {
        if (ssf != null) {
            return ssf;
        }
        throw new Exception("SecureSocketFactory is null");
    }

    public static SSLSocketFactory loadSocketFactory(String SERVER_TLS_FILE) throws Exception {
        synchronized (load_lock) {
            try {
                if (certificate_factory == null) {
                    certificate_factory = CertificateFactory.getInstance("X.509");
                }
                if (generator == null) {
                    generator = KeyPairGenerator.getInstance("RSA");
                    generator.initialize(2048, new SecureRandom());
                }
                JsonArray tls_config = null;
                Context context = new InitialContext();
                if (SERVER_TLS_FILE == null || SERVER_TLS_FILE.length() == 0) {
                    Logger.getLogger(TLS.class.getName()).log(Level.CONFIG, "TLS file string is empty or not defined");
                } else {
                    File file = new File(SERVER_TLS_FILE);
                    try (JsonReader reader = new JsonReader(new FileReader(file))) {
                        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
                        tls_config = gson.fromJson(reader, JsonArray.class);
                    } catch (Exception ex) {
                        throw new ServletException(ex);
                    }
                }

                try {
                    key_store = KeyStore.getInstance(KeyStore.getDefaultType());
                    key_store.load(new FileInputStream(new File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + "cacerts")), base_password);
                } catch (Exception ex) {
                    Logger.getLogger(TLS.class.getName()).log(Level.CONFIG, "Error: loading JDK default TLS File '"+System.getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + " cacerts password 'changeit', you need to provid correct settings in tomcat.tls.properties with correct path and password", ex);
                    key_store.load(null, base_password);
                }
                
                for (int i = 0; i < tls_config.size(); i++) {
                    JsonObject json = tls_config.get(i).getAsJsonObject();
                    String format = json.get("format").getAsString();
                    if (format.equalsIgnoreCase("jks")) {
                        String name = json.get("name").getAsString();
                        String key_store_name = json.get("keystore").getAsString();
                        String path = json.get("path").getAsString();
                        String alias = json.get("alias").getAsString();
                        String passwordz = json.get("password").getAsString();
                        String cert_protocol = json.get("protocol").getAsString();
                        SSLSocketFactory ssf = null;
                        if (passwordz == null || passwordz.isBlank()) {
                            throw new Exception("jks password is blank");
                        }
                        char[] password = passwordz.toCharArray();
                        try (InputStream trustStream = new FileInputStream(path)) {
                            KeyStore key_store_x = KeyStore.getInstance(KeyStore.getDefaultType());
                            key_store_x.load(trustStream, password);
                            Key key = key_store_x.getKey(alias, password);
                            key_store.setKeyEntry(alias, key, base_password, key_store_x.getCertificateChain(alias));
                        } catch (Exception ex) {
                            Logger.getLogger(TLS.class.getName()).log(Level.SEVERE, "TLS File '"+name+"' Error", ex);
                            throw ex;
                        }
                    } else if (format.equalsIgnoreCase("cert")) {
                        String name = json.get("name").getAsString();
                        String alias = json.get("alias").getAsString();
                        String password = json.get("password").getAsString();
                        String key_store_name = json.get("keystore").getAsString();
                        String path = json.get("path").getAsString();
                        String cert_protocol = json.get("protocol").getAsString();
                        SSLSocketFactory ssf = null;
                        try (FileInputStream fis = new FileInputStream(path)) {
                            X509Certificate ca = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new BufferedInputStream(fis));
                            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                            generator.initialize(2048, new SecureRandom());
                            KeyPair key_pair = generator.generateKeyPair();
                            Certificate[] certificateChain = new Certificate[]{ca};
                            key_store.setKeyEntry(alias, key_pair.getPrivate(), base_password, certificateChain);
                            //key_store.setCertificateEntry(alias, ca);
                        } catch (Exception ex) {
                            Logger.getLogger(TLS.class.getName()).log(Level.SEVERE, "TLS File '"+name+"' Error", ex);
                            throw ex;
                        }
                    }
                }
                String protocol = "TLSv1.3";
                TrustManagerFactory trust_factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trust_factory.init(key_store);
                KeyManagerFactory key_manager_factory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                key_manager_factory.init(key_store, base_password);
                SSLContext sc = SSLContext.getInstance(protocol);
                sc.init(key_manager_factory.getKeyManagers(), trust_factory.getTrustManagers(), new java.security.SecureRandom());
                ssf = sc.getSocketFactory();
                //socket_factory_map.put(name, ssf);
                try {
                    StringBuilder b = new StringBuilder("TLS Loaded Aliases list\n");         
                    Enumeration<String> aliases =  key_store.aliases();
                    while (aliases.hasMoreElements()) {
                        String alias = aliases.nextElement();
                        b.append("  alisa: ").append(alias).append("\n");
                        Certificate[] certificates = key_store.getCertificateChain(alias);
                        if (certificates == null || certificates.length == 0) {
                            b.append("    certificate: ").append(certificates == null ? "null" : "0").append("\n");
                            continue;
                        }
                        for (Certificate certificate : certificates) {
                            b.append("    certificate: ").append(certificate == null ? "null" : certificate.toString()).append("\n");
                        }
                    }
                    Logger.getLogger(TLS.class.getName()).log(Level.FINE, b.toString());
                    return ssf;
                } catch (Exception ex) {
                    ssf = null;
                    throw ex;
                }
            } catch (Exception ex) {
                ssf = null;
                Logger.getLogger(TLS.class.getName()).log(Level.SEVERE, "TLS File Error file context is not defined in context.xml", ex);
                throw ex;
            }
        }
    }
    
    private static void createUntrustedManager() throws Exception {
        if (untrusted_ssf != null) {
            return;
        }
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }
        };
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        untrusted_ssf = sslContext.getSocketFactory();
    }
    
    private static void getServerCertificates(URL connection_url) throws Exception {
        TLS.getServerCertificates(connection_url, true);
    }

    private static void getServerCertificates(URL connection_url, Boolean update_certificate) throws Exception {
        String host = connection_url.getHost();
        synchronized (cached_host_map) {
            if (update_certificate == false && cached_host_map.containsKey(host) == true) {
                return;
            }
            HttpsURLConnection https_url_connection = (HttpsURLConnection) connection_url.openConnection();
            https_url_connection.setSSLSocketFactory(untrusted_ssf);
            KeyPair key_pair = generator.generateKeyPair();
            https_url_connection.connect();
            Certificate[] certificate_chain = https_url_connection.getServerCertificates();
            key_store.setKeyEntry(host, key_pair.getPrivate(), base_password, certificate_chain);
            HostCertificate host_certificate = new HostCertificate();
            host_certificate.host = host;
            host_certificate.certificate_chain = certificate_chain;

            TrustManagerFactory trust_factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trust_factory.init(key_store);
            KeyManagerFactory key_manager_factory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            key_manager_factory.init(key_store, base_password);
            SSLContext sc = SSLContext.getInstance(protocol);
            sc.init(key_manager_factory.getKeyManagers(), trust_factory.getTrustManagers(), new java.security.SecureRandom());
            ssf = sc.getSocketFactory();

            cached_host_map.put(host, host_certificate);
        }
    }
    
    public static HttpsURLConnection getHttpsConnection(URL connection_url, Boolean force_trust_url) throws Exception {
        if (force_trust_url == false) {
            TLS.getServerCertificates(connection_url);
        }
        HttpsURLConnection https_url_connection = (HttpsURLConnection) connection_url.openConnection();
        https_url_connection.setSSLSocketFactory(ssf);
        return https_url_connection;
    }
    
    public static HttpsURLConnection getTrustedHttpsConnection(URL connection_url) throws Exception {
        return TLS.getHttpsConnection(connection_url, true);
    }
    
    public static HttpsURLConnection getUntrustedHttpsConnection(URL connection_url) throws Exception {
        return TLS.getHttpsConnection(connection_url, false);
    }
    
    public static InputStream getTrustedHttpsConnectionInputStream(URL connection_url) throws Exception {
        return TLS.getHttpsConnectionInputStream(connection_url, true);
    }

    public static InputStream getUntrustedHttpsConnectionInputStream(URL connection_url) throws Exception {
        return TLS.getHttpsConnectionInputStream(connection_url, false);
    }

    public static InputStream getHttpsConnectionInputStream(URL connection_url, Boolean force_trust_url) throws Exception {
        try {
            TLS.getServerCertificates(connection_url, false);
            HttpsURLConnection https_url_connection = (HttpsURLConnection) connection_url.openConnection();
            https_url_connection.setSSLSocketFactory(ssf);
            return https_url_connection.getInputStream();
        } catch (javax.net.ssl.SSLHandshakeException ex) {
            if (force_trust_url == false) {
                TLS.getServerCertificates(connection_url, true);
                return TLS.getHttpsConnectionInputStream(connection_url, force_trust_url);
            }
            throw ex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    /**
     * These methods are obfuscated in T.java class file
     * T.ep
     */
    public static String decrypt_parameter(String encrypted_text, byte[] encryption_key, byte[] iv_spec) throws Exception {
        return decrypt_text(URLDecoder.decode(encrypted_text, StandardCharsets.UTF_8), encryption_key, iv_spec);
    }

    /**
     * T.et
     */
    public static String decrypt_text(String encrypted_text, byte[] encryption_key, byte[] iv_spec) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(iv_spec);
        if (encryption_key.length % 16 != 0) {
            throw new Exception("encryption_key " + encryption_key.length + " % 16 = " + (encryption_key.length % 16));
        }
        SecretKeySpec skeySpec = new SecretKeySpec(encryption_key, "AES");
        //SecretKeySpec skeySpec = new SecretKeySpec(Arrays.copyOf(encrypted_text_bytes, encrypted_text_bytes.length + (encrypted_text_bytes.length % 16)), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] encrypted_text_base64_bytes = Base64.getDecoder().decode(encrypted_text);
        if (encrypted_text_base64_bytes.length % 16 != 0) {
            throw new Exception("encrypted_text_base64_bytes " + encrypted_text_base64_bytes.length + " % 16 = " + (encrypted_text_base64_bytes.length % 16));
        }
        byte[] plain_text_bytes = cipher.doFinal();
        String plain_text = new String(plain_text_bytes);
        return plain_text;
    }
    
    /**
     * T.aaa
     */
    public static String generateRandomKey(int length) {
        String possibleDigits = shuffle(standardKey());
        StringBuilder randomNumber = new StringBuilder();

        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(possibleDigits.length());
            char randomChar = possibleDigits.charAt(randomIndex);
            randomNumber.append(randomChar);
        }

        return randomNumber.toString();
    }
    
    /**
     * T.rrr
     */
    public static String shuffle(String content) {
        StringBuilder string = new StringBuilder(content);
        char temp;
        int randomIndex;
        Random random = new Random();
        for (int i = string.length() - 1; i > 0; i--) {
            randomIndex = random.nextInt(string.length() - 1);
            temp = string.charAt(i);
            string.setCharAt(i, string.charAt(randomIndex));
            string.setCharAt(randomIndex, temp);
        }

        return string.toString();
    }
    
    /**
     * T.lu
     */
    public static String encryptKey(Integer method_id, String key, String vi) {
        Logger.getLogger(TLS.class.getName()).log(Level.INFO, "key: " + key);
        Logger.getLogger(TLS.class.getName()).log(Level.INFO, "vi: " + vi);
        StringBuilder method_key = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            StringBuilder word = new StringBuilder("" + ((int)key.charAt(i) * (int)vi.charAt(i)));
            while (word.length() < 5) {
                word.insert(0, "0");
            }
            if (i % 2 == 0) {
                word.append((int)key.charAt(i));
            } else {
                word.append((int)vi.charAt(i));
            }
            while (word.length() < 8) {
                word.insert(5, "0");
            }
            method_key.append(word);
        }
        replaceZeros(method_key, shuffle(randomKey()));
        return method_key.toString();
    }
    
    /**
     * T.jh
     */
    public static void decryptKey(Integer method_id, String method_key, StringBuilder key, StringBuilder vi) {
        StringBuilder m_key = new StringBuilder(method_key);
        replaceLetters(m_key);
        method_key = m_key.toString();
        for (int i = 0; i < 128; i+=8) {
            Integer low = Integer.valueOf(method_key.substring(i, i+5));
            Integer hight = Integer.valueOf(method_key.substring(i+5, i+8));
            if ((i/8) % 2 == 0) {
                key.append((char)(int)hight);
                vi.append((char)((int) low / (int)hight));
            } else {
                vi.append((char)(int)hight);
                key.append((char)((int) low / (int)hight));
            }
        }
    }
    
    /**
     * T.o0
     */
    public static void replaceZeros(StringBuilder input_string, String finite_string) {
        Random random = new Random();
        for (int i = 0; i < input_string.length(); i++) {
            if (input_string.charAt(i) == '0') {
                char random_char = finite_string.charAt(random.nextInt(finite_string.length()));
                input_string.replace(i, i+1, ""+random_char);
            }
        }
    }
    
    /**
     * T.oo0
     */
    public static void replaceLetters(StringBuilder input_string) {
        for (int i = 0; i < input_string.length(); i++) {
            if ((int)input_string.charAt(i) >= 58) {
                input_string.replace(i, i+1, ""+(char)48);
            }
        }
    }
    
    /**
     * T.qw
     */
    public static String randomKey() {
        StringBuilder qw = new StringBuilder();
        for (int i = 0x41; i <= 0x5A; i++) {
            qw.append((char) i);
        }
        for (int i = 0x61; i <= 0x7A; i++) {
            qw.append((char) i);
        }
        return qw.toString();
    }
    
    /**
     * T.wq
     */
    public static String standardKey() {
        StringBuilder wq = new StringBuilder();
        for (int i = 0x41; i <= 0x5A; i++) {
            wq.append((char) i);
        }
        for (int i = 0x61; i <= 0x7A; i++) {
            wq.append((char) i);
        }
        for (int i = 0x30; i <= 0x39; i++) {
            wq.append((char) i);
        }
        return wq.toString();
    }
}

class HostCertificate {
    public String host;
    public Certificate[] certificate_chain;

}
