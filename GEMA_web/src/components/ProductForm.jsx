
import { useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import { X, Save, Loader, Camera, Plus, Image as ImageIcon } from 'lucide-react';
import { motion } from 'framer-motion';
import { categoryService, warehouseService } from '../services/inventoryService';
import { uploadService } from '../services/uploadService';
import { useAuth } from '../context/AuthContext';

export default function ProductForm({ isOpen, onClose, onSubmit, initialData, title }) {
    const { user } = useAuth();
    const [categories, setCategories] = useState([]);
    const [warehouses, setWarehouses] = useState([]);
    const [formData, setFormData] = useState({
        nombre: '',
        sku: '',
        categoria: '',
        idAlmacen: '',
        cantidad: 0,
        precioCompra: 0,
        precioVenta: 0,
        stockMinimo: 5,
        descripcion: '',
        imagenUrl: ''
    });
    const [loading, setLoading] = useState(false);
    const [loadingData, setLoadingData] = useState(false);
    const [uploading, setUploading] = useState(false);
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
                nombre: initialData.nombre || initialData.nombreProducto || '',
                sku: initialData.sku || '',
                categoria: initialData.categoria || '',
                idAlmacen: initialData.idAlmacen || '',
                cantidad: initialData.cantidad ?? initialData.cantidadActual ?? 0,
                precioCompra: initialData.precioCompra || 0,
                precioVenta: initialData.precioVenta || 0,
                stockMinimo: initialData.stockMinimo || 5,
                descripcion: initialData.descripcion || '',
                imagenUrl: initialData.imagenUrl || initialData.imagen_url || ''
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
                descripcion: '',
                imagenUrl: ''
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

    const handleFileUpload = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        setUploading(true);
        setError('');
        try {
            const data = await uploadService.uploadImage(file, 'products');
            if (data && data.url) {
                setFormData(prev => ({ ...prev, imagenUrl: data.url }));
            } else {
                setError('Error al subir la imagen.');
            }
        } catch (err) {
            console.error('Upload error:', err);
            setError('Error de conexión al subir la imagen.');
        } finally {
            setUploading(false);
        }
    };

    const handleSubmit = async (e) => {
        if (e) e.preventDefault();
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

    if (!isOpen) return null;

    return createPortal(
        <motion.div 
            initial={{ opacity: 0, y: 100 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 100 }}
            transition={{ type: 'spring', damping: 25, stiffness: 200 }}
            className="fixed inset-0 top-0 left-0 right-0 bottom-0 z-[10000] bg-white dark:bg-[#020617] flex flex-col overflow-hidden"
        >
            <div className="flex-1 flex flex-col max-w-5xl mx-auto w-full h-full relative overflow-hidden bg-white dark:bg-[#020617]">
                
                {/* Header with glass effect */}
                <div className="flex justify-between items-center p-8 border-b border-gray-100 dark:border-slate-800 bg-white/50 dark:bg-slate-900/50 backdrop-blur-md sticky top-0 z-10">
                    <div>
                        <h2 className="text-3xl font-black text-gray-900 dark:text-white tracking-tight">
                            {initialData ? 'Editar Producto' : 'Nuevo Producto'}
                        </h2>
                        <p className="text-gray-500 dark:text-slate-400 text-sm font-medium">Completa los detalles del inventario</p>
                    </div>
                    <button 
                        onClick={onClose} 
                        className="text-gray-400 hover:text-gray-900 dark:hover:text-white transition-all p-3 hover:bg-gray-100 dark:hover:bg-slate-800 rounded-2xl active:scale-90"
                    >
                        <X size={24} />
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-8 custom-scrollbar">
                    {error && (
                        <div className="bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 p-4 rounded-2xl text-sm font-bold border border-red-100 dark:border-red-900/30 mb-6 flex items-center gap-3 animate-in shake duration-500">
                            <div className="w-2 h-2 bg-red-600 rounded-full animate-pulse" />
                            {error}
                        </div>
                    )}

                    <div className="space-y-8">
                        {/* Image Upload Section - Premium Style */}
                        <div className="flex flex-col md:flex-row gap-10 items-start bg-slate-50 dark:bg-slate-900/40 p-8 rounded-[2.5rem] border border-slate-100 dark:border-slate-800/50">
                            <div className="relative group flex-shrink-0">
                                <div className="w-48 h-48 rounded-[2.5rem] overflow-hidden bg-white dark:bg-slate-800 border-4 border-white dark:border-slate-700 shadow-2xl flex items-center justify-center transition-all group-hover:scale-[1.02] duration-500">
                                    {formData.imagenUrl ? (
                                        <img src={formData.imagenUrl} alt="Product" className="w-full h-full object-cover" />
                                    ) : (
                                        <div className="text-slate-200 dark:text-slate-700 flex flex-col items-center">
                                            <ImageIcon size={48} className="mb-3 opacity-20" />
                                            <span className="text-[10px] font-black uppercase tracking-[0.3em] opacity-40">Sin Imagen</span>
                                        </div>
                                    )}
                                    {uploading && (
                                        <div className="absolute inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center">
                                            <Loader className="w-8 h-8 text-white animate-spin" />
                                        </div>
                                    )}
                                </div>
                                <div className="absolute -bottom-3 -right-3 flex gap-2">
                                    <div className="relative group/btn">
                                        <input 
                                            type="file" 
                                            accept="image/*"
                                            onChange={handleFileUpload}
                                            className="absolute inset-0 w-full h-full opacity-0 cursor-pointer z-10"
                                            disabled={uploading}
                                        />
                                        <div className="p-4 bg-primary text-white rounded-2xl shadow-xl shadow-primary/40 hover:scale-110 group-hover/btn:rotate-12 transition-all cursor-pointer">
                                            <Plus size={24} />
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div className="flex-1 space-y-6 w-full pt-2">
                                <div className="flex flex-col items-center justify-center p-8 border-2 border-dashed border-slate-200 dark:border-white/5 rounded-[2.5rem] bg-slate-50 dark:bg-white/[0.02] hover:bg-slate-100 dark:hover:bg-white/[0.04] transition-all group/upload relative overflow-hidden">
                                    <input
                                        type="file"
                                        onChange={handleFileUpload}
                                        className="absolute inset-0 opacity-0 cursor-pointer z-10"
                                        accept="image/*"
                                    />
                                    <div className="w-16 h-16 bg-primary/10 rounded-2xl flex items-center justify-center text-primary mb-4 group-hover/upload:scale-110 transition-transform">
                                        <Plus size={32} />
                                    </div>
                                    <p className="text-[10px] font-black uppercase tracking-[0.2em] text-slate-400 dark:text-white/30">Subir Nueva Imagen</p>
                                </div>
                            </div>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-6">
                            <div className="space-y-6 md:col-span-2">
                                <div className="group">
                                    <label className="block text-xs font-black text-gray-400 uppercase tracking-widest mb-2 px-1 transition-colors group-focus-within:text-primary">Nombre del Producto</label>
                                    <input
                                        type="text"
                                        name="nombre"
                                        value={formData.nombre}
                                        onChange={handleChange}
                                        required
                                        className="w-full px-5 py-4 bg-gray-50 dark:bg-slate-900 border border-transparent focus:border-primary/30 rounded-2xl focus:ring-4 focus:ring-primary/5 outline-none transition-all text-gray-900 dark:text-white font-bold text-lg"
                                        placeholder="Ej: Laptop HP Pavilion 15"
                                    />
                                </div>
                            </div>

                            <div className="space-y-2">
                                <label className="block text-xs font-black text-gray-400 uppercase tracking-widest mb-1 px-1">SKU / Código</label>
                                <input
                                    type="text"
                                    name="sku"
                                    value={formData.sku}
                                    onChange={handleChange}
                                    required
                                    className="w-full px-5 py-3.5 bg-gray-50 dark:bg-slate-900 border border-transparent focus:border-primary/30 rounded-2xl focus:ring-4 focus:ring-primary/5 outline-none transition-all text-gray-900 dark:text-white font-medium"
                                    placeholder="SKU-001"
                                />
                            </div>

                            <div className="space-y-2">
                                <label className="block text-xs font-black text-gray-400 uppercase tracking-widest mb-1 px-1">Categoría</label>
                                <select
                                    name="categoria"
                                    value={formData.categoria}
                                    onChange={handleChange}
                                    required
                                    className="w-full px-5 py-3.5 bg-gray-50 dark:bg-slate-900 border border-transparent focus:border-primary/30 rounded-2xl focus:ring-4 focus:ring-primary/5 outline-none transition-all text-gray-900 dark:text-white font-medium appearance-none"
                                >
                                    <option value="">Seleccionar...</option>
                                    {categories.map(c => (
                                        <option key={c.idCategoria || c.id} value={c.nombre}>{c.nombre}</option>
                                    ))}
                                </select>
                            </div>

                            <div className="space-y-2">
                                <label className="block text-xs font-black text-gray-400 uppercase tracking-widest mb-1 px-1">Ubicación / Almacén</label>
                                <select
                                    name="idAlmacen"
                                    value={formData.idAlmacen}
                                    onChange={handleChange}
                                    required
                                    className="w-full px-5 py-3.5 bg-gray-50 dark:bg-slate-900 border border-transparent focus:border-primary/30 rounded-2xl focus:ring-4 focus:ring-primary/5 outline-none transition-all text-gray-900 dark:text-white font-medium"
                                >
                                    <option value="">Seleccionar...</option>
                                    {warehouses.map(w => (
                                        <option key={w.idAlmacen || w.id} value={w.idAlmacen || w.id}>{w.nombre}</option>
                                    ))}
                                </select>
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div className="space-y-2">
                                    <label className="block text-xs font-black text-gray-400 uppercase tracking-widest mb-1 px-1">Stock</label>
                                    <input
                                        type="number"
                                        name="cantidad"
                                        value={formData.cantidad}
                                        onChange={handleChange}
                                        className="w-full px-5 py-3.5 bg-gray-50 dark:bg-slate-900 border border-transparent focus:border-primary/30 rounded-2xl focus:ring-4 focus:ring-primary/5 outline-none transition-all text-gray-900 dark:text-white font-bold"
                                    />
                                </div>
                                <div className="space-y-2">
                                    <label className="block text-xs font-black text-gray-400 uppercase tracking-widest mb-1 px-1">Mínimo</label>
                                    <input
                                        type="number"
                                        name="stockMinimo"
                                        value={formData.stockMinimo}
                                        onChange={handleChange}
                                        className="w-full px-5 py-3.5 bg-gray-50 dark:bg-slate-900 border border-transparent focus:border-primary/30 rounded-2xl focus:ring-4 focus:ring-primary/5 outline-none transition-all text-gray-900 dark:text-white font-bold"
                                    />
                                </div>
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div className="space-y-2">
                                    <label className="block text-xs font-black text-gray-400 uppercase tracking-widest mb-1 px-1">P. Compra</label>
                                    <div className="relative">
                                        <span className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 font-bold">$</span>
                                        <input
                                            type="number"
                                            name="precioCompra"
                                            step="0.01"
                                            value={formData.precioCompra}
                                            onChange={handleChange}
                                            className="w-full pl-8 pr-4 py-3.5 bg-gray-50 dark:bg-slate-900 border border-transparent focus:border-primary/30 rounded-2xl focus:ring-4 focus:ring-primary/5 outline-none transition-all text-gray-900 dark:text-white font-bold"
                                        />
                                    </div>
                                </div>
                                <div className="space-y-2">
                                    <label className="block text-xs font-black text-gray-400 uppercase tracking-widest mb-1 px-1">P. Venta</label>
                                    <div className="relative">
                                        <span className="absolute left-4 top-1/2 -translate-y-1/2 text-primary font-black">$</span>
                                        <input
                                            type="number"
                                            name="precioVenta"
                                            step="0.01"
                                            value={formData.precioVenta}
                                            onChange={handleChange}
                                            required
                                            className="w-full pl-8 pr-4 py-3.5 bg-blue-50/50 dark:bg-blue-900/10 border border-primary/10 focus:border-primary/30 rounded-2xl focus:ring-4 focus:ring-primary/5 outline-none transition-all text-primary font-black"
                                        />
                                    </div>
                                </div>
                            </div>

                            <div className="md:col-span-2 space-y-2">
                                <label className="block text-xs font-black text-gray-400 uppercase tracking-widest mb-1 px-1">Notas / Descripción</label>
                                <textarea
                                    name="descripcion"
                                    value={formData.descripcion}
                                    onChange={handleChange}
                                    rows="3"
                                    className="w-full px-5 py-4 bg-gray-50 dark:bg-slate-900 border border-transparent focus:border-primary/30 rounded-3xl focus:ring-4 focus:ring-primary/5 outline-none transition-all text-gray-900 dark:text-white font-medium resize-none"
                                    placeholder="Escribe notas adicionales aquí..."
                                />
                            </div>
                        </div>
                    </div>
                </form>

                {/* Footer Buttons */}
                <div className="p-8 border-t border-gray-100 dark:border-slate-800 bg-gray-50/50 dark:bg-slate-900/50 flex gap-4">
                    <button
                        type="button"
                        onClick={onClose}
                        className="flex-1 py-4 px-6 border-2 border-gray-200 dark:border-slate-700 rounded-2xl text-gray-600 dark:text-gray-400 font-black uppercase tracking-widest hover:bg-white dark:hover:bg-slate-800 hover:border-gray-300 transition-all active:scale-95"
                    >
                        Cancelar
                    </button>
                    <button
                        onClick={handleSubmit}
                        disabled={loading || uploading}
                        className="flex-[2] py-4 px-6 bg-primary hover:bg-blue-600 text-white font-black uppercase tracking-widest rounded-2xl shadow-xl shadow-primary/30 transition-all hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-3 disabled:opacity-50"
                    >
                        {loading ? <Loader className="animate-spin" size={20} /> : <Save size={20} />}
                        {loading ? 'GUARDANDO...' : 'GUARDAR PRODUCTO'}
                    </button>
                </div>
            </div>
        </motion.div>,
        document.body
    );
}
