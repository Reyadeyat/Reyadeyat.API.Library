package net.reyadeyat.api.library.authentication;

import java.sql.Timestamp;
import java.time.Instant;

public class Authentication {
    
    public String api_jwt;
    public Integer id;
    public Timestamp instantiation_timestamp;
    
    
    public Authentication(String api_jwt) {
        this.api_jwt = api_jwt;
        this.id = -1;
        instantiation_timestamp = new Timestamp(Instant.now().getEpochSecond());
    }
    
    public boolean isAuthentic() {
        return true;
    }
}
