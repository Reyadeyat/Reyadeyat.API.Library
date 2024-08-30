package net.reyadeyat.api.library.http;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import org.apache.commons.text.StringEscapeUtils;

public class DecryptedParameter {
    
    public String parameter_name;
    public String parameter_value;
    
    public DecryptedParameter(String parameter_name, String parameter_value) {
        this.parameter_name = parameter_name;
        this.parameter_value = parameter_value;
    }
    
    public static DecryptedParameter getDecrytedParameter(String parameter_encrypted_name, String parameter_encrypted_value, Cipher de_cipher) throws Exception {
        try {
            String parameter_decrypted_name = null;
            String parameter_decrypted_value = null;
            byte[] de_parameter_name_bytes;
            byte[] de_parameter_value_bytes;
            if (parameter_encrypted_name != null) {
                parameter_encrypted_name = URLDecoder.decode(parameter_encrypted_name, StandardCharsets.UTF_8);
                synchronized (de_cipher) {
                    de_parameter_name_bytes = de_cipher.doFinal(Base64.getDecoder().decode(parameter_encrypted_name));
                }
                parameter_decrypted_name = StringEscapeUtils.escapeJava(new String(de_parameter_name_bytes, StandardCharsets.UTF_8));
            }
            if (parameter_encrypted_value != null) {
                parameter_encrypted_value = URLDecoder.decode(parameter_encrypted_value, StandardCharsets.UTF_8);
                synchronized (de_cipher) {
                    de_parameter_value_bytes = de_cipher.doFinal(Base64.getDecoder().decode(parameter_encrypted_value));
                }
                parameter_decrypted_value = StringEscapeUtils.escapeJava(new String(de_parameter_value_bytes, StandardCharsets.UTF_8));
            }
            return new DecryptedParameter(parameter_decrypted_name, parameter_decrypted_value);
        } catch (Exception ex) {
            throw ex;
        }
    }
}
