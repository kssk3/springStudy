package com.todoapp.pressentation.controller;

import com.todoapp.dataaccess.entity.User;
import com.todoapp.pressentation.dto.request.LoginRequest;
import com.todoapp.pressentation.dto.request.SignUpRequest;
import com.todoapp.pressentation.dto.response.LoginResponse;
import com.todoapp.pressentation.dto.response.SignUpResponse;
import com.todoapp.business.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        User user = userService.signUp(request);

        SignUpResponse response = SignUpResponse.from(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 로그인 API
     *
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
