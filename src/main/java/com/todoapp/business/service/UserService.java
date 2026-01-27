package com.todoapp.business.service;

import com.todoapp.common.exception.InvalidCredentialsException;
import com.todoapp.common.security.CustomUserDetails;
import com.todoapp.dataaccess.entity.User;
import com.todoapp.pressentation.dto.request.LoginRequest;
import com.todoapp.pressentation.dto.request.SignUpRequest;
import com.todoapp.dataaccess.repository.UserRepository;
import com.todoapp.pressentation.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public User signUp(SignUpRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        String encodePassword = passwordEncoder.encode(request.getPassword());

        // User Entity create
        User user = User.builder()
                .email(request.getEmail())
                .password(encodePassword)
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        return userRepository.save(user);
    }

    /**
     * 로그인 - Spring Security AuthenticationManager
     *
     * @param request 로그인 요청 (이메일, 비밀번호)
     * @return LoginResponse 로그인 성공 정보
     * @throws InvalidCredentialsException 이메일 또는 비밀번호 불일치
     */
    public LoginResponse login(LoginRequest request) {
        // 1단계 : Spring Security 인증 수행
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return LoginResponse.of(
                userDetails.getUserId(),
                userDetails.getEmail(),
                userDetails.getName()
        );
    }


}
