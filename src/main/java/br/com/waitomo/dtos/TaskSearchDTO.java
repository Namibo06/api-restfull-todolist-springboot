package br.com.waitomo.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskSearchDTO {
    private String search;
    private UserIdDTO user_id;
}
