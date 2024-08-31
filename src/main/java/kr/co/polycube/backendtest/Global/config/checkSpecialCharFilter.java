package kr.co.polycube.backendtest.Global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class checkSpecialCharFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String queryString = request.getQueryString();
        String requestURI = request.getRequestURI();
        String fullRequest = requestURI + (queryString != null ? "?" + queryString : "");

        if (fullRequest.matches(".*[^a-zA-Z0-9/?&=:.-].*")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"reason\": \"URL contains non-allowed special characters\"}");
            response.getWriter().flush();
            return;
        }
        filterChain.doFilter(request, response);
    }

}