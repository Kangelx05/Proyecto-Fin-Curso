package api.service;

import api.domain.User;
import api.dto.UserRequest;
import api.dto.UserResponse;
import api.mappers.UserMapper;
import api.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<UserResponse> findAll() throws Exception {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users) {
            userResponses.add(UserMapper.toResponse(user));
        }
        return userResponses;
    }

    public boolean changePassword(int id, String password, String previousPassword) throws Exception {
        User user = userRepository.findUserById(id);
        if (user.getPassword().equals(previousPassword)) {
            user.setPassword(password);
            userRepository.save(user);
            return true;
        }else{
            return false;
        }
    }


    public UserResponse createUser(@NotNull User requestedUser) throws Exception {
        if (userRepository.existsByUsername(requestedUser.getUsername())) {
            throw new Exception("Usuario ya existente: " + requestedUser.getUsername());
        }

        User savedUser = userRepository.save(requestedUser);

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
