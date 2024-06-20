package br.com.waitomo.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataUserRegisterDTO {
    private Long id;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
