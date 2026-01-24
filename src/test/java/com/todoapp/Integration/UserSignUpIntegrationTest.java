package com.todoapp;

import static org.assertj.core.api.Assertions.*;

import com.todoapp.dataaccess.entity.User;
import com.todoapp.pressentation.dto.request.SignUpRequest;
import com.todoapp.dataaccess.repository.UserRepository;
import com.todoapp.business.service.UserService;
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
        SignUpRequest request = SignUpRequest.builder()
                .email("test123@gmail.com")
                .password("Password123!")
                .passwordConfirm("Password123!")
                .name("강두기")
                .phoneNumber("010-1234-1234")
                .build();

        User user = userService.signUp(request);

        User savedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(savedUser.getName()).isEqualTo("강두기");
        assertThat(savedUser.getEmail()).isEqualTo("test123@gmail.com");

        assertThat(savedUser.getPassword()).isNotEqualTo("Password123!");
        assertThat(savedUser.getPassword()).startsWith("$2a$");
    }

    @Test
    @DisplayName("회원가입 후 중복 이메일로 재가입 시 실패")
    void signUp_ThenDuplicateEmail_Fail() {
        SignUpRequest first = SignUpRequest.builder()
                .email("test123@gmail.com")
                .password("Password123!")
                .passwordConfirm("Password123!")
                .name("강두기")
                .phoneNumber("010-1234-1234")
                .build();

        userService.signUp(first);

        SignUpRequest second = SignUpRequest.builder()
                .email("test123@gmail.com")
                .password("Password123!")
                .passwordConfirm("Password123!")
                .name("강맑음")
                .phoneNumber("010-1234-1234")
                .build();

        assertThatThrownBy(() -> userService.signUp(second))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(userRepository.count()).isEqualTo(1);
    }
}
