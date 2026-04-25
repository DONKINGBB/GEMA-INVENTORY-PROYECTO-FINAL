package com.example.demo.service.impl;

import com.example.demo.model.CategoriaProducto;
import com.example.demo.repository.CategoriaProductoRepository;
import com.example.demo.service.CategoriaProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class CategoriaProductoServiceImpl implements CategoriaProductoService {

    private final CategoriaProductoRepository categoriaProductoRepository;

    @Override
    public List<CategoriaProducto> getAll() {
        return categoriaProductoRepository.findAll();
    }

    @Override
    public CategoriaProducto getById(Integer id) { // <-- CAMBIO: Integer
        return categoriaProductoRepository.findById(id).orElse(null);
    }

    @Override
    public CategoriaProducto save(CategoriaProducto categoriaProducto) {
        return categoriaProductoRepository.save(categoriaProducto);
    }

    @Override
    public void delete(Integer id) { // <-- CAMBIO: Integer
        categoriaProductoRepository.deleteById(id);
    }

    @Override
    public CategoriaProducto update(Integer id, CategoriaProducto categoriaProducto) { // <-- CAMBIO: Integer
        CategoriaProducto aux = categoriaProductoRepository.findById(id).orElse(null);

        if (aux != null) {
            aux.setNombre(categoriaProducto.getNombre());
            return categoriaProductoRepository.save(aux);
        }
        return null;
    }
}