package com.example.demo.controller;

import com.example.demo.dto.ReporteDto;
import com.example.demo.model.Reporte;
import com.example.demo.service.ReporteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    // Método privado para convertir Entidad a DTO
    private ReporteDto convertirADto(Reporte r) {
        return ReporteDto.builder()
                .id(r.getId())
                .idTipo(r.getIdTipo())
                .idFormato(r.getIdFormato())
                .fechaGeneracion(r.getFechaGeneracion())
                .generadoPor(r.getGeneradoPor())
                .build();
    }

    @GetMapping("/reporte")
    public ResponseEntity<List<ReporteDto>> lista() {
        List<Reporte> reportes = reporteService.getAll();
        if (reportes == null || reportes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<ReporteDto> dtos = reportes.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/reporte/{id}")
    public ResponseEntity<ReporteDto> getById(@PathVariable String id) {
        Reporte r = reporteService.getById(id);
        if (r == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(r));
    }

    @PostMapping("/reporte")
    public ResponseEntity<ReporteDto> save(@RequestBody ReporteDto dto) {
        Reporte r = Reporte.builder()
                .idTipo(dto.getIdTipo())
                .idFormato(dto.getIdFormato())
                .generadoPor(dto.getGeneradoPor())
                .build();

        Reporte guardado = reporteService.save(r);
        return ResponseEntity.ok(convertirADto(guardado));
    }

    @PutMapping("/reporte/{id}")
    public ResponseEntity<ReporteDto> update(@PathVariable String id, @RequestBody ReporteDto dto) {
        Reporte datosParaActualizar = Reporte.builder()
                .idTipo(dto.getIdTipo())
                .idFormato(dto.getIdFormato())
                .generadoPor(dto.getGeneradoPor())
                .build();

        Reporte actualizado = reporteService.update(id, datosParaActualizar);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(actualizado));
    }

    @DeleteMapping("/reporte/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (reporteService.getById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        reporteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}