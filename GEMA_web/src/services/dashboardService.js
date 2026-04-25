import api from './api';

export const dashboardService = {
    getSummary: async (userId) => {
        try {
            const response = await api.get(`/dashboard/summary?userId=${userId}`);
            return response.data;
        } catch (error) {
            console.error("Error fetching dashboard summary", error);
            throw error;
        }
    }
};
