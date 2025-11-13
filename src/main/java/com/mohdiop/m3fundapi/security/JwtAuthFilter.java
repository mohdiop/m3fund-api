package com.mohdiop.m3fundapi.security;

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

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                if (jwtService.isValidAccessToken(authHeader)) {
                    Long userId = jwtService.getUserIdFromToken(authHeader);
                    var authorities = jwtService.getUserRolesFromToken(authHeader)
                            .stream().map((userRole) -> new SimpleGrantedAuthority(userRole.name()))
                            .toList();
                    var auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                // Si le token est invalide ou expiré, on ignore simplement et continue
                // Cela permet aux routes publiques de fonctionner même avec un token invalide
            }
        }
        filterChain.doFilter(request, response);
    }
}
