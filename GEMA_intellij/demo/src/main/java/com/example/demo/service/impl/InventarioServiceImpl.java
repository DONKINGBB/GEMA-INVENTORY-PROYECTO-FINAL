package com.example.demo.service.impl;

import com.example.demo.dto.InventarioDto;
import com.example.demo.dto.ProductoSeleccionDto;
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
        private final com.example.demo.service.NotificationService notificationService;
        private final com.example.demo.repository.UsuarioRepository usuarioRepo;
        private final com.example.demo.service.TenancyService tenancyService;

        private void checkLowStockAndNotify(Inventario inv) {
                Producto p = inv.getProducto();
                if (p.getStockMinimo() != null && inv.getCantidadActual() <= p.getStockMinimo()) {
                        // Notificar al dueño del negocio (o al usuario configurado)
                        usuarioRepo.findById(inv.getProducto().getIdNegocio()).ifPresent(u -> {
                                notificationService.sendNotification(u,
                                                "¡Alerta de Stock Bajo!",
                                                "El producto " + p.getNombre() + " ha alcanzado el mínimo ("
                                                                + inv.getCantidadActual() + ")",
                                                "LOW_STOCK");
                        });
                }
        }

        @Override
        public List<InventarioDto> obtenerPorUsuario(String idUsuario) {
                // Obtenemos el negocio del usuario para mostrar TODO el inventario de la empresa
                String nId = tenancyService.resolveNegocioId(idUsuario);
                return inventarioRepo.findByProductoIdNegocio(nId).stream()
                                .map(inv -> InventarioDto.builder()
                                                .idInventario(inv.getIdInventario())
                                                .idProducto(inv.getProducto().getIdProducto())
                                                .nombreProducto(inv.getProducto().getNombre())
                                                .categoria(inv.getProducto().getCategoria())
                                                .sku(inv.getProducto().getSku())
                                                .descripcion(inv.getProducto().getDescripcion())
                                                .precioCompra(inv.getProducto().getPrecioCompra())
                                                .precioVenta(inv.getProducto().getPrecioVenta())
                                                .stockMinimo(inv.getProducto().getStockMinimo())
                                                .idAlmacen(inv.getAlmacen().getIdAlmacen())
                                                .cantidadActual(inv.getCantidadActual())
                                                .imagenUrl(inv.getProducto().getImagenUrl())
                                                .fechaCreacion(inv.getFechaCreacion() != null ? inv.getFechaCreacion().toString() : java.time.LocalDateTime.now().toString())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        public InventarioDto guardarInventario(InventarioDto dto) {
                // 1. Buscamos las entidades relacionadas
                Producto prod = productoRepo.findById(dto.getIdProducto())
                                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

                Almacen alm = almacenRepo.findById(dto.getIdAlmacen()) // Asumiendo que ID almacen es Integer o String
                                                                       // según tu base
                                .orElseThrow(() -> new RuntimeException("Almacén no encontrado"));

                // 2. Creamos la entidad
                Inventario inv = new Inventario();
                inv.setProducto(prod);
                inv.setAlmacen(alm);
                inv.setCantidadActual(dto.getCantidadActual());

                // 3. Guardamos
                Inventario guardado = inventarioRepo.save(inv);

                // 3.1 Notificamos si es necesario
                checkLowStockAndNotify(guardado);

                // 4. Devolvemos DTO
                return InventarioDto.builder()
                                .idInventario(guardado.getIdInventario())
                                .nombreProducto(prod.getNombre())
                                .cantidadActual(guardado.getCantidadActual())
                                .categoria(inv.getProducto().getCategoria())
                                .imagenUrl(inv.getProducto().getImagenUrl())
                                .build();
        }

        @Override
        public InventarioDto actualizarStock(String idInventario, Integer nuevaCantidad) {
                Inventario inv = inventarioRepo.findById(idInventario)
                                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));

                inv.setCantidadActual(nuevaCantidad);
                Inventario actualizado = inventarioRepo.save(inv);

                // Notificamos si es necesario
                checkLowStockAndNotify(actualizado);

                return InventarioDto.builder()
                                .idInventario(actualizado.getIdInventario())
                                .cantidadActual(actualizado.getCantidadActual())
                                .build();
        }

        @Override
        public List<ProductoSeleccionDto> obtenerProductosPorAlmacen(String userId, Integer idAlmacen) {
                String nId = tenancyService.resolveNegocioId(userId);
                return inventarioRepo
                                .findByAlmacenIdAlmacenAndAlmacenIdNegocioAndCantidadActualGreaterThan(idAlmacen,
                                                nId, 0)
                                .stream()
                                .map(inv -> ProductoSeleccionDto.builder()
                                                .idProducto(inv.getProducto().getIdProducto())
                                                .nombre(inv.getProducto().getNombre())
                                                .sku(inv.getProducto().getSku())
                                                .precioVenta(inv.getProducto().getPrecioVenta())
                                                .stockActual(inv.getCantidadActual())
                                                .imagenUrl(inv.getProducto().getImagenUrl())
                                                .build())
                                .collect(Collectors.toList());
        }
}