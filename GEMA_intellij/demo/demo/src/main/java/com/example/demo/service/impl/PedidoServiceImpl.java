package com.example.demo.service.impl;

import com.example.demo.model.Pedido;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.service.PedidoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;

    @Override
    public List<Pedido> getAll() {
        return pedidoRepository.findAll();
    }

    @Override
    public Pedido getById(String id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    @Override
    public Pedido save(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Override
    public void delete(String id) {
        pedidoRepository.deleteById(id);
    }

    @Override
    public Pedido update(String id, Pedido pedido) {
        Pedido existente = pedidoRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setIdCliente(pedido.getIdCliente());
            existente.setIdUsuario(pedido.getIdUsuario());
            existente.setIdAlmacenOrigen(pedido.getIdAlmacenOrigen());
            existente.setFechaPedido(pedido.getFechaPedido());
            existente.setIdEstado(pedido.getIdEstado());
            existente.setTotal(pedido.getTotal());
            return pedidoRepository.save(existente);
        }
        return null;
    }
}
