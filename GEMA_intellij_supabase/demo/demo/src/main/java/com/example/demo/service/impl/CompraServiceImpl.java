package com.example.demo.service.impl;

import com.example.demo.model.Compra;
import com.example.demo.repository.CompraRepository;
import com.example.demo.service.CompraService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;

    @Override
    public List<Compra> getAll() {
        return compraRepository.findAll();
    }

    @Override
    public Compra getById(String id) {
        return compraRepository.findById(id).orElse(null);
    }

    @Override
    public Compra save(Compra compra) {
        return compraRepository.save(compra);
    }

    @Override
    public void delete(String id) {
        compraRepository.deleteById(id);
    }

    @Override
    public Compra update(String id, Compra compra) {
        Compra aux = compraRepository.findById(id).orElse(null);

        if (aux != null) {
            aux.setIdProveedor(compra.getIdProveedor());
            aux.setIdUsuario(compra.getIdUsuario());
            aux.setIdAlmacenDestino(compra.getIdAlmacenDestino());
            aux.setTotal(compra.getTotal());
            // No actualizamos fechaCompra, es automática
            return compraRepository.save(aux);
        }
        return null;
    }
}