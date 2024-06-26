package br.com.waitomo.services;

import br.com.waitomo.dtos.ApiResponseMessageStatus;
import br.com.waitomo.dtos.DataUserRegisterDTO;
import br.com.waitomo.dtos.UserDTO;
import br.com.waitomo.dtos.UserIdDTO;
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

    public ApiResponseMessageStatus updateUserById(Long id, UserDTO userDTO){
        userIdExists(id);

        try{
            UserModel userModel=modelMapper.map(userDTO,UserModel.class);
            userModel.setId(userModel.getId());
            userModel.setUsername(userModel.getUsername());
            userModel.setEmail(userModel.getEmail());
            userRepository.save(userModel);

            ApiResponseMessageStatus apiResponseMessageStatus = new ApiResponseMessageStatus();
            String message = "Atualizado com sucesso!";
            Integer status = 200;
            apiResponseMessageStatus.setMessage(message);
            apiResponseMessageStatus.setStatus(status);

            return modelMapper.map(userModel, ApiResponseMessageStatus.class);
        }
        catch (Exception e){
            throw new RuntimeException("Não foi possivel atualizar: ",e);
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
