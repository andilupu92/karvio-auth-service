package karvio.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}