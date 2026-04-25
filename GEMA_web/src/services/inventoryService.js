import api from './api';

export const inventoryMovementService = {
    getAll: async () => {
        try {
            const response = await api.get('/movimiento-inventario');
            return response.data;
        } catch (error) {
            console.error("Error fetching inventory movements", error);
            throw error;
        }
    },
    create: async (data) => {
        try {
            const response = await api.post('/movimiento-inventario', data);
            return response.data;
        } catch (error) {
            console.error("Error creating inventory movement", error);
            throw error;
        }
    }
};

export const categoryService = {
    getAll: async (userId) => {
        try {
            const endpoint = userId ? `/catalogos/categorias?userId=${userId}` : '/catalogos/categorias';
            const response = await api.get(endpoint);
            return response.data;
        } catch (error) {
            console.error("Error fetching categories", error);
            throw error;
        }
    },
    create: async (data, userId) => {
        try {
            const endpoint = userId ? `/catalogos/categorias?userId=${userId}` : '/catalogos/categorias';
            const response = await api.post(endpoint, data);
            return response.data;
        } catch (error) {
            console.error("Error creating category", error);
            throw error;
        }
    },
    update: async (id, data) => {
        try {
            const response = await api.put(`/catalogos/categorias/${id}`, data);
            return response.data;
        } catch (error) {
            console.error("Error updating category", error);
            throw error;
        }
    },
    delete: async (id) => {
        try {
            await api.delete(`/catalogos/categorias/${id}`);
        } catch (error) {
            console.error("Error deleting category", error);
            throw error;
        }
    }
};

export const warehouseService = {
    getAll: async (userId) => {
        try {
            const endpoint = userId ? `/catalogos/almacenes?userId=${userId}` : '/catalogos/almacenes';
            const response = await api.get(endpoint);
            return response.data;
        } catch (error) {
            console.error("Error fetching warehouses", error);
            throw error;
        }
    },
    create: async (data, userId) => {
        try {
            const endpoint = userId ? `/catalogos/almacenes?userId=${userId}` : '/catalogos/almacenes';
            const response = await api.post(endpoint, data);
            return response.data;
        } catch (error) {
            console.error("Error creating warehouse", error);
            throw error;
        }
    },
    update: async (id, data) => {
        try {
            const response = await api.put(`/catalogos/almacenes/${id}`, data);
            return response.data;
        } catch (error) {
            console.error("Error updating warehouse", error);
            throw error;
        }
    },
    delete: async (id) => {
        try {
            await api.delete(`/catalogos/almacenes/${id}`);
        } catch (error) {
            console.error("Error deleting warehouse", error);
            throw error;
        }
    }
};
