package net.reyadeyat.api.library.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Response {
    public HttpURLConnection connection;
    public Request request;
    public Integer response_code;
    public Map<String, List<String>> response_header_fields;
    public StringBuilder response_message;
    public ResponseHeaders response_headers;
    public ResponseCookies cookies;
    private StringBuilder response_success;
    private StringBuilder response_error;
                    
    public Response(HttpURLConnection connection, Request request) throws Exception {
        this.connection = connection;
        this.request = request;
        this.response_header_fields = connection.getHeaderFields();
        this.response_headers = new ResponseHeaders(request.request_headers.log_response_headers);
        this.cookies = new ResponseCookies(response_header_fields.get("Set-Cookie"));
        this.response_code = connection.getResponseCode();
        String message = connection.getResponseMessage();
        this.response_message = new StringBuilder(message == null ? "" : message);
    }

    public Integer execute() throws Exception {
        if (response_code < 300) {
            
        } else {//response_code >= 300
            
        }
        return response_code;
    }
    
    public Boolean isSuucess() {
        return response_code < 300;
    }
    
    public Integer getResponseCode() {
        return response_code;
    }
    
    public ResponseCookies getResponseCookies() {
        return cookies;
    }
    
    public StringBuilder getResponseMessage() {
        return response_message;
    }
    
    public StringBuilder getResponseBodySuccess() {
        if (response_success != null) {
            return response_success;
        }
        if (this.response_code >= 300) {
            return new StringBuilder();
        }
        response_success = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(Connect.getInStream(connection, connection.getInputStream())))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response_success.append(inputLine);
            }
            in.close();
        } catch (Exception ex) {
            if (request.log_errors == true) {
                Logger.getLogger(Connect.class.getName()).log(Level.WARNING, "Error Reading Response Stream ["+response_code+"] from URL '"+request.url+"' " + ex.getMessage(), ex);
            }
        }
        return response_success;
    }
    
    public InputStream getResponseSuccessStream() throws Exception {
        return Connect.getInStream(connection, connection.getInputStream());
    }
    
    public StringBuilder getResponseBodyError() {
        if (response_error != null) {
            return response_error;
        }
        if (this.response_code < 300) {
            return new StringBuilder();
        }
        response_error = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(Connect.getInStream(connection, connection.getErrorStream())))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response_error.append(inputLine);
            }
            in.close();
        } catch (Exception ex) {
            if (request.log_errors == true) {
                Logger.getLogger(Connect.class.getName()).log(Level.WARNING, "Error Reading Error Stream ["+response_code+"] from URL '"+request.url+"' " + ex.getMessage(), ex);
            }
        }
        return response_error;
    }
    
    public InputStream getResponseErrorStream() throws Exception {
        return Connect.getInStream(connection, connection.getErrorStream());
    }
    
    public void disconnect() {
        connection.disconnect();
    }
    
    public HttpURLConnection getConnection() {
        return connection;
    }
    
    public void getResponseHeaders(StringBuilder response_headers, Map<String, List<String>> response_headers_map) {
        Map<String, List<String>> response_headers_m = connection.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : response_headers_m.entrySet()) {
            if (entry.getValue() == null || entry.getValue().size() == 0) {
                continue;
            }
            response_headers_map.put(entry.getKey() == null ? "null" : entry.getKey(), entry.getValue());
            response_headers.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
    }
    
    public StringBuilder getResponseHeadersMap() throws Exception {
        StringBuilder response_headers = new StringBuilder();
        Map<String, List<String>> response_headers_m = connection.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : response_headers_m.entrySet()) {
            if (entry.getValue() == null || entry.getValue().size() == 0) {
                continue;
            }
            response_headers.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return response_headers;
    }
    
    public Map<String, List<String>> getResponseHeadersMap(StringBuilder response_headers) throws Exception {
        Map<String, List<String>> response_headers_m = connection.getHeaderFields();
        Map<String, List<String>> response_headers_map = new TreeMap<String, List<String>>();
        if (response_headers != null) {
            for (Map.Entry<String, List<String>> entry : response_headers_m.entrySet()) {
                if (entry.getValue() == null || entry.getValue().size() == 0) {
                    continue;
                }
                response_headers_map.put(entry.getKey() == null ? "null" : entry.getKey(), entry.getValue());
                response_headers.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
        return response_headers_map;
    }
    
    public ResponseHeaders getResponseHeaders() {
        return response_headers;
    }
}
