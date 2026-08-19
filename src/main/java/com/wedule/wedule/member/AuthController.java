package com.wedule.wedule.member;

import com.wedule.wedule.member.dto.AuthLoginRequest;
import com.wedule.wedule.member.dto.AuthLoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Post /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> login(@RequestBody  AuthLoginRequest request) {
        String token = authService.login(request.getEmail(), request.getpassword());
        return ResponseEntity.ok(new AuthLoginResponse(token));
    }
}
