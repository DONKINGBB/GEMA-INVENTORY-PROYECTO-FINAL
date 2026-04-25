package com.example.demo.service.impl;

import com.example.demo.dto.DashboardStats;
import com.example.demo.repository.BalanceFinancieroRepository;
import com.example.demo.repository.FacturaRepository;
import com.example.demo.repository.InventarioRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Esto crea el constructor automáticamente para inyectar los repos
public class DashboardServiceImpl implements DashboardService {

    private final InventarioRepository inventarioRepo;
    private final PedidoRepository pedidoRepo;
    private final FacturaRepository facturaRepo;
    private final BalanceFinancieroRepository balanceRepo;

    @Override
    public DashboardStats obtenerEstadisticas() {
        // ... (tu código de inventario, pedidos, facturas sigue igual) ...

        // 4. Beneficio Mes
        // Double beneficioMes = balanceRepo.calcularBeneficioMesActual(); // <--- COMENTA ESTO
        Double beneficioMes = 0.0; // <--- PON ESTO TEMPORALMENTE

        return DashboardStats.builder()
                // ...
                .beneficio_mes(beneficioMes)
                .build();
    }
}