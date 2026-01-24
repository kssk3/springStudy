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

    public static LoginResponse of(User user) {
        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                "로그인에 성공했습니다."
        );
    }
}
