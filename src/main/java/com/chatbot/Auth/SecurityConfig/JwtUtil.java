package com.chatbot.Auth.SecurityConfig;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    public final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Générer un token
    public String generateToken(String email, String id) {
        return Jwts.builder()
                .setSubject(email)
                .claim("id", id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 heures
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Extraire l'email depuis le token
    public String extractEmail(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

    // Vérifier si le token est valide
    public boolean validateToken(String token, String userEmail) {
        return extractEmail(token).equals(userEmail) && !isTokenExpired(token);
    }

    // Vérifier l'expiration du token
    private boolean isTokenExpired(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }
}
