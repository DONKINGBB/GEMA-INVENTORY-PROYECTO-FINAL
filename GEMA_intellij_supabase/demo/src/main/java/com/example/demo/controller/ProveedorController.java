package com.example.demo.controller;

import com.example.demo.dto.ProveedorDto;
import com.example.demo.model.Proveedor;
import com.example.demo.service.ProveedorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;
    private final com.example.demo.service.TenancyService tenancyService;

    // Método privado para convertir Entidad a DTO
    private ProveedorDto convertirADto(Proveedor p) {
        return ProveedorDto.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .contacto(p.getContacto())
                .direccion(p.getDireccion())
                .fechaRegistro(p.getFechaRegistro())
                .build();
    }

    @GetMapping("/proveedor")
    public ResponseEntity<List<ProveedorDto>> lista(@RequestParam String userId) {
        String nId = tenancyService.resolveNegocioId(userId);
        List<Proveedor> proveedores = proveedorService.listarPorNegocio(nId);
        
        if (proveedores == null || proveedores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<ProveedorDto> dtos = proveedores.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/proveedor/{id}")
    public ResponseEntity<ProveedorDto> getById(@PathVariable String id) {
        Proveedor p = proveedorService.getById(id);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(p));
    }

    @PostMapping("/proveedor")
    public ResponseEntity<ProveedorDto> save(@RequestBody ProveedorDto dto, @RequestParam String userId) {
        String nId = tenancyService.resolveNegocioId(userId);
        Proveedor p = Proveedor.builder()
                .nombre(dto.getNombre())
                .contacto(dto.getContacto())
                .direccion(dto.getDireccion())
                .idNegocio(nId)
                .build();

        Proveedor guardado = proveedorService.save(p);
        return ResponseEntity.ok(convertirADto(guardado));
    }

    @PutMapping("/proveedor/{id}")
    public ResponseEntity<ProveedorDto> update(@PathVariable String id, @RequestBody ProveedorDto dto) {
        Proveedor datosParaActualizar = Proveedor.builder()
                .nombre(dto.getNombre())
                .contacto(dto.getContacto())
                .direccion(dto.getDireccion())
                .build();

        Proveedor actualizado = proveedorService.update(id, datosParaActualizar);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(actualizado));
    }

    @DeleteMapping("/proveedor/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        // Primero verificamos si existe
        if (proveedorService.getById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        proveedorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}