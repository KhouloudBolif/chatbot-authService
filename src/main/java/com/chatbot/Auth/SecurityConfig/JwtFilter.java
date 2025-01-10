package com.chatbot.Auth.SecurityConfig;

import com.chatbot.Auth.SecurityConfig.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

   /* @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);

            if (email != null && jwtUtil.validateToken(token, email)) {
                // Placeholder pour l'authentification
                // Vous pouvez ajouter des détails utilisateur au SecurityContext si nécessaire.
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }

        // Continue la chaîne de filtres
        filterChain.doFilter(request, response);
    }*/

    @Autowired
    private TokenBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.replace("Bearer ", "");

            // Vérifier si le token est dans la liste noire
            if (blacklistService.isTokenBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token invalidé");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
