package net.reyadeyat.api.library.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

public class RequestHeaders {
    
    Map<String, List<String>> request_headers_map;
    StringBuilder request_headers_log;
    boolean log_request_headers;
    boolean log_response_headers;
    
    public RequestHeaders() {
        this(true, "anonymous", false, false);
    }
    
    public RequestHeaders(boolean is_api, String user_agent, boolean log_request_headers, boolean log_response_headers) {
        request_headers_map = new HashMap<>();
        request_headers_log = new StringBuilder();
        this.log_request_headers = log_request_headers;
        this.log_response_headers = log_response_headers;
        if (is_api == true) {
            request_headers_map.put("User-Agent", new ArrayList<>(Arrays.asList(new String[]{user_agent})));
            request_headers_map.put("Accept-Charset", new ArrayList<>(Arrays.asList(new String[]{"UTF-8"})));
            request_headers_map.put("Cache-Control", new ArrayList<>(Arrays.asList(new String[]{"no-cache"})));
            request_headers_map.put("Connection", new ArrayList<>(Arrays.asList(new String[]{"Keep-Alive"})));
            request_headers_map.put("Accept-Encoding", new ArrayList<>(Arrays.asList(new String[]{"gzip, deflate"})));
        }
    }
    
    public void addHeader(String header, String value) {
        if (request_headers_map.containsKey(header) == false) {
            request_headers_map.put(header, new ArrayList());
        }
        request_headers_map.get(header).add(value);
    }
    
    public void setHeader(String header, String value) {
        if (request_headers_map.containsKey(header) == true) {
            request_headers_map.get(header).clear();
            request_headers_map.get(header).add(value);
            return;
        }
        addHeader(header, value);
    }
    
    StringBuilder manageConnection(HttpsURLConnection connection) {
        for (Map.Entry<String, List<String>> header_entry : request_headers_map.entrySet()) {
            String header_name = header_entry.getKey();
            
            List<String> header_list = header_entry.getValue();
            for (String header : header_list) {
                connection.addRequestProperty(header_name, header);
                if (log_request_headers == true) {
                    request_headers_log.append(header_entry).append(": ").append(header).append("\n");
                }
            }
        }
        return request_headers_log;
    }
    
    public StringBuilder get_request_headers_log() {
        return request_headers_log;
    }
}
