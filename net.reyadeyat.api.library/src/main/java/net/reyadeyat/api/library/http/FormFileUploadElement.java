package net.reyadeyat.api.library.http;

import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class FormFileUploadElement {
    private String file_name;
    private String file_content_type;
    private InputStream file_input_stream;
    
    public FormFileUploadElement(String file_name, String file_content_type, InputStream file_input_stream) throws Exception {
        if (file_name == null) {
            throw new Exception("Error File Upload file_name is null");
        }
        if (file_content_type == null) {
            throw new Exception("Error File Upload content_type is null");
        }
        this.file_name = file_name;
        this.file_content_type = file_content_type;
        this.file_input_stream = file_input_stream;
    }
    
    public String getFileName() {
        return file_name;
    }
    
    public String getFileContentType() {
        return file_content_type;
    }
    
    public InputStream getFileInputStream() {
        return file_input_stream;
    }
    
    public InputStream getFileGzipInputStream() throws Exception {
        return new GZIPInputStream(file_input_stream);
    }
}
