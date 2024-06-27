package br.com.waitomo.api_response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponseApi {
    private String message;
    private Integer status;
    private String token;
}
