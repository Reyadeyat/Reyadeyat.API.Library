package net.reyadeyat.api.library.http;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RequestPayload {
    
    String content;
    List<RequestParameter> form_parameter_list;
    FormFileUpload[] form_file_upload;
    
    StringBuilder payload_log;
    boolean log_request_payload;
    
    public RequestPayload(boolean log_request_payload) {
        this(log_request_payload, null, null, null);
    }
    
    public RequestPayload(boolean log_request_payload, String content) {
        this(log_request_payload, content, null, null);
    }
    
    public RequestPayload(boolean log_request_payload, List<RequestParameter> form_parameter_list) {
        this(log_request_payload, null, form_parameter_list, null);
    }
    
    public RequestPayload(boolean log_request_payload, FormFileUpload[] form_file_upload) {
        this(log_request_payload, null, null, form_file_upload);
    }
    
    public RequestPayload(boolean log_request_payload, List<RequestParameter> form_parameter_list, FormFileUpload[] form_file_upload) {
        this(log_request_payload, null, form_parameter_list, form_file_upload);
    }
    
    public RequestPayload(boolean log_request_payload, String content, List<RequestParameter> form_parameter_list, FormFileUpload[] form_file_upload) {
        this.log_request_payload = log_request_payload;
        this.content = content;
        this.form_parameter_list = form_parameter_list;
        this.form_file_upload = form_file_upload;
        payload_log = new StringBuilder();
    }
    
    void append_payload_log(byte[] payload_log) {
        if (log_request_payload == false) {
            return;
        }
        this.payload_log.append(new String(payload_log, StandardCharsets.UTF_8));
    }
    
    void append_payload_log(String payload_log) {
        if (log_request_payload == false) {
            return;
        }
        this.payload_log.append(payload_log);
    }
    
    StringBuilder get_payload_log() {
        return this.payload_log;
    }
}
