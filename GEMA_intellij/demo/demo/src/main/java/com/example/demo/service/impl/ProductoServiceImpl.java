package com.example.demo.service.impl;

import com.example.demo.dto.ProductoDto;
import com.example.demo.model.Almacen;
import com.example.demo.model.Inventario;
import com.example.demo.model.Producto;
import com.example.demo.repository.AlmacenesRepository;
import com.example.demo.repository.InventarioRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepo;
    private final InventarioRepository inventarioRepo;
    private final AlmacenesRepository almacenRepo;

    @Override
    @Transactional
    public Producto crearProductoCompleto(ProductoDto dto) {
        Producto p = new Producto();
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setPrecioVenta(dto.getPrecioVenta());
        p.setStockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : 5);

        p.setSku(dto.getSku());
        p.setCategoria(dto.getCategoria());
        p.setPrecioCompra(dto.getPrecioCompra());
        p.setIdUsuario(dto.getIdUsuario());

        Producto guardado = productoRepo.save(p);

        Almacen almacen = almacenRepo.findById(1)
                .orElseThrow(() -> new RuntimeException("¡ERROR CRÍTICO: No existe el Almacén ID 1 en la BD!"));

        Inventario inv = new Inventario();
        inv.setProducto(guardado);
        inv.setAlmacen(almacen);
        inv.setCantidadActual(dto.getCantidad());

        inventarioRepo.save(inv);

        return productoRepo.save(p);
    }
}