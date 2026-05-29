package auto.trace.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOtpEmail(String toEmail, String otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Code");
            helper.setText(buildEmailBody(otpCode), true); // true = HTML

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email to: " + toEmail, e);
        }
    }

    // ─── Template HTML pentru email ─────────────────────────────────
    private String buildEmailBody(String otpCode) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 480px; margin: auto;">
                    <h2 style="color: #1a1a1a;">Password Reset Request</h2>
                    <p style="color: #555;">Use the code below to reset your password.
                       This code expires in <strong>10 minutes</strong>.</p>
                    
                    <div style="
                        background-color: #f4f4f4;
                        border-radius: 12px;
                        padding: 24px;
                        text-align: center;
                        margin: 24px 0;">
                        <span style="
                            font-size: 36px;
                            font-weight: bold;
                            letter-spacing: 8px;
                            color: #111;">%s</span>
                    </div>
                    
                    <p style="color: #999; font-size: 13px;">
                        If you didn't request this, you can safely ignore this email.
                    </p>
                </div>
                """.formatted(otpCode);
    }
}
