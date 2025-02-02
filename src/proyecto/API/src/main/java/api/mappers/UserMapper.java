package api.mappers;

import api.domain.User;
import api.dto.UserRequest;
import api.dto.UserResponse;

public abstract class UserMapper {

    public static void updateUserFromRequest(User user, UserRequest request) {

        user.setName(request.name());
        user.setSurnames(request.surnames());
        user.setUsername(request.username());
        user.setPassword(request.password());
        user.setEmail(request.email());
        user.setPhone(request.phone());

    }

    public static UserResponse toResponse(User user) {
        if (user == null) return null;
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getSurnames(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone()
        );
    }

}
