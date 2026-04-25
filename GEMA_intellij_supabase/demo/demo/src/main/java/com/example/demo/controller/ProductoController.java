package com.example.demo.controller;

import com.example.demo.dto.ProductoDto;
import com.example.demo.model.Producto;
import com.example.demo.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody ProductoDto dto) {
        return ResponseEntity.ok(productoService.crearProductoCompleto(dto));
    }
}