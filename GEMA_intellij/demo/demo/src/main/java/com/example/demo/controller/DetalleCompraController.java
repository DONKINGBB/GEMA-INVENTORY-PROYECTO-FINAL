package com.example.demo.controller;

import com.example.demo.dto.DetalleCompraDto;
import com.example.demo.model.DetalleCompra;
import com.example.demo.service.DetalleCompraService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class DetalleCompraController {

    private final DetalleCompraService detalleCompraService;

    // Método privado para convertir Entidad a DTO
    private DetalleCompraDto convertirADto(DetalleCompra d) {
        return DetalleCompraDto.builder()
                .id(d.getId())
                .idCompra(d.getIdCompra())
                .idProducto(d.getIdProducto())
                .cantidad(d.getCantidad())
                .precioCosto(d.getPrecioCosto())
                .build();
    }

    @GetMapping("/detalle-compra")
    public ResponseEntity<List<DetalleCompraDto>> lista() {
        List<DetalleCompra> detalles = detalleCompraService.getAll();
        if (detalles == null || detalles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<DetalleCompraDto> dtos = detalles.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/detalle-compra/{id}")
    public ResponseEntity<DetalleCompraDto> getById(@PathVariable String id) {
        DetalleCompra d = detalleCompraService.getById(id);
        if (d == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(d));
    }

    @PostMapping("/detalle-compra")
    public ResponseEntity<DetalleCompraDto> save(@RequestBody DetalleCompraDto dto) {
        DetalleCompra d = DetalleCompra.builder()
                .idCompra(dto.getIdCompra())
                .idProducto(dto.getIdProducto())
                .cantidad(dto.getCantidad())
                .precioCosto(dto.getPrecioCosto())
                .build();

        DetalleCompra guardado = detalleCompraService.save(d);
        return ResponseEntity.ok(convertirADto(guardado));
    }

    @PutMapping("/detalle-compra/{id}")
    public ResponseEntity<DetalleCompraDto> update(@PathVariable String id, @RequestBody DetalleCompraDto dto) {
        DetalleCompra datosParaActualizar = DetalleCompra.builder()
                .idCompra(dto.getIdCompra())
                .idProducto(dto.getIdProducto())
                .cantidad(dto.getCantidad())
                .precioCosto(dto.getPrecioCosto())
                .build();

        DetalleCompra actualizado = detalleCompraService.update(id, datosParaActualizar);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(actualizado));
    }

    @DeleteMapping("/detalle-compra/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (detalleCompraService.getById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        detalleCompraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}