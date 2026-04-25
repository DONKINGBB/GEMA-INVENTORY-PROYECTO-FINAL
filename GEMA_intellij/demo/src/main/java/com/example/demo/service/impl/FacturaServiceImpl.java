package com.example.demo.service.impl;

import com.example.demo.dto.FacturaDto;
import com.example.demo.model.Factura;
import com.example.demo.model.Cliente;
import com.example.demo.model.CatEstadosFactura; // Tu catalogo de estados
import com.example.demo.repository.FacturaRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.CatEstadosFacturaRepository; // Asegúrate de tener este repo
import com.example.demo.service.FacturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime; // O Timestamp
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacturaServiceImpl implements FacturaService {

    private final FacturaRepository facturaRepo;
    private final ClienteRepository clienteRepo;
    private final CatEstadosFacturaRepository estadoRepo; // Repositorio del catálogo

    @Override
    public List<FacturaDto> obtenerTodas() {
        return facturaRepo.findAll().stream()
                .map(f -> FacturaDto.builder()
                        .idFactura(f.getIdFactura())
                        .nombreCliente(f.getCliente().getNombre()) // Navegamos la relación
                        .total(f.getTotal().doubleValue()) // Si es BigDecimal en modelo
                        .idEstado(f.getEstado().getIdEstado())
                        .fechaEmision(f.getFechaEmision().toString())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public FacturaDto crearFactura(FacturaDto dto) {
        // 1. Buscar Cliente
        Cliente cliente = clienteRepo.findById(dto.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // 2. Buscar Estado (Por defecto 1: Por cobrar, o el que venga en el DTO)
        Integer estadoId = (dto.getIdEstado() != null) ? dto.getIdEstado() : 1;
        CatEstadosFactura estado = estadoRepo.findById(estadoId)
                .orElseThrow(() -> new RuntimeException("Estado no valido"));

        // 3. Crear Entidad
        Factura f = new Factura();
        f.setCliente(cliente);
        f.setEstado(estado);
        f.setTotal(java.math.BigDecimal.valueOf(dto.getTotal()));
        f.setFechaEmision(LocalDateTime.now()); // O Timestamp actual

        // 4. Guardar
        Factura guardada = facturaRepo.save(f);

        return FacturaDto.builder()
                .idFactura(guardada.getIdFactura())
                .nombreCliente(cliente.getNombre())
                .total(guardada.getTotal().doubleValue())
                .build();
    }
}