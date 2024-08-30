package net.reyadeyat.api.library.http;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class T {

    public static String ep(String dt, byte[] kd, byte[] w) throws Exception {
        return et(URLDecoder.decode(dt, StandardCharsets.UTF_8), kd, w);
    }

    public static String et(String dt, byte[] kd, byte[] w) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(w);
        if (kd.length % 16 != 0) {
            throw new Exception("kd " + kd.length + " % 16 = " + (kd.length % 16));
        }
        SecretKeySpec ks = new SecretKeySpec(kd, "AES");
        //SecretKeySpec ks = new SecretKeySpec(Arrays.copyOf(encrypted_text_bytes, encrypted_text_bytes.length + (encrypted_text_bytes.length % 16)), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, ks, iv);
        byte[] ooo = Base64.getDecoder().decode(dt);
        if (ooo.length % 16 != 0) {
            throw new Exception("ooo " + ooo.length + " % 16 = " + (ooo.length % 16));
        }
        byte[] ggg = cipher.doFinal();
        String gggg = new String(ggg);
        return gggg;
    }

    public static String aaa(int length) {
        String bbb = rrr(wq());
        StringBuilder ccc = new StringBuilder();

        Random r = new Random();

        for (int i = 0; i < length; i++) {
            int ir = r.nextInt(bbb.length());
            char cr = bbb.charAt(ir);
            ccc.append(cr);
        }

        return ccc.toString();
    }

    public static String rrr(String content) {
        StringBuilder br = new StringBuilder(content);
        char temp;
        int ir;
        Random r = new Random();
        for (int i = br.length() - 1; i > 0; i--) {
            ir = r.nextInt(br.length() - 1);
            temp = br.charAt(i);
            br.setCharAt(i, br.charAt(ir));
            br.setCharAt(ir, temp);
        }

        return br.toString();
    }
    
    public static String lu(Integer dok, String key, String vi) {
        StringBuilder zoo = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            StringBuilder wie = new StringBuilder("" + ((int)key.charAt(i) * (int)vi.charAt(i)));
            while (wie.length() < 5) {
                wie.insert(0, "0");
            }
            if (i % 2 == 0) {
                wie.append((int)key.charAt(i));
            } else {
                wie.append((int)vi.charAt(i));
            }
            while (wie.length() < 8) {
                wie.insert(5, "0");
            }
            zoo.append(wie);
        }
        o0(zoo, rrr(qw()));
        return zoo.toString();
    }
    
    public static void jh(Integer dok, String zoo, StringBuilder key, StringBuilder vi) {
        StringBuilder m_key = new StringBuilder(zoo);
        oo0(m_key);
        zoo = m_key.toString();
        for (int i = 0; i < 128; i+=8) {
            Integer low = Integer.valueOf(zoo.substring(i, i+5));
            Integer hight = Integer.valueOf(zoo.substring(i+5, i+8));
            if ((i/8) % 2 == 0) {
                key.append((char)(int)hight);
                vi.append((char)((int) low / (int)hight));
            } else {
                vi.append((char)(int)hight);
                key.append((char)((int) low / (int)hight));
            }
        }
    }
    
    public static void o0(StringBuilder ede, String ef) {
        Random r = new Random();
        for (int i = 0; i < ede.length(); i++) {
            if (ede.charAt(i) == '0') {
                char fe = ef.charAt(r.nextInt(ef.length()));
                ede.replace(i, i+1, ""+fe);
            }
        }
    }
    
    public static void oo0(StringBuilder ede) {
        for (int i = 0; i < ede.length(); i++) {
            if ((int)ede.charAt(i) >= 58) {
                ede.replace(i, i+1, ""+(char)48);
            }
        }
    }
    
    public static String qw() {
        StringBuilder qw = new StringBuilder();
        for (int i = 0x41; i <= 0x5A; i++) {
            qw.append((char) i);
        }
        for (int i = 0x61; i <= 0x7A; i++) {
            qw.append((char) i);
        }
        return qw.toString();
    }
    
    public static String wq() {
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
