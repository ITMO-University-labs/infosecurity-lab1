package itmo.infosecurity.lab1.dto;

import jakarta.validation.constraints.NotNull;

public record ErrorResponseDto(
        @NotNull int code,
        @NotNull String message
) {}