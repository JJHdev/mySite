package com.project.mySite.component.filter;

import com.project.mySite.component.Utils.JwtUtil;
import com.project.mySite.token.Token;
import com.project.mySite.token.TokenService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class JwtRequestFilter extends OncePerRequestFilter {

    @Value("${jwt.accessExp}")
    private long ACCESS_TOKEN_TIME = 1800000;

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    public JwtRequestFilter(JwtUtil jwtUtil, TokenService tokenService,AuthenticationEntryPoint authenticationEntryPoint,AccessDeniedHandler accessDeniedHandler) {
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // 정적 리소스 경로 및 로그인/회원가입 경로를 필터링하지 않음
        return path.startsWith("/css") || path.startsWith("/js") || path.startsWith("/img") ||
                path.startsWith("/vendor")     || path.startsWith("/favicon.ico") ||
                path.startsWith("/static/css") || path.startsWith("/static/js") ||
                path.startsWith("/static/img") || path.startsWith("/static/vendor") || path.startsWith("refresh-token");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String requestPath = request.getRequestURI();
        System.out.println("Request Path: " + requestPath);
        String accessToken = null;
        String refreshToken = null;
        String userId = null;

        // refreshToken 쿠키에서 추출
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                } else if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        try {

            if (accessToken != null && jwtUtil.validateToken(accessToken)) {
                // 토큰 spring 보안추가 및 저장
                jwtUtil.setAuthentication(accessToken,request);

            } else if (accessToken == null || !jwtUtil.validateToken(accessToken)) {
                SecurityContextHolder.clearContext();
                invalidateCookie(response, "accessToken");
                System.out.println("accessToken 유효성 검사 통과못함");

                // Refresh Token이 있는지 확인
                if (refreshToken != null) {
                    Optional<Token> optionalToken = tokenService.getTokenFromJwt(refreshToken);
                    // refreshToken이 있는지? null인지 유무 판단하며 만료되었을 경우 삭제 조치
                    if (optionalToken.isPresent() && tokenService.verifyExpiration(optionalToken.get()).isPresent()) {
                        Authentication authentication =  jwtUtil.getAuthentication(refreshToken);
                        String newAccessToken = jwtUtil.generateAccessToken(authentication);
                        // 토큰 spring 보안추가 및 저장
                        jwtUtil.setAuthentication(newAccessToken,request);
                        addAccessToken(response, "accessToken", newAccessToken, (int) ACCESS_TOKEN_TIME / 1000);

                    }
                }
            }

            // 로그인 및 회원가입 경로를 예외 처리
            if (requestPath.equals("/user/login") || requestPath.equals("/user/register") || requestPath.equals("/user/findPassword")) {
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    // 사용자가 이미 로그인된 경우
                    response.sendRedirect("/");
                    return;
                }
            }

            chain.doFilter(request, response);
        } catch (AccessDeniedException e) {
            invalidateCookie(response, "accessToken");
            invalidateCookie(response, "refreshToken");
            accessDeniedHandler.handle(request, response, e);
        } catch (Exception e) {
            invalidateCookie(response, "accessToken");
            invalidateCookie(response, "refreshToken");
            authenticationEntryPoint.commence(request, response, (AuthenticationException) e);
        }

    }

    private void invalidateCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private void addAccessToken(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(false);
        //추후 변경
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

}
