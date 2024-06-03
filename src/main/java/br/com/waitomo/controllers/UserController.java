package br.com.waitomo.controllers;

import br.com.waitomo.api_response.ApiResponse;
import br.com.waitomo.dtos.UserDTO;
import br.com.waitomo.services.UserService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findUserById(@PathVariable @NonNull Long id){
        UserDTO findUser = userService.findUserById(id);
        return ResponseEntity.ok(findUser);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO user, UriComponentsBuilder uriBuilder){
        UserDTO createUser = userService.createUser(user);
        URI pathUser = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(pathUser).body(createUser);
    }

    @PostMapping("/auth")
    public ResponseEntity<ApiResponse<UserDTO>> authenticateUser(@RequestBody UserDTO user){
        UserDTO isAuthenticated = userService.authenticateUser(user);

        if (isAuthenticated != null) {
            ApiResponse<UserDTO> response = new ApiResponse<>("Usuário encontrado",200,isAuthenticated);
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<UserDTO> response = new ApiResponse<>("Usuário não encontrado",404,null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUserById(@PathVariable @NonNull Long id, @RequestBody @Valid UserDTO user){
        UserDTO userDTO = userService.updateUserById(id,user);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @NonNull Long id){
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
