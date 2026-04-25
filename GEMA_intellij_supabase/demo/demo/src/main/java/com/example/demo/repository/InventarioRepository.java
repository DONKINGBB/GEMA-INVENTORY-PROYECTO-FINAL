package com.example.demo.repository;

import com.example.demo.model.Inventario; // Asegúrate de que tu entidad se llame así
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, String> {

    // Calcula: Suma de (Precio Venta del Producto * Cantidad en Inventario)
    // COALESCE se usa para devolver 0 si la tabla está vacía en lugar de NULL
    @Query("SELECT COALESCE(SUM(i.producto.precioVenta * i.cantidadActual), 0) FROM Inventario i")
    Double calcularValorTotal();
    List<Inventario> findByProductoIdUsuario(String idUsuario);
}