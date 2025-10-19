package com.assessment.ragchat.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private String apiKey;

    private boolean isSwaggerRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/webjars/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isSwaggerRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestApiKey = request.getHeader("x-api-key");

        if (requestApiKey != null && requestApiKey.equals(apiKey)) {
            // If API key is valid, create and set the Authentication object
            ApiKeyAuth authentication = new ApiKeyAuth(requestApiKey, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            // Handle invalid API key (e.g., return 401 Unauthorized)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}