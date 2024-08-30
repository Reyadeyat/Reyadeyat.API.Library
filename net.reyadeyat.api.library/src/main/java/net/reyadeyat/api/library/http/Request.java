package net.reyadeyat.api.library.http;

import java.net.URI;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import javax.net.ssl.HttpsURLConnection;

abstract public class Request {
    
    METHOD method;
    CONTENT_TYPE content_type;
    URL url;
    int connection_time_out;
    boolean force_trust_url;
    String content;
    boolean follow_redirects;
    SessionManager session_manager;
    RequestHeaders request_headers;
    RequestPayload request_payload;
    RequestCookies request_cookies;
    boolean log_errors;
    
    public Request(METHOD method, CONTENT_TYPE content_type, String url, int connection_time_out, boolean force_trust_url, RequestPayload request_payload, boolean follow_redirects, RequestHeaders request_headers, RequestCookies request_cookies, SessionManager session_manager, boolean log_errors) throws Exception {
        this.method = method;
        this.content_type = content_type;
        this.url = new URI(url).toURL();
        this.connection_time_out = connection_time_out;
        this.force_trust_url = force_trust_url;
        this.follow_redirects = follow_redirects;
        this.request_cookies = request_cookies;
        this.session_manager = session_manager;
        this.log_errors = log_errors;
        this.request_headers = request_headers;
    }

    public void connect() {
        connect(false);
    }
    
    public FutureTask connect(boolean asynchronized) {
        if (asynchronized) {
            final Request request = this;
            FutureTask future_task = new FutureTask(new Callable<Response>() {
                @Override
                public Response call() throws Exception {
                    Response response = null;
                    try {
                        response = Connect.connect(request);
                        connectionSuccess(response);
                        return response;
                    } catch (Exception exception) {
                        connectionFailure(exception);
                        return response;
                    }
                }
            });
            return future_task;
        }
        Response response = null;
        try {
            response = Connect.connect(this);
            connectionSuccess(response);
        } catch (Exception exception) {
            connectionFailure(exception);
        }
        return null;
    }
    
    abstract public void connectionSuccess(Response response);
    abstract public void connectionFailure(Exception exception);
    
    void manageConnection(HttpsURLConnection connection) throws Exception {
        connection.setRequestMethod(method.toString());
        if (request_headers != null) {
            request_headers.manageConnection(connection);
        }
        if (request_cookies != null) {
            request_cookies.manageConnection(connection);        
        }
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(follow_redirects);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        //connection.setAllowUserInteraction(true);
        connection.setConnectTimeout(connection_time_out);
        connection.setReadTimeout(connection_time_out);
    }
    
    public StringBuilder getRequestHeadersLog() {
        return request_headers == null ? new StringBuilder() : request_headers.get_request_headers_log();
    }
    
    public StringBuilder getPayloadLog() {
        return request_payload == null ? new StringBuilder() : request_payload.get_payload_log();
    }
    
    public enum METHOD {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE"),
        OPTIONS("OPTIONS"),
        HEAD("HEAD"),
        TRACE("TRACE"),
        PATCH("PATCH");

        private final String method;

        // Constructor
        METHOD(String method) {
            this.method = method;
        }

        @Override
        public String toString() {
            return method;
        }
    }
    
    public enum CONTENT_TYPE {
        FORM_DATA("multipart/form-data"),
        FORM_URL_ENCODED("application/x-www-form-urlencoded"),
        APPLICATION_TEXT("application/text"),
        APPLICATION_JSON("application/json"),
        APPLICATION_XML("application/xml"),
        TEXT_PLAIN("text/plain"),
        TEXT_HTML("text/html"),
        TEXT_CSS("text/css"),
        APPLICATION_OCTET_STREAM("application/octet-stream"),
        APPLICATION_JAVASCRIPT("application/javascript"),
        APPLICATION_PDF("application/pdf"),
        IMAGE_JPEG("image/jpeg"),
        IMAGE_PNG("image/png"),
        IMAGE_BMP("image/bmp"),
        IMAGE_GIF("image/gif"),
        IMAGE_SVG("image/svg+xml"),
        VIDEO_MP4("video/mp4"),
        
        MS_DOC("application/msword"),
        MS_DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        MS_EXCEL("application/vnd.ms-excel"),
        MS_XLS("application/vnd.ms-excel"),
        MS_EXCELX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
        MS_XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
        MS_POWR_POINT("application/vnd.ms-powerpoint"),
        MS_PPT("application/vnd.ms-powerpoint"),
        MS_POWR_POINTX("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
        MS_PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation"),

        LIBRE_OFFICE_ODT("application/vnd.oasis.opendocument.text"),
        LIBRE_OFFICE_ODS("application/vnd.oasis.opendocument.spreadsheet"),
        LIBRE_OFFICE_ODP("application/vnd.oasis.opendocument.presentation");

        private final String content_type;

        // Constructor
        CONTENT_TYPE(String content_type) {
            this.content_type = content_type;
        }

        @Override
        public String toString() {
            return content_type;
        }
    }
}
