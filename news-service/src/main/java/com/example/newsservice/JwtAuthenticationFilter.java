package com.example.newsservice;


import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                Claims claims = jwtService.parseToken(token);
                String username = claims.getSubject();

                Object rolesClaim = claims.get("roles");
                List<String> roles = rolesClaim instanceof List<?>
                        ? ((List<?>) rolesClaim).stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .collect(Collectors.toList())
                        : List.of();

                var authorities = roles.stream()
                        .map(role -> role.startsWith("ROLE_")
                                ? role
                                : "ROLE_" + role)
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                var authentication =
                        new UsernamePasswordAuthenticationToken(
                                username, null, authorities
                        );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

            } catch (Exception exception) {
                SecurityContextHolder.clearContext();
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Недействительный или просроченный токен"
                );
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
