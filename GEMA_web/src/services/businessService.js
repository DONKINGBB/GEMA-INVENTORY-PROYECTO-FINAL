
import api from './api';

export const businessService = {
    getMiNegocio: async () => {
        try {
            const response = await api.get('/negocios/mi-negocio');
            return response.data;
        } catch (error) {
            console.error("Error fetching business info", error);
            throw error;
        }
    },
    
    getMisNegocios: async () => {
        try {
            const response = await api.get('/negocios/mis-negocios');
            return response.data;
        } catch (error) {
            console.error("Error fetching my businesses list", error);
            throw error;
        }
    },
    
    createNegocio: async (businessData) => {
        try {
            const response = await api.post('/negocios/create', businessData);
            return response.data;
        } catch (error) {
            console.error("Error creating business", error);
            throw error;
        }
    },
    
    joinNegocio: async (data) => {
        try {
            const response = await api.post('/negocios/join', data);
            return response.data;
        } catch (error) {
            console.error("Error joining business", error);
            throw error;
        }
    },
    
    switchNegocio: async (id) => {
        try {
            const response = await api.post(`/negocios/switch/${id}`);
            return response.data;
        } catch (error) {
            console.error("Error switching business", error);
            throw error;
        }
    },
    
    updateNegocio: async (id, businessData) => {
        try {
            const response = await api.put(`/negocios/${id}`, businessData);
            return response.data;
        } catch (error) {
            console.error("Error updating business info", error);
            throw error;
        }
    }
};
