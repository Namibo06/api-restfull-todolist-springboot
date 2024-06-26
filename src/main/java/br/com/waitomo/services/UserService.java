package br.com.waitomo.services;

import br.com.waitomo.dtos.*;
import br.com.waitomo.models.UserModel;
import br.com.waitomo.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    //injeções
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    //services
    public DataUserRegisterDTO createUser(DataUserRegisterDTO user) {
        UserModel userModel = modelMapper.map(user, UserModel.class);

        userModel.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(userModel);

        DataUserRegisterDTO userResponse = modelMapper.map(userModel, DataUserRegisterDTO.class);
        return userResponse;
    }

    public UserDTO findUserById(Long id) {
        UserModel userModel=userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        return modelMapper.map(userModel,UserDTO.class);
    }

    public ApiResponseMessageStatus updateUserById(Long id, UserUpdateDTO userUpdateDTO){
        userIdExists(id);

        try {
            UserModel userModelData = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
            String currentPassword = userModelData.getPassword();
            String currentToken = userModelData.getToken();

            // Verifica se o email já existe para outro usuário
            if (!userModelData.getEmail().equals(userUpdateDTO.getEmail()) && userRepository.existsByEmail(userUpdateDTO.getEmail())) {
                throw new RuntimeException("Email já está em uso por outro usuário.");
            }

            // Atualiza somente os campos permitidos
            if (userUpdateDTO.getUsername() != null) {
                userModelData.setUsername(userUpdateDTO.getUsername());
            }
            if (userUpdateDTO.getEmail() != null) {
                userModelData.setEmail(userUpdateDTO.getEmail());
            }

            // Mantém a senha e o token atuais
            userModelData.setPassword(currentPassword);
            userModelData.setToken(currentToken);

            userRepository.save(userModelData); // Isso atualiza o registro existente

            ApiResponseMessageStatus apiResponseMessageStatus = new ApiResponseMessageStatus();
            apiResponseMessageStatus.setMessage("Atualizado com sucesso!");
            apiResponseMessageStatus.setStatus(200);

            return apiResponseMessageStatus;
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível atualizar: ", e);
        }
    }

    public UserDTO updatePasswordUserById(Long id, UserDTO userDTO){
        userIdExists(id);

        UserModel userModel=modelMapper.map(userDTO,UserModel.class);
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        userRepository.save(userModel);
        return modelMapper.map(userModel,UserDTO.class);
    }

    public void deleteUserById(Long id){
        userIdExists(id);

        userRepository.deleteById(id);
    }

    public void userIdExists(Long id){
        Optional<UserModel> userIdExists = userRepository.findById(id);
        String message="Id do usuário " + id + " não encontrado";

        if(userIdExists.isEmpty()){
            throw new EntityNotFoundException(message);
        }
    }

    public void updateToken(String email,String token){
        UserModel userModel = userRepository.findByEmailUpdateToken(email);
        if(userModel == null){
            throw new EntityNotFoundException("Usuário não encontrado");
        }

        userModel.setToken(token);
        userRepository.save(userModel);
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository.findByEmail(login);
    }

    public UserIdDTO findUserByToken(String token) {
        UserModel userModel = userRepository.findUserByToken(token);
        if(userModel == null){
            throw new EntityNotFoundException("Usuário não encontrado por token");
        }

        return modelMapper.map(userModel, UserIdDTO.class);
    }
}
