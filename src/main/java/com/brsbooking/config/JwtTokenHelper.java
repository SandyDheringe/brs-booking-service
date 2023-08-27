package com.brsbooking.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.ArrayList;

@Configuration
public class JwtTokenHelper {

    private final Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);

    public UserDetails extractUserDetailsFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject(); // Extract username from claims
        // You can also extract other information such as roles, authorities, etc.

        // Create a UserDetails object based on the extracted information
        return new org.springframework.security.core.userdetails.User(
                username,
                "", // Use an empty password as it's not needed for JWT authentication
                new ArrayList<>()
        );
    }
}
