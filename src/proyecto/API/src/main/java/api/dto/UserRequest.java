package api.dto;

import api.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link User}
 */
public class UserRequest implements Serializable {
    @NotNull
    @Size(max = 45)
    @NotBlank
    private final String name;
    @NotNull
    @Size(max = 45)
    @NotBlank
    private final String surnames;
    @NotNull
    @Size(max = 45)
    @NotBlank
    private final String username;
    @NotNull
    @Size(max = 45)
    @NotBlank
    private final String password;
    @Size(max = 45)
    @Email
    private final String email;
    @NotNull
    @Size(max = 45)
    private final String phone;

    public UserRequest(String name, String surnames, String username, String password, String email, String phone) {
        this.name = name;
        this.surnames = surnames;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
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

    public String getPassword() {
        return password;
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
        UserRequest entity = (UserRequest) o;
        return Objects.equals(this.name, entity.name) &&
                Objects.equals(this.surnames, entity.surnames) &&
                Objects.equals(this.username, entity.username) &&
                Objects.equals(this.password, entity.password) &&
                Objects.equals(this.email, entity.email) &&
                Objects.equals(this.phone, entity.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surnames, username, password, email, phone);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "name = " + name + ", " +
                "surnames = " + surnames + ", " +
                "username = " + username + ", " +
                "password = " + password + ", " +
                "email = " + email + ", " +
                "phone = " + phone + ")";
    }
}