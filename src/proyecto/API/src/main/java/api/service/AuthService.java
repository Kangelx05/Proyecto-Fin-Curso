package api.service;

import api.domain.User;
import api.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for authenticating users and issuing JWTs for general API access.
 * It relies on the {@link UserRepository} to fetch stored user records and
 * the {@link JwtService} to generate tokens.  Passwords are compared
 * directly as stored; in a production system they should be hashed.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    /**
     * Authenticate a user using the supplied credentials.  If the username
     * and password match a record in the database a JWT is generated and
     * returned.  Otherwise an exception is thrown.
     *
     * @param username the username of the user
     * @param password the plaintext password
     * @return a JWT representing the authenticated user
     * @throws Exception if authentication fails
     */
    public String login(@NotNull @NotBlank String username, @NotNull @NotBlank String password) throws Exception {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new Exception("Invalid username or password");
        }
        return jwtService.generateUserToken(user.getId());
    }
}