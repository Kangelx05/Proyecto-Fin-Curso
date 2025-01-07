package api.mappers;

public abstract class UserMapper {

    public static void updateUserFromRequest(User user, UserRequest request) {

        user.setName(request.getName());
        user.setSurnames(request.getSurnames());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

    }

    public static UserResponse toResponse(User user) {
        if (user == null) return null;
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getSurnames(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getPhone()
        );
    }

}
