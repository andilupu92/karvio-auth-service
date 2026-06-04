package karvio.dto;

public record RefreshTokenResponse(
        String newAccessToken,
        String newRefreshToken
)
{ }
