package com.example.demo.controller;

import com.example.demo.dto.DetallePedidoDto;
import com.example.demo.model.DetallePedido;
import com.example.demo.service.DetallePedidoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api")
@RestController
@AllArgsConstructor
public class DetallePedidoController {

    private final DetallePedidoService detallePedidoService;

    private DetallePedidoDto convertirADto(DetallePedido d) {
        return DetallePedidoDto.builder()
                .id(d.getId())
                .idPedido(d.getIdPedido())
                .idProducto(d.getIdProducto())
                .cantidad(d.getCantidad())
                .precioUnitario(d.getPrecioUnitario())
                .build();
    }

    @GetMapping("/detalle-pedido")
    public ResponseEntity<List<DetallePedidoDto>> lista() {
        List<DetallePedido> detalles = detallePedidoService.getAll();
        List<DetallePedidoDto> dtos = detalles.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/detalle-pedido/{id}")
    public ResponseEntity<DetallePedidoDto> getById(@PathVariable String id) {
        DetallePedido d = detallePedidoService.getById(id);
        if (d == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(d));
    }

    @PostMapping("/detalle-pedido")
    public ResponseEntity<DetallePedidoDto> save(@RequestBody DetallePedidoDto dto) {
        DetallePedido d = DetallePedido.builder()
                .idPedido(dto.getIdPedido())
                .idProducto(dto.getIdProducto())
                .cantidad(dto.getCantidad())
                .precioUnitario(dto.getPrecioUnitario())
                .build();
        return ResponseEntity.ok(convertirADto(detallePedidoService.save(d)));
    }

    @PutMapping("/detalle-pedido/{id}")
    public ResponseEntity<DetallePedidoDto> update(@PathVariable String id, @RequestBody DetallePedidoDto dto) {
        DetallePedido d = DetallePedido.builder()
                .idPedido(dto.getIdPedido())
                .idProducto(dto.getIdProducto())
                .cantidad(dto.getCantidad())
                .precioUnitario(dto.getPrecioUnitario())
                .build();
        DetallePedido actualizado = detallePedidoService.update(id, d);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(actualizado));
    }

    @DeleteMapping("/detalle-pedido/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        detallePedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
