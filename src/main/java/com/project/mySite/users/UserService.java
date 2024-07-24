package com.project.mySite.users;

import com.project.mySite.component.Utils.JwtUtil;
import com.project.mySite.component.Utils.ServiceResult;
import com.project.mySite.component.Utils.Utils;
import com.project.mySite.component.exception.ValidationUserException;
import com.project.mySite.component.security.MyUserDetailsService;
import com.project.mySite.email.EmailRepository;
import com.project.mySite.token.Token;
import com.project.mySite.token.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final EmailRepository emailRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository, JwtUtil jwtUtil,
                       EmailRepository emailRepository, PasswordEncoder passwordEncoder, TokenService tokenService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.emailRepository = emailRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService  = tokenService;
        this.authenticationManager = authenticationManager;
    }

    public ServiceResult<Users> register(UsersDTO usersDTO){

        //UserDTO to user
        Users users = UserDtoToUser(usersDTO);

        //유효성검사
        try {
            ValidUserIdDuplicate(users);
            validPasswordMatch(users, usersDTO);
            ValidEmailDuplicate(users);
            ValidEmailCheck(users);
            users.setPassword(passwordEncoder.encode(users.getPassword()));
            Users savedUser = userRepository.save(users);
            return ServiceResult.success(savedUser);
        } catch (ValidationUserException e){
            return ServiceResult.failure(e.getMessage());
        } catch (Exception e) {
            return ServiceResult.failure("An unexpected error occurred: " + e.getMessage());
        }
    }

    public ServiceResult<Users> checkUser(UsersDTO usersDTO){

        //UserDTO to user
        Users users = UserDtoToUser(usersDTO);

        //유효성검사
        try {
            ValidUserIdDuplicate(users);
            ValidUserIdCheck(users);
            return ServiceResult.success(users);
        } catch (ValidationUserException e){
            return ServiceResult.failure(e.getMessage());
        } catch (Exception e) {
            return ServiceResult.failure("An unexpected error occurred: " + e.getMessage());
        }
    }

    public ServiceResult<UsersDTO> login(UsersDTO usersDTO, HttpServletRequest request) {

        //UserDTO to user
        Users users = UserDtoToUser(usersDTO);

        try{
            validateUserIdAndPassword(users);

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usersDTO.getUserId(), usersDTO.getPassword()));

            String accessToken = jwtUtil.generateAccessToken(authentication);
            Token saveRefreshToken = tokenService.createRefreshToken(authentication);

            usersDTO.setAccessToken(accessToken);
            usersDTO.setRefreshToken(saveRefreshToken.getToken());

            jwtUtil.setAuthentication(accessToken,request);

            if(usersDTO.getAccessToken() == null || usersDTO.getAccessToken().isEmpty() ||  usersDTO.getRefreshToken() == null || usersDTO.getRefreshToken().isEmpty() ) {
                throw new ValidationUserException("로그인에 실패하였습니다. 관리자에게 문의해주시길 바랍니다.");
            }

            return ServiceResult.success(usersDTO);
        } catch (ValidationUserException e){
            return ServiceResult.failure(e.getMessage());
        } catch (AuthenticationException e) {
            return ServiceResult.failure("Invalid credentials: " + e.getMessage());
        } catch (Exception e) {
            return ServiceResult.failure("An unexpected error occurred: " + e.getMessage());
        }
    }

    private Users UserDtoToUser(UsersDTO usersDTO){
        Users users = new Users();

        users.setUserId(usersDTO.getUserId());
        users.setUserName(usersDTO.getUserName());
        users.setPassword(usersDTO.getPassword());
        users.setEmail(usersDTO.getEmail1()+usersDTO.getEmail2_hidden());
        users.setGender(usersDTO.getGender());
        users.setStatus(usersDTO.getStatus());
        users.setCreateDate(Utils.formatLocalDateTime(usersDTO.getCreateDate()));

        return users;
    }

    private void ValidUserIdDuplicate(Users users) {
        userRepository.findByUserId(users.getUserId())
            .ifPresent(m -> {
                throw new ValidationUserException("등록된 유저아이디가 있습니다.");
            });
    }

    private void validPasswordMatch(Users users , UsersDTO usersDTO) {
        if (!users.getPassword().equals(usersDTO.getPasswordChck())) {
            throw new ValidationUserException("비밀번호가 서로 다릅니다.");
        }
    }

    private void ValidEmailCheck(Users users) {
        // 이메일 인증 상태 확인
        boolean isEmailVerified = emailRepository.findByEmailAndEmailStatus(users.getEmail(), true).isPresent();

        if (!isEmailVerified) {
            throw new ValidationUserException("이메일 인증이 완료되지 않았습니다.");
        }

        // 중복 이메일 체크
        userRepository.findByEmail(users.getEmail())
            .ifPresent(m -> {
                throw new ValidationUserException("등록된 유저 이메일이 있습니다.");
            });
    }

    private void ValidUserIdCheck(Users users) {
        if (users.getUserId() == null || users.getUserId().trim().isEmpty() || users.getUserId().length() < 4) {
            throw new ValidationUserException("아이디는 최소 4글자 이상작성되어야 합니다.");
        }
    }

    private void ValidEmailDuplicate(Users users) {
        userRepository.findByEmail(users.getEmail())
            .ifPresent(m -> {
                throw new ValidationUserException("등록된 이메일주소가 있습니다.");
            });
    }

    private void validateUserIdAndPassword(Users users) {
        // 사용자 ID 검증
        Optional<Users> optionalUser = userRepository.findByUserId(users.getUserId());
        if (optionalUser.isEmpty()) {
            throw new ValidationUserException("가입된 계정 ID가 없습니다.");
        }

        // 비밀번호 검증
        Users foundUser = optionalUser.get();
        if (!passwordEncoder.matches(users.getPassword(), foundUser.getPassword())) {
            throw new ValidationUserException("비밀번호가 일치하지 않습니다.");
        }
    }

}
