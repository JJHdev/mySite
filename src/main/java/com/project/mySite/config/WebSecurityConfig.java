package com.project.mySite.config;

import com.project.mySite.component.Utils.JwtUtil;
import com.project.mySite.component.exception.CustomAccessDeniedException;
import com.project.mySite.component.exception.JwtAuthenticationEntryPoint;
import com.project.mySite.component.filter.JwtRequestFilter;
import com.project.mySite.component.security.MyUserDetailsService;
import com.project.mySite.token.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final MyUserDetailsService myUserDetailsService;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedException customAccessDeniedException;

    @Autowired
    public WebSecurityConfig(MyUserDetailsService myUserDetailsService,JwtUtil jwtUtil,TokenService tokenService
                            ,JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,CustomAccessDeniedException customAccessDeniedException ) {
        this.myUserDetailsService = myUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.customAccessDeniedException = customAccessDeniedException;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/user/login", "/user/register", "/user/checkUser", "/email/findSend", "/user/findPassword" , "/email/send", "/email/verify" , "/refresh-token").permitAll() // 로그인 및 회원가입 경로 허용
                        .requestMatchers("/css/**", "/img/**", "/js/**", "/scss/**", "/vendor/**", "/favicon.ico").permitAll() // 정적 파일 접근 허용
                        .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable) // 기본 폼 로그인을 비활성화
                .logout(LogoutConfigurer::permitAll)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedException)
                )
                .addFilterBefore(new JwtRequestFilter(jwtUtil, tokenService,jwtAuthenticationEntryPoint,customAccessDeniedException), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
