package com.example.demo.controller;

import com.example.demo.dto.TipoMovimientoDto;
import com.example.demo.model.TipoMovimiento;
import com.example.demo.service.TipoMovimientoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class TipoMovimientoController {

    private final TipoMovimientoService tipoMovimientoService;

    // Método privado para convertir Entidad a DTO
    private TipoMovimientoDto convertirADto(TipoMovimiento t) {
        return TipoMovimientoDto.builder()
                .id(t.getId())
                .nombre(t.getNombre())
                .build();
    }

    @GetMapping("/tipo-movimiento")
    public ResponseEntity<List<TipoMovimientoDto>> lista() {
        List<TipoMovimiento> tipos = tipoMovimientoService.getAll();
        if (tipos == null || tipos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<TipoMovimientoDto> dtos = tipos.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/tipo-movimiento/{id}")
    public ResponseEntity<TipoMovimientoDto> getById(@PathVariable Integer id) { // <-- CAMBIO: Integer
        TipoMovimiento t = tipoMovimientoService.getById(id);
        if (t == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(t));
    }

    @PostMapping("/tipo-movimiento")
    public ResponseEntity<TipoMovimientoDto> save(@RequestBody TipoMovimientoDto dto) {
        TipoMovimiento t = TipoMovimiento.builder()
                .nombre(dto.getNombre())
                .build();

        TipoMovimiento guardado = tipoMovimientoService.save(t);
        return ResponseEntity.ok(convertirADto(guardado));
    }

    @PutMapping("/tipo-movimiento/{id}")
    public ResponseEntity<TipoMovimientoDto> update(@PathVariable Integer id, @RequestBody TipoMovimientoDto dto) { // <-- CAMBIO: Integer
        TipoMovimiento datosParaActualizar = TipoMovimiento.builder()
                .nombre(dto.getNombre())
                .build();

        TipoMovimiento actualizado = tipoMovimientoService.update(id, datosParaActualizar);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(actualizado));
    }

    @DeleteMapping("/tipo-movimiento/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) { // <-- CAMBIO: Integer
        if (tipoMovimientoService.getById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        tipoMovimientoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}