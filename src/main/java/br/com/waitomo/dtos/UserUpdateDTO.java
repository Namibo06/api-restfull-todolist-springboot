package br.com.waitomo.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {
    private String username;
    private String email;
}
