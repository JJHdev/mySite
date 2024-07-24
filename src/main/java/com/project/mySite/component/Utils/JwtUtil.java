package com.project.mySite.component.Utils;

import com.project.mySite.component.security.MyUserDetailsService;
import com.project.mySite.email.EmailRepository;
import com.project.mySite.users.UserRepository;
import com.project.mySite.users.UserService;
import com.project.mySite.users.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
@Log4j2
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.acessExp}")
    private long ACESS_TOKEN_TIME;
    @Value("${jwt.refreshExp}")
    private long REFRESH_TOKEN_TIME;

    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    public JwtUtil(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    public String generateAccessToken(Authentication authentication) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + ACESS_TOKEN_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(authentication.getName())
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + REFRESH_TOKEN_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(authentication.getName())
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaimsFromToken(token);
        String userPrincipal = claims.getSubject();
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(userPrincipal);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public boolean validateToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String userId = claims.getSubject();
        Date expiration = claims.getExpiration();
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(claims.getSubject());

        boolean isExpired = expiration.before(new Date());

        return (userId.equals(userDetails.getUsername()) && !isExpired);
    }

    public String getExtractUserId(String token) {
        return getClaimsFromToken(token).getSubject();
    }


    public void setAuthentication(String token, HttpServletRequest request) {
        Authentication auth = getAuthentication(token);
        if (auth instanceof UsernamePasswordAuthenticationToken) {
            ((UsernamePasswordAuthenticationToken) auth).setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        }
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


}
