package com.example.demo.service;

import com.example.demo.dto.FacturaDto;
import java.util.List;

public interface FacturaService {
    List<FacturaDto> obtenerTodas();
    FacturaDto crearFactura(FacturaDto dto);
}