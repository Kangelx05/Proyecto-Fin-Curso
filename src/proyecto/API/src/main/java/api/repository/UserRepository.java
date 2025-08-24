package api.repository;

import api.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserById(int id);

    /**
     * Look up a user by their username.
     *
     * @param username the username to search for
     * @return the matching {@link User} or null if none exists
     */
    User findByUsername(@Size(max = 45) @NotNull @NotBlank String username);

    boolean existsByUsername(@Size(max = 45) @NotNull @NotBlank String username);
}