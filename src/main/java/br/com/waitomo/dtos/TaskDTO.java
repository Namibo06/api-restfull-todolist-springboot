package br.com.waitomo.dtos;

import br.com.waitomo.models.UserModel;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private UserDTO user_id;
}
