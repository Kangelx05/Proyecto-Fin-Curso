package api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 45)
    @NotNull
    @NotBlank
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Size(max = 45)
    @NotNull
    @NotBlank
    @Column(name = "surnames", nullable = false, length = 45)
    private String surnames;

    @Size(max = 45)
    @NotNull
    @NotBlank
    @Column(name = "username", nullable = false, length = 45)
    private String username;

    @Size(max = 45)
    @NotNull
    @NotBlank
    @Column(name = "password", nullable = false, length = 45)
    private String password;

    @Size(max = 45)
    @Column(name = "email", length = 45)
    private String email;

    @Size(max = 45)
    @NotNull
    @NotBlank
    @Column(name = "phone", nullable = false, length = 45)
    private String phone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurnames() {
        return surnames;
    }

    public void setSurnames(String surnames) {
        this.surnames = surnames;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}