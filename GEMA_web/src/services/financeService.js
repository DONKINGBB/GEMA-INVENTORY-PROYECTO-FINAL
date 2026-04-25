import api from './api';

export const financeService = {
    getAll: async () => {
        try {
            const response = await api.get('/balance');
            return response.data;
        } catch (error) {
            console.error("Error fetching finances", error);
            throw error;
        }
    },

    getById: async (id) => {
        try {
            const response = await api.get(`/balance/${id}`);
            return response.data;
        } catch (error) {
            console.error("Error fetching finance record", error);
            throw error;
        }
    },

    create: async (data) => {
        try {
            const response = await api.post('/balance', data);
            return response.data;
        } catch (error) {
            console.error("Error creating finance record", error);
            throw error;
        }
    },

    update: async (id, data) => {
        try {
            const response = await api.put(`/balance/${id}`, data);
            return response.data;
        } catch (error) {
            console.error("Error updating finance record", error);
            throw error;
        }
    },

    delete: async (id) => {
        try {
            await api.delete(`/balance/${id}`);
        } catch (error) {
            console.error("Error deleting finance record", error);
            throw error;
        }
    }
};
