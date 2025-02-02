package api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * DTO for {@link api.domain.User}
 */
public record UserResponse(Integer id, @NotNull @NotBlank @Size(max = 45) String name,
                           @NotNull @NotBlank @Size(max = 45) String surnames,
                           @NotNull @NotBlank @Size(max = 45) String username, @Size(max = 45) String email,
                           @NotNull @NotBlank @Size(max = 9) String phone) implements Serializable {
}