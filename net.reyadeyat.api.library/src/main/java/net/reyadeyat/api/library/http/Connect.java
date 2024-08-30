package net.reyadeyat.api.library.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HttpsURLConnection;
import net.reyadeyat.api.library.util.Randomizer;

public class Connect {
    
    static final Random randoms = new Random();
    static final Integer random_upper_bound = 999999;
    static final Integer random_lower_bound = 100000;
    
    static private byte[] hyphens = "--".getBytes(StandardCharsets.UTF_8);
    static private byte[] crlf = "\r\n".getBytes(StandardCharsets.UTF_8);
    static private byte[] disposition = "Content-Disposition: form-data; name=\"".getBytes(StandardCharsets.UTF_8);
    static private byte[] disposition_file_name="; filename=\"".getBytes(StandardCharsets.UTF_8);
    static private byte[] dqouts = "\"".getBytes(StandardCharsets.UTF_8);
    
    static Response connect(Request request) throws Exception {
        byte[] form_boundary = null;
        byte[] form_file_bounday = null;
        byte[] user_content = request.content == null || request.content.isBlank() ? null : request.content.getBytes(StandardCharsets.UTF_8);
        if (request.method == Request.METHOD.GET
                || (request.method == Request.METHOD.POST
                && request.content_type == Request.CONTENT_TYPE.FORM_URL_ENCODED
                && request.content_type == Request.CONTENT_TYPE.FORM_DATA)) {
            request.url = buildGetParameters(request.url, request.request_payload);
        }
        /*URL connectionURL = new URL(url);
        HttpURLConnection connection = null;
        SSLSocketFactory ssf = TLS.getSocketFactory();
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connectionURL.openConnection();
        httpsURLConnection.setSSLSocketFactory(ssf);//(ssf != null ? ssf : (SSLSocketFactory) SSLSocketFactory.getDefault());
        connection = httpsURLConnection;*/
        
        Boolean force_trust_url = request.force_trust_url;
        HttpsURLConnection connection = TLS.getHttpsConnection(request.url, force_trust_url);
        
        if (request.method == Request.METHOD.POST && request.content_type == Request.CONTENT_TYPE.FORM_URL_ENCODED) {
            connection.setRequestProperty("Content-Type", Request.CONTENT_TYPE.FORM_URL_ENCODED.toString());
        } else if (request.method == Request.METHOD.POST && request.content_type == Request.CONTENT_TYPE.FORM_DATA) {
            String form_boundary_str = "******multipart-form-boundary-"+Randomizer.random(randoms, random_lower_bound, random_upper_bound);
            String form_file_boundary_str = "******multipart-form-file-boundary-"+Randomizer.random(randoms, random_lower_bound, random_upper_bound);
            form_boundary = form_boundary_str.getBytes(StandardCharsets.UTF_8);
            form_file_bounday = form_file_boundary_str.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + form_boundary_str);
        } else {
            connection.setRequestProperty("Content-Type", request.content_type.toString());
        }
        
        request.manageConnection(connection);
        
        connection.connect();
        
        /*GET OPTIONS HEAD TRACE does not have body*/
        if (request.request_payload != null && (request.method == Request.METHOD.POST == true || request.method == Request.METHOD.PUT == true)) {
            try (OutputStream out = connection.getOutputStream()) {
                if (request.content_type == Request.CONTENT_TYPE.FORM_URL_ENCODED) {
                    buildPostParameters(request.request_payload, out);
                } else if (request.request_payload.form_parameter_list != null && request.content_type == Request.CONTENT_TYPE.FORM_DATA) {
                    for (RequestParameter request_parameter : request.request_payload.form_parameter_list) {
                        buildFormParameter(request.request_payload, out, form_boundary, request_parameter);
                    }
                    buildFormFileUpload(request.request_payload, out, form_boundary, form_file_bounday);
                    closeForm(request.request_payload, out, form_boundary);
                } else {
                    if (user_content != null && user_content.length > 0) {
                        Connect.write(request.request_payload, out, user_content);
                    }
                }
                out.flush();
            } catch (Exception ex) {
                if (request.log_errors == true) {
                    Logger.getLogger(Connect.class.getName()).log(Level.CONFIG, "Error Writing to URL '"+request.url+"' " + ex.getMessage(), ex);
                }
                throw ex;
            }
        }
        
