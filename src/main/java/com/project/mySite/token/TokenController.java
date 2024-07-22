package com.project.mySite.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mySite.component.Utils.JwtUtil;
import com.project.mySite.component.security.MyUserDetailsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class TokenController {

    private JwtUtil jwtUtil;
    private MyUserDetailsService myUserDetailsService;
    private TokenService tokenService;

    public TokenController(JwtUtil jwtUtil, MyUserDetailsService myUserDetailsService,TokenService tokenService ){
        this.jwtUtil = jwtUtil;
        this.myUserDetailsService = myUserDetailsService;
        this.tokenService = tokenService;
    }

    @PostMapping("/refresh-token")
    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = null;

        // refreshToken 쿠키에서 추출
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        // Refresh Token이 있는지 확인
        if (refreshToken != null) {
            Optional<Token> optionalToken = tokenService.getTokenFromJwt(refreshToken);

            // refreshToken이 있는지? null인지 유무 판단하며 만료되었을 경우 삭제 조치
            if (optionalToken.isPresent() && tokenService.verifyExpiration(optionalToken.get()).isPresent()) {
                // refreshToken으로부터 userId 추출 및 accessToken 재발급
                String userId = jwtUtil.getExtractUserId(refreshToken);
                UserDetails userDetails = this.myUserDetailsService.loadUserByUsername(userId);
                String newAccessToken = jwtUtil.generateAccessToken(userDetails);

                // 새로운 Access Token을 JSON 응답으로 추가
                response.setContentType("application/json");
                response.getWriter().write("{\"accessToken\": \"" + newAccessToken + "\"}");

                // 토큰 spring 보안추가 및 저장
                jwtUtil.setAuthentication(userDetails,request);

            } else {
                // RefreshToken이 유효하지 않을 경우
                invalidateCookie(response, "refreshToken");
                response.sendRedirect("/user/login");
            }
        } else {
            // RefreshToken이 유효하지 않을 경우
            invalidateCookie(response, "refreshToken");
            response.sendRedirect("/user/login");
        }
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
