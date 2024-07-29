package com.project.mySite.email;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class EmailDto {
    @Id
    private String email;
    private String code;
    private LocalDateTime expiration;
    private String userId;
    private String userName;

}
