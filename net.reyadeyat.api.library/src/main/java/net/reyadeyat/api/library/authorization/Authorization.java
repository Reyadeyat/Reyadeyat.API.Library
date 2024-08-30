package net.reyadeyat.api.library.authorization;

import java.util.HashMap;
import net.reyadeyat.api.library.authentication.Authentication;


public class Authorization {
    
    final public Authentication authentication;
    final public HashMap<String, AuthorizedResource> authorized_resource_map;
    final public String message;
    
    public Authorization(Authentication authentication, HashMap<String, AuthorizedResource> authorized_resource_map, String message) {
        this.authentication = authentication;
        this.authorized_resource_map = authorized_resource_map;
        this.message = message;
    }
    
    public Authentication getAuthentication() {
        return authentication;
    }
    
    public String getMessage() {
        return message;
    }
    
    public boolean isAuthorized(AuthorizedResource authorized_resource) {
        return this.authorized_resource_map.get(authorized_resource.getName()) != null;
    }
}
