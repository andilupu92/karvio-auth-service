package auto.trace.controller;

import auto.trace.dto.*;
import auto.trace.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final AuthService authService;

    AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return new ResponseEntity<>(authService.register(registerRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(authService.login(loginRequest), HttpStatus.ACCEPTED);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return new ResponseEntity<>(authService.refreshToken(request), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestHeader("X-User-Id") Long userId) {
        authService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
