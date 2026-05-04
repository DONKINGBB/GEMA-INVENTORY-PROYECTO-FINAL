
import api from './api';

export const userService = {
    getProfile: async (id) => {
        try {
            const response = await api.get(`/usuarios/usuario/${id}`);
            return response.data;
        } catch (error) {
            console.error("Error fetching user profile", error);
            throw error;
        }
    },
    
    updateProfile: async (id, profileData) => {
        try {
            const response = await api.put(`/usuarios/${id}/perfil`, profileData);
            return response.data;
        } catch (error) {
            console.error("Error updating user profile", error);
            throw error;
        }
    },
    
    updateNotifications: async (id, notificationSettings) => {
        try {
            const response = await api.put(`/usuarios/${id}/notificaciones`, notificationSettings);
            return response.data;
        } catch (error) {
            console.error("Error updating notification settings", error);
            throw error;
        }
    },
    
    changePassword: async (id, userData) => {
        try {
            // Using the base update endpoint which allows password changes
            const response = await api.put(`/usuarios/usuario/${id}`, userData);
            return response.data;
        } catch (error) {
            console.error("Error changing password", error);
            throw error;
        }
    },

    getTeam: async () => {
        try {
            const response = await api.get('/usuarios/usuario');
            return response.data;
        } catch (error) {
            console.error("Error fetching team", error);
            throw error;
        }
    },

    addUser: async (userData) => {
        try {
            const response = await api.post('/usuarios/usuario', userData);
            return response.data;
        } catch (error) {
            console.error("Error adding team member", error);
            throw error;
        }
    },

    updateUser: async (id, userData) => {
        try {
            const response = await api.put(`/usuarios/usuario/${id}`, userData);
            return response.data;
        } catch (error) {
            console.error("Error updating team member", error);
            throw error;
        }
    },

    deleteUser: async (id) => {
        try {
            await api.delete(`/usuarios/${id}`);
            return true;
        } catch (error) {
            console.error("Error deleting team member", error);
            throw error;
        }
    }
};
