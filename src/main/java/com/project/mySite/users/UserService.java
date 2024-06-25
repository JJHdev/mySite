package com.project.mySite.users;

import com.project.mySite.UtilsComponent.ServiceResult;
import com.project.mySite.UtilsComponent.Utils;
import com.project.mySite.email.EmailRepository;
import com.project.mySite.exception.DuplicateUserException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;

    @Autowired
    public UserService(UserRepository userRepository,EmailRepository emailRepository) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
    }

    public ServiceResult<Users> register(UsersDTO usersDTO){

        //DB 입력할 domain setting
        Users users = new Users();
        users.setUserId(usersDTO.getUserId());
        users.setUserName(usersDTO.getUserName());
        users.setPassword(usersDTO.getPassword());
        users.setEmail(usersDTO.getEmail1()+usersDTO.getEmail2_hidden());
        users.setGender(usersDTO.getGender());
        users.setStatus(usersDTO.getStatus());
        users.setCreateDate(Utils.formatLocalDateTime(usersDTO.getCreateDate()));

        //유효성검사
        try {
            ValidUserIdDuplicate(users);
            validPasswordMatch(users, usersDTO);
            ValidEmailDuplicate(users);
            ValidEmailDuplicate(users);
            ValidEmailCheck(users);
            Users savedUser = userRepository.save(users);
            return ServiceResult.success(savedUser);
        } catch (DuplicateUserException e){
            return ServiceResult.failure(e.getMessage());
        } catch (Exception e) {
            return ServiceResult.failure("An unexpected error occurred: " + e.getMessage());
        }
    }

    public ServiceResult<Users> checkUser(UsersDTO usersDTO){

        Users users = new Users();
        users.setUserId(usersDTO.getUserId());
        //유효성검사
        try {
            ValidUserIdDuplicate(users);
            ValidUserIdCheck(users);
            return ServiceResult.success(users);
        } catch (DuplicateUserException e){
            return ServiceResult.failure(e.getMessage());
        } catch (Exception e) {
            return ServiceResult.failure("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void ValidUserIdDuplicate(Users users) {
        userRepository.findByUserId(users.getUserId())
            .ifPresent(m -> {
                throw new DuplicateUserException("등록된 유저아이디가 있습니다.");
            });
    }

    private void validPasswordMatch(Users users , UsersDTO usersDTO) {
        if (!users.getPassword().equals(usersDTO.getPasswordChck())) {
            throw new DuplicateUserException("비밀번호가 서로 다릅니다.");
        }
    }

    private void ValidEmailCheck(Users users) {
        // 이메일 인증 상태 확인
        boolean isEmailVerified = emailRepository.findByEmailAndEmailStatus(users.getEmail(), true).isPresent();

        if (!isEmailVerified) {
            throw new DuplicateUserException("이메일 인증이 완료되지 않았습니다.");
        }

        // 중복 이메일 체크
        userRepository.findByEmail(users.getEmail())
            .ifPresent(m -> {
                throw new DuplicateUserException("등록된 유저 이메일이 있습니다.");
            });
    }

    private void ValidUserIdCheck(Users users) {
        if (users.getUserId() == null || users.getUserId().trim().isEmpty() || users.getUserId().length() < 4) {
            throw new DuplicateUserException("아이디는 최소 4글자 이상작성되어야 합니다.");
        }
    }

    private void ValidEmailDuplicate(Users member) {
        userRepository.findByEmail(member.getEmail())
            .ifPresent(m -> {
                throw new DuplicateUserException("등록된 이메일주소가 있습니다.");
            });
    }


}
