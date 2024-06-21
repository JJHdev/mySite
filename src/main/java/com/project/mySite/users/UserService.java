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
