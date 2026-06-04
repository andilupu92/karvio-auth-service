package karvio.dto;

import jakarta.validation.constraints.NotBlank;

public record FeedbackRequest(
                              @NotBlank(message = "Description is required")
                              String description
) { }
