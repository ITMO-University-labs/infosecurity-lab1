package itmo.infosecurity.lab1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public record UserDto(@Email
                      @NotBlank
                      @Size(max = 255)
                      String email,

                      @NotBlank
                      @Size(min = 4, max = 32)
                      String nickname,

                      @NotBlank
                      @Size(min = 6, max = 32)
                      String password) {
}
