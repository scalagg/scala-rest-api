package gg.scala.restapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ApiKeyAuthFilter extends OncePerRequestFilter {
    private final String apiKey;
    private final List<String> excludedPaths = List.of(
        "/swagger-ui/",
        "/v3/api-docs/",
        "/swagger-ui.html",
        "/swagger-resources/",
        "/webjars/",
        "/css/",
        "/js/",
        "/images/",
        "/actuator/health"
    );

    public ApiKeyAuthFilter(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return excludedPaths.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestApiKey = request.getHeader("Authorization");

        if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "API_USER", null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_API_USER"))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
