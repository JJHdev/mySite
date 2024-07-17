package com.project.mySite.component.filter;

import com.project.mySite.component.Utils.JwtUtil;
import com.project.mySite.component.security.MyUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
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


@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final MyUserDetailsService myUserDetailsService;
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtRequestFilter(MyUserDetailsService myUserDetailsService, JwtUtil jwtUtil) {
        this.myUserDetailsService = myUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        System.out.println("Request Path: " + requestPath);

        String accessToken = null;
        String refreshToken = null;
        String username = null;

        // Get JWT from cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                } else if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }
        
        if (accessToken != null) {
            try {
                username = jwtUtil.extractUsername(accessToken);
            } catch (ExpiredJwtException e) {
                System.out.println("Access token has expired: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Invalid access token: " + e.getMessage());
                invalidateCookie(response, "accessToken");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid access token. Please login again.");
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.myUserDetailsService.loadUserByUsername(username);

            if (jwtUtil.isTokenExpired(accessToken)) {
                if (refreshToken != null && !jwtUtil.isTokenExpired(refreshToken)) {

                    String newAccessToken = jwtUtil.generateAccessToken(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
                    Cookie newAccessTokenCookie = new Cookie("accessToken", newAccessToken);
                    newAccessTokenCookie.setHttpOnly(true);
                    newAccessTokenCookie.setSecure(true);
                    newAccessTokenCookie.setPath("/");
                    newAccessTokenCookie.setMaxAge(10); // 10초
                    response.addCookie(newAccessTokenCookie);

                } else {
                    invalidateCookie(response, "refreshToken");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh token has expired. Please login again.");
                    return;
                }
            }

            if (userDetails != null) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        // 로그인 및 회원가입 경로를 예외 처리
         if (requestPath.equals("/user/login") || requestPath.equals("/user/register")) {
            if (username != null && SecurityContextHolder.getContext().getAuthentication() != null) {
                // 사용자가 이미 로그인된 경우
                response.sendRedirect("/");
                return;
            }else {
                System.out.println("Skipping JWT filter for path: " + requestPath);
                chain.doFilter(request, response);
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
