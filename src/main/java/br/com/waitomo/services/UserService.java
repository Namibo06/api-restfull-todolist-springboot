package br.com.waitomo.services;

import br.com.waitomo.dtos.DataUserRegisterDTO;
import br.com.waitomo.dtos.UserDTO;
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

        userModel.setUsername(user.getUsername());
        userModel.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(userModel);

        DataUserRegisterDTO userResponse = modelMapper.map(userModel, DataUserRegisterDTO.class);
        return userResponse;
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

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository.findByEmail(login);
    }
}
