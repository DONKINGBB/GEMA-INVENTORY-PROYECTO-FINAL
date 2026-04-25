package com.example.demo.service.impl;

import com.example.demo.model.Proveedor;
import com.example.demo.repository.ProveedorRepository;
import com.example.demo.service.ProveedorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    @Override
    public List<Proveedor> getAll() {
        return proveedorRepository.findAll();
    }

    @Override
    public Proveedor getById(String id) {
        return proveedorRepository.findById(id).orElse(null);
    }

    @Override
    public Proveedor save(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    @Override
    public void delete(String id) {
        proveedorRepository.deleteById(id);
    }

    @Override
    public Proveedor update(String id, Proveedor proveedor) {
        Proveedor aux = proveedorRepository.findById(id).orElse(null);

        if (aux != null) {
            aux.setNombre(proveedor.getNombre());
            aux.setContacto(proveedor.getContacto());
            aux.setDireccion(proveedor.getDireccion());
            // No actualizamos fechaRegistro, es automática
            return proveedorRepository.save(aux);
        }
        return null;
    }
}