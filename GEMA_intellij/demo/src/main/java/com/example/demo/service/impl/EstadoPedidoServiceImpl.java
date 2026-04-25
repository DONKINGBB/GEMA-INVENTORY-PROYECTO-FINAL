package com.example.demo.service.impl;

import com.example.demo.model.EstadoPedido;
import com.example.demo.repository.EstadoPedidoRepository;
import com.example.demo.service.EstadoPedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstadoPedidoServiceImpl implements EstadoPedidoService {

    private final EstadoPedidoRepository estadoPedidoRepository;

    @Override
    public List<EstadoPedido> getAll() {
        return estadoPedidoRepository.findAll();
    }

    @Override
    public EstadoPedido getById(Integer id) {
        Optional<EstadoPedido> estado = estadoPedidoRepository.findById(id);
        return estado.orElse(null);
    }

    @Override
    public EstadoPedido save(EstadoPedido estadoPedido) {
        return estadoPedidoRepository.save(estadoPedido);
    }

    @Override
    public EstadoPedido update(Integer id, EstadoPedido estadoPedido) {
        if (!estadoPedidoRepository.existsById(id)) {
            return null;
        }
        estadoPedido.setId(id);
        return estadoPedidoRepository.save(estadoPedido);
    }

    @Override
    public void delete(Integer id) {
        estadoPedidoRepository.deleteById(id);
    }
}
