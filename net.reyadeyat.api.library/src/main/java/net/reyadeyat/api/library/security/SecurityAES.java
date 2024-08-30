/*
 * Copyright (C) 2023 Reyadeyat
 *
 * Reyadeyat/RELATIONAL.API is licensed under the
 * BSD 3-Clause "New" or "Revised" License
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://reyadeyat.net/LICENSE/RELATIONAL.API.LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.reyadeyat.api.library.security;

import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Mohammad Nabil Mostafa
 * <a href="mailto:code@mnabil.net">code@mnabil.net</a>
 */
public class SecurityAES implements Security {
    
    final private static String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    final private static String ALPHABETIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    
    private int key_length;
    private byte[] encryption_key;
    private byte[] encryption_key_iv_spec;
    private Cipher encrypt_cipher;
    private Cipher decrypt_cipher;
    
    public SecurityAES() throws Exception {
        this(16);
    }
    
    public SecurityAES(int key_length) throws Exception {
        this(key_length, generate_random_key(key_length).getBytes(StandardCharsets.US_ASCII), generate_random_key(key_length).getBytes(StandardCharsets.US_ASCII));
    }
    
    public SecurityAES(int key_length, byte[] encryption_key, byte[] encryption_key_iv_spec) throws Exception {
        if (encryption_key == null) {
            throw new Exception("encryption_key is null!");
        }
        if (encryption_key_iv_spec == null) {
            throw new Exception("encryption_key_iv_spec is null!");
        }
        if (encryption_key.length != key_length) {
            throw new Exception("encryption_key.length ["+encryption_key.length+"] != key_length ["+key_length+"]");
        }
        if (encryption_key_iv_spec.length != key_length) {
            throw new Exception("encryption_key_iv_spec.length ["+encryption_key_iv_spec.length+"] != key_length ["+key_length+"]");
        }
        this.key_length = key_length;
        this.encryption_key = encryption_key;
        this.encryption_key_iv_spec = encryption_key_iv_spec;
        encrypt_cipher = create_encrypt_cipher(this.encryption_key, this.encryption_key_iv_spec);
        decrypt_cipher = create_decrypt_cipher(this.encryption_key, this.encryption_key_iv_spec);
        
    }
    
    public SecurityAES(Cipher cipher, Boolean encrypt) throws Exception {
        this(encrypt ? cipher : null, !encrypt ? cipher : null);
    }
    
    public SecurityAES(Cipher encrypt_cipher, Cipher decrypt_cipher) throws Exception {
        this.encrypt_cipher = encrypt_cipher;
        this.decrypt_cipher = decrypt_cipher;
    }
    
    @Override
    public String encrypt_text(String plain_text) throws Exception {
        byte[] encrypted_text_bytes = encrypt_cipher.doFinal(plain_text.getBytes("UTF-8"));
        String encrypted_text = Base64.getEncoder().encodeToString(encrypted_text_bytes);
        return encrypted_text;
    }
    
    @Override
    public String decrypt_text(String encrypted_text) throws Exception {
        byte[] encrypted_text_base64_bytes = Base64.getDecoder().decode(encrypted_text);
        if (encrypted_text_base64_bytes.length % 16 != 0) {
            throw new Exception("encrypted_text_base64_bytes "+encrypted_text_base64_bytes.length+" % 16 = " + (encrypted_text_base64_bytes.length % 16));
        }
        byte[] plain_text_bytes = decrypt_cipher.doFinal(encrypted_text_base64_bytes);
        String plain_text = new String(plain_text_bytes);
        return plain_text;
    }
    
    public static String generate_random_key(int length) {
        String possibleDigits = shuffle(ALPHANUMERIC);
        StringBuilder randomNumber = new StringBuilder();

        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(possibleDigits.length());
            char randomChar = possibleDigits.charAt(randomIndex);
            randomNumber.append(randomChar);
        }

        return randomNumber.toString();
    }
    
    private Cipher create_encrypt_cipher(byte[] encryption_key, byte[] encryption_key_iv_spec) throws Exception {
        encrypt_cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        SecretKeySpec secretKeySpec = new SecretKeySpec(encryption_key, "AES");
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(encryption_key_iv_spec);
        encrypt_cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, paramSpec);
        return encrypt_cipher;
    }
    
    private Cipher create_decrypt_cipher(byte[] encryption_key, byte[] encryption_key_iv_spec) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(encryption_key_iv_spec);
        if (encryption_key.length % 16 != 0) {
            throw new Exception("encryption_key "+encryption_key.length+" % 16 = " + (encryption_key.length % 16));
        }
        SecretKeySpec skeySpec = new SecretKeySpec(encryption_key, "AES");
        decrypt_cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        decrypt_cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);    
        return decrypt_cipher;
    }

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
    
    public static String encrypt_method(Integer method_id, String key, String vi) {
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
        replace_zeros(method_key, shuffle(ALPHABETIC));
        return method_key.toString();
    }
    
    public static void decrypt_method(Integer method_id, String method_key, StringBuilder key, StringBuilder vi) {
        StringBuilder m_key = new StringBuilder(method_key);
        replace_letters(m_key);
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
    
    public static void replace_zeros(StringBuilder input_string, String finite_string) {
        Random random = new Random();
        for (int i = 0; i < input_string.length(); i++) {
            if (input_string.charAt(i) == '0') {
                char random_char = finite_string.charAt(random.nextInt(finite_string.length()));
                input_string.replace(i, i+1, ""+random_char);
            }
        }
    }
    
    public static void replace_letters(StringBuilder input_string) {
        for (int i = 0; i < input_string.length(); i++) {
            if ((int)input_string.charAt(i) >= 58) {
                input_string.replace(i, i+1, ""+(char)48);
            }
        }
    }
}