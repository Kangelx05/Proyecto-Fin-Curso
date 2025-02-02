package api.service;

import api.domain.User;
import api.dto.UserRequest;
import api.dto.UserResponse;
import api.mappers.UserMapper;
import api.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository usuarioRepository){
        this.userRepository = usuarioRepository;
    }

    public UserResponse findById(int id) throws Exception {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new Exception();
        }
        return UserMapper.toResponse(user);
    }


    public UserResponse createUser(@NotNull UserRequest requestedUser) throws Exception {
        if (userRepository.existsByUsername(requestedUser.username())) {
            throw new Exception("Usuario ya existente: " + requestedUser.username());
        }

        User user = new User();
        UserMapper.updateUserFromRequest(user, requestedUser);
        User savedUser = userRepository.save(user);

        return UserMapper.toResponse(savedUser);
    }


    public UserResponse updateUser(@NotNull UserRequest requestedUser, int id) throws Exception {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new Exception("Id no encontrada: " + id);
        }
        UserMapper.updateUserFromRequest(user, requestedUser);
        return UserMapper.toResponse(userRepository.save(user));
    }


    public void deleteUser(int id) throws Exception {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new Exception();
        }
        userRepository.delete(user);
    }


}
