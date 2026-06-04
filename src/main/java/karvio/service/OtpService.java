package karvio.service;

import karvio.entity.PasswordResetOtp;
import karvio.repository.PasswordResetOtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final PasswordResetOtpRepository otpRepository;

    @Value("${otp.expiration.minutes}")
    private int otpExpirationMinutes;

    @Transactional
    public String generateAndSaveOtp(String email) {

        otpRepository.deleteAllByEmail(email);

        String otpCode = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(otpExpirationMinutes);

        otpRepository.save(new PasswordResetOtp(email, otpCode, expiresAt));

        return otpCode;
    }

    public boolean validateOtp(String email, String otpCode) {
        return findValidOtp(email, otpCode).isPresent();
    }

    @Transactional
    public boolean validateAndConsumeOtp(String email, String otpCode) {
        Optional<PasswordResetOtp> otpOptional = findValidOtp(email, otpCode);

        if (otpOptional.isEmpty()) return false;

        PasswordResetOtp otp = otpOptional.get();
        otp.setUsed(true);
        otpRepository.save(otp);

        return true;
    }

    private Optional<PasswordResetOtp> findValidOtp(String email, String otpCode) {
        Optional<PasswordResetOtp> otpOptional = otpRepository
                .findTopByEmailOrderByExpiresAtDesc(email);

        if (otpOptional.isEmpty()) return Optional.empty();

        PasswordResetOtp otp = otpOptional.get();

        if (otp.isUsed()) return Optional.empty();
        if (LocalDateTime.now().isAfter(otp.getExpiresAt())) return Optional.empty();
        if (!otp.getOtpCode().equals(otpCode)) return Optional.empty();

        return Optional.of(otp);
    }

}