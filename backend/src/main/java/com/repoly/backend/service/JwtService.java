package com.repoly.backend.service;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {

    private final String SECRET_KEY;
    private final SecretKey key;

    public JwtService(@Value("${app.secret.key}") String secretKey) {
        this.SECRET_KEY = secretKey;
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key, SignatureAlgorithm.HS384)
                .compact();
    }

    public String extractUsername(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    //oauth github
    public String generateGithubState(String username) {

        return Jwts.builder()
                .subject(username)
                .claim("purpose", "github-oauth")
                .claim("nonce", UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(key, SignatureAlgorithm.HS384)
                .compact();
    }

    public String extractGithubUsername(String state) {

        var claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(state)
                .getPayload();

        String purpose = claims.get("purpose", String.class);

        if (!"github-oauth".equals(purpose)) {
            throw new IllegalArgumentException("Invalid GitHub OAuth state");
        }

        return claims.getSubject();
    }
}