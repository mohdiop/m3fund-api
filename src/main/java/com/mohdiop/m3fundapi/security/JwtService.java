package com.mohdiop.m3fundapi.security;

import com.mohdiop.m3fundapi.entity.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Getter
    private final long refreshTokenValidityMs = 30L * 24L * 60L * 60L * 1000L;
    @Value("${jwt.secret}")
    private String jwtSecret;
    private SecretKey secretKey;

    @PostConstruct
    void init() {
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret.getBytes(StandardCharsets.UTF_8)));
    }

    private String generateToken(Long userId, TokenType tokenType, long expiryAt, Set<UserRole> userRoles) {
        Date now = Date.from(Instant.now());
        Date expirationDate = new Date(now.getTime() + expiryAt);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", tokenType.name())
                .claim("roles", userRoles.stream().map(UserRole::name).collect(Collectors.toList()))
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String generateAccessToken(Long userId, Set<UserRole> userRoles) {
        long accessTokenValidityMs = 5L * 60L * 60L * 1000L;
        return generateToken(userId, TokenType.ACCESS_TOKEN, accessTokenValidityMs, userRoles);
    }

    public String generateRefreshToken(Long userId, Set<UserRole> userRoles) {
        return generateToken(userId, TokenType.REFRESH_TOKEN, refreshTokenValidityMs, userRoles);
    }

    public boolean isValidAccessToken(String accessToken) {
        Claims claims = parseAllClaims(accessToken);
        if (claims == null) return false;
        String type = (String) claims.get("type");
        return TokenType.ACCESS_TOKEN.name().equals(type);
    }

    public boolean isValidRefreshToken(String refreshToken) {
        Claims claims = parseAllClaims(refreshToken);
        if (claims == null) return false;
        String type = (String) claims.get("type");
        return TokenType.REFRESH_TOKEN.name().equals(type);
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseAllClaims(token);
        if (claims == null) throw new IllegalArgumentException("Token invalide.");
        return Long.parseLong(claims.getSubject());
    }

    public Set<UserRole> getUserRolesFromToken(String token) {
        Claims claims = parseAllClaims(token);
        if (claims == null) throw new IllegalArgumentException("Token invalide");
        List<?> roles = (List<?>) claims.get("roles");
        return roles.stream()
                .map(Object::toString)
                .map(UserRole::valueOf)
                .collect(Collectors.toSet());
    }

    private Claims parseAllClaims(String token) {
        String rawToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken)
                .getPayload();
    }

    private enum TokenType {
        ACCESS_TOKEN,
        REFRESH_TOKEN
    }
}

