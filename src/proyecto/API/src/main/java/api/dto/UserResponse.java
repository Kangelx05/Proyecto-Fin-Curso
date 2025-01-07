package api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link api.domain.User}
 */
public class UserResponse implements Serializable {
    private final Integer id;
    @NotNull
    @NotBlank
    @Size(max = 45)
    private final String name;
    @NotNull
    @NotBlank
    @Size(max = 45)
    private final String surnames;
    @NotNull
    @NotBlank
    @Size(max = 45)
    private final String username;
    @Size(max = 45)
    private final String email;
    @NotNull
    @NotBlank
    @Size(max = 9)
    private final String phone;

    public UserResponse(Integer id, String name, String surnames, String username, String email, String phone) {
        this.id = id;
        this.name = name;
        this.surnames = surnames;
        this.username = username;
        this.email = email;
        this.phone = phone;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurnames() {
        return surnames;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserResponse entity = (UserResponse) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.name, entity.name) &&
                Objects.equals(this.surnames, entity.surnames) &&
                Objects.equals(this.username, entity.username) &&
                Objects.equals(this.email, entity.email) &&
                Objects.equals(this.phone, entity.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surnames, username, email, phone);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "name = " + name + ", " +
                "surnames = " + surnames + ", " +
                "username = " + username + ", " +
                "email = " + email + ", " +
                "phone = " + phone + ")";
    }
}