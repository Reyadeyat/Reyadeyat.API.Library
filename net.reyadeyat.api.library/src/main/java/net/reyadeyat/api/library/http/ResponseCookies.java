package net.reyadeyat.api.library.http;

import java.util.List;
import java.util.TreeMap;

public class ResponseCookies {
    
    public List<String> raw_cookie_list;
    public TreeMap<String, Cookie> cookie_map;
    
    public ResponseCookies(List<String> raw_cookie_list) {
        cookie_map = new TreeMap<String, Cookie>();
        if (raw_cookie_list == null) {
            return;
        }
        this.raw_cookie_list = raw_cookie_list;
        for (String raw_cookie : this.raw_cookie_list) {
            String[] cookie_parts = raw_cookie.split(";");

            String[] nameValue = cookie_parts[0].split("=");
            String cookie_name = nameValue[0].trim();
            String cookie_value = nameValue[1].trim();
            String path = "/";
            boolean secure = false;
            boolean http_only = false;
            String same_site = "Lax";
            for (int i = 1; i < cookie_parts.length; i++) {
                String part = cookie_parts[i].trim();
                if (part.equalsIgnoreCase("Secure")) {
                    secure = true;
                } else if (part.equalsIgnoreCase("HttpOnly")) {
                    http_only = true;
                } else if (part.startsWith("Path=")) {
                    path = part.substring(5);
                } else if (part.startsWith("SameSite=")) {
                    same_site = part.substring(9);
                }
            }
            
            Cookie cookie = new Cookie(cookie_name, cookie_value, path, secure, http_only, same_site);
            cookie_map.put(cookie_name, cookie);
        }
    }
    
}
