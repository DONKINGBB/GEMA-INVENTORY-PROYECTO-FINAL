package com.example.demo.controller;

import com.example.demo.dto.EstadoPedidoDto;
import com.example.demo.model.EstadoPedido;
import com.example.demo.service.EstadoPedidoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class EstadoPedidoController {

    private final EstadoPedidoService estadoPedidoService;

    private EstadoPedidoDto convertirADto(EstadoPedido e) {
        return EstadoPedidoDto.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .build();
    }

    @GetMapping("/estado-pedido")
    public ResponseEntity<List<EstadoPedidoDto>> lista() {
        List<EstadoPedido> estados = estadoPedidoService.getAll();

        List<EstadoPedidoDto> dtos = estados.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/estado-pedido/{id}")
    public ResponseEntity<EstadoPedidoDto> getById(@PathVariable Integer id) {
        EstadoPedido e = estadoPedidoService.getById(id);
        if (e == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(e));
    }

    @PostMapping("/estado-pedido")
    public ResponseEntity<EstadoPedidoDto> save(@RequestBody EstadoPedidoDto dto) {
        EstadoPedido e = EstadoPedido.builder()
                .nombre(dto.getNombre())
                .build();
        return ResponseEntity.ok(convertirADto(estadoPedidoService.save(e)));
    }

    @PutMapping("/estado-pedido/{id}")
    public ResponseEntity<EstadoPedidoDto> update(@PathVariable Integer id, @RequestBody EstadoPedidoDto dto) {
        EstadoPedido e = EstadoPedido.builder()
                .nombre(dto.getNombre())
                .build();
        EstadoPedido actualizado = estadoPedidoService.update(id, e);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(actualizado));
    }

    @DeleteMapping("/estado-pedido/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        estadoPedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
