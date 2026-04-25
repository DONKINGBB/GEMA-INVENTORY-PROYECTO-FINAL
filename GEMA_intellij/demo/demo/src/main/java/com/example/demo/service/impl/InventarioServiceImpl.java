package com.example.demo.service.impl;

import com.example.demo.dto.InventarioDto;
import com.example.demo.model.Inventario;
import com.example.demo.model.Producto;
import com.example.demo.model.Almacen; // Asumo que tienes esta entidad
import com.example.demo.repository.InventarioRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.AlmacenesRepository; // O como se llame tu repo de almacenes
import com.example.demo.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepo;
    private final ProductoRepository productoRepo;
    private final AlmacenesRepository almacenRepo;

    @Override
    public List<InventarioDto> obtenerPorUsuario(String idUsuario) {
        return inventarioRepo.findByProductoIdUsuario(idUsuario).stream()
                .map(inv -> InventarioDto.builder()
                        .idInventario(inv.getIdInventario())
                        .idProducto(inv.getProducto().getIdProducto())
                        .nombreProducto(inv.getProducto().getNombre())
                        .sku(inv.getProducto().getSku())
                        .idAlmacen(inv.getAlmacen().getIdAlmacen())
                        .cantidadActual(inv.getCantidadActual())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public InventarioDto guardarInventario(InventarioDto dto) {
        // 1. Buscamos las entidades relacionadas
        Producto prod = productoRepo.findById(dto.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Almacen alm = almacenRepo.findById(dto.getIdAlmacen()) // Asumiendo que ID almacen es Integer o String según tu base
                .orElseThrow(() -> new RuntimeException("Almacén no encontrado"));

        // 2. Creamos la entidad
        Inventario inv = new Inventario();
        inv.setProducto(prod);
        inv.setAlmacen(alm);
        inv.setCantidadActual(dto.getCantidadActual());

        // 3. Guardamos
        Inventario guardado = inventarioRepo.save(inv);

        // 4. Devolvemos DTO
        return InventarioDto.builder()
                .idInventario(guardado.getIdInventario())
                .nombreProducto(prod.getNombre())
                .cantidadActual(guardado.getCantidadActual())
                .build();
    }

    @Override
    public InventarioDto actualizarStock(String idInventario, Integer nuevaCantidad) {
        Inventario inv = inventarioRepo.findById(idInventario)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));

        inv.setCantidadActual(nuevaCantidad);
        Inventario actualizado = inventarioRepo.save(inv);

        return InventarioDto.builder()
                .idInventario(actualizado.getIdInventario())
                .cantidadActual(actualizado.getCantidadActual())
                .build();
    }
}