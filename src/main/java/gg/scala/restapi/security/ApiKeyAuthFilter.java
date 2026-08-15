package gg.scala.restapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class ApiKeyAuthFilter extends OncePerRequestFilter {
    private final String apiKey;

    public ApiKeyAuthFilter(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String rawAuthHeader = request.getHeader("Authorization");
        
        // Handle duplicate headers (some clients send the same header twice, causing comma concatenation)
        String requestApiKey = null;
        if (rawAuthHeader != null) {
            // If multiple values were concatenated, take the first one
            requestApiKey = rawAuthHeader.split(",")[0].trim();
        }
        
        if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
            System.out.println("ApiKeyAuthFilter: FAILED - Path=" + request.getRequestURI() + 
                ", RawHeader='" + rawAuthHeader + "'" +
                ", ExtractedKey='" + requestApiKey + "'" + 
                ", ExpectedKey='" + apiKey + "'");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("{\"error\": \"Invalid or missing API key\"}");
            response.setContentType("application/json");
            return;
        }

        System.out.println("ApiKeyAuthFilter: SUCCESS - Path=" + request.getRequestURI());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "API_USER", null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_API_USER"))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}