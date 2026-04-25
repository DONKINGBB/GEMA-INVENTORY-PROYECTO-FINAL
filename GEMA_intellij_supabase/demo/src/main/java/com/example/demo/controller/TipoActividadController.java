package com.example.demo.controller;

import com.example.demo.dto.TipoActividadDto;
import com.example.demo.model.TipoActividad;
import com.example.demo.service.TipoActividadService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class TipoActividadController {

    private final TipoActividadService tipoActividadService;

    private TipoActividadDto convertirADto(TipoActividad t) {
        return TipoActividadDto.builder()
                .id(t.getId())
                .nombre(t.getNombre())
                .build();
    }

    @GetMapping("/tipo-actividad")
    public ResponseEntity<List<TipoActividadDto>> lista() {
        List<TipoActividad> tipos = tipoActividadService.getAll();
        List<TipoActividadDto> dtos = tipos.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/tipo-actividad/{id}")
    public ResponseEntity<TipoActividadDto> getById(@PathVariable Integer id) {
        TipoActividad t = tipoActividadService.getById(id);
        if (t == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(t));
    }

    @PostMapping("/tipo-actividad")
    public ResponseEntity<TipoActividadDto> save(@RequestBody TipoActividadDto dto) {
        TipoActividad t = TipoActividad.builder()
                .nombre(dto.getNombre())
                .build();
        return ResponseEntity.ok(convertirADto(tipoActividadService.save(t)));
    }

    @PutMapping("/tipo-actividad/{id}")
    public ResponseEntity<TipoActividadDto> update(@PathVariable Integer id, @RequestBody TipoActividadDto dto) {
        TipoActividad t = TipoActividad.builder()
                .nombre(dto.getNombre())
                .build();
        TipoActividad actualizado = tipoActividadService.update(id, t);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(actualizado));
    }

    @DeleteMapping("/tipo-actividad/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        tipoActividadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
