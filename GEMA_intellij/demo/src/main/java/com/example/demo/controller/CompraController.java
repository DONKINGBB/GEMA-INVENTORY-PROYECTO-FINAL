package com.example.demo.controller;

import com.example.demo.dto.CompraDto;
import com.example.demo.model.Compra;
import com.example.demo.service.CompraService;
import com.example.demo.service.TenancyService;
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

    private final com.example.demo.repository.ProveedorRepository proveedorRepo;
    private final TenancyService tenancyService;

    // Método privado para convertir Entidad a DTO
    private CompraDto convertirADto(Compra c) {
        String nombreProv = "Proveedor Desconocido";
        if (c.getIdProveedor() != null) {
            nombreProv = proveedorRepo.findById(c.getIdProveedor())
                            .map(p -> p.getNombre())
                            .orElse("ID: " + c.getIdProveedor());
        }

        return CompraDto.builder()
                .id(c.getId())
                .idProveedor(c.getIdProveedor())
                .nombreProveedor(nombreProv)
                .idUsuario(c.getIdUsuario())
                .idAlmacenDestino(c.getIdAlmacenDestino())
                .fechaCompra(c.getFechaCompra())
                .total(c.getTotal())
                .build();
    }

    @GetMapping("/compra")
    public ResponseEntity<List<CompraDto>> lista(@RequestParam(required = false) String userId) {
        List<Compra> compras;
        if (userId != null && !userId.isEmpty()) {
            String nId = tenancyService.resolveNegocioId(userId);
            compras = compraService.getByUserId(nId);
        } else {
            compras = compraService.getAll();
        }

        if (compras == null || compras.isEmpty()) {
            // Nota: Retornar 204 No Content hace que Retrofit devuelva body null.
            // A veces es mejor devolver lista vacia 200 OK.
            // Por consistencia con codigo previo, dejaremos empty list si size=0
             return ResponseEntity.ok(java.util.Collections.emptyList());
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
        String nId = tenancyService.resolveNegocioId(dto.getIdUsuario());
        Compra c = Compra.builder()
                .idProveedor(dto.getIdProveedor())
                .idUsuario(nId)
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