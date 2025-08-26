package com.RentalApplication.rent.service.Security;

import com.RentalApplication.rent.service.Entity.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JWTService {
 /*   private final Key SECRET_KEY = Keys.hmacShaKeyFor(
            Decoders.BASE64.decode("7F9h4gq19X3X1Lq5b1E3a6r9E9m7x0XlU0W2c6q1z0g="));
    private final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24h
*/

    /*private static final String SECRET = "mysupersecretkeymysupersecretkey1234"; // >= 32 chars
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

     */

    private static final String SECRET = "mysupersecretkeymysupersecretkey1234"; // >= 32 chars
    private static final long TOKEN_EXPIRATION = 1000 * 60 * 60; // 1 hour in milliseconds
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());


    public String generateToken(Users user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }



    // Extract username/email from token
    public String extractUsername(String token) {
      //  return getClaims(token).getSubject();

        try {
            return getClaims(token).getSubject();
        } catch (ExpiredJwtException e) {
            // Return the subject from expired token
            return e.getClaims().getSubject();
        }

    }

    // Extract role from token
    public String extractRole(String token) {
        //return (String) getClaims(token).get("role");

        try {
            return (String) getClaims(token).get("role");
        } catch (ExpiredJwtException e) {
            // Return the role from expired token
            return (String) e.getClaims().get("role");
        }

    }


    public boolean isTokenExpired(String token) {
        try {
            getClaims(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return true;
        }
    }


    // Validate token expiration
    public boolean isTokenValid(String token) {
        try {
            return !getClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // same key used for signing
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String handleToken(String token, Users user) {
        if (token == null || !isTokenValid(token) || isTokenExpired(token)) {
            return generateToken(user);
        }
        return token;
    }






}
