package com.project.mySite.component.filter;

import com.project.mySite.component.Utils.JwtUtil;
import com.project.mySite.component.security.MyUserDetailsService;
import com.project.mySite.token.Token;
import com.project.mySite.token.TokenService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;



public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
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

        /*

        if (refreshToken != null) {
            System.out.println("Refresh Token is not null");
             Optional<Token> optionalToken = tokenService.getTokenFromJwt(refreshToken);
            if (optionalToken.isPresent() && tokenService.verifyExpiration(optionalToken.get()).isPresent()) {
                System.out.println("Refresh Token is present and valid");
            }
           } else {
            System.out.println("Refresh Token is invalid or expired");
        }


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
                    chain.doFilter(request, response);
                    return;
                }
            } else {
                // RefreshToken이 유효하지 않을 경우
                invalidateCookie(response, "refreshToken");
                chain.doFilter(request, response);
                return;
            }

        */

        if (accessToken != null && jwtUtil.validateToken(accessToken)) {
            // 토큰 spring 보안추가 및 저장
            jwtUtil.setAuthentication(accessToken,request);
        } else if (accessToken == null) {
            SecurityContextHolder.clearContext();
            System.out.println("accessToken 이 없음");
        } else if (!jwtUtil.validateToken(accessToken)) {
            SecurityContextHolder.clearContext();
            invalidateCookie(response, "accessToken");
            System.out.println("accessToken 유효성 검사 통과못함");
        }

        // 로그인 및 회원가입 경로를 예외 처리
        if (requestPath.equals("/user/login") || requestPath.equals("/user/register")) {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                // 사용자가 이미 로그인된 경우
                response.sendRedirect("/");
                return;
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

}
