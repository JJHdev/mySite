package com.project.mySite.email;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<Email, String> {
    // 인증코드 발송한 이메일 주소 조회
    Optional<Email> findByEmail(String email);
    // User 테이블에서 인증코드 받아서 확인했는지 체크
    Optional<Email> findByEmailAndEmailStatus(String email, boolean emailStatus);

}
