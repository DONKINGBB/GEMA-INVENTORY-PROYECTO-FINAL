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

    // Método para convertir Entidad -> DTO
    private DetallePedidoDto convertirADto(DetallePedido d) {
        return DetallePedidoDto.builder()
                // Borramos .id() porque el DTO ya no tiene ese campo
                .idProducto(d.getIdProducto()) // Mapeo correcto
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

    // CORREGIDO: Usamos String porque tu ID es UUID
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
                .idProducto(dto.getIdProducto())
                .cantidad(dto.getCantidad())
                .precioUnitario(dto.getPrecioUnitario())
                .build();
        return ResponseEntity.ok(convertirADto(detallePedidoService.save(d)));
    }

    // CORREGIDO: Usamos String
    @PutMapping("/detalle-pedido/{id}")
    public ResponseEntity<DetallePedidoDto> update(@PathVariable String id, @RequestBody DetallePedidoDto dto) {
        DetallePedido d = DetallePedido.builder()
                .idProducto(dto.getIdProducto())
                .cantidad(dto.getCantidad())
                .precioUnitario(dto.getPrecioUnitario())
                .build();

        // Asegúrate de que tu servicio acepte (String, DetallePedido)
        DetallePedido actualizado = detallePedidoService.update(id, d);

        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirADto(actualizado));
    }

    // CORREGIDO: Usamos String
    @DeleteMapping("/detalle-pedido/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        detallePedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}