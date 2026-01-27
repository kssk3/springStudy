package com.todoapp.Integration;

import static org.assertj.core.api.Assertions.*;

import com.todoapp.business.service.UserService;
import com.todoapp.common.exception.InvalidCredentialsException;
import com.todoapp.dataaccess.repository.UserRepository;
import com.todoapp.pressentation.dto.request.LoginRequest;
import com.todoapp.pressentation.dto.request.SignUpRequest;
import com.todoapp.pressentation.dto.response.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class UserLoginIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SignUpRequest request = SignUpRequest.builder()
                .email("kss8014@gmail.com")
                .password("Password123@")
                .passwordConfirm("Password123@")
                .name("강두기")
                .phoneNumber("010-9203-5808")
                .build();

        userService.signUp(request);
    }

    @Test
    @DisplayName("올바른 이메일과 비밀번호로 로그인 성공")
    void login_WithValidCredentials_Success() {
        // given
        LoginRequest request = new LoginRequest("kss8014@gmail.com", "Password123@");

        // when
        LoginResponse response = userService.login(request);

        // then
        assertThat(response.getEmail()).isEqualTo("kss8014@gmail.com");
        assertThat(response.getName()).isEqualTo("강두기");
        assertThat(response.getMessage()).isEqualTo("로그인에 성공했습니다.");
    }

    @Test
    @DisplayName("존재하지 않은 이메일로 로그인 시도 시 실패")
    void login_WithNonExistentEmail_Fail() {
        // given
        LoginRequest request = new LoginRequest("test123@gmail.com", "Password123@");

        // when & then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("이메일 또는 비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시도 시 실패")
    void login_WithWrongPassword_Fail() {
        // given
        LoginRequest request = new LoginRequest("kss8014@gmail.com", "TestPassword123@");

        // when & then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("이메일 또는 비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("로그인 성공 후 반환된 userId가 올바른지 확인")
    void login_Success_ReturnCorrectUserId() {
        // given
        LoginRequest request = new LoginRequest("kss8014@gmail.com", "Password123@");

        // when
        LoginResponse response = userService.login(request);

        assertThat(userRepository.findById(response.getId())).isPresent();
    }
}
