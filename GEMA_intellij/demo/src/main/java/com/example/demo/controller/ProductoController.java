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

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable String id, @RequestBody ProductoDto dto) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> obtenerPorId(@PathVariable String id) {
        return ResponseEntity.ok(productoService.obtenerProductoPorId(id));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<java.util.List<Producto>> listarPorUsuario(@PathVariable String idUsuario) {
        // Nota: Asegúrate de que tu 'productoService' tenga un método llamado 'listarPorUsuario'
        // Si tu servicio se llama diferente, ajusta el nombre aquí.
        return ResponseEntity.ok(productoService.listarPorUsuario(idUsuario));
    }
}