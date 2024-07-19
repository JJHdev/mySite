package com.project.mySite.token;

import com.project.mySite.component.Utils.JwtUtil;
import com.project.mySite.users.Users;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Transactional
@Service
public class TokenService {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.acessExp}")
    private long ACESS_TOKEN_TIME;
    @Value("${jwt.refreshExp}")
    private long REFRESH_TOKEN_TIME;

    private final TokenRepository tokenRepository;

    @Autowired
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Token createRefreshToken(Users users) {
        JwtUtil jwtUtil = new JwtUtil();

        String refreshToken = jwtUtil.generateRefreshToken(users);

        LocalDateTime expirationTime = LocalDateTime.now().plusSeconds(REFRESH_TOKEN_TIME / 1000);

        Token token = Token.builder()
                .userId(users.getUserId())
                .token(refreshToken)
                .expiration(expirationTime)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().before(new Date())) {
            refreshTokenRepository.delete(token);
            return Optional.empty();
        }
        return Optional.of(token);
    }

    public void deleteByUsername(String username) {
        refreshTokenRepository.deleteByUsername(username);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

}
