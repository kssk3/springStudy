package com.todoapp.todo.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.todoapp.business.service.UserService;
import com.todoapp.pressentation.dto.request.LoginRequest;
import com.todoapp.pressentation.dto.request.SignUpRequest;
import com.todoapp.pressentation.dto.response.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TodoJwtIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private String accessToken;

    @BeforeEach
    void setUp() {
        // 1. 회원 가입
        SignUpRequest request = SignUpRequest.builder()
                .email("test123@gmail.com")
                .password("Password123@")
                .passwordConfirm("Password123@")
                .name("강두기")
                .phoneNumber("010-1234-1234")
                .build();

        userService.signUp(request);

        LoginRequest loginRequest = new LoginRequest(request.getEmail(), request.getPassword());
        LoginResponse response = userService.login(loginRequest);

        this.accessToken = response.getAccessToken();
    }

    @Test
    @DisplayName("유효한 JWT로 Todo 생성 성공")
    void createTodo_WithValidJWT_Success() throws Exception {
        // given
        String requestBody = """
                {
                    "title": "테스트 제목",
                    "description": "JWT 인증 성공"
                }
                """;
        // when & then
        mockMvc.perform(post("/api/todos")
                        .header("Authorization", "Bearer " + this.accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(jsonPath("$.title").value("테스트 제목"))
                .andExpect(jsonPath("$.description").value("JWT 인증 성공"));
    }

    @Test
    @DisplayName("토큰 없이 Todo 생성 시도 - 401")
    void createTodo_WithoutToken_Unauthorized() throws Exception {
        // given
        String requestBody = """
                {
                    "title": "테스트 제목",
                    "description": "JWT 인증 성공"
                }
                """;

        mockMvc.perform(post("/api/todos")
                        .header("Authorization", "Bearer invalid.token.here")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("유요한 JWT로 Todo 목록 조회 성공")
    void getTodos_WithValidJWT_Success() throws Exception {
        // given
        String createRequest = """
                {
                    "title": "테스트 제목",
                    "description": "JWT 인증 성공"
                }
                """;

        mockMvc.perform(post("/api/todos")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest));

        // when & then
        mockMvc.perform(get("/api/todos")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("테스트 제목"));
    }

    @Test
    @DisplayName("토큰 없이 Todo 목록 조회 시 - 401")
    void getTodos_WithOut_Token_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Bearer 접두사 없는 토큰 - 401")
    void createTodo_Without_BearerPrefix_Unauthorized() throws Exception {
        // given
        String createRequest = """
                {
                    "title": "테스트 제목",
                    "description": "JWT 인증 성공"
                }
                """;

        mockMvc.perform(post("/api/todos")
                .header("Authorization", accessToken) // Bearer 누락
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest))
                .andExpect(status().isUnauthorized());
    }

}
