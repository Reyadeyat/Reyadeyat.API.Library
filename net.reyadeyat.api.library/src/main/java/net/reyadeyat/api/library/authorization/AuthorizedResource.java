package net.reyadeyat.api.library.authorization;

import com.google.gson.JsonObject;

public class AuthorizedResource {
    
    private String name;
    private JsonObject resource;
    
    public AuthorizedResource(String name, JsonObject resource) {
        this.name = name;
        this.resource = resource;
    }
    
    public String getName() {
        return name;
    }
    
    public JsonObject getResource() {
        return resource;
    }
}
