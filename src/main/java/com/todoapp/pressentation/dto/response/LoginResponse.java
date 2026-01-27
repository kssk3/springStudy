package com.todoapp.pressentation.dto.response;

import com.todoapp.dataaccess.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String email;
    private String name;
    private String message;

    public static LoginResponse of(Long id, String email, String name) {
        return new LoginResponse(id, email, name, "로그인에 성공했습니다.");
    }
}
