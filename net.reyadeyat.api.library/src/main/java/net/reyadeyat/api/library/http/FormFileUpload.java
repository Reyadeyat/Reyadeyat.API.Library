package net.reyadeyat.api.library.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class FormFileUpload {
    private String parameter_name;
    private ArrayList<FormFileUploadElement> file_upload_element_list;
    
    static private byte[] hyphens = "--".getBytes(StandardCharsets.UTF_8);
    static private byte[] crlf = "\r\n".getBytes(StandardCharsets.UTF_8);
    static private byte[] dqouts = "\"".getBytes(StandardCharsets.UTF_8);
    static private byte[] file_disposition_p1 = "Content-Disposition: form-data; name=\"".getBytes(StandardCharsets.UTF_8);
    static private byte[] file_disposition_p2 = "\"; filename=\"".getBytes(StandardCharsets.UTF_8);
    static private byte[] contenttype = "Content-Type: ".getBytes(StandardCharsets.UTF_8);
    static private byte[] contenttype_mixed = "Content-type: multipart/mixed, boundary=".getBytes(StandardCharsets.UTF_8);
    
    public FormFileUpload(String parameter_name, FormFileUploadElement file_upload_element) throws Exception {
        this(parameter_name, new ArrayList<FormFileUploadElement>(Arrays.asList(new FormFileUploadElement[]{file_upload_element})));
    }
    
    public FormFileUpload(String parameter_name, ArrayList<FormFileUploadElement> file_upload_element_list) throws Exception {
        if (parameter_name == null) {
            throw new Exception("Error File Upload parameter_name is null");
        }
        
        if (file_upload_element_list == null || file_upload_element_list.size() == 0) {
            throw new Exception("Error File Upload input_stream is null");
        }
        this.parameter_name = parameter_name;
        this.file_upload_element_list = file_upload_element_list;
    }
    
    public void upload(OutputStream out, byte[] form_boundary, byte[] form_file_boundary) throws Exception {
        if (file_upload_element_list.size() == 1) {
            uploadFile(out, form_boundary, file_upload_element_list.get(0));
        } else {
            uploadMultiFiles(out, form_boundary, form_file_boundary);
        }
    }
    
    private void uploadFile(OutputStream out, byte[] form_boundary, FormFileUploadElement file_upload_element) throws Exception {
        out.write(hyphens);
        out.write(form_boundary);
        out.write(crlf);
        out.write(file_disposition_p1);
        out.write(parameter_name.getBytes(StandardCharsets.UTF_8));
        out.write(file_disposition_p2);
        out.write(file_upload_element.getFileName().getBytes(StandardCharsets.UTF_8));
        out.write(dqouts);
        out.write(crlf);
        out.write(contenttype);
        out.write(file_upload_element.getFileContentType().getBytes(StandardCharsets.UTF_8));
        out.write(crlf);
        out.write(crlf);
        
        byte[] buffer = new byte[1024*1024/2];
        int read = -1;
        try (InputStream input_stream = file_upload_element.getFileInputStream()) {
            while ((read = input_stream.read(buffer)) > -1) {
                out.write(buffer, 0, read);
            }
            input_stream.close();
        } catch (Exception ex) {
            throw ex;
        }
        out.write(crlf);
    }
    
    private void uploadMultiFiles(OutputStream out, byte[] form_boundary, byte[] form_file_boundary) throws Exception {
        out.write(hyphens);
        out.write(form_boundary);
        out.write(crlf);
        out.write(file_disposition_p1);
        out.write(parameter_name.getBytes(StandardCharsets.UTF_8));
        out.write(file_disposition_p2);
        out.write(crlf);
        out.write(contenttype_mixed);
        out.write(form_file_boundary);
        out.write(crlf);
        out.write(crlf);
        
        for (FormFileUploadElement file_upload_element : file_upload_element_list) {
            uploadFile(out, form_file_boundary, file_upload_element);
        }
    }
    
    public String getName() {
        return parameter_name;
    }
}
