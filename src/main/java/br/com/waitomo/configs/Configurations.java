package br.com.waitomo.configs;

import br.com.waitomo.dtos.TaskDTO;
import br.com.waitomo.models.TaskModel;
import br.com.waitomo.models.UserModel;
import br.com.waitomo.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;

@Configuration
public class Configurations {
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
