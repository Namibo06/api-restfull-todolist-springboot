package br.com.waitomo.api_response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private String message;
    private int status;
    private T data;

    public ApiResponse(String message, int status, T data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }
}
