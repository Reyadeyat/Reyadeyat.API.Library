package net.reyadeyat.api.library.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import jakarta.servlet.ServletException;
import java.io.FileReader;
import java.lang.reflect.Modifier;
import java.util.TreeMap;

public class IP {
    private static JsonObject ip_set_json;
    
    static protected TreeMap<String, IPRule> reject_ip_map;
    static protected TreeMap<String, IPRule> accept_ip_map;
    static protected IPRule star_rule;
    
    static public Boolean initialized() throws ServletException {
        return reject_ip_map != null && accept_ip_map != null;
    }
    
    static public void loadTomcatIP() throws ServletException {
        
        if (reject_ip_map != null && accept_ip_map != null) {
            return;
        }
        try {
            star_rule = null;
            Context context = new InitialContext();
            String SERVER_IP_FILE = (String) context.lookup("java:comp/env/SERVER_IP_FILE");
            if (SERVER_IP_FILE == null || SERVER_IP_FILE.length() == 0) {
                Logger.getLogger(IP.class.getName()).log(Level.CONFIG, "IP file string is empty or not defined");
            } else {
                File file = new File(SERVER_IP_FILE);
                try (JsonReader reader = new JsonReader(new FileReader(file))) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
                    ip_set_json = gson.fromJson(reader, JsonObject.class);
                } catch (Exception ex) {
                    throw new ServletException(ex);
                }
            }
            
            reject_ip_map = new TreeMap<String, IPRule>();
            accept_ip_map = new TreeMap<String, IPRule>();
            
            JsonArray reject_ip_set_json = ip_set_json.get("reject").getAsJsonArray();
            for (int i = 0; i < reject_ip_set_json.size(); i++) {
                JsonObject ip_rule = reject_ip_set_json.get(i).getAsJsonObject();
                String ip = ip_rule.get("ip").getAsString();
                String rule = ip_rule.get("rule").getAsString();
                reject_ip_map.put(ip, new IPRule(ip, rule));
            }
            
            JsonArray trust_ip_set_json = ip_set_json.get("accept").getAsJsonArray();
            for (int i = 0; i < trust_ip_set_json.size(); i++) {
                JsonObject ip_rule = trust_ip_set_json.get(i).getAsJsonObject();
                String ip = ip_rule.get("ip").getAsString();
                String rule = ip_rule.get("rule").getAsString();
                accept_ip_map.put(ip, new IPRule(ip, rule));
            }
            
            star_rule = accept_ip_map.get("*");
        } catch (Exception ex) {
            reject_ip_map = null;
            accept_ip_map = null;
            Logger.getLogger(IP.class.getName()).log(Level.CONFIG, "IP File Error file context not defined in context.xml", ex);
            throw new ServletException(ex);
        }
    }
    
    //private static String[] ipv4_pattern = new String[]{"*.*.*","*.*", "*"};
    //private static String[] ipv6_pattern = new String[]{""};
    public static IPRule isValidIP(String ip) throws Exception {
        if (reject_ip_map != null && accept_ip_map != null) {
            loadTomcatIP();
        }
        IPRule tomcat_ip_rule;
        if (ip.indexOf(".") > 0) {//ipv4
            
            tomcat_ip_rule = reject_ip_map.get(ip);
            if (tomcat_ip_rule != null) {
                return null;
            }
            
            String[] parts = ip.split("\\.");
            StringBuilder b = new StringBuilder();
            for (String part : parts) {
                b.append(part);
                
                tomcat_ip_rule = reject_ip_map.get(b.toString());
                if (tomcat_ip_rule != null) {
                    return null;
                }
                
                b.append(".");
            }
            
            tomcat_ip_rule = accept_ip_map.get(ip);
            if (tomcat_ip_rule != null) {
                return tomcat_ip_rule;
            }
            
            b.delete(0, b.length());
            for (String part : parts) {
                b.append(part);
                
                tomcat_ip_rule = accept_ip_map.get(b.toString());
                if (tomcat_ip_rule != null) {
                    return tomcat_ip_rule;
                }
                
                b.append(".");
            }
        } else if (ip.indexOf(":") > 0) {//ipv6
            String[] parts = ip.split(":");
            StringBuilder b = new StringBuilder();
            for (String part : parts) {
                b.append(part);
                
                tomcat_ip_rule = reject_ip_map.get(b.toString());
                if (tomcat_ip_rule != null) {
                    return tomcat_ip_rule;
                }
                
                b.append(":");
            }
            
            b.delete(0, b.length());
            for (String part : parts) {
                b.append(part);
                
                tomcat_ip_rule = accept_ip_map.get(b.toString());
                if (tomcat_ip_rule != null) {
                    return tomcat_ip_rule;
                }
                
                b.append(":");
            }
        }
        
        if (star_rule != null) {
            return star_rule;
        }
        return null;
    }
}
