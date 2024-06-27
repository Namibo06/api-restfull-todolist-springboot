package br.com.waitomo.controllers;

import br.com.waitomo.api_response.ApiResponseMessageStatus;
import br.com.waitomo.api_response.TokenResponseApi;
import br.com.waitomo.dtos.*;
import br.com.waitomo.services.TokenService;
import br.com.waitomo.services.UserService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager auth;
    private final TokenService tokenService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findUserById(@PathVariable @NonNull Long id){
        UserDTO findUser = userService.findUserById(id);
        return ResponseEntity.ok(findUser);
    }

    @GetMapping("findUser/{token}")
    public ResponseEntity<UserIdDTO> findUserByToken(@PathVariable String token){
        UserIdDTO findUser = userService.findUserByToken(token);
        return ResponseEntity.ok(findUser);
    }

    @PostMapping
    public ResponseEntity<DataUserRegisterDTO> createUser(@RequestBody @Valid DataUserRegisterDTO user, UriComponentsBuilder uriBuilder){
        DataUserRegisterDTO createUser = userService.createUser(user);
        URI pathUser = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(pathUser).body(createUser);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseApi> authenticateUser(@RequestBody @Valid DataUserRegisterDTO credentials){
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword());
        Authentication authentication = auth.authenticate(token);

        TokenResponseApi tokenResponse = tokenService.createToken();

        userService.updateToken(credentials.getEmail(), tokenResponse.getToken());

        return ResponseEntity.ok().body(tokenResponse);
    }

    @PutMapping("updateUser/{id}")
    public ResponseEntity<ApiResponseMessageStatus> updateUserById(@PathVariable @NonNull Long id, @RequestBody @Valid UserUpdateDTO user){
        ApiResponseMessageStatus userDTO = userService.updateUserById(id,user);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("updatePassword/{id}")
    public ResponseEntity<ApiResponseMessageStatus> updatePasswordUserById(@PathVariable @NonNull Long id, @RequestBody @Valid UserDTO user){
        ApiResponseMessageStatus userDTO = userService.updatePasswordUserById(id,user);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @NonNull Long id){
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
