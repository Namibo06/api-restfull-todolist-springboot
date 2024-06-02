package br.com.waitomo.services;

import br.com.waitomo.dtos.UserDTO;
import br.com.waitomo.models.UserModel;
import br.com.waitomo.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.web.client.HttpClientErrorException.UnprocessableEntity;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    //injeções
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    //services
    public UserDTO createUser(UserDTO user) {
        UserModel userModel=modelMapper.map(user,UserModel.class);
        userRepository.save(userModel);
        return modelMapper.map(userModel,UserDTO.class);
    }

    public UserDTO findUserById(Long id) {
        UserModel userModel=userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        return modelMapper.map(userModel,UserDTO.class);
    }

    public UserDTO updateUserById(Long id, UserDTO userDTO){
        userIdExists(id);

        UserModel userModel=modelMapper.map(userDTO,UserModel.class);
        userModel.setId(id);
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
}
