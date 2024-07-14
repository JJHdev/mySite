package com.project.mySite.component.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;


@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.acessExp}")
    private long ACESS_TOKEN_TIME;
    @Value("${jwt.refreshExp}")
    private long REFRESH_TOKEN_TIME;

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, ACESS_TOKEN_TIME);
    }

    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, REFRESH_TOKEN_TIME);
    }

    private String generateToken(Authentication authentication, long validity) {
        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return getClaimsFromToken(token).getExpiration().before(new Date());
    }

}
