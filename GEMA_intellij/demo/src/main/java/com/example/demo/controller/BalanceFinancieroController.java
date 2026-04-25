package com.example.demo.controller;

import com.example.demo.dto.BalanceFinancieroDto;
import com.example.demo.model.BalanceFinanciero;
import com.example.demo.service.BalanceFinancieroService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class BalanceFinancieroController {

    private final BalanceFinancieroService service;

    @Autowired
    private com.example.demo.service.TenancyService tenancyService;

    @Autowired
    private com.example.demo.repository.BalanceFinancieroRepository repository;

    private BalanceFinancieroDto convertirADto(BalanceFinanciero b) {
        return BalanceFinancieroDto.builder()
                .id(b.getIdBalance())
                .idTipoBalance(b.getTipoBalance() != null ? b.getTipoBalance().getIdTipoBalance() : null)
                .fuente(b.getFuente())
                .monto(b.getMonto())
                .fecha(b.getFecha())
                .referencia(b.getReferencia())
                .idNegocio(b.getIdNegocio())
                .build();
    }

    @GetMapping("/balances")
    public ResponseEntity<List<BalanceFinancieroDto>> getBalances(@RequestParam String userId) {
        String nId = tenancyService.resolveNegocioId(userId);
        
        // RECUPERACIÓN AUTOMÁTICA DE DATOS:
        // Buscamos los que ya tengan su nId O los que sean NULL (datos perdidos previos)
        List<BalanceFinanciero> balances = repository.findByNegocioAutoRecover(nId);
        
        List<BalanceFinancieroDto> dtos = balances.stream()
                .map(b -> {
                    // Si el balance era huérfano, lo reclamamos para el usuario actual (reparación silenciosa)
                    if (b.getIdNegocio() == null) {
                        b.setIdNegocio(nId);
                        repository.save(b);
                    }
                    return convertirADto(b);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/balance")
    public ResponseEntity<List<BalanceFinancieroDto>> lista() {
        List<BalanceFinanciero> balances = service.getAll();
        List<BalanceFinancieroDto> dtos = balances.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/balance/{id}")
    public ResponseEntity<BalanceFinancieroDto> getById(@PathVariable String id) {
        BalanceFinanciero b = service.getById(id);
        if (b == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(b));
    }

    @PostMapping("/balance")
    public ResponseEntity<BalanceFinancieroDto> save(@RequestBody BalanceFinancieroDto dto) {
        BalanceFinanciero b = BalanceFinanciero.builder()
                .idBalance(dto.getId()) // opcional si usas UUID
                .fuente(dto.getFuente())
                .monto(dto.getMonto())
                .fecha(dto.getFecha())
                .referencia(dto.getReferencia())
                .build();
        return ResponseEntity.ok(convertirADto(service.save(b)));
    }

    @PutMapping("/balance/{id}")
    public ResponseEntity<BalanceFinancieroDto> update(@PathVariable String id, @RequestBody BalanceFinancieroDto dto) {
        BalanceFinanciero b = BalanceFinanciero.builder()
                .fuente(dto.getFuente())
                .monto(dto.getMonto())
                .fecha(dto.getFecha())
                .referencia(dto.getReferencia())
                .build();
        BalanceFinanciero actualizado = service.update(id, b);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(actualizado));
    }

    @DeleteMapping("/balance/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
