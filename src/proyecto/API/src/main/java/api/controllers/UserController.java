package api.controllers;

import api.domain.User;
import api.dto.UserRequest;
import api.dto.UserResponse;
import api.mappers.UserMapper;
import api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            operationId = "getUserById",
            summary     = "Get user by ID",
            description = "Retrieve a user record by its unique identifier."
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable int id) throws Exception {
        User user = userService.findById(id);
        return ResponseEntity.ok(UserMapper.toResponse(user));
    }

    @Operation(
            operationId = "findAll",
            summary     = "Get all users",
            description = "Gets all the users stored in the database."
    )
    @GetMapping("")
    public ResponseEntity<List<UserResponse>> findAll() throws Exception {
        List<User> users = userService.findAll();
        List<UserResponse> responses = users.stream().map(UserMapper::toResponse).toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(
            operationId = "changePassword",
            summary     = "Change the password of a user",
            description = "Changes the password of a user."
    )
    @PutMapping("/password/{id}")
    public boolean changePassword(@PathVariable int id, @RequestBody String password, @RequestBody String previousPassword) throws Exception {

        return userService.changePassword(id, password, previousPassword);
    }

    @Operation(
            operationId = "createUser",
            summary     = "Create a new user",
            description = "Create a new user with the provided details."
    )
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRequest requestedUser
    ) throws Exception {
        // map the request into a domain entity
        User user = UserMapper.getUserFromRequest(requestedUser);
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toResponse(createdUser));
    }

    @Operation(
            operationId = "updateUser",
            summary     = "Update user by ID",
            description = "Update the user record identified by its ID with new data."
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @Valid @RequestBody UserRequest requestedUser,
            @PathVariable int id
    ) throws Exception {
        User user = UserMapper.getUserFromRequest(requestedUser);
        User updatedUser = userService.updateUser(user, id);
        return ResponseEntity.ok(UserMapper.toResponse(updatedUser));
    }

    @Operation(
            operationId = "deleteUser",
            summary     = "Delete user by ID",
            description = "Remove the user record identified by its ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) throws Exception {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}