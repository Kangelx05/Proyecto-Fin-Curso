package api.controllers;

import api.domain.User;
import api.dto.UserRequest;
import api.dto.UserResponse;
import api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
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
        UserResponse user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(
            operationId = "findAll",
            summary     = "Get all users",
            description = "Gets all the users stored in the database."
    )
    @GetMapping("")
    public ResponseEntity<List<UserResponse>> findAll() throws Exception {
        List<UserResponse> user = userService.findAll();
        return ResponseEntity.ok(user);
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
            @Valid @RequestBody User requestedUser
    ) throws Exception {
        UserResponse createdUser = userService.createUser(requestedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
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
        UserResponse updatedUser = userService.updateUser(requestedUser, id);
        return ResponseEntity.ok(updatedUser);
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