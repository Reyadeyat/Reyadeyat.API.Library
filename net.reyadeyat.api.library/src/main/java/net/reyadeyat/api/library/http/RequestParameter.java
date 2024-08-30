package net.reyadeyat.api.library.http;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;

public class RequestParameter {
    
    public String parameter_name;
    public String parameter_value;
    public String parameter_content_type;
    public Cipher en_cipher;
    public Boolean encrypt;
    
    public RequestParameter(String parameter_name, String parameter_value) {
        this(parameter_name, parameter_value, "text/plain; charset=UTF-8", null);
    }
    
    public RequestParameter(String parameter_name, String parameter_value, Cipher en_cipher) {
        this(parameter_name, parameter_value, "text/plain; charset=UTF-8", en_cipher);
    }
    
    public RequestParameter(String parameter_name, String parameter_value, String parameter_content_type, Cipher en_cipher) {
        this.parameter_name = parameter_name;
        this.parameter_value = parameter_value;
        this.parameter_content_type = parameter_content_type;
        this.en_cipher = en_cipher;
    }
    
    public String get_parameter_name() {
        return parameter_name;
    }
    
    public String get_parameter_value() {
        return parameter_value;
    }
    
    public String get_parameter_content_type() {
        return parameter_content_type;
    }
    
    public byte[] get_parameter_en_name() throws Exception {
        if (en_cipher != null) {
            byte[] en_param_name_bytes;
            synchronized (en_cipher) {
                en_param_name_bytes = en_cipher.doFinal(parameter_name.getBytes(StandardCharsets.UTF_8));
            }
            String en_parameter_name = URLEncoder.encode(Base64.getEncoder().encodeToString(en_param_name_bytes), StandardCharsets.UTF_8);
            return en_parameter_name.getBytes(StandardCharsets.UTF_8);
        }
        return parameter_name.getBytes(StandardCharsets.UTF_8);
    }
    
    public byte[] get_parameter_en_value() throws Exception {
        if (en_cipher != null) {
            byte[] en_param_name_bytes;
            synchronized (en_cipher) {
                en_param_name_bytes = en_cipher.doFinal(parameter_value.getBytes(StandardCharsets.UTF_8));
            }
            String en_parameter_name = URLEncoder.encode(Base64.getEncoder().encodeToString(en_param_name_bytes), StandardCharsets.UTF_8);
            return en_parameter_name.getBytes(StandardCharsets.UTF_8);
        }
        return parameter_value.getBytes(StandardCharsets.UTF_8);
    }
    
    public byte[] get_parameter_en_content_type() throws Exception {
        if (en_cipher != null) {
            byte[] en_param_name_bytes;
            synchronized (en_cipher) {
                en_param_name_bytes = en_cipher.doFinal(parameter_content_type.getBytes(StandardCharsets.UTF_8));
            }
            String en_parameter_name = URLEncoder.encode(Base64.getEncoder().encodeToString(en_param_name_bytes), StandardCharsets.UTF_8);
            return en_parameter_name.getBytes(StandardCharsets.UTF_8);
        }
        return parameter_content_type.getBytes(StandardCharsets.UTF_8);
    }
}
