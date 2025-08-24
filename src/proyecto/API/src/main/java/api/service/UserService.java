package api.service;

import api.domain.User;
// Services operate on domain entities; DTOs and mappers belong in controllers.
import api.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository usuarioRepository){
        this.userRepository = usuarioRepository;
    }

    /**
     * Retrieve a user by its identifier.
     *
     * @param id the primary key of the user
     * @return the corresponding {@link User}
     * @throws Exception if the user does not exist
     */
    public User findById(int id) throws Exception {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new Exception();
        }
        return user;
    }

    /**
     * Retrieve all users.
     *
     * @return a list of all {@link User} entities
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean changePassword(int id, String password, String previousPassword) throws Exception {
        User user = userRepository.findUserById(id);
        if (user.getPassword().equals(previousPassword)) {
            user.setPassword(password);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }


    /**
     * Create a new user.  The caller must provide a fully populated
     * {@link User} entity; this service will enforce username uniqueness
     * and persist the entity.
     *
     * @param requestedUser the user entity to create
     * @return the persisted {@link User}
     * @throws Exception if a user with the same username already exists
     */
    public User createUser(@NotNull User requestedUser) throws Exception {
        if (userRepository.existsByUsername(requestedUser.getUsername())) {
            throw new Exception("Usuario ya existente: " + requestedUser.getUsername());
        }
        return userRepository.save(requestedUser);
    }

    /**
     * Update an existing user.  Mutable fields are copied from the supplied
     * entity onto the existing record; immutable identifiers are not changed.
     *
     * @param updatedUser the entity carrying updated values
     * @param id          the identifier of the user to update
     * @return the updated {@link User}
     * @throws Exception if the user cannot be found
     */
    public User updateUser(@NotNull User updatedUser, int id) throws Exception {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new Exception("Id no encontrada: " + id);
        }
        user.setName(updatedUser.getName());
        user.setSurnames(updatedUser.getSurnames());
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setPhone(updatedUser.getPhone());
        return userRepository.save(user);
    }


    public void deleteUser(int id) throws Exception {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new Exception();
        }
        userRepository.delete(user);
    }


}
