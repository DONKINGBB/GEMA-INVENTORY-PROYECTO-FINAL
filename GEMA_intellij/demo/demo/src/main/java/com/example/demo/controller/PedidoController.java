package com.example.demo.controller;

import com.example.demo.dto.PedidoDto;
import com.example.demo.model.Pedido;
import com.example.demo.service.PedidoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api")
@RestController
@AllArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    private PedidoDto convertirADto(Pedido p) {
        return PedidoDto.builder()
                .id(p.getId())
                .idCliente(p.getIdCliente())
                .idUsuario(p.getIdUsuario())
                .idAlmacenOrigen(p.getIdAlmacenOrigen())
                .fechaPedido(p.getFechaPedido())
                .idEstado(p.getIdEstado())
                .total(p.getTotal())
                .build();
    }

    @GetMapping("/pedido")
    public ResponseEntity<List<PedidoDto>> lista() {
        List<Pedido> pedidos = pedidoService.getAll();
        List<PedidoDto> dtos = pedidos.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/pedido/{id}")
    public ResponseEntity<PedidoDto> getById(@PathVariable String id) {
        Pedido p = pedidoService.getById(id);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(p));
    }

    @PostMapping("/pedido")
    public ResponseEntity<PedidoDto> save(@RequestBody PedidoDto dto) {
        Pedido p = Pedido.builder()
                .idCliente(dto.getIdCliente())
                .idUsuario(dto.getIdUsuario())
                .idAlmacenOrigen(dto.getIdAlmacenOrigen())
                .fechaPedido(dto.getFechaPedido())
                .idEstado(dto.getIdEstado())
                .total(dto.getTotal())
                .build();
        return ResponseEntity.ok(convertirADto(pedidoService.save(p)));
    }

    @PutMapping("/pedido/{id}")
    public ResponseEntity<PedidoDto> update(@PathVariable String id, @RequestBody PedidoDto dto) {
        Pedido p = Pedido.builder()
                .idCliente(dto.getIdCliente())
                .idUsuario(dto.getIdUsuario())
                .idAlmacenOrigen(dto.getIdAlmacenOrigen())
                .fechaPedido(dto.getFechaPedido())
                .idEstado(dto.getIdEstado())
                .total(dto.getTotal())
                .build();
        Pedido actualizado = pedidoService.update(id, p);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(actualizado));
    }

    @DeleteMapping("/pedido/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
