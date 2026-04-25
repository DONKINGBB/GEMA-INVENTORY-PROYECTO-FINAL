package com.example.demo.controller;

import com.example.demo.dto.CategoriaProductoDto;
import com.example.demo.model.CategoriaProducto;
import com.example.demo.service.CategoriaProductoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CategoriaProductoController {

    private final CategoriaProductoService categoriaProductoService;

    // Método privado para convertir Entidad a DTO
    private CategoriaProductoDto convertirADto(CategoriaProducto c) {
        return CategoriaProductoDto.builder()
                .id(c.getIdCategoria())
                .nombre(c.getNombre())
                .build();
    }

    @GetMapping("/categoria-producto")
    public ResponseEntity<List<CategoriaProductoDto>> lista() {
        List<CategoriaProducto> categorias = categoriaProductoService.getAll();
        if (categorias == null || categorias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<CategoriaProductoDto> dtos = categorias.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/categoria-producto/{id}")
    public ResponseEntity<CategoriaProductoDto> getById(@PathVariable Integer id) { // <-- CAMBIO: Integer
        CategoriaProducto c = categoriaProductoService.getById(id);
        if (c == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(c));
    }

    @PostMapping("/categoria-producto")
    public ResponseEntity<CategoriaProductoDto> save(@RequestBody CategoriaProductoDto dto) {
        CategoriaProducto c = CategoriaProducto.builder()
                .nombre(dto.getNombre())
                .build();
        // Al guardar, la BD asignará el ID autoincremental

        CategoriaProducto guardado = categoriaProductoService.save(c);
        return ResponseEntity.ok(convertirADto(guardado));
    }

    @PutMapping("/categoria-producto/{id}")
    public ResponseEntity<CategoriaProductoDto> update(@PathVariable Integer id, @RequestBody CategoriaProductoDto dto) { // <-- CAMBIO: Integer
        CategoriaProducto datosParaActualizar = CategoriaProducto.builder()
                .nombre(dto.getNombre())
                .build();

        CategoriaProducto actualizado = categoriaProductoService.update(id, datosParaActualizar);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(actualizado));
    }

    @DeleteMapping("/categoria-producto/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) { // <-- CAMBIO: Integer
        if (categoriaProductoService.getById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        categoriaProductoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}