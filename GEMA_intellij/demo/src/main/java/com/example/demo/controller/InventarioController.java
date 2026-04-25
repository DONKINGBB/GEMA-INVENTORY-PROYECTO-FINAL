package com.example.demo.controller;

import com.example.demo.dto.InventarioDto;
import com.example.demo.dto.ProductoSeleccionDto;
import com.example.demo.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    public ResponseEntity<List<InventarioDto>> listar(@RequestParam String userId) {
        return ResponseEntity.ok(inventarioService.obtenerPorUsuario(userId));
    }

    @PostMapping
    public ResponseEntity<InventarioDto> crear(@RequestBody InventarioDto dto) {
        return ResponseEntity.ok(inventarioService.guardarInventario(dto));
    }

    public ResponseEntity<InventarioDto> actualizarStock(@PathVariable String id, @RequestParam Integer cantidad) {
        return ResponseEntity.ok(inventarioService.actualizarStock(id, cantidad));
    }

    @GetMapping("/seleccion-por-almacen")
    public ResponseEntity<List<ProductoSeleccionDto>> listarPorAlmacen(@RequestParam String userId, @RequestParam Integer almacenId) {
        return ResponseEntity.ok(inventarioService.obtenerProductosPorAlmacen(userId, almacenId));
    }
}