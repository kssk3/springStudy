package com.todoapp.pressentation.dto.response;

import com.todoapp.dataaccess.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String email;
    private String name;
    private String accessToken;
    private String refreshToken;
    private String message;
}
