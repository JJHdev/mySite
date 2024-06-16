package com.project.mySite.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "USERS")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_key_generator")
    @SequenceGenerator(name = "user_key_generator", sequenceName = "user_key_seq", allocationSize = 1)
    @Column(name = "USER_KEY")
    private Long userKey;

    @NotNull
    @Column(name = "USER_ID", nullable = false)
    private String userId;

    @NotNull
    @Size(max = 50)
    @Column(name = "USER_NAME", nullable = false)
    private String userName;

    @NotNull
    @Size(max = 300)
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @NotNull
    @Pattern(regexp = "[MF]")
    @Column(name = "GENDER", nullable = false)
    private String gender;

    @Email
    @Size(max = 200)
    @Column(name = "EMAIL", unique = true)
    private String email;

    @Size(max = 300)
    @Column(name = "PROFILE_PICTURE_URL")
    private String profilePictureUrl;

    @Size(max = 20)
    @Column(name = "STATUS", nullable = false)
    private String status = "ACTIVE";

    @Column(name = "CREATE_DATE", nullable = false, updatable = false)
    private Timestamp createDate;

    @Column(name = "UPDATE_DATE")
    private LocalDateTime updateDate;

    @Column(name = "UPDATE_ID")
    private String updateId;

    @Override
    public String toString() {
        return "Users{" +
                "userKey=" + userKey +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", status='" + status + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", updateId='" + updateId + '\'' +
                '}';
    }

    public Long getUserKey() {
        return userKey;
    }

    public void setUserKey(Long userKey) {
        this.userKey = userKey;
    }

    public @NotNull String getUserId() {
        return userId;
    }

    public void setUserId(@NotNull String userId) {
        this.userId = userId;
    }

    public @NotNull @Size(max = 50) String getUserName() {
        return userName;
    }

    public void setUserName(@NotNull @Size(max = 50) String userName) {
        this.userName = userName;
    }

    public @NotNull @Size(max = 300) String getPassword() {
        return password;
    }

    public void setPassword(@NotNull @Size(max = 300) String password) {
        this.password = password;
    }

    public @NotNull @Pattern(regexp = "[MF]") String getGender() {
        return gender;
    }

    public void setGender(@NotNull @Pattern(regexp = "[MF]") String gender) {
        this.gender = gender;
    }

    public @Email @Size(max = 200) String getEmail() {
        return email;
    }

    public void setEmail(@Email @Size(max = 200) String email) {
        this.email = email;
    }

    public @Size(max = 300) String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(@Size(max = 300) String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public @Size(max = 20) String getStatus() {
        return status;
    }

    public void setStatus(@Size(max = 20) String status) {
        this.status = status;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateId() {
        return updateId;
    }

    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }
}
