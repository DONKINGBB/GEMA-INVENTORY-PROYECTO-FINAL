package com.example.demo.service.impl;

import com.example.demo.model.DetallePedido;
import com.example.demo.repository.DetallePedidoRepository;
import com.example.demo.service.DetallePedidoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class DetallePedidoServiceImpl implements DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;

    @Override
    public List<DetallePedido> getAll() {
        return detallePedidoRepository.findAll();
    }

    @Override
    public DetallePedido getById(String id) {
        return detallePedidoRepository.findById(id).orElse(null);
    }

    @Override
    public DetallePedido save(DetallePedido detalle) {
        return detallePedidoRepository.save(detalle);
    }

    @Override
    public void delete(String id) {
        detallePedidoRepository.deleteById(id);
    }

    @Override
    public DetallePedido update(String id, DetallePedido detalle) {
        DetallePedido existente = detallePedidoRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setIdPedido(detalle.getIdPedido());
            existente.setIdProducto(detalle.getIdProducto());
            existente.setCantidad(detalle.getCantidad());
            existente.setPrecioUnitario(detalle.getPrecioUnitario());
            return detallePedidoRepository.save(existente);
        }
        return null;
    }
}
