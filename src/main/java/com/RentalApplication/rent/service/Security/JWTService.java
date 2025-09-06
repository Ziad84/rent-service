package com.RentalApplication.rent.service.Security;

import com.RentalApplication.rent.service.Entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;



@Slf4j
@Service
public class JWTService {

    private final SecretKey key;
    private final long tokenExpirationMs;

    public JWTService(
            @Value("${jwt.secret}") String base64Secret,
            @Value("${jwt.expiration-ms}") long tokenExpirationMs
    ) {

        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret));
        this.tokenExpirationMs = tokenExpirationMs;
    }


    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString()) // Using ID instead of email
                .claim("roles", user.getRole().getName())
                .claim("email", user.getEmail()) // Optional: keep email as a claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpirationMs))
                .signWith(key)
                .compact();
    }

    // Extract user ID from token
    public String extractUserId(String token) {
        try {
            return getClaims(token).getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    // For backward compatibility if needed
    public String extractUsername(String token) {
        try {
            return (String) getClaims(token).get("email");
        } catch (ExpiredJwtException e) {
            return (String) e.getClaims().get("email");
        }
    }

    public String extractRole(String token) {
        try {
            return (String) getClaims(token).get("roles");
        } catch (ExpiredJwtException e) {
            return (String) e.getClaims().get("roles");
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return getClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String handleToken(String token, User user) {
        if (token == null || !isTokenValid(token) || isTokenExpired(token)) {
            return generateToken(user);
        }
        return token;
    }
}
