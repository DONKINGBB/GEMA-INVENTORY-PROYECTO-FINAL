package com.example.demo.controller;

import com.example.demo.dto.CompraDto;
import com.example.demo.model.Compra;
import com.example.demo.service.CompraService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CompraController {

    private final CompraService compraService;

    // Método privado para convertir Entidad a DTO
    private CompraDto convertirADto(Compra c) {
        return CompraDto.builder()
                .id(c.getId())
                .idProveedor(c.getIdProveedor())
                .idUsuario(c.getIdUsuario())
                .idAlmacenDestino(c.getIdAlmacenDestino())
                .fechaCompra(c.getFechaCompra())
                .total(c.getTotal())
                .build();
    }

    @GetMapping("/compra")
    public ResponseEntity<List<CompraDto>> lista() {
        List<Compra> compras = compraService.getAll();
        if (compras == null || compras.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<CompraDto> dtos = compras.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/compra/{id}")
    public ResponseEntity<CompraDto> getById(@PathVariable String id) {
        Compra c = compraService.getById(id);
        if (c == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(c));
    }

    @PostMapping("/compra")
    public ResponseEntity<CompraDto> save(@RequestBody CompraDto dto) {
        Compra c = Compra.builder()
                .idProveedor(dto.getIdProveedor())
                .idUsuario(dto.getIdUsuario())
                .idAlmacenDestino(dto.getIdAlmacenDestino())
                .total(dto.getTotal())
                .build();

        Compra guardada = compraService.save(c);
        return ResponseEntity.ok(convertirADto(guardada));
    }

    @PutMapping("/compra/{id}")
    public ResponseEntity<CompraDto> update(@PathVariable String id, @RequestBody CompraDto dto) {
        Compra datosParaActualizar = Compra.builder()
                .idProveedor(dto.getIdProveedor())
                .idUsuario(dto.getIdUsuario())
                .idAlmacenDestino(dto.getIdAlmacenDestino())
                .total(dto.getTotal())
                .build();

        Compra actualizada = compraService.update(id, datosParaActualizar);
        if (actualizada == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(actualizada));
    }

    @DeleteMapping("/compra/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (compraService.getById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        compraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}