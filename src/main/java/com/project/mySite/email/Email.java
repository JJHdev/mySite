package com.project.mySite.email;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "email_verification")
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_verification_seq")
    @SequenceGenerator(name = "email_verification_seq", sequenceName = "email_verification_seq", allocationSize = 1)
    @Column(name = "email_id", unique = true, nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "email_status", nullable = false)
    private boolean emailStatus;

    @Column(name = "code")
    private String code;

    @Column(name = "expiration")
    private LocalDateTime expiration;

    @Builder
    public Email(String email, String code, LocalDateTime expiration) {
        this.email = email;
        this.emailStatus = false;
        this.code = code;
        this.expiration = expiration;
    }
}