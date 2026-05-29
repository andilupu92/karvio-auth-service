package auto.trace.controller;

import auto.trace.dto.ForgotPasswordRequest;
import auto.trace.dto.ResetPasswordRequest;
import auto.trace.dto.VerifyOtpRequest;
import auto.trace.service.AuthService;
import auto.trace.service.EmailService;
import auto.trace.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PasswordResetController {

    private final OtpService otpService;
    private final EmailService emailService;
    private final AuthService authService;


    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {

        if (!authService.existsByEmail(request.email())) {
            return ResponseEntity.ok("If this email exists, an OTP has been sent.");
        }

        String otpCode = otpService.generateAndSaveOtp(request.email());
        emailService.sendOtpEmail(request.email(), otpCode);

        return ResponseEntity.ok("If this email exists, an OTP has been sent.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {

        boolean isValid = otpService.validateOtp(request.email(), request.otpCode());

        if (!isValid) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired OTP code.");
        }

        return ResponseEntity.ok("OTP verified successfully.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {

        boolean isValid = otpService.validateAndConsumeOtp(request.email(), request.otpCode());

        if (!isValid) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired OTP code.");
        }

        authService.updatePassword(request.email(), request.newPassword());

        return ResponseEntity.ok("Password reset successfully.");
    }
}