        return new Response(connection, request);
    }
    
    static public InputStream getInStream(HttpURLConnection con, InputStream stream) throws Exception {
        if ("gzip".equals(con.getContentEncoding())) {
            return new GZIPInputStream(stream);
        }
        PushbackInputStream pb = new PushbackInputStream(stream, 2); //we need a pushbackstream to look ahead
        byte[] signature = new byte[2];
        //read the signature
        int len = pb.read(signature);
        if (len == -1) {//There is no data in this stream => fail safe
            return new ByteArrayInputStream(new byte[0]);
        }
        //push back the signature to the stream
        pb.unread(signature, 0, len);
        //GZIP_MAGIC check if matches standard gzip magic number
        if (signature[0] == (byte) 0x1f && signature[1] == (byte) 0x8b) {
            return new GZIPInputStream(pb);
        }
        return pb;
    }
    
    static protected void buildPostParameters(RequestPayload request_payload, OutputStream out) throws Exception {
        if (request_payload.form_parameter_list == null) {
            return;
        }
        StringBuilder payload = new StringBuilder();
        for (RequestParameter parameter : request_payload.form_parameter_list) {
            //result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            payload.append(URLEncoder.encode(parameter == null ? "null" : parameter.parameter_name, "UTF-8"));
            payload.append("=");
            if (parameter.parameter_value != null) {
                //result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                payload.append(parameter.parameter_value);
            }
            payload.append("&");
        }
        payload.deleteCharAt(payload.length() - 1);
        Connect.write(request_payload, out, payload.toString());
    }
    
    static private void write(RequestPayload request_payload, OutputStream out, String payload) throws Exception {
        out.write(payload.getBytes(StandardCharsets.UTF_8));
        if (request_payload != null) {
            request_payload.append_payload_log(payload);
        }
    }
    
    static private void write(RequestPayload request_payload, OutputStream out, byte[] payload) throws Exception {
        out.write(payload);
        if (request_payload != null) {
            request_payload.append_payload_log(payload);
        }
    }
    
    static private void buildFormParameter(RequestPayload request_payload, OutputStream out, byte[] form_boundary, RequestParameter request_parameter) throws Exception {
        Connect.write(request_payload, out, hyphens);
        Connect.write(request_payload, out, form_boundary);
        Connect.write(request_payload, out, crlf);
        Connect.write(request_payload, out, "Content-Disposition: form-data; name=\"");
        Connect.write(request_payload, out, request_parameter.parameter_name);
        Connect.write(request_payload, out, dqouts);
        Connect.write(request_payload, out, crlf);
        Connect.write(request_payload, out, "Content-Type: ");
        Connect.write(request_payload, out, request_parameter.parameter_content_type);
        Connect.write(request_payload, out, crlf);
        Connect.write(request_payload, out, crlf);
        Connect.write(request_payload, out, request_parameter.parameter_value);
        Connect.write(request_payload, out, crlf);
    }
    
    static private void buildFormFileUpload(RequestPayload request_payload, OutputStream out, byte[] form_boundary, byte[] form_file_bounday) throws Exception {
        if (request_payload.form_file_upload == null) {
            return;
        }
        for (int i = 0; i < request_payload.form_file_upload.length; i++) {
            if (request_payload.form_file_upload[i] == null) {
                throw new Exception("Error uploadFile file at index "+i+" is null");
            }
            try {
                request_payload.form_file_upload[i].upload(out, form_boundary, form_file_bounday);
            } catch(Exception ex) {
                throw new Exception("Error uploadFile '"+request_payload.form_file_upload[i].getName()+"'" + ex.getMessage(), ex);
            }
        }
    }
    
    static private void closeForm(RequestPayload request_payload, OutputStream out, byte[] form_boundary) throws Exception {
        Connect.write(request_payload, out, hyphens);
        Connect.write(request_payload, out, form_boundary);
        Connect.write(request_payload, out, hyphens);
        Connect.write(request_payload, out, crlf);
    }

    static protected URL buildGetParameters(URL url, RequestPayload request_payload) throws Exception {
        if (request_payload == null || request_payload.form_parameter_list == null || request_payload.form_parameter_list.size() == 0) {
            return url;
        }
        StringBuilder result = new StringBuilder();
        result.append(url);
        result.append("?");
        for (RequestParameter parameter : request_payload.form_parameter_list) {
            result.append(URLEncoder.encode(parameter.parameter_name, "UTF-8"));
            result.append("=");
            //result.append(URLEncoder.encode(parameter.parameter_value, "UTF-8"));
            result.append(URLEncoder.encode(parameter.parameter_value == null ? "" : parameter.parameter_value, "UTF-8"));
            result.append("&");
        }
        result.deleteCharAt(result.length()-1);
        return new URI(result.toString()).toURL();
    }
}
