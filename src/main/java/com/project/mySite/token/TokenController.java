package com.project.mySite.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mySite.component.Utils.JwtUtil;
import com.project.mySite.component.security.MyUserDetailsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class TokenController {

    private final AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private MyUserDetailsService myUserDetailsService;
    private TokenService tokenService;

    public TokenController(JwtUtil jwtUtil, MyUserDetailsService myUserDetailsService, TokenService tokenService, AuthenticationManager authenticationManager){
        this.jwtUtil = jwtUtil;
        this.myUserDetailsService = myUserDetailsService;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/refresh-token")
    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = null;

        String requestPath = request.getRequestURI();
        System.out.println("Request Path: " + requestPath);

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

                Authentication authentication =  jwtUtil.getAuthentication(refreshToken);
                String newAccessToken = jwtUtil.generateAccessToken(authentication);

                // 토큰 spring 보안추가 및 저장
                jwtUtil.setAuthentication(newAccessToken,request);

            } else {
                // RefreshToken이 유효하지 않을 경우
                invalidateCookie(response, "refreshToken");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            // RefreshToken이 유효하지 않을 경우
            invalidateCookie(response, "refreshToken");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
