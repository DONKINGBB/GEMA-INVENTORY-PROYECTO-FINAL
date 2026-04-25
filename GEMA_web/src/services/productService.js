import api from './api';

export const productService = {
    getAll: async (userId) => {
        try {
            const response = await api.get(`/inventario?userId=${userId}`);
            return response.data.map(inv => ({
                id: inv.idProducto,
                idProducto: inv.idProducto,
                idInventario: inv.idInventario,
                nombre: inv.nombreProducto,
                sku: inv.sku,
                cantidad: inv.cantidadActual,
                precioCompra: inv.precioCompra,
                precioVenta: inv.precioVenta,
                stockMinimo: inv.stockMinimo,
                categoria: inv.categoria,
                descripcion: inv.descripcion,
                idAlmacen: inv.idAlmacen
            }));
        } catch (error) {
            console.error("Error fetching products", error);
            throw error;
        }
    },

    create: async (productData) => {
        try {
            const response = await api.post('/productos', productData);
            return response.data;
        } catch (error) {
            console.error("Error creating product", error);
            throw error;
        }
    },

    update: async (id, productData) => {
        try {
            const response = await api.put(`/productos/${id}`, productData);
            return response.data;
        } catch (error) {
            console.error("Error updating product", error);
            throw error;
        }
    },

    delete: async (id) => {
        try {
            await api.delete(`/productos/${id}`);
        } catch (error) {
            console.error("Error deleting product", error);
            throw error;
        }
    }
};
