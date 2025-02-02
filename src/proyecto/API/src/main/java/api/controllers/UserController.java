package api.controllers;

import api.dto.UserRequest;
import api.dto.UserResponse;
import api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users") // Ruta base para todos los endpoints
public class UserController {

    private final UserService userService;

    // Inyección por constructor
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1) Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable int id) throws Exception {
        // Llamamos al servicio
        UserResponse user = userService.findById(id);
        return ResponseEntity.ok(user); // 200 OK
    }

    // 2) Crear usuario nuevo
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest requestedUser) throws Exception{
        // Llamamos al servicio
        UserResponse createdUser = userService.createUser(requestedUser);
        // Retornamos 201 Created + el objeto creado
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // 3) Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UserRequest requestedUser, @PathVariable int id) throws Exception {
        UserResponse updatedUser = userService.updateUser(requestedUser, id);
        return ResponseEntity.ok(updatedUser); // 200 OK
    }

    // 4) Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) throws Exception {
        userService.deleteUser(id);
        // Retornamos 204 No Content, ya que no hay cuerpo que devolver
        return ResponseEntity.noContent().build();
    }
}
