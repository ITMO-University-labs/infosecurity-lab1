package itmo.infosecurity.lab1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserResponseDto(@Email
                              @NotBlank
                              @Size(max = 64)
                              String email,

                              @NotBlank
                              @Size(max = 32)
                              String nickname
) {}
