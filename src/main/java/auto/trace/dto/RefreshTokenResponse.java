package auto.trace.dto;

public record RefreshTokenResponse(
        String newAccessToken,
        String newRefreshToken
)
{ }
