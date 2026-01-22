package com.todoapp.pressentation.dto.response;

import com.todoapp.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpResponse {

    private Long id;
    private String email;
    private String name;
    private String message;

    public static SignUpResponse from(User user) {
        return new SignUpResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                "회원가입이 완료되었습니다."
        );
    }
}
