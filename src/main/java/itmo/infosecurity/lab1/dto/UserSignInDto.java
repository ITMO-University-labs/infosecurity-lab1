package itmo.infosecurity.lab1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserSignInDto(@Email
                            @NotBlank
                            @Size(max = 255)
                            String email,

                            @NotBlank
                            @Size(min = 6, max = 32)
                            String password) {
}
