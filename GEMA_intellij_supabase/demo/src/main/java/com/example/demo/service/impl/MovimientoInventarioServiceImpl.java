package com.example.demo.service.impl;

import com.example.demo.model.MovimientoInventario;
import com.example.demo.repository.MovimientoInventarioRepository;
import com.example.demo.service.MovimientoInventarioService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class MovimientoInventarioServiceImpl implements MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoInventarioRepository;

    @Override
    public List<MovimientoInventario> getAll() {
        return movimientoInventarioRepository.findAll();
    }

    @Override
    public MovimientoInventario getById(String id) {
        return movimientoInventarioRepository.findById(id).orElse(null);
    }

    @Override
    public MovimientoInventario save(MovimientoInventario movimientoInventario) {
        // En un caso real, el save() de un movimiento
        // debería ser llamado desde otro servicio (ej. Compras)
        // y este método calcularía el stock_nuevo.
        // Por ahora, lo guardamos tal cual llega.
        return movimientoInventarioRepository.save(movimientoInventario);
    }

    @Override
    public void delete(String id) {
        // Generalmente, los movimientos de inventario no se borran,
        // se revierten con un movimiento opuesto.
        // Pero seguimos el patrón CRUD:
        movimientoInventarioRepository.deleteById(id);
    }

    @Override
    public MovimientoInventario update(String id, MovimientoInventario movimientoInventario) {
        // Los movimientos de inventario rara vez se actualizan.
        // Pero seguimos el patrón CRUD:
        MovimientoInventario aux = movimientoInventarioRepository.findById(id).orElse(null);

        if (aux != null) {
            aux.setIdProducto(movimientoInventario.getIdProducto());
            aux.setIdAlmacen(movimientoInventario.getIdAlmacen());
            aux.setIdUsuario(movimientoInventario.getIdUsuario());
            aux.setIdTipoMovimiento(movimientoInventario.getIdTipoMovimiento());
            aux.setCantidad(movimientoInventario.getCantidad());
            aux.setStockAnterior(movimientoInventario.getStockAnterior());
            aux.setStockNuevo(movimientoInventario.getStockNuevo());
            aux.setReferencia(movimientoInventario.getReferencia());
            aux.setObservaciones(movimientoInventario.getObservaciones());
            // No actualizamos fechaMovimiento
            return movimientoInventarioRepository.save(aux);
        }
        return null;
    }
}