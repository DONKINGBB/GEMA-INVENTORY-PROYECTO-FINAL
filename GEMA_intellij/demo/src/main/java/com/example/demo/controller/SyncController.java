package com.example.demo.controller;

import com.example.demo.dto.SyncPayloadDto;
import com.example.demo.model.Cliente;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.service.ProductoService;
import com.example.demo.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
@CrossOrigin(origins = "*") // Para desarrollo
public class SyncController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteRepository clienteRepo;

    @PostMapping("/batch")
    public ResponseEntity<Map<String, String>> syncBatch(@RequestBody SyncPayloadDto payload) {
        // En una app real de producción, aquí se resolverían conflictos usando campos
        // 'updatedAt'.
        // Para este MVP, si viene un producto/pedido/cliente lo creamos o actualizamos
        // directamente.

        try {
            if (payload.getProductos() != null) {
                payload.getProductos().forEach(producto -> {
                    // La lógica interna de productService.crearProductoCompleto debería hacer
                    // upsert
                    productoService.crearProductoCompleto(producto);
                });
            }

            if (payload.getPedidos() != null) {
                payload.getPedidos().forEach(pedido -> {
                    pedidoService.save(pedido);
                });
            }

            if (payload.getClientes() != null) {
                payload.getClientes().forEach(cliente -> {
                    clienteRepo.save(cliente);
                });
            }

            return ResponseEntity.ok(Map.of("status", "success", "message", "Batch synchronization completed."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
