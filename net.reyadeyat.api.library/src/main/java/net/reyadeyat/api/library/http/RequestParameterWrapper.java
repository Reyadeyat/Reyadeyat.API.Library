package net.reyadeyat.api.library.http;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class RequestParameterWrapper extends HttpServletRequestWrapper {

    public RequestParameterWrapper(HttpServletRequest request) {
        super(request);
    }
    
    @Override
    public String getParameter(String paramName) {
        return super.getParameter(paramName);
    }

    @Override
    public String[] getParameterValues(String paramName) {
        return super.getParameterValues(paramName);
    }

}
