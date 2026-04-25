
import { useState, useEffect } from 'react';
import { X, Save, Loader } from 'lucide-react';
import { categoryService, warehouseService } from '../services/inventoryService';
import { useAuth } from '../context/AuthContext';

export default function ProductForm({ isOpen, onClose, onSubmit, initialData, title }) {
    const { user } = useAuth();
    const [categories, setCategories] = useState([]);
    const [warehouses, setWarehouses] = useState([]);
    const [formData, setFormData] = useState({
        nombre: '',
        sku: '',
        categoria: '', // Use 'categoria' instead of 'idCategoria'
        idAlmacen: '',
        cantidad: 0,
        precioCompra: 0,
        precioVenta: 0,
        stockMinimo: 5,
        descripcion: ''
    });
    const [loading, setLoading] = useState(false);
    const [loadingData, setLoadingData] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        const loadDependencies = async () => {
            setLoadingData(true);
            try {
                const [cats, whs] = await Promise.all([
                    categoryService.getAll(user?.id),
                    warehouseService.getAll(user?.id)
                ]);
                setCategories(cats || []);
                setWarehouses(whs || []);
            } catch (err) {
                console.error("Error loading dependencies", err);
            } finally {
                setLoadingData(false);
            }
        };
        if (isOpen) {
            loadDependencies();
        }
    }, [isOpen]);

    useEffect(() => {
        if (initialData) {
            setFormData({
                nombre: initialData.nombre || '',
                sku: initialData.sku || '',
                categoria: initialData.categoria || '', // Use the string name
                idAlmacen: initialData.idAlmacen || '',
                cantidad: initialData.cantidad || 0,
                precioCompra: initialData.precioCompra || 0,
                precioVenta: initialData.precioVenta || 0,
                stockMinimo: initialData.stockMinimo || 5,
                descripcion: initialData.descripcion || ''
            });
        } else {
            setFormData({
                nombre: '',
                sku: '',
                categoria: '',
                idAlmacen: '',
                cantidad: 0,
                precioCompra: 0,
                precioVenta: 0,
                stockMinimo: 5,
                descripcion: ''
            });
        }
        setError('');
    }, [initialData, isOpen]);

    if (!isOpen) return null;

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            await onSubmit(formData);
            onClose();
        } catch (err) {
            console.error(err);
            setError('Error al guardar el producto. Verifique los datos.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4 animate-in fade-in duration-200">
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto">
                <div className="flex justify-between items-center p-6 border-b border-gray-100">
                    <h2 className="text-xl font-bold text-gray-900">{title || 'Producto'}</h2>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition p-2 hover:bg-gray-100 rounded-full">
                        <X size={24} />
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="p-6 space-y-6">
                    {error && (
                        <div className="bg-red-50 text-red-600 p-3 rounded-lg text-sm">
                            {error}
                        </div>
                    )}

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        {/* Nombre */}
                        <div className="md:col-span-2">
                            <label className="block text-sm font-medium text-gray-700 mb-1">Nombre del Producto</label>
                            <input
                                type="text"
                                name="nombre"
                                value={formData.nombre}
                                onChange={handleChange}
                                required
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition"
                                placeholder="Ej: Laptop HP Pavilion"
                            />
                        </div>

                        {/* SKU */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">SKU (Código)</label>
                            <input
                                type="text"
                                name="sku"
                                value={formData.sku}
                                onChange={handleChange}
                                required
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition"
                                placeholder="Ej: LP-001"
                            />
                        </div>

                        {/* Categoría */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Categoría</label>
                            <select
                                name="categoria"
                                value={formData.categoria}
                                onChange={handleChange}
                                required
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition"
                            >
                                <option value="">Seleccione una categoría</option>
                                {categories.map(c => (
                                    <option key={c.idCategoria || c.id} value={c.nombre}>{c.nombre}</option>
                                ))}
                            </select>
                        </div>

                        {/* Almacén/Proveedor */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Almacén/Proveedor</label>
                            <select
                                name="idAlmacen"
                                value={formData.idAlmacen}
                                onChange={handleChange}
                                required
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition"
                            >
                                <option value="">Seleccione un almacén</option>
                                {warehouses.map(w => (
                                    <option key={w.idAlmacen || w.id} value={w.idAlmacen || w.id}>{w.nombre}</option>
                                ))}
                            </select>
                        </div>

                        {/* Cantidad */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Cantidad Actual</label>
                            <input
                                type="number"
                                name="cantidad"
                                value={formData.cantidad}
                                onChange={handleChange}
                                min="0"
                                required
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition"
                            />
                        </div>

                        {/* Stock Mínimo */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Stock Mínimo (Alerta)</label>
                            <input
                                type="number"
                                name="stockMinimo"
                                value={formData.stockMinimo}
                                onChange={handleChange}
                                min="0"
                                required
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition"
                            />
                        </div>

                        {/* Precio Compra */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Precio Compra ($)</label>
                            <input
                                type="number"
                                name="precioCompra"
                                value={formData.precioCompra}
                                onChange={handleChange}
                                min="0"
                                step="0.01"
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition"
                            />
                        </div>

                        {/* Precio Venta */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Precio Venta ($)</label>
                            <input
                                type="number"
                                name="precioVenta"
                                value={formData.precioVenta}
                                onChange={handleChange}
                                min="0"
                                step="0.01"
                                required
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition"
                            />
                        </div>

                        {/* Descripción */}
                        <div className="md:col-span-2">
                            <label className="block text-sm font-medium text-gray-700 mb-1">Descripción</label>
                            <textarea
                                name="descripcion"
                                value={formData.descripcion}
                                onChange={handleChange}
                                rows="3"
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition resize-none"
                                placeholder="Detalles adicionales del producto..."
                            />
                        </div>
                    </div>

                    <div className="flex justify-end gap-3 pt-4 border-t border-gray-100">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-6 py-2 border border-gray-300 rounded-lg text-gray-700 font-medium hover:bg-gray-50 transition"
                        >
                            Cancelar
                        </button>
                        <button
                            type="submit"
                            disabled={loading}
                            className="px-6 py-2 bg-primary text-white rounded-lg font-medium hover:bg-primary-dark transition shadow-lg disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
                        >
                            {loading && <Loader size={18} className="animate-spin" />}
                            {loading ? 'Guardando...' : 'Guardar Producto'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
