package com.project.mySite.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    Optional<Token> findByUserIdAndToken(String userId, String token);
    Integer deleteByUserId(String userId);
}
