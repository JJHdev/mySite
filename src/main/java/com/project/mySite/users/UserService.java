package com.project.mySite.users;

import com.project.mySite.UtilsComponent.ServiceResult;
import com.project.mySite.UtilsComponent.Utils;
import com.project.mySite.exception.DuplicateUserException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ServiceResult<Users> register(UsersForm usersForm){

        //DB 입력할 domain setting
        Users users = new Users();
        users.setUserId(usersForm.getUserId());
        users.setUserName(usersForm.getUserName());
        users.setPassword(usersForm.getPassword());
        users.setEmail(usersForm.getEmail1()+usersForm.getEmail2());
        users.setGender(usersForm.getGender());
        users.setStatus(usersForm.getStatus());
        users.setCreateDate(Utils.formatLocalDateTime(usersForm.getCreateDate()));

        //유효성검사
        try {
            ValidateDuplicateUsersByUserId(users);
            ValidateDuplicateUsersByEmail(users);
            Users savedUser = userRepository.save(users);
            return ServiceResult.success(savedUser);
        } catch (DuplicateUserException e){
            return ServiceResult.failure(e.getMessage());
        } catch (Exception e) {
            return ServiceResult.failure("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void ValidateDuplicateUsersByUserId(Users users) {
        userRepository.findByUserId(users.getUserId())
            .ifPresent(m -> {
                throw new DuplicateUserException("등록된 유저아이디가 있습니다.");
            });
    }

    private void ValidateDuplicateUsersByEmail(Users member) {
        userRepository.findByEmail(member.getEmail())
            .ifPresent(m -> {
                throw new DuplicateUserException("등록된 이메일주소가 있습니다.");
            });
    }


}
