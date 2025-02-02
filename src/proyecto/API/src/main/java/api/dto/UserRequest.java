package api.dto;

import api.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
public record UserRequest(@NotNull @Size(max = 45) @NotBlank String name,
                          @NotNull @Size(max = 45) @NotBlank String surnames,
                          @NotNull @Size(max = 45) @NotBlank String username,
                          @NotNull @Size(max = 45) @NotBlank String password, @Size(max = 45) @Email String email,
                          @NotNull @Size(max = 45) String phone) implements Serializable {
}