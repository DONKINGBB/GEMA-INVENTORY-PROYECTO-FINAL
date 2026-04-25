package com.example.demo.controller;

import com.example.demo.dto.TipoBalanceDto;
import com.example.demo.model.TipoBalance;
import com.example.demo.service.TipoBalanceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class TipoBalanceController {

    private final TipoBalanceService service;

    private TipoBalanceDto convertirADto(TipoBalance t) {
        return TipoBalanceDto.builder()
                .id(t.getId())
                .nombre(t.getNombre())
                .build();
    }

    @GetMapping("/tipo-balance")
    public ResponseEntity<List<TipoBalanceDto>> lista(@RequestParam(name = "nombre", defaultValue = "", required = false) String nombre) {
        List<TipoBalance> tipos = service.getAll();
        List<TipoBalanceDto> dtos = tipos.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());

        if (!nombre.isEmpty()) {
            dtos = dtos.stream()
                    .filter(t -> t.getNombre().equalsIgnoreCase(nombre))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/tipo-balance/{id}")
    public ResponseEntity<TipoBalanceDto> getById(@PathVariable Integer id) {
        TipoBalance t = service.getById(id);
        if (t == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(t));
    }

    @PostMapping("/tipo-balance")
    public ResponseEntity<TipoBalanceDto> save(@RequestBody TipoBalanceDto dto) {
        TipoBalance t = TipoBalance.builder()
                .nombre(dto.getNombre())
                .build();
        return ResponseEntity.ok(convertirADto(service.save(t)));
    }

    @PutMapping("/tipo-balance/{id}")
    public ResponseEntity<TipoBalanceDto> update(@PathVariable Integer id, @RequestBody TipoBalanceDto dto) {
        TipoBalance t = TipoBalance.builder()
                .nombre(dto.getNombre())
                .build();
        TipoBalance actualizado = service.update(id, t);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(actualizado));
    }

    @DeleteMapping("/tipo-balance/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
