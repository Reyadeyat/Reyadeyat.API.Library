package net.reyadeyat.api.library.http;

import java.util.List;

public class Cookie {
    
    public String cookie_name;
    public String cookie_value;
    public String path;
    public Boolean secure;
    public Boolean http_only;
    public String same_site;
    
    public Cookie(String cookie_name, String cookie_value, String path, Boolean secure, Boolean http_only, String same_site) {
        this.cookie_name = cookie_name;
        this.cookie_value = cookie_value;
        this.path = path;
        this.secure = secure;
        this.http_only = http_only;
        this.same_site = same_site;
    }
    
}
