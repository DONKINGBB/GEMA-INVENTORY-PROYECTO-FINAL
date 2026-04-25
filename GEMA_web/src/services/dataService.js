
import api from './api';

export const orderService = {
    getAll: async (userId) => {
        try {
            const response = await api.get(`/pedidos?userId=${userId}`);
            return response.data;
        } catch (error) {
            console.error("Error fetching orders", error);
            throw error;
        }
    },

    // Additional methods based on controller
    create: async (orderData, userId) => {
        try {
            const response = await api.post(`/pedidos?userId=${userId}`, orderData);
            return response.data;
        } catch (error) {
            console.error("Error creating order", error);
            throw error;
        }
    },

    markDelivered: async (id) => {
        try {
            await api.put(`/pedidos/${id}/entregado`);
        } catch (error) {
            console.error("Error updating order status", error);
            throw error;
        }
    },

    delete: async (id) => {
        try {
            await api.delete(`/pedidos/${id}`);
        } catch (error) {
            console.error("Error deleting order", error);
            throw error;
        }
    }
};

export const clientService = {
    getAll: async (userId) => {
        try {
            // Controller expects userId param: /api/clientes?userId=...
            const response = await api.get(`/clientes?userId=${userId}`);
            return response.data;
        } catch (error) {
            console.error("Error fetching clients", error);
            throw error;
        }
    },

    create: async (clientData, userId) => {
        try {
            const response = await api.post(`/clientes?userId=${userId}`, clientData);
            return response.data;
        } catch (error) {
            console.error("Error creating client", error);
            throw error;
        }
    },

    update: async (id, clientData) => {
        try {
            // Controller expects PUT /api/clientes/{id} with body
            const response = await api.put(`/clientes/${id}`, clientData);
            return response.data;
        } catch (error) {
            console.error("Error updating client", error);
            throw error;
        }
    },

    delete: async (id) => {
        try {
            await api.delete(`/clientes/${id}`);
        } catch (error) {
            console.error("Error deleting client", error);
            throw error;
        }
    }
};
export const warehouseService = {
    getAll: async (userId) => {
        try {
            const response = await api.get(`/catalogos/almacenes?userId=${userId}`);
            return response.data;
        } catch (error) {
            console.error("Error fetching warehouses", error);
            throw error;
        }
    }
};

export const inventoryService = {
    getProductsByWarehouse: async (userId, warehouseId) => {
        try {
            const response = await api.get(`/inventario/seleccion-por-almacen?userId=${userId}&almacenId=${warehouseId}`);
            return response.data;
        } catch (error) {
            console.error("Error fetching products by warehouse", error);
            throw error;
        }
    }
};
