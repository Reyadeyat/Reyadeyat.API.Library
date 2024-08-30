package net.reyadeyat.api.library.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

public class ResponseHeaders {
    
    Map<String, List<String>> response_header_map;
    StringBuilder response_headers_log;
    
    public ResponseHeaders(boolean log_response_headers) {
        response_header_map = new HashMap<>();
        response_headers_log = new StringBuilder();
    }
    
    public void addHeader(String header, String value) {
        if (response_header_map.containsKey(header) == false) {
            response_header_map.put(header, new ArrayList());
        }
        response_header_map.get(header).add(value);
    }
    
    public void setHeader(String header, String value) {
        if (response_header_map.containsKey(header) == true) {
            response_header_map.get(header).clear();
            response_header_map.get(header).add(value);
            return;
        }
        addHeader(header, value);
    }
    
    StringBuilder manageConnection(HttpsURLConnection connection, boolean log_request_headers) {
        for (Map.Entry<String, List<String>> header_entry : response_header_map.entrySet()) {
            String header_name = header_entry.getKey();
            
            List<String> header_list = header_entry.getValue();
            for (String header : header_list) {
                connection.addRequestProperty(header_name, header);
                if (log_request_headers == true) {
                    response_headers_log.append(header_entry).append(": ").append(header).append("\n");
                }
            }
        }
        return response_headers_log;
    }
    
    public StringBuilder get_response_headers_log() {
        return response_headers_log;
    }
}
