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
    // Calcula: Suma de (Precio Venta del Producto * Cantidad en Inventario) filtrado por usuario
    // Calcula: Suma de (Precio Venta del Producto * Cantidad en Inventario) filtrado por usuario
    // Calcula: Suma de (Precio Venta del Producto * Cantidad en Inventario) filtrado por usuario (Dueño del Producto)
    // Calcula: Suma de (Precio Venta del Producto * Cantidad en Inventario) filtrado por Negocio
    @Query("SELECT COALESCE(SUM(COALESCE(i.producto.precioVenta, 0.0) * COALESCE(i.cantidadActual, 0)), 0.0) FROM Inventario i WHERE i.producto.idNegocio = :idNegocio")
    Double calcularValorTotal(String idNegocio);

    // Contar productos con stock bajo (cantidad <= stock minimo, default 5)
    @Query("SELECT COUNT(i) FROM Inventario i WHERE i.producto.idNegocio = :idNegocio AND i.cantidadActual <= COALESCE(i.producto.stockMinimo, 5)")
    Integer countLowStock(String idNegocio);

    List<Inventario> findByProductoIdNegocio(String idNegocio);

    // Buscar por Almacén y Negocio con Stock > 0
    List<Inventario> findByAlmacenIdAlmacenAndAlmacenIdNegocioAndCantidadActualGreaterThan(Integer idAlmacen, String idNegocio, Integer cantidad);
    
    // Nuevo: Buscar Inventario específico para restar stock
    java.util.Optional<Inventario> findByProductoIdProductoAndAlmacenIdAlmacen(String idProducto, Integer idAlmacen);
    
    // Buscar todos los inventarios de un producto (para saber en qué almacén está)
    List<Inventario> findByProductoIdProducto(String idProducto);

    void deleteByProductoIdProducto(String idProducto);
}