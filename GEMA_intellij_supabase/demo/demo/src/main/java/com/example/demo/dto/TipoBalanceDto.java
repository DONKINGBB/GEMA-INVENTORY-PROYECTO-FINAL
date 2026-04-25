package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TipoBalanceDto {
    private Integer id;
    private String nombre;
}
