package com.example.demo.dto;

import com.example.demo.model.Cliente;
import com.example.demo.model.Pedido;
import java.util.List;

public class SyncPayloadDto {
    private List<ProductoDto> productos;
    private List<Pedido> pedidos;
    private List<Cliente> clientes;

    // Getters and Setters
    public List<ProductoDto> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoDto> productos) {
        this.productos = productos;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }
}
