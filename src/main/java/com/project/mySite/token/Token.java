package com.project.mySite.token;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "token")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_seq")
    @SequenceGenerator(name = "token_seq", sequenceName = "token_seq", allocationSize = 1)
    @Column(name = "token_Id", unique = true, nullable = false)
    private Long tokenId;
    @Column(name = "user_Id", nullable = false, unique = true)
    private String userId;
    @Column(name = "token", nullable = false)
    private String token;
    @Column(name = "expiration")
    private LocalDateTime expiration;

    @Builder
    public Token(Long tokenId, String userId, String token, LocalDateTime expiration) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.token = token;
        this.expiration = expiration;
    }
}
