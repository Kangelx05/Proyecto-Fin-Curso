package api.service;

import api.domain.User;
import api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository usuarioRepository){
        this.userRepository = usuarioRepository;
    }


}
