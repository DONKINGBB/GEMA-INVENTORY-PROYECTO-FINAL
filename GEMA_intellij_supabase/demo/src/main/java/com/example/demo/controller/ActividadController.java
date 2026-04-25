package com.example.demo.controller;

import com.example.demo.dto.ActividadDto;
import com.example.demo.model.Actividad;
import com.example.demo.service.ActividadService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ActividadController {

    private final ActividadService actividadService;
    private final com.example.demo.service.TenancyService tenancyService;

    private ActividadDto convertirADto(Actividad a) {
        return ActividadDto.builder()
                .id(a.getId())
                .idUsuario(a.getIdUsuario())
                .idTipoActividad(a.getIdTipoActividad())
                .descripcion(a.getDescripcion())
                .fecha(a.getFecha())
                .entidadAfectada(a.getEntidadAfectada())
                .build();
    }

    @GetMapping("/actividad")
    public ResponseEntity<List<ActividadDto>> lista(@RequestParam(name = "idUsuario") String idUsuario) {
        String nId = tenancyService.resolveNegocioId(idUsuario);
        List<Actividad> actividades = actividadService.listarPorNegocio(nId);

        List<ActividadDto> dtos = actividades.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/actividad/{id}")
    public ResponseEntity<ActividadDto> getById(@PathVariable String id) {
        Actividad a = actividadService.getById(id);
        if (a == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(a));
    }

    @PostMapping("/actividad")
    public ResponseEntity<ActividadDto> save(@RequestBody ActividadDto dto) {
        String nId = tenancyService.resolveNegocioId(dto.getIdUsuario());
        Actividad a = Actividad.builder()
                .id(dto.getId()) // opcional si usas UUID
                .idUsuario(dto.getIdUsuario())
                .idNegocio(nId)
                .idTipoActividad(dto.getIdTipoActividad())
                .descripcion(dto.getDescripcion())
                .fecha(dto.getFecha())
                .entidadAfectada(dto.getEntidadAfectada())
                .build();
        return ResponseEntity.ok(convertirADto(actividadService.save(a)));
    }

    @PutMapping("/actividad/{id}")
    public ResponseEntity<ActividadDto> update(@PathVariable String id, @RequestBody ActividadDto dto) {
        Actividad a = Actividad.builder()
                .idUsuario(dto.getIdUsuario())
                .idTipoActividad(dto.getIdTipoActividad())
                .descripcion(dto.getDescripcion())
                .fecha(dto.getFecha())
                .entidadAfectada(dto.getEntidadAfectada())
                .build();
        Actividad actualizado = actividadService.update(id, a);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(actualizado));
    }

    @DeleteMapping("/actividad/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        actividadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
