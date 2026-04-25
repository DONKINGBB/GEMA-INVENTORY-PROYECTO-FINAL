package com.example.demo.service.impl;

import com.example.demo.dto.DashboardStats;
import com.example.demo.repository.FacturaRepository;
import com.example.demo.repository.InventarioRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final InventarioRepository inventarioRepo;
    private final PedidoRepository pedidoRepo;
    private final FacturaRepository facturaRepo;
    private final com.example.demo.repository.CompraRepository compraRepo;
    private final com.example.demo.service.TenancyService tenancyService;

    @Override
    public DashboardStats obtenerEstadisticas(String userId) {
        String nId = tenancyService.resolveNegocioId(userId);
        
        Double valorInventario = inventarioRepo.calcularValorTotal(nId);
        if (valorInventario == null) valorInventario = 0.0;

        long pendientesLong = pedidoRepo.countByIdEstadoAndIdNegocio(1, nId);
        Integer pedidosPendientes = (int) pendientesLong;

        Integer lowStock = inventarioRepo.countLowStock(nId);
        if (lowStock == null) lowStock = 0;

        java.time.LocalDate now = java.time.LocalDate.now();
        int mes = now.getMonthValue();
        int anio = now.getYear();

        Double ingresosMes = pedidoRepo.sumTotalByEstadoAndMonthAndYear(2, mes, anio, nId);
        if (ingresosMes == null) ingresosMes = 0.0;
        
        Double gastosMes = compraRepo.sumTotalByMonthAndYear(mes, anio, nId);
        if (gastosMes == null) gastosMes = 0.0;

        Double beneficioMes = ingresosMes - gastosMes;

        return DashboardStats.builder()
                .valor_inventario(valorInventario)
                .pedidos_pendientes(pedidosPendientes)
                .productos_bajo_stock(lowStock)
                .beneficio_mes(beneficioMes)
                .build();
    }
}