package com.example.demo.service.impl;

import com.example.demo.model.DetalleCompra;
import com.example.demo.repository.DetalleCompraRepository;
import com.example.demo.service.DetalleCompraService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class DetalleCompraServiceImpl implements DetalleCompraService {

    private final DetalleCompraRepository detalleCompraRepository;

    @Override
    public List<DetalleCompra> getAll() {
        return detalleCompraRepository.findAll();
    }

    @Override
    public DetalleCompra getById(String id) {
        return detalleCompraRepository.findById(id).orElse(null);
    }

    @Override
    public DetalleCompra save(DetalleCompra detalleCompra) {
        return detalleCompraRepository.save(detalleCompra);
    }

    @Override
    public void delete(String id) {
        detalleCompraRepository.deleteById(id);
    }

    @Override
    public DetalleCompra update(String id, DetalleCompra detalleCompra) {
        DetalleCompra aux = detalleCompraRepository.findById(id).orElse(null);

        if (aux != null) {
            aux.setIdCompra(detalleCompra.getIdCompra());
            aux.setIdProducto(detalleCompra.getIdProducto());
            aux.setCantidad(detalleCompra.getCantidad());
            aux.setPrecioCosto(detalleCompra.getPrecioCosto());
            return detalleCompraRepository.save(aux);
        }
        return null;
    }
}