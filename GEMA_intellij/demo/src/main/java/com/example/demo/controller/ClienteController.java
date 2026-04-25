package com.example.demo.controller;

import com.example.demo.model.Cliente;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.service.TenancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteRepository clienteRepo;
    private final TenancyService tenancyService;

    @GetMapping
    public ResponseEntity<List<Cliente>> listar(@RequestParam String userId) {
        String nId = tenancyService.resolveNegocioId(userId);
        return ResponseEntity.ok(clienteRepo.findByIdNegocio(nId));
    }


    @PostMapping
    public ResponseEntity<Cliente> crear(@RequestBody Cliente cliente, @RequestParam String userId) {
        String nId = tenancyService.resolveNegocioId(userId);
        cliente.setIdNegocio(nId);
        if (cliente.getIdTipoCliente() == null) cliente.setIdTipoCliente(1);
        return ResponseEntity.ok(clienteRepo.save(cliente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable String id, @RequestBody Cliente clienteDetails) {
        return clienteRepo.findById(id).map(cliente -> {
            cliente.setNombre(clienteDetails.getNombre());
            cliente.setDireccion(clienteDetails.getDireccion());
            cliente.setContacto(clienteDetails.getContacto());
            return ResponseEntity.ok(clienteRepo.save(cliente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        if (clienteRepo.existsById(id)) {
            clienteRepo.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}