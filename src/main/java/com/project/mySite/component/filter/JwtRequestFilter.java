package com.project.mySite.component.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mySite.component.Utils.JwtUtil;
import com.project.mySite.component.security.MyUserDetailsService;
import com.project.mySite.token.Token;
import com.project.mySite.token.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.acessExp}")
    private long ACESS_TOKEN_TIME;
    @Value("${jwt.refreshExp}")
    private long REFRESH_TOKEN_TIME;

    private final MyUserDetailsService myUserDetailsService;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @Autowired
    public JwtRequestFilter(MyUserDetailsService myUserDetailsService, JwtUtil jwtUtil,TokenService tokenService) {
        this.myUserDetailsService = myUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        System.out.println("Request Path: " + requestPath);

        String accessToken = null;
        String refreshToken = null;
        String userId = null;

        // access토큰 로컬 스토리지에서 추출
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }

        // refreshToken 쿠키에서 추출
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }


        // 로그인 및 회원가입 경로를 예외 처리
        if (requestPath.equals("/user/login") || requestPath.equals("/user/register")) {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                // 사용자가 이미 로그인된 경우
                response.sendRedirect("/");
                return;
            } else {
                System.out.println("Skipping JWT filter for path: " + requestPath);
                chain.doFilter(request, response);
                return;
            }
        }

        // accessToken이 없거나, 인증 만료되었을 경우
        if (accessToken == null || jwtUtil.isTokenExpired(accessToken)) {

            // Access token이 만료된 경우, refresh token을 사용해 새로운 access token 발행 로직으로 넘어감
            if (refreshToken != null) {
                Optional<Token> optionalToken = tokenService.getTokenFromJwt(refreshToken);

                // refreshToken이 있는지?? null인지 유무 판단하며 만료되었을 경우 삭제 조치
                if (optionalToken.isPresent() && tokenService.verifyExpiration(optionalToken.get()).isPresent()) {
                    // refreshToken으로부터 userId 추출 및 accessToken 재발급
                    userId = jwtUtil.getExtractUserId(refreshToken);
                    UserDetails userDetails = this.myUserDetailsService.loadUserByUsername(userId);
                    String newAccessToken = jwtUtil.generateAccessToken(userDetails);

                    // 새로운 Access Token을 JSON 응답으로 추가
                    response.setContentType("application/json");
                    response.getWriter().write("{\"accessToken\": \"" + newAccessToken + "\"}");

                    // 토큰 spring 보안추가 및 저장
                    jwtUtil.setAuthentication(userDetails,request);
                }else{
                    // RefreshToken이 유효하지 않을 경우
                    invalidateCookie(response, "refreshToken");
                    response.sendRedirect("/user/login");
                    return;
                }
            } else {
                // RefreshToken이 유효하지 않을 경우
                invalidateCookie(response, "refreshToken");
                response.sendRedirect("/user/login");
                return;
            }
            
        // accessToken이 있거나, 인증이 만료되지 않았을 경우
        } else {
            userId = jwtUtil.getExtractUserId(accessToken);

            // userId이 있으며, 권한인증이 없을 경우 (로그인전, 세션이 만료, 서버 재시작, 필터체인 우회, 로그아웃)
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = myUserDetailsService.loadUserByUsername(userId);

                // accessToken에서 userId와 DB에 있는 userId의 정보와 같은지 체크
                if (jwtUtil.validateToken(accessToken, userDetails)) {
                    jwtUtil.setAuthentication(userDetails,request);
                }
            }
        }
        chain.doFilter(request, response);
    }

    private void invalidateCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}
