package karvio.dto;

public record TokenRequest(String token,
                           String email,
                           String firstName,
                           String lastName
) { }
