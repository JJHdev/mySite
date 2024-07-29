package com.project.mySite.token;

import com.project.mySite.component.Utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Transactional
@Service
public class TokenService {

    @Value("${jwt.refreshExp}")
    private long REFRESH_TOKEN_TIME;

    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;


    @Autowired
    public TokenService(TokenRepository tokenRepository, JwtUtil jwtUtil) {
        this.tokenRepository = tokenRepository;
        this.jwtUtil = jwtUtil;
    }

    public Token createRefreshToken(Authentication authentication) {

        String refreshToken = jwtUtil.generateRefreshToken(authentication);
        LocalDateTime expirationTime = LocalDateTime.now().plusSeconds(REFRESH_TOKEN_TIME / 1000);

        Token token = Token.builder()
                .userId(authentication.getName())
                .token(refreshToken)
                .expiration(expirationTime)
                .build();
        return tokenRepository.save(token);
    }

    public Integer deleteByUserId(String userId) {
        return tokenRepository.deleteByUserId(userId);
    }

    public Optional<Token> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public Optional<Token> verifyExpiration(Token token) {
        if (token.getExpiration().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            return Optional.empty();
        }
        return Optional.of(token);
    }

    public Optional<Token> getTokenFromJwt(String refreshToken) {
        String userId = jwtUtil.getExtractUserId(refreshToken);
        return tokenRepository.findByUserIdAndToken(userId, refreshToken);
    }


}
