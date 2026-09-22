package com.saluslaboris.api.controller;

import com.saluslaboris.api.dto.JwtResponse;
import com.saluslaboris.api.dto.LoginRequest;
import com.saluslaboris.api.dto.PerfilResponse;
import com.saluslaboris.api.security.UserPrincipal;
import com.saluslaboris.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(auth.login(request));
    }

    @GetMapping("/me")
    public PerfilResponse me(@AuthenticationPrincipal UserPrincipal user) {
        return auth.perfil(user.id());
    }
}