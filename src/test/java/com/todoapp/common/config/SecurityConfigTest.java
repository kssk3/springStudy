package com.todoapp.common.config;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.todoapp.pressentation.dto.request.TodoCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("회원가입 API는 인증 없이 접근 가능")
    void signupEndPoint_WithoutAuthentication_ShouldBeAccessible() throws Exception {
        String body = """
                {
                    "email": "test123@gmail.com",
                    "password": "Password123@",
                    "passwordConfirm": "Password123@",
                    "name": "강두기",
                    "phoneNumber": "010-1234-1234"
                }
                """;

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("로그인 API는 인증 없이 접근 가능")
    void loginEndPoint_WithoutAuthentication_ShouldBeAccessible() throws Exception {
        String body = """
                {
                    "email": "test123@gmail.com",
                    "password": "Password123@",
                    "passwordConfirm": "Password123@",
                    "name": "강두기",
                    "phoneNumber": "010-1234-1234"
                }
                """;

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        String login = """
                {
                    "email": "test123@gmail.com",
                    "password": "Password123@"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(login))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("TODO API 인증 없이 접근 불가 (401 Unauthorized)")
    void todoEndPoint_WithoutAuthentication_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("TODO 생성 API도 인증 없이 접근 불가 (401)")
    void createTodoEndPoint_WithoutAuthentication_ShouldReturn401() throws Exception {
        String createTodo = """
                {
                    "title": "테스트 코드",
                    "description": "Hello"
                }
                """;

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createTodo))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("TODO 조회 API도 인증 없이 접근 불가 (401)")
    void findTodoEndPoint_WithoutAuthentication_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("TODO 삭제 API도 인증 없이 접근 불가 (401)")
    void deleteTodoEndPoint_WithoutAuthentication_ShouldReturn401() throws Exception {
        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isUnauthorized());
    }
}