
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
    }
};
