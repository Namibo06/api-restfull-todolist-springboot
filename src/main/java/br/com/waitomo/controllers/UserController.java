package br.com.waitomo.controllers;

import br.com.waitomo.api_response.ApiResponse;
import br.com.waitomo.dtos.*;
import br.com.waitomo.services.TokenService;
import br.com.waitomo.services.UserService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword());
            Authentication authentication = auth.authenticate(token);

            String tokenResponse = tokenService.createToken();

            TokenResponseApi tokenResponseApi = new TokenResponseApi();
            tokenResponseApi.setMessage("Token criado com sucesso!");
            tokenResponseApi.setStatus(200);
            tokenResponseApi.setToken(tokenResponse);

            userService.updateToken(credentials.getEmail(), tokenResponseApi.getToken());
            Long userId = userService.findUserIdByEmail(credentials.getEmail());
            tokenResponseApi.setUser_id(userId);

            return ResponseEntity.ok().body(tokenResponseApi);
        } catch (AuthenticationException e) {
            TokenResponseApi tokenResponseApiUnauthorized = new TokenResponseApi();
            tokenResponseApiUnauthorized.setMessage("Autenticação Falhou");
            tokenResponseApiUnauthorized.setStatus(403);
            tokenResponseApiUnauthorized.setToken(null);
            tokenResponseApiUnauthorized.setUser_id(null);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(tokenResponseApiUnauthorized);
        } catch (Exception e) {
            TokenResponseApi tokenResponseApiException = new TokenResponseApi();
            tokenResponseApiException.setMessage("Erro Interno");
            tokenResponseApiException.setStatus(500);
            tokenResponseApiException.setToken(null);
            tokenResponseApiException.setUser_id(null);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(tokenResponseApiException);
        }
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
