import api from './api';

export const productService = {
    getAll: async (userId) => {
        try {
            const response = await api.get(`/inventario?userId=${userId}`);
            return response.data.map(inv => ({
                id: inv.idProducto || inv.id,
                idProducto: inv.idProducto || inv.id,
                idInventario: inv.idInventario,
                nombre: inv.nombreProducto || inv.nombre,
                sku: inv.sku,
                cantidad: inv.cantidadActual || inv.cantidad || 0,
                precioCompra: inv.precioCompra || 0,
                precioVenta: inv.precioVenta || 0,
                stockMinimo: inv.stockMinimo || 0,
                categoria: inv.categoria,
                descripcion: inv.descripcion,
                idAlmacen: inv.idAlmacen,
                imagenUrl: inv.imagenUrl || inv.imagen_url || ''
            }));
        } catch (error) {
            console.error("Error fetching products", error);
            throw error;
        }
    },

    create: async (productData) => {
        try {
            const mappedData = {
                ...productData,
                nombreProducto: productData.nombre,
                cantidadActual: productData.cantidad,
                imagen_url: productData.imagenUrl
            };
            const response = await api.post('/productos', mappedData);
            return response.data;
        } catch (error) {
            console.error("Error creating product", error);
            throw error;
        }
    },

    update: async (id, productData) => {
        try {
            const mappedData = {
                ...productData,
                nombreProducto: productData.nombre,
                cantidadActual: productData.cantidad,
                imagen_url: productData.imagenUrl
            };
            const response = await api.put(`/productos/${id}`, mappedData);
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
