package com.project.mySite.users;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class UsersDTO {

    private String userId;
    private String userName;
    private String password;
    private String passwordChck;
    private String gender;
    private String email1;
    private String email2_hidden;
    private String profilePictureUrl;
    private String status = "ACTIVE";
    private LocalDateTime createDate = LocalDateTime.now();


}
