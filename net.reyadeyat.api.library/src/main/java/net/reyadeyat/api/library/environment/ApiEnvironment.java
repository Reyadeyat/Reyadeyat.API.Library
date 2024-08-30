package net.reyadeyat.api.library.environment;

import java.security.MessageDigest;
import java.util.Base64;
import net.reyadeyat.api.library.util.BooleanParser;

abstract public class ApiEnvironment {
    static private MessageDigest sha_256_md;
    static private ApiEnvironment api_environment;
    
    public ApiEnvironment() throws Exception {
        api_environment = this;
        ApiEnvironment.sha_256_md = MessageDigest.getInstance("SHA-256");
    }
    
    abstract protected String _getProperty(String property_name);
    static public String getProperty(String property_name) {
        return api_environment._getProperty(property_name);
    }
    
    abstract protected String _getString(String property_name);
    static public String getString(String property_name) {
        return api_environment._getProperty(property_name);
    }
    
    abstract protected int _getInteger(String integer_property) throws Exception;
    static public int getInteger(String integer_property) throws Exception {
        return Integer.parseInt(api_environment._getProperty(integer_property));
    }
    
    abstract protected boolean _getBoolean(String boolean_property) throws Exception;
    static public boolean getBoolean(String boolean_property) throws Exception {
        return BooleanParser.parse(api_environment.getProperty(boolean_property));
    }
    
    static public String sha256(String message) throws Exception {
        sha_256_md.update(message.getBytes("UTF-8"));
        byte[] digest = sha_256_md.digest();
        return Base64.getEncoder().encodeToString(digest);
    }
}
