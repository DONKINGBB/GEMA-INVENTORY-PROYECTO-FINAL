package com.example.demo.controller;

import com.example.demo.dto.DetallePedidoDto;
import com.example.demo.dto.PedidoDto;
import com.example.demo.model.DetallePedido;
import com.example.demo.model.Pedido;
import com.example.demo.model.Producto; // Necesario para el stock
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProductoRepository; // Necesario para el stock
import com.example.demo.service.ActividadService;
import com.example.demo.model.Actividad;
import com.example.demo.service.TenancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; // Para seguridad en datos
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepo;

    // INYECCIÓN NECESARIA PARA RESTAR EL INVENTARIO
    // INYECCIÓN NECESARIA PARA RESTAR EL INVENTARIO
    @Autowired
    private com.example.demo.repository.InventarioRepository inventarioRepo;

    @Autowired
    private TenancyService tenancyService;

    @Autowired
    private ActividadService actividadService;

    @PostMapping
    @Transactional // IMPORTANTE: Si algo falla (ej. falta stock), cancela todo el proceso
    public ResponseEntity<?> crearPedido(@RequestBody PedidoDto dto, @RequestParam String userId) {
        String nId = tenancyService.resolveNegocioId(userId);

        // 0. Validar Almacén
        if (dto.getIdAlmacenOrigen() == null) {
            return ResponseEntity.badRequest().body("Error: Debes especificar un almacén de origen.");
        }

        // 1. Configurar la cabecera del Pedido
        Pedido pedido = new Pedido();

        // --- SOLUCIÓN ERROR SQL: Generamos el ID manualmente ---
        pedido.setId(java.util.UUID.randomUUID().toString());

        pedido.setIdCliente(dto.getIdCliente());
        pedido.setIdUsuario(userId); // Quien lo hizo
        pedido.setIdNegocio(nId);    // Para el negocio
        pedido.setIdAlmacenOrigen(dto.getIdAlmacenOrigen());
        pedido.setFechaPedido(Timestamp.from(Instant.now()));
        pedido.setIdEstado(1); // 1 = Pendiente
        
        // Asignar nuevos campos
        pedido.setNombre(dto.getNombre() != null ? dto.getNombre() : "Pedido " + pedido.getId().substring(0,8));
        pedido.setFechaLimite(dto.getFechaLimite());

        // 2. Procesar Detalles, Calcular Total y RESTAR STOCK
        BigDecimal totalGeneral = BigDecimal.ZERO;
        List<DetallePedido> detallesEntidad = new ArrayList<>();

        if (dto.getDetalles() != null) {
            for (DetallePedidoDto detDto : dto.getDetalles()) {

                // --- LÓGICA DE CONTROL DE STOCK (CORREGIDA) ---
                // Buscamos en INVENTARIO, no en Producto global
                Optional<com.example.demo.model.Inventario> inventarioOpt = 
                    inventarioRepo.findByProductoIdProductoAndAlmacenIdAlmacen(detDto.getIdProducto(), dto.getIdAlmacenOrigen());

                if (inventarioOpt.isPresent()) {
                    com.example.demo.model.Inventario inv = inventarioOpt.get();

                    // Validación: ¿Hay suficiente stock?
                    if (inv.getCantidadActual() < detDto.getCantidad()) {
                        return ResponseEntity.badRequest()
                                .body("Error: No hay suficiente stock para: " + inv.getProducto().getNombre() + 
                                      " en este almacén. Disponible: " + inv.getCantidadActual());
                    }

                    // Acción: Restar stock y actualizar INVENTARIO
                    inv.setCantidadActual(inv.getCantidadActual() - detDto.getCantidad());
                    inventarioRepo.save(inv);

                } else {
                    return ResponseEntity.badRequest()
                            .body("Error: Producto no encontrado en este almacén (ID: " + detDto.getIdProducto() + ")");
                }
                // ----------------------------------

                // Crear el objeto Detalle para el Pedido
                DetallePedido detalle = new DetallePedido();
                detalle.setIdProducto(detDto.getIdProducto());
                detalle.setCantidad(detDto.getCantidad());
                detalle.setPrecioUnitario(detDto.getPrecioUnitario());

                // Nota: No guardamos nombreProducto aquí porque no existe en tu tabla detalle

                // Calcular subtotal de la línea
                BigDecimal cantidadBig = new BigDecimal(detDto.getCantidad());
                BigDecimal subtotalLinea = detDto.getPrecioUnitario().multiply(cantidadBig);

                // Sumar al total general
                totalGeneral = totalGeneral.add(subtotalLinea);

                // Asignar el subtotal al detalle (FALTABA ESTO)
                detalle.setSubtotal(subtotalLinea);

                // --- CORECCIÓN IMPORTANTE ---
                // Vincular el detalle con el pedido (bidireccional)
                detalle.setPedido(pedido);

                detallesEntidad.add(detalle);
            }
        }

        // 3. Asignar totales y lista final
        pedido.setTotal(totalGeneral);
        pedido.setDetalles(detallesEntidad);

        // 4. Guardar Pedido
        Pedido guardado = pedidoRepo.save(pedido);

        // 5. REGISTRAR ACTIVIDAD DE VENTA
        try {
            Actividad act = Actividad.builder()
                .id(java.util.UUID.randomUUID().toString())
                .idUsuario(userId)
                .idNegocio(nId)
                .idTipoActividad(2) // 2 = VENTA / INGRESO
                .descripcion("Venta Registrada - " + pedido.getNombre())
                .fecha(java.time.LocalDateTime.now())
                .entidadAfectada("Pedido")
                .build();
            actividadService.save(act);
        } catch (Exception e) {
            // No bloqueamos el flujo principal por un error en logs
        }

        return ResponseEntity.ok(guardado);
    }

    @Autowired
    private ProductoRepository productoRepo;

    // ... (existing code) ...

    // LISTAR PEDIDOS CON NOMBRES DE PRODUCTOS
    @GetMapping
    public ResponseEntity<List<PedidoDto>> listar(@RequestParam String userId) {
        String nId = tenancyService.resolveNegocioId(userId);
        List<Pedido> pedidos = pedidoRepo.findByIdNegocio(nId);
        List<PedidoDto> dtos = new ArrayList<>();

        for (Pedido p : pedidos) {
             PedidoDto dto = new PedidoDto();
             dto.setId(p.getId());
             dto.setFechaPedido(p.getFechaPedido());
             dto.setFechaLimite(p.getFechaLimite());
             dto.setTotal(p.getTotal());
             dto.setIdEstado(p.getIdEstado());
             dto.setNombre(p.getNombre());
             dto.setIdAlmacenOrigen(p.getIdAlmacenOrigen());
             dto.setIdCliente(p.getIdCliente());
             // ... otros campos simples si faltan

             List<DetallePedidoDto> detallesDto = new ArrayList<>();
             if (p.getDetalles() != null) {
                 for (DetallePedido det : p.getDetalles()) {
                      String nombreProd = "ID: " + det.getIdProducto(); // Fallback
                      // Buscar nombre real
                      Optional<Producto> prod = productoRepo.findById(det.getIdProducto());
                      if (prod.isPresent()) {
                          nombreProd = prod.get().getNombre();
                      }

                      detallesDto.add(new DetallePedidoDto(
                          det.getIdProducto(),
                          nombreProd, 
                          det.getCantidad(),
                          det.getPrecioUnitario()
                      ));
                 }
             }
             dto.setDetalles(detallesDto);
             dtos.add(dto);
        }
        return ResponseEntity.ok(dtos);
    }

    // ACTUALIZAR ESTADO (Marcar Entregado)
    @PutMapping("/{id}/entregado")
    public ResponseEntity<?> marcarEntregado(@PathVariable String id) {
        return pedidoRepo.findById(id).map(pedido -> {
            pedido.setIdEstado(2); // 2 = Completado/Entregado
            pedidoRepo.save(pedido);

            // REGISTRAR ACTIVIDAD DE COMPLETADO
            try {
                String nId = tenancyService.resolveNegocioId(pedido.getIdUsuario());
                Actividad act = Actividad.builder()
                    .id(java.util.UUID.randomUUID().toString())
                    .idUsuario(pedido.getIdUsuario())
                    .idNegocio(nId)
                    .idTipoActividad(2) 
                    .descripcion("Venta Completada - " + (pedido.getNombre() != null ? pedido.getNombre() : "ID: " + pedido.getId()))
                    .fecha(java.time.LocalDateTime.now())
                    .entidadAfectada("Pedido")
                    .build();
                actividadService.save(act);
            } catch (Exception e) {}

            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @Autowired
    private com.example.demo.repository.BalanceFinancieroRepository balanceRepo;
    @Autowired
    private com.example.demo.repository.CatTiposBalanceRepository catRepo; // Repositorio oficial para tipos

    // ELIMINAR PEDIDO - SOLO PERMITIDO PARA PEDIDOS PENDIENTES (1)
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> eliminarPedido(@PathVariable String id, @RequestParam String userId) {
        Optional<Pedido> pedidoOpt = pedidoRepo.findById(id);
        if (!pedidoOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Pedido pedido = pedidoOpt.get();

        // SEGURIDAD: SI EL PEDIDO YA ESTÁ COMPLETADO (2), BLOQUEAR EL BORRADO
        if (pedido.getIdEstado() != null && pedido.getIdEstado() == 2) {
            return ResponseEntity.status(403).body("No se pueden eliminar pedidos completados para proteger los registros financieros.");
        }

        // --- RESTAURAR STOCK SOLO PARA PEDIDOS PENDIENTES (1) ---
        if (pedido.getIdEstado() != null && pedido.getIdEstado() == 1) {
            for (DetallePedido det : pedido.getDetalles()) {
                Optional<com.example.demo.model.Inventario> invOpt = 
                    inventarioRepo.findByProductoIdProductoAndAlmacenIdAlmacen(det.getIdProducto(), pedido.getIdAlmacenOrigen());
                
                if (invOpt.isPresent()) {
                    com.example.demo.model.Inventario inv = invOpt.get();
                    inv.setCantidadActual(inv.getCantidadActual() + det.getCantidad());
                    inventarioRepo.save(inv);
                }
            }
        }

        // Registrar actividad del borrado
        try {
            Actividad act = Actividad.builder()
                .id(java.util.UUID.randomUUID().toString())
                .idUsuario(userId)
                .idNegocio(tenancyService.resolveNegocioId(userId))
                .idTipoActividad(3) // 3 = ELIMINACIÓN
                .descripcion("Pedido Pendiente Eliminado - Stock Restaurado")
                .fecha(java.time.LocalDateTime.now())
                .build();
            actividadService.save(act);
        } catch (Exception e) {}

        pedidoRepo.delete(pedido);
        return ResponseEntity.ok().build();
    }
}