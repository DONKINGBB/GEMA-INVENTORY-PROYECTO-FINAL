package com.example.demo.controller;

import com.example.demo.dto.MovimientoInventarioDto;
import com.example.demo.model.MovimientoInventario;
import com.example.demo.service.MovimientoInventarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoInventarioService;

    // Método privado para convertir Entidad a DTO
    private MovimientoInventarioDto convertirADto(MovimientoInventario m) {
        return MovimientoInventarioDto.builder()
                .id(m.getId())
                .idProducto(m.getIdProducto())
                .idAlmacen(m.getIdAlmacen())
                .idUsuario(m.getIdUsuario())
                .idTipoMovimiento(m.getIdTipoMovimiento())
                .cantidad(m.getCantidad())
                .stockAnterior(m.getStockAnterior())
                .stockNuevo(m.getStockNuevo())
                .fechaMovimiento(m.getFechaMovimiento())
                .referencia(m.getReferencia())
                .observaciones(m.getObservaciones())
                .build();
    }

    @GetMapping("/movimiento-inventario")
    public ResponseEntity<List<MovimientoInventarioDto>> lista() {
        List<MovimientoInventario> movimientos = movimientoInventarioService.getAll();
        if (movimientos == null || movimientos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<MovimientoInventarioDto> dtos = movimientos.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/movimiento-inventario/{id}")
    public ResponseEntity<MovimientoInventarioDto> getById(@PathVariable String id) {
        MovimientoInventario m = movimientoInventarioService.getById(id);
        if (m == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(m));
    }

    @PostMapping("/movimiento-inventario")
    public ResponseEntity<MovimientoInventarioDto> save(@RequestBody MovimientoInventarioDto dto) {
        MovimientoInventario m = MovimientoInventario.builder()
                .idProducto(dto.getIdProducto())
                .idAlmacen(dto.getIdAlmacen())
                .idUsuario(dto.getIdUsuario())
                .idTipoMovimiento(dto.getIdTipoMovimiento())
                .cantidad(dto.getCantidad())
                .stockAnterior(dto.getStockAnterior())
                .stockNuevo(dto.getStockNuevo())
                .referencia(dto.getReferencia())
                .observaciones(dto.getObservaciones())
                .build();

        MovimientoInventario guardado = movimientoInventarioService.save(m);
        return ResponseEntity.ok(convertirADto(guardado));
    }

    @PutMapping("/movimiento-inventario/{id}")
    public ResponseEntity<MovimientoInventarioDto> update(@PathVariable String id, @RequestBody MovimientoInventarioDto dto) {
        MovimientoInventario datosParaActualizar = MovimientoInventario.builder()
                .idProducto(dto.getIdProducto())
                .idAlmacen(dto.getIdAlmacen())
                .idUsuario(dto.getIdUsuario())
                .idTipoMovimiento(dto.getIdTipoMovimiento())
                .cantidad(dto.getCantidad())
                .stockAnterior(dto.getStockAnterior())
                .stockNuevo(dto.getStockNuevo())
                .referencia(dto.getReferencia())
                .observaciones(dto.getObservaciones())
                .build();

        MovimientoInventario actualizado = movimientoInventarioService.update(id, datosParaActualizar);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(actualizado));
    }

    @DeleteMapping("/movimiento-inventario/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (movimientoInventarioService.getById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        // Como mencioné en el servicio, esto es poco común, pero
        // mantenemos el patrón CRUD estándar.
        movimientoInventarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}