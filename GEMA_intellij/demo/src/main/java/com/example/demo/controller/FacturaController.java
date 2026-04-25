package com.example.demo.controller;

import com.example.demo.dto.FacturaDto;
import com.example.demo.service.FacturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaService facturaService;

    @GetMapping
    public ResponseEntity<List<FacturaDto>> listar() {
        return ResponseEntity.ok(facturaService.obtenerTodas());
    }

    @PostMapping
    public ResponseEntity<FacturaDto> crear(@RequestBody FacturaDto dto) {
        return ResponseEntity.ok(facturaService.crearFactura(dto));
    }
}