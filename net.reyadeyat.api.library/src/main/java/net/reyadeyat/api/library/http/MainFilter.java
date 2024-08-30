package net.reyadeyat.api.library.http;

import net.reyadeyat.api.library.authentication.Authenticator;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public class MainFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request_parameter_wrapper = new RequestParameterWrapper((HttpServletRequest)request);
        Authenticator.prepareSession(request_parameter_wrapper);
        chain.doFilter(request_parameter_wrapper, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    public void destroy() {
        
    }
}