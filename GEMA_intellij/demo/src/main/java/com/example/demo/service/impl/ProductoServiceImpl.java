package com.example.demo.service.impl;

import com.example.demo.dto.ProductoDto;
import com.example.demo.model.Almacen;
import com.example.demo.model.Inventario;
import com.example.demo.model.Producto;
import com.example.demo.repository.AlmacenesRepository;
import com.example.demo.repository.InventarioRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.service.ProductoService;
import com.example.demo.service.TenancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepo;
    private final InventarioRepository inventarioRepo;
    private final AlmacenesRepository almacenRepo;
    private final ProductoRepository productoRepository; // Redundante pero lo dejaremos para no romper
    private final TenancyService tenancyService;
    
    // NUEVOS REPOSITORIOS
    private final com.example.demo.repository.CompraRepository compraRepo;
    private final com.example.demo.repository.ProveedorRepository proveedorRepo;

    @Override
    @Transactional
    public Producto crearProductoCompleto(ProductoDto dto) {
        String nId = tenancyService.resolveNegocioId(dto.getIdUsuario());
        String actualUser = dto.getIdUsuario(); // El ID real del usuario
        
        // 1. Obtener Proveedor General (o crear uno dummy) para registrar la compra
        String idProveedor = obtenerIdProveedorGeneral(nId);
        
        // 1. Validar si ya existe un producto con el mismo SKU para este negocio
        java.util.Optional<Producto> existente = productoRepo.findBySkuAndIdNegocio(dto.getSku(), nId);

        if (existente.isPresent()) {
            Producto p = existente.get();
            // Buscar inventario en Almacén seleccionado (o 1 por defecto)
            Integer almacenId = dto.getIdAlmacen() != null ? dto.getIdAlmacen() : 1;
            
            Inventario inv = inventarioRepo.findByProductoIdProductoAndAlmacenIdAlmacen(p.getIdProducto(), almacenId)
                    .orElse(null);

            int cantidadAgregar = dto.getCantidad() != null ? dto.getCantidad() : 0;

            if (inv != null) {
                // Sumar al stock existente
                inv.setCantidadActual(inv.getCantidadActual() + cantidadAgregar);
                inventarioRepo.save(inv);
            } else {
                // El producto existe, pero no tiene inventario en este almacén
                Almacen almacen = almacenRepo.findById(almacenId)
                        .orElseThrow(() -> new RuntimeException("Error: Almacén ID " + almacenId + " no encontrado"));
                
                Inventario nuevoInv = new Inventario();
                nuevoInv.setProducto(p);
                nuevoInv.setAlmacen(almacen);
                nuevoInv.setCantidadActual(cantidadAgregar);
                inventarioRepo.save(nuevoInv);
            }
            
            // --- REGISTRAR COMPRA AUTOMÁTICA (GASTO) ---
            registrarCompraAutomatica(actualUser, nId, idProveedor, p.getPrecioCompra(), cantidadAgregar, almacenId);

            return p; // Retornamos el producto existente
        }

        // 2. Si NO existe, creamos uno nuevo (Lógica original)
        Producto p = new Producto();
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setPrecioVenta(dto.getPrecioVenta());
        p.setStockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : 5);

        p.setSku(dto.getSku());
        p.setCategoria(dto.getCategoria());
        p.setPrecioCompra(dto.getPrecioCompra());
        p.setIdNegocio(nId);
        
        // --- FASE 2: GUARDAR URLs MULTIMEDIA ---
        p.setImagenUrl(dto.getImagenUrl());
        p.setModelo3dUrl(dto.getModelo3dUrl());

        Producto guardado = productoRepo.save(p);

        Integer almacenId = dto.getIdAlmacen() != null ? dto.getIdAlmacen() : 1;
        Almacen almacen = almacenRepo.findById(almacenId)
                .orElseThrow(() -> new RuntimeException("¡ERROR CRÍTICO: No existe el Almacén ID " + almacenId + " en la BD!"));

        Inventario inv = new Inventario();
        inv.setProducto(guardado);
        inv.setAlmacen(almacen);
        inv.setCantidadActual(dto.getCantidad());

        inventarioRepo.save(inv);

        // --- REGISTRAR COMPRA AUTOMÁTICA (GASTO) ---
        registrarCompraAutomatica(actualUser, nId, idProveedor, dto.getPrecioCompra(), dto.getCantidad(), almacenId);

        return productoRepo.save(p);
    }
    
    private void registrarCompraAutomatica(String idUsuario, String idNegocio, String idProveedor, Double precioCompra, Integer cantidad, Integer almacenId) {
        if (cantidad == null || cantidad <= 0 || precioCompra == null || precioCompra <= 0) return;

        BigDecimal totalCompra = BigDecimal.valueOf(precioCompra).multiply(BigDecimal.valueOf(cantidad));
        
        com.example.demo.model.Compra compra = new com.example.demo.model.Compra();
        compra.setIdUsuario(idUsuario);
        compra.setIdNegocio(idNegocio); // <--- ¡AÑADIDO!
        compra.setIdProveedor(idProveedor);
        compra.setIdAlmacenDestino(almacenId);
        compra.setTotal(totalCompra);
        compra.setFechaCompra(java.sql.Timestamp.from(java.time.Instant.now()));

        compraRepo.save(compra);
    }
    
    private String obtenerIdProveedorGeneral(String idNegocio) {
        // Buscar proveedor general del negocio primero
        java.util.List<com.example.demo.model.Proveedor> provs = proveedorRepo.findAll();
        // Nota: En un sistema real deberíamos filtrar por idNegocio aquí:
        // proveedorRepo.findByIdNegocio(idNegocio)
        
        if (!provs.isEmpty()) {
            return provs.get(0).getId();
        }
        
        // Crear uno dummy para este negocio si no hay ninguno
        com.example.demo.model.Proveedor p = new com.example.demo.model.Proveedor();
        p.setNombre("Proveedor General");
        p.setDireccion("Oficina Central");
        p.setContacto("-");
        p.setIdNegocio(idNegocio); // <--- ¡AÑADIDO!
        return proveedorRepo.save(p).getId();
    }

    @Override
    @Transactional
    public Producto actualizarProducto(String id, ProductoDto dto) {
        // 1. Buscar el producto existente
        Producto p = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // 2. Actualizar los campos
        p.setNombre(dto.getNombre());
        p.setSku(dto.getSku());
        p.setCategoria(dto.getCategoria());
        p.setPrecioCompra(dto.getPrecioCompra());
        p.setPrecioVenta(dto.getPrecioVenta());
        p.setDescripcion(dto.getDescripcion());
        p.setStockMinimo(dto.getStockMinimo());
        
        // --- FASE 2: ACTUALIZAR MULTIMEDIA ---
        if (dto.getImagenUrl() != null) p.setImagenUrl(dto.getImagenUrl());
        if (dto.getModelo3dUrl() != null) p.setModelo3dUrl(dto.getModelo3dUrl());
        
        // Nota: No actualizamos id_usuario (el dueño no cambia) ni el stock (eso se hace en inventario)

        // 3. Guardar cambios del producto
        Producto actualizado = productoRepo.save(p);

        // 4. Sincronización de Stock y Finanzas (NUEVO)
        if (dto.getCantidad() != null) {
            List<Inventario> inventarios = inventarioRepo.findByProductoIdProducto(id);
            Integer idAlmacenDestino = dto.getIdAlmacen() != null ? dto.getIdAlmacen() : (inventarios.isEmpty() ? 1 : inventarios.get(0).getAlmacen().getIdAlmacen());
            
            if (!inventarios.isEmpty()) {
                Inventario inv = inventarios.get(0);
                int cantidadAnterior = inv.getCantidadActual();
                int nuevaCantidad = dto.getCantidad();
                
                if (nuevaCantidad > cantidadAnterior) {
                    // Si aumenta la cantidad, es una compra implicita (Gasto)
                    int diferencia = nuevaCantidad - cantidadAnterior;
                    String actualUser = dto.getIdUsuario() != null ? dto.getIdUsuario() : p.getIdNegocio(); // Fallback segun modelo
                    String nId = p.getIdNegocio();
                    String idProveedor = obtenerIdProveedorGeneral(nId);
                    
                    registrarCompraAutomatica(actualUser, nId, idProveedor, p.getPrecioCompra(), diferencia, idAlmacenDestino);
                }
                
                // Actualizar cantidad física
                inv.setCantidadActual(nuevaCantidad);
                
                // Si cambió el almacén, actualizarlo también
                if (dto.getIdAlmacen() != null && !inv.getAlmacen().getIdAlmacen().equals(dto.getIdAlmacen())) {
                     Almacen nuevoAlmacen = almacenRepo.findById(dto.getIdAlmacen())
                         .orElseThrow(() -> new RuntimeException("Almacen destino no encontrado"));
                     inv.setAlmacen(nuevoAlmacen);
                }
                inventarioRepo.save(inv);
                
            } else {
                // Caso raro: No hay inventario previo, creamos uno y registramos gasto total
                Almacen almacen = almacenRepo.findById(idAlmacenDestino)
                        .orElseThrow(() -> new RuntimeException("Almacen no encontrado"));
                Inventario inv = new Inventario();
                inv.setProducto(p);
                inv.setAlmacen(almacen);
                inv.setCantidadActual(dto.getCantidad());
                inventarioRepo.save(inv);
                
                // Registrar gasto inicial si es > 0
                if (dto.getCantidad() > 0) {
                    String nId = p.getIdNegocio();
                    String idProveedor = obtenerIdProveedorGeneral(nId);
                    registrarCompraAutomatica(dto.getIdUsuario(), nId, idProveedor, p.getPrecioCompra(), dto.getCantidad(), idAlmacenDestino);
                }
            }
        }
        
        return actualizado;
    }

    @Override
    @Transactional // ¡Importante para que borre ambos o ninguno!
    public void eliminarProducto(String id) {
        // 1. Primero borramos el inventario asociado (para evitar error de llave foránea)
        inventarioRepo.deleteByProductoIdProducto(id);

        // 2. Ahora sí borramos el producto
        productoRepo.deleteById(id);
    }

    @Override
    public List<Producto> listarPorUsuario(String idUsuario) {
        String nId = tenancyService.resolveNegocioId(idUsuario);
        // Llama al repositorio que modificamos (ahora busca por Empresa)
        return productoRepository.findByIdNegocio(nId);
    }
    
    @Override
    public ProductoDto obtenerProductoPorId(String id) {
        Producto p = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                
        // Buscar en qué almacén está (tomamos el primero que tenga stock o el primero en general)
        List<Inventario> inventarios = inventarioRepo.findByProductoIdProducto(id);
        
        Integer idAlmacen = null;
        Integer cantidad = 0;
        
        if (!inventarios.isEmpty()) {
            // Preferimos el que tenga más stock o simplemente el primero
            Inventario inv = inventarios.get(0);
            idAlmacen = inv.getAlmacen().getIdAlmacen();
            cantidad = inv.getCantidadActual();
        }
        
        ProductoDto dto = new ProductoDto();
        dto.setNombre(p.getNombre());
        dto.setSku(p.getSku());
        dto.setDescripcion(p.getDescripcion());
        dto.setPrecioCompra(p.getPrecioCompra());
        dto.setPrecioVenta(p.getPrecioVenta());
        dto.setCategoria(p.getCategoria());
        dto.setStockMinimo(p.getStockMinimo());
        dto.setIdUsuario(p.getIdNegocio()); // Mantenemos el nombre del campo en el DTO por compatibilidad con Android por ahora
        
        dto.setImagenUrl(p.getImagenUrl());
        dto.setModelo3dUrl(p.getModelo3dUrl());
        
        // Datos extraidos del inventario
        dto.setIdAlmacen(idAlmacen);
        dto.setCantidad(cantidad); // Sobreescribimos con la cantidad real en inventario
        
        return dto;
    }
}