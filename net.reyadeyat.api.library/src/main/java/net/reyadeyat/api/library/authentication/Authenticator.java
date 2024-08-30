package net.reyadeyat.api.library.authentication;

import net.reyadeyat.api.library.http.Request;
import net.reyadeyat.api.library.http.RequestCookies;
import net.reyadeyat.api.library.http.RequestHeaders;
import net.reyadeyat.api.library.http.RequestPayload;
import net.reyadeyat.api.library.http.Response;
import net.reyadeyat.api.library.http.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.reyadeyat.api.library.environment.ApiEnvironment;

public class Authenticator {
    
    final public static String API_JWT_HEADER_NAME="net.reyadeyat.api.authentication.jwt.header.name";
    final public static String AUTHENTICATION_TOKEN="AUTHENTICATION_TOKEN";
    
    public static void prepareSession(HttpServletRequest request) {
        try {
            String api_jwt = request.getHeader(ApiEnvironment.getString(Authenticator.API_JWT_HEADER_NAME));
            if (api_jwt == null) {
                return;
            }
            HttpSession session = request.getSession();
            String authentication_token = (String) session.getAttribute(Authenticator.AUTHENTICATION_TOKEN);
            String authentication_api = ApiEnvironment.getString("net.reyadeyat.api.authentication");
            if (authentication_api == null && authentication_token == null) {
                authentication_token = Authenticator.generateAuthenticationToken();
                session.setAttribute(Authenticator.AUTHENTICATION_TOKEN, authentication_token);
            } else if (authentication_api != null) {
                synchronized (session) {
                    Logger.getLogger(Authenticator.class.getName()).log(Level.INFO, "Session " + session.toString());
                    authentication_token = (String) session.getAttribute(Authenticator.AUTHENTICATION_TOKEN);
                    if (authentication_token == null) {
                        authenticate(authentication_api, request, session);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, "\nFail Stop Main Filter Exception\n", ex);
        }
    }
    
    public static String generateAuthenticationToken() {
        Base64.Encoder base64_encoder = Base64.getEncoder();
        SecureRandom secureRandom = new SecureRandom();
        byte[] random_bytes = new byte[222];
        secureRandom.nextBytes(random_bytes);
        String padded_time = pad(String.valueOf(System.currentTimeMillis()), 'n', 16);
        StringBuilder authentication_token_buffer = new StringBuilder()
                .append(padded_time)
                .append(UUID.randomUUID())
                .append(String.valueOf((long)(Math.random()*Math.pow(10, 16))))
                .append(hexBuffer(random_bytes));
        String authentication_token = authentication_token_buffer.toString();
        return base64_encoder.encodeToString(authentication_token.getBytes());
    }
    
    public static String pad(String string, char pad_char, int length ) {
        StringBuilder padded_string = new StringBuilder(string);
        while (padded_string.length() < length) {
            padded_string.append(pad_char);
        }
        return padded_string.toString();
    }
    
    public static String hexBuffer(byte[] buffer) {
        StringBuilder hex_buffer = new StringBuilder();
        for (byte b : buffer) {
            hex_buffer.append(String.format("%02X", b));
        }
        return hex_buffer.toString();
    }
    
    final public static boolean authenticate(String authentication_api, HttpServletRequest http_servlet_request, HttpSession http_session) throws Exception {
        String api_jwt = http_servlet_request.getHeader(ApiEnvironment.getString(Authenticator.API_JWT_HEADER_NAME));
        Request.METHOD method = Request.METHOD.POST;
        Request.CONTENT_TYPE content_type = Request.CONTENT_TYPE.APPLICATION_JSON;
        String url = authentication_api;
        int connection_time_out = ApiEnvironment.getInteger("net.reyadeyat.api.authentication.connection_timeout");
        boolean force_trust_url = ApiEnvironment.getBoolean("net.reyadeyat.api.authentication.force_trust_url");
        RequestPayload request_payload = new RequestPayload(false);
        boolean follow_redirects = ApiEnvironment.getBoolean("net.reyadeyat.api.authentication.follow_redirects");
        RequestHeaders request_headers = new RequestHeaders(true, ApiEnvironment.getString("net.reyadeyat.api.authentication.user_agent"), ApiEnvironment.getBoolean("net.reyadeyat.api.authentication.log_request_headers"), ApiEnvironment.getBoolean("net.reyadeyat.api.authentication.log_response_headers"));
        request_headers.addHeader(ApiEnvironment.getString(Authenticator.API_JWT_HEADER_NAME), api_jwt);
        RequestCookies request_cookies = null;
        SessionManager session_manager = null;
        boolean log_errors = ApiEnvironment.getBoolean("net.reyadeyat.api.authentication.log_errors");
        Request request = new Request(method, content_type, url, connection_time_out, force_trust_url, request_payload, follow_redirects, request_headers, request_cookies, session_manager, log_errors) {
            
            @Override
            public void connectionSuccess(Response response) {
                http_session.setAttribute(Authenticator.AUTHENTICATION_TOKEN, response.getResponseBodySuccess().toString());
                Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, "Authentication successful '"+response.getResponseBodySuccess().toString()+"'");
            }
            
            @Override
            public void connectionFailure(Exception exception) {
                if (exception == null) {
                    Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, "Authentication Failiure");
                    return;
                }
                Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, "Main Filter Authentication Failed with Exception", exception);
            }
        };
        
        request.connect();
        
        return true;
    }
}
