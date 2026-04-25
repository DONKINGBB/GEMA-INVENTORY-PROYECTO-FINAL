package com.example.demo.service.impl; // O el paquete donde lo tengas

import com.example.demo.model.DetallePedido;
import com.example.demo.repository.DetallePedidoRepository;
import com.example.demo.service.DetallePedidoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
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
    public DetallePedido save(DetallePedido detallePedido) {
        return detallePedidoRepository.save(detallePedido);
    }

    @Override
    public DetallePedido update(String id, DetallePedido detallePedido) {
        // Verificamos si existe antes de actualizar
        if (detallePedidoRepository.existsById(id)) {
            // MUY IMPORTANTE: Asignamos el ID al objeto para que JPA sepa que es una actualización
            detallePedido.setIdDetalle(Integer.valueOf(id));
            return detallePedidoRepository.save(detallePedido);
        }
        return null;
    }

    @Override
    public void delete(String id) {
        detallePedidoRepository.deleteById(id);
    }
}