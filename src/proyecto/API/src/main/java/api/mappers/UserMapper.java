package api.mappers;

import api.domain.User;
import api.dto.UserRequest;
import api.dto.UserResponse;

public abstract class UserMapper {

    public static User getUserFromRequest(UserRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setSurnames(request.surnames());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        return user;

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

    /**
     * Update an existing {@link User} in place from a request DTO.  Only
     * mutable fields are updated; the entity's identifier is left intact.
     *
     * @param user    the entity to update
     * @param request the DTO containing new values
     * @return the updated {@link User}
     */
    public static User getUserFromRequest(User user, UserRequest request) {
        if (user == null) {
            user = new User();
        }
        user.setName(request.name());
        user.setSurnames(request.surnames());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        return user;
    }

}
