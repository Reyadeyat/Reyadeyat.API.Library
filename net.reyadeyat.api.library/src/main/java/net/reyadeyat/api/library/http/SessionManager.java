package net.reyadeyat.api.library.http;

import java.util.HashMap;
import javax.crypto.Cipher;

public class SessionManager {
    
    public String session_key;
    public Integer session_window;
    public HashMap<String, Cipher> cipher_map;
    public HashMap<String, String> cookies_map;
    public ResponseCookies cookies;
    
    public SessionManager(String session_key, Integer session_window, HashMap<String, Cipher> cipher_map, HashMap<String, String> cookies_map, ResponseCookies cookies) {
        this.session_key = session_key;
        this.session_window = session_window;
        this.cipher_map = cipher_map;
        this.cookies_map = cookies_map;
        this.cookies = cookies;
    }
    
}
