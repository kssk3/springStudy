package com.todoapp;

import static org.assertj.core.api.Assertions.*;

import com.todoapp.domain.User;
import com.todoapp.dto.request.SignUpRequest;
import com.todoapp.exception.DuplicateEmailException;
import com.todoapp.repository.UserRepository;
import com.todoapp.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserSignUpIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void 회원가입_성공_테스트() {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .email("test123@gmail.com")
                .password("Password123!")
                .passwordConfirm("Password123!")
                .name("강두기")
                .phoneNumber("010-1234-1234")
                .build();

        // when
        Long userId = userService.signUp(request);

        // then
        User savedUser = userRepository.findById(userId).orElseThrow();
        assertThat(savedUser.getName()).isEqualTo("강두기");
        assertThat(savedUser.getEmail()).isEqualTo("test123@gmail.com");
        // 비밀번호가 암호화 되었는지 확인

        assertThat(savedUser.getPassword()).isNotEqualTo("Password123!");
        assertThat(savedUser.getPassword()).startsWith("$2a$");
    }

    @Test
    @DisplayName("회원가입 후 중복 이메일로 재가입 시 실패")
    void signUp_ThenDuplicateEmail_Fail() {
        // given
        SignUpRequest first = SignUpRequest.builder()
                .email("test123@gmail.com")
                .password("Password123!")
                .passwordConfirm("Password123!")
                .name("강두기")
                .phoneNumber("010-1234-1234")
                .build();

        userService.signUp(first);

        // when & then
        SignUpRequest second = SignUpRequest.builder()
                .email("test123@gmail.com")
                .password("Password123!")
                .passwordConfirm("Password123!")
                .name("강맑음")
                .phoneNumber("010-1234-1234")
                .build();

        assertThatThrownBy(() -> userService.signUp(second))
                .isInstanceOf(DuplicateEmailException.class);

        assertThat(userRepository.count()).isEqualTo(1);
    }
}