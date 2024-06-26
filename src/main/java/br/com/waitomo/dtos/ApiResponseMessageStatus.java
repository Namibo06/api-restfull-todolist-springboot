package br.com.waitomo.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseMessageStatus {
    private String message;
    private Integer status;
}
