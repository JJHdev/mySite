package com.project.mySite.users;

import com.project.mySite.email.Email;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUserId(String userId);
    Optional<Users> findByEmail(String email);
    Optional<Users> findByUserIdAndUserNameAndEmail(String userId, String userName, String email);

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.password = :password WHERE u.userId = :userId AND u.userName = :userName AND u.email = :email")
    int updatePassword(@Param("userId") String userId, @Param("userName") String userName, @Param("email") String email, @Param("password") String password);
}
