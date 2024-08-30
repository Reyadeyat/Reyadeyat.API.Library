package net.reyadeyat.api.library.http;

import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;

public class RequestCookies {
    
    List<Cookie> cookies;
    
    public RequestCookies() {
        this(new ArrayList<>());
    }
    
    public RequestCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }
    
    public void addCookie(Cookie cookie) {
        this.cookies.add(cookie);
    }
    
    public void manageConnection(HttpsURLConnection connection) {
        StringBuilder cookies_build = new StringBuilder();
        for (Cookie cookie : cookies) {
            cookies_build.append(cookie.cookie_name).append("=").append(cookie.cookie_value).append(";");
            if (cookie.path != null && cookie.path.isBlank() == false) {
                cookies_build.append("Path=").append(cookie.path).append(";");
            } else if (cookie.secure == true) {
                cookies_build.append("Secure;");
            } else if (cookie.http_only == true) {
                cookies_build.append("HttpOnly;");
            } else if (cookie.same_site != null && cookie.same_site.isBlank() == false) {
                cookies_build.append("SameSite=").append(cookie.same_site).append(";");
            }
        }
        connection.setRequestProperty("Cookie", cookies_build.toString());
    }
}
