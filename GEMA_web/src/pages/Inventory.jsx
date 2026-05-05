import { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { 
    Search, Plus, Filter, Edit2, Trash2, Download, 
    Package, LayoutGrid, List, MoreHorizontal, 
    TrendingDown, ArrowUpRight, Box, AlertCircle,
    Zap, Sparkles, ChevronRight, BarChart3
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { useAuth } from '../context/AuthContext';
import { productService } from '../services/productService';
import ProductForm from '../components/ProductForm';

export default function Inventory() {
    const { user } = useAuth();
    const location = useLocation();
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');
    const [viewMode, setViewMode] = useState('grid'); // 'list' or 'grid'

    // Modal State
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingProduct, setEditingProduct] = useState(null);
    const [itemToDelete, setItemToDelete] = useState(null);

    const fetchProducts = async () => {
        if (!user?.id) return;
        try {
            setLoading(true);
            const data = await productService.getAll(user.id);
            if (Array.isArray(data)) {
                setProducts(data);
            } else {
                console.warn("API returned non-array", data);
                setProducts([]);
            }
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchProducts();
    }, [user]);

    useEffect(() => {
        if (location.state?.editProduct) {
            handleEdit(location.state.editProduct);
        }
    }, [location.state]);

    const handleCreate = () => {
        setEditingProduct(null);
        setIsModalOpen(true);
    };

    const handleEdit = (product) => {
        setEditingProduct(product);
        setIsModalOpen(true);
    };

    const confirmDelete = (product) => {
        setItemToDelete(product);
    };

    const handleDelete = async () => {
        if (!itemToDelete) return;
        try {
            await productService.delete(itemToDelete.idProducto || itemToDelete.id);
            setItemToDelete(null);
            await fetchProducts(); // Refresh list
        } catch (err) {
            alert("Error al eliminar el producto");
        }
    };

    const handleSave = async (formData) => {
        const productData = {
            ...formData,
            idUsuario: user.id
        };

        try {
            if (editingProduct) {
                await productService.update(editingProduct.idProducto || editingProduct.id, productData);
            } else {
                await productService.create(productData);
            }
            await fetchProducts(); // Refresh list
            setIsModalOpen(false);
        } catch (err) {
            console.error("Error saving product:", err);
        }
    };

    const filteredProducts = products.filter(p =>
        p.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        p.sku?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        p.categoria?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const containerVariants = {
        hidden: { opacity: 0 },
        visible: {
            opacity: 1,
            transition: { staggerChildren: 0.05, delayChildren: 0.1 }
        }
    };

    const itemVariants = {
        hidden: { y: 20, opacity: 0 },
        visible: { 
            y: 0, 
            opacity: 1,
            transition: { type: 'spring', stiffness: 300, damping: 25 }
        }
    };

    return (
        <motion.div 
            initial="hidden"
            animate="visible"
            variants={containerVariants}
            className="space-y-12 pb-20"
        >
            {/* Header Section */}
            <div className="flex flex-col md:flex-row justify-between items-start md:items-end gap-8">
                <div className="space-y-4">
                    <div className="flex items-center gap-3">
                        <div className="w-10 h-[1px] bg-primary" />
                        <span className="text-[10px] font-black text-primary uppercase tracking-[0.4em]">Gestión de Stock</span>
                    </div>
                    <h1 className="text-5xl md:text-7xl font-black text-slate-900 dark:text-white tracking-tighter leading-none">
                        INVENTARIO
                    </h1>
                    <p className="text-slate-500 dark:text-white/40 font-medium flex items-center gap-2 text-sm">
                        <Zap size={14} className="text-primary fill-primary/20" />
                        Monitoreo inteligente de existencias y niveles de catálogo.
                    </p>
                </div>
                
                <motion.button
                    whileHover={{ scale: 1.05, y: -2 }}
                    whileTap={{ scale: 0.95 }}
                    onClick={handleCreate}
                    className="bg-primary hover:bg-blue-500 text-white px-10 py-5 rounded-[2rem] shadow-2xl shadow-primary/30 flex items-center gap-4 font-black uppercase text-xs tracking-widest transition-all group"
                >
                    <Plus size={20} className="group-hover:rotate-90 transition-transform duration-500" />
                    <span>Añadir Producto</span>
                </motion.button>
            </div>

            {/* Filter & Controls Bar */}
            <div className="bg-white dark:bg-slate-900/40 backdrop-blur-xl p-6 flex flex-col lg:flex-row gap-6 items-center border border-slate-200 dark:border-white/5 shadow-xl shadow-black/5 dark:shadow-none rounded-[2.5rem]">
                <div className="relative flex-1 w-full group">
                    <Search className="absolute left-6 top-1/2 -translate-y-1/2 text-slate-400 dark:text-white/10 group-focus-within:text-primary transition-colors" size={20} />
                    <input
                        type="text"
                        placeholder="Buscar por nombre, SKU o categoría..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="w-full bg-slate-50 dark:bg-white/[0.02] border border-slate-200 dark:border-white/5 rounded-2xl py-4 pl-16 pr-6 text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-white/10 focus:outline-none focus:ring-4 focus:ring-primary/10 transition-all text-sm font-medium"
                    />
                </div>
                
                <div className="flex items-center gap-4 w-full lg:w-auto">
                    <div className="flex bg-slate-100 dark:bg-white/[0.03] p-1.5 rounded-2xl border border-slate-200 dark:border-white/5">
                        {[
                            { mode: 'grid', icon: LayoutGrid },
                            { mode: 'list', icon: List }
                        ].map((btn) => (
                            <button
                                key={btn.mode}
                                onClick={() => setViewMode(btn.mode)}
                                className={`p-3 rounded-xl transition-all duration-300 ${
                                    viewMode === btn.mode 
                                    ? 'bg-primary text-white shadow-xl shadow-primary/30' 
                                    : 'text-slate-400 dark:text-white/20 hover:text-primary dark:hover:text-white/50'
                                }`}
                            >
                                <btn.icon size={20} />
                            </button>
                        ))}
                    </div>
                    
                    <button className="p-4 bg-slate-100 dark:bg-white/[0.03] hover:bg-slate-200 dark:hover:bg-white/[0.06] border border-slate-200 dark:border-white/5 text-slate-400 dark:text-white/30 transition-all group">
                        <Filter size={20} className="group-hover:text-primary group-hover:scale-110 transition-all" />
                    </button>
                    <button className="p-4 bg-slate-100 dark:bg-white/[0.03] hover:bg-slate-200 dark:hover:bg-white/[0.06] border border-slate-200 dark:border-white/5 text-slate-400 dark:text-white/30 transition-all group">
                        <Download size={20} className="group-hover:text-primary group-hover:scale-110 transition-all" />
                    </button>
                </div>
            </div>

            {/* Content Area */}
            {loading ? (
                <div className="flex flex-col items-center justify-center py-40 space-y-8">
                    <div className="relative">
                        <div className="w-20 h-20 border-2 border-primary/10 rounded-full animate-[ping_2s_infinite]" />
                        <div className="absolute inset-0 w-20 h-20 border-t-2 border-primary rounded-full animate-spin" />
                        <Package className="absolute inset-0 m-auto text-primary animate-pulse" size={32} />
                    </div>
                    <div className="text-center space-y-2">
                        <p className="text-sm font-black text-slate-500 dark:text-white/60 uppercase tracking-[0.4em]">Sincronizando</p>
                        <p className="text-[10px] font-bold text-slate-400 dark:text-white/20 uppercase tracking-[0.2em]">Accediendo al núcleo de datos...</p>
                    </div>
                </div>
            ) : filteredProducts.length === 0 ? (
                <motion.div 
                    initial={{ opacity: 0, scale: 0.95 }}
                    animate={{ opacity: 1, scale: 1 }}
                    className="bg-white dark:bg-slate-900/40 backdrop-blur-xl p-32 text-center border-2 border-dashed border-slate-200 dark:border-white/10 rounded-[3rem] shadow-xl shadow-black/5 dark:shadow-none"
                >
                    <div className="w-24 h-24 bg-slate-100 dark:bg-white/[0.02] rounded-[2rem] flex items-center justify-center mx-auto mb-8 border border-slate-200 dark:border-white/5">
                        <Package className="text-slate-400 dark:text-white/10" size={48} />
                    </div>
                    <h3 className="text-3xl font-black text-slate-900 dark:text-white mb-3 tracking-tighter uppercase">Sin Coincidencias</h3>
                    <p className="text-slate-500 dark:text-white/20 max-w-sm mx-auto text-sm font-medium tracking-wide">
                        No pudimos encontrar productos con <span className="text-primary font-bold">"{searchTerm}"</span>. Intenta ajustar los filtros de búsqueda.
                    </p>
                </motion.div>
            ) : viewMode === 'grid' ? (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
                    <AnimatePresence>
                        {filteredProducts.map((product) => (
                            <motion.div
                                key={product.idProducto || product.id}
                                variants={itemVariants}
                                exit={{ opacity: 0, scale: 0.9 }}
                                className="bg-white dark:bg-slate-900/40 backdrop-blur-xl group relative overflow-hidden border border-slate-200 dark:border-white/5 rounded-[2.5rem] shadow-xl shadow-black/5 dark:shadow-none hover:-translate-y-2 transition-all duration-500 ease-out"
                            >
                                {/* Decorative Glow */}
                                <div className="absolute -top-10 -right-10 w-32 h-32 bg-primary/2 blur-3xl group-hover:bg-primary/10 transition-all duration-500 pointer-events-none" />

                                {/* Category Badge */}
                                <div className="absolute top-5 left-5 z-[30]">
                                    <span className="px-3 py-1.5 bg-white dark:bg-slate-800 backdrop-blur-xl text-slate-500 dark:text-slate-400 rounded-xl text-[9px] font-black uppercase tracking-[0.2em] border border-slate-200/50 dark:border-white/5 shadow-sm group-hover:bg-white dark:group-hover:bg-slate-700 transition-colors">
                                        {product.categoria || 'General'}
                                    </span>
                                </div>

                                {/* Actions Menu (Hover) */}
                                <div className="absolute top-5 right-5 z-20 opacity-0 group-hover:opacity-100 transition-all translate-y-2 group-hover:translate-y-0 flex gap-2">
                                    <button 
                                        onClick={() => handleEdit(product)}
                                        className="p-3 bg-white/10 hover:bg-primary text-white rounded-xl shadow-2xl backdrop-blur-xl border border-white/5 transition-all"
                                    >
                                        <Edit2 size={14} />
                                    </button>
                                    <button 
                                        onClick={() => confirmDelete(product)}
                                        className="p-3 bg-white/10 hover:bg-rose-600 text-white rounded-xl shadow-2xl backdrop-blur-xl border border-white/5 transition-all"
                                    >
                                        <Trash2 size={14} />
                                    </button>
                                </div>

                                {/* Image Section */}
                                <div className="aspect-[4/3] w-full bg-gradient-to-br from-slate-50 dark:from-white/[0.03] to-transparent border-b border-slate-100 dark:border-white/5 flex items-center justify-center overflow-hidden relative">
                                    {product.imagenUrl || product.imagen_url ? (
                                        <img 
                                            src={product.imagenUrl || product.imagen_url} 
                                            alt={product.nombre} 
                                            className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-1000"
                                        />
                                    ) : (
                                        <div className="flex flex-col items-center gap-3">
                                            <div className="w-16 h-16 rounded-[1.5rem] bg-white/[0.03] flex items-center justify-center border border-white/5 group-hover:border-primary/30 transition-all">
                                                <Package size={32} className="text-white/10 group-hover:text-primary transition-all duration-500" />
                                            </div>
                                            <span className="text-[10px] font-black text-white/10 uppercase tracking-[0.3em]">{product.sku}</span>
                                        </div>
                                    )}
                                    <div className="absolute inset-0 bg-gradient-to-t from-slate-950/60 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity z-10" />
                                </div>

                                {/* Info Section */}
                                <div className="p-8 space-y-6 relative z-10">
                                    <div>
                                        <h3 className="text-xl font-black text-slate-900 dark:text-white truncate uppercase tracking-tighter group-hover:text-primary transition-colors duration-500 leading-tight">{product.nombre}</h3>
                                        <div className="flex items-center gap-2 mt-2">
                                            <div className="w-1 h-1 rounded-full bg-primary" />
                                            <p className="text-[10px] text-slate-500 dark:text-white/30 font-black uppercase tracking-widest">{product.sku}</p>
                                        </div>
                                    </div>

                                    <div className="flex items-end justify-between gap-4 pt-4 border-t border-slate-100 dark:border-white/[0.03]">
                                        <div className="space-y-1">
                                            <p className="text-3xl font-black text-slate-900 dark:text-white tracking-tighter">
                                                <span className="text-primary text-xl font-black mr-1">$</span>
                                                {(product.precioVenta || 0).toLocaleString()}
                                            </p>
                                            <div className="flex items-center gap-2 mt-2">
                                                <div className="p-1 bg-slate-50 dark:bg-white/5 rounded-md">
                                                    <BarChart3 size={10} className="text-slate-400 dark:text-white/30" />
                                                </div>
                                                <p className="text-[9px] font-black text-slate-400 dark:text-white/20 uppercase tracking-[0.2em]">
                                                    Stock: <span className="text-slate-600 dark:text-white/60">{product.cantidad || 0}</span>
                                                </p>
                                            </div>
                                        </div>
                                        
                                        <div className="pb-1">
                                            {(product.cantidad || 0) > (product.stockMinimo || 0) ? (
                                                <span className="px-3 py-1.5 bg-emerald-500/5 text-emerald-400 rounded-xl text-[8px] font-black uppercase tracking-[0.2em] border border-emerald-500/10 shadow-[0_0_15px_rgba(16,185,129,0.1)]">
                                                    Saludable
                                                </span>
                                            ) : (product.cantidad || 0) > 0 ? (
                                                <span className="px-3 py-1.5 bg-orange-500/5 text-orange-400 rounded-xl text-[8px] font-black uppercase tracking-[0.2em] border border-orange-500/10 shadow-[0_0_15px_rgba(245,158,11,0.1)]">
                                                    Critico
                                                </span>
                                            ) : (
                                                <span className="px-3 py-1.5 bg-rose-500/5 text-rose-400 rounded-xl text-[8px] font-black uppercase tracking-[0.2em] border border-rose-500/10 shadow-[0_0_15px_rgba(239,68,68,0.1)]">
                                                    Agotado
                                                </span>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            </motion.div>
                        ))}
                    </AnimatePresence>
                </div>
            ) : (
                <div className="bg-white dark:bg-slate-900/40 backdrop-blur-xl overflow-hidden border border-slate-200 dark:border-white/5 rounded-[2.5rem] shadow-xl shadow-black/5 dark:shadow-none">
                    <div className="overflow-x-auto">
                        <table className="w-full text-left">
                            <thead>
                                <tr className="border-b border-slate-100 dark:border-white/5 text-[9px] font-black text-slate-400 dark:text-white/20 uppercase tracking-[0.4em] bg-slate-50 dark:bg-white/[0.02]">
                                    <th className="p-8">Identificación</th>
                                    <th className="p-8">Categoría</th>
                                    <th className="p-8">Valor Unitario</th>
                                    <th className="p-8">Existencias</th>
                                    <th className="p-8">Status</th>
                                    <th className="p-8 text-right">Gestión</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 dark:divide-white/[0.03]">
                                {filteredProducts.map((product) => (
                                    <tr key={product.idProducto || product.id} className="hover:bg-slate-100/60 dark:hover:bg-white/[0.02] transition-colors group">
                                        <td className="p-8">
                                            <div className="flex items-center gap-6">
                                                <div className="w-16 h-16 rounded-2xl bg-white/[0.02] border border-white/5 overflow-hidden flex-shrink-0 flex items-center justify-center relative">
                                                    {product.imagenUrl || product.imagen_url ? (
                                                        <img src={product.imagenUrl || product.imagen_url} className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700" />
                                                    ) : (
                                                        <Package size={24} className="text-white/10" />
                                                    )}
                                                    <div className="absolute inset-0 ring-1 ring-inset ring-white/5 rounded-2xl" />
                                                </div>
                                                <div className="min-w-0">
                                                    <p className="font-black text-slate-900 dark:text-white text-base uppercase tracking-tight truncate max-w-[280px] group-hover:text-primary transition-colors">{product.nombre}</p>
                                                    <div className="flex items-center gap-2 mt-1">
                                                        <span className="text-[10px] text-slate-400 dark:text-white/20 font-black uppercase tracking-widest">{product.sku}</span>
                                                    </div>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="p-8">
                                            <span className="px-4 py-2 bg-slate-100 dark:bg-white/[0.05] border border-slate-200 dark:border-white/5 rounded-xl text-[10px] font-black text-slate-500 dark:text-white/40 uppercase tracking-widest transition-all">
                                                {product.categoria || 'General'}
                                            </span>
                                        </td>
                                        <td className="p-8">
                                            <p className="font-black text-slate-900 dark:text-white text-lg tracking-tighter">
                                                <span className="text-primary text-sm mr-1">$</span>
                                                {(product.precioVenta || 0).toLocaleString()}
                                            </p>
                                        </td>
                                        <td className="p-8">
                                            <div className="flex flex-col">
                                                <span className="font-black text-slate-900 dark:text-white text-lg tracking-tighter">{product.cantidad || 0} <span className="text-[10px] text-slate-400 dark:text-white/20 ml-1">unid.</span></span>
                                                <div className="w-16 h-1 bg-white/5 rounded-full mt-2 overflow-hidden">
                                                    <div className="h-full bg-primary/40 rounded-full" style={{ width: `${Math.min((product.cantidad / (product.stockMinimo * 2 || 10)) * 100, 100)}%` }} />
                                                </div>
                                            </div>
                                        </td>
                                        <td className="p-8">
                                            {(product.cantidad || 0) > (product.stockMinimo || 0) ? (
                                                <div className="flex items-center gap-3 text-emerald-400 font-black text-[9px] uppercase tracking-widest">
                                                    <div className="w-1.5 h-1.5 rounded-full bg-emerald-500 shadow-[0_0_10px_rgba(16,185,129,0.8)] animate-pulse" />
                                                    Optimo
                                                </div>
                                            ) : (product.cantidad || 0) > 0 ? (
                                                <div className="flex items-center gap-3 text-orange-400 font-black text-[9px] uppercase tracking-widest">
                                                    <div className="w-1.5 h-1.5 rounded-full bg-orange-500 shadow-[0_0_10px_rgba(245,158,11,0.8)]" />
                                                    Alerta
                                                </div>
                                            ) : (
                                                <div className="flex items-center gap-3 text-rose-500 font-black text-[9px] uppercase tracking-widest">
                                                    <div className="w-1.5 h-1.5 rounded-full bg-rose-500 shadow-[0_0_10px_rgba(239,68,68,0.8)]" />
                                                    Sin Stock
                                                </div>
                                            )}
                                        </td>
                                        <td className="p-8 text-right">
                                            <div className="flex justify-end gap-3 opacity-0 group-hover:opacity-100 transition-all -translate-x-4 group-hover:translate-x-0">
                                                <button onClick={() => handleEdit(product)} className="w-12 h-12 flex items-center justify-center text-white/40 hover:text-white hover:bg-primary rounded-2xl transition-all border border-white/5">
                                                    <Edit2 size={18} />
                                                </button>
                                                <button onClick={() => confirmDelete(product)} className="w-12 h-12 flex items-center justify-center text-white/40 hover:text-white hover:bg-rose-600 rounded-2xl transition-all border border-white/5">
                                                    <Trash2 size={18} />
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            <ProductForm
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onSubmit={handleSave}
                initialData={editingProduct}
                title={editingProduct ? 'Editar Producto' : 'Nuevo Producto'}
            />

            {/* Premium Delete Confirmation Modal */}
            <AnimatePresence>
                {itemToDelete && (
                    <div className="fixed inset-0 z-[100] flex items-center justify-center p-6">
                        <motion.div 
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            exit={{ opacity: 0 }}
                            className="absolute inset-0 bg-black/80 backdrop-blur-2xl" 
                            onClick={() => setItemToDelete(null)}
                        />
                        <motion.div 
                            initial={{ scale: 0.9, opacity: 0, y: 30 }}
                            animate={{ scale: 1, opacity: 1, y: 0 }}
                            exit={{ scale: 0.9, opacity: 0, y: 30 }}
                            className="bg-white dark:bg-slate-950/80 backdrop-blur-2xl w-full max-w-md relative z-10 p-12 text-center border border-slate-200 dark:border-rose-500/20 rounded-[3rem] shadow-2xl"
                        >
                            <div className="w-24 h-24 bg-rose-500/10 rounded-[2.5rem] flex items-center justify-center mx-auto mb-10 text-rose-500 relative">
                                <div className="absolute inset-0 bg-rose-500/20 blur-2xl rounded-full animate-pulse" />
                                <AlertCircle size={48} className="relative z-10" />
                            </div>
                            <h3 className="text-3xl font-black text-slate-900 dark:text-white mb-4 tracking-tighter uppercase leading-none">Confirmar Eliminación</h3>
                            <p className="text-slate-500 dark:text-white/40 text-sm leading-relaxed mb-10 font-medium">
                                ¿Estás seguro de que deseas eliminar <span className="text-slate-900 dark:text-white font-bold italic">"{itemToDelete.nombre}"</span>? Esta acción es irreversible y afectará los registros históricos.
                            </p>
                            <div className="grid grid-cols-2 gap-4">
                                <button
                                    onClick={() => setItemToDelete(null)}
                                    className="py-5 bg-white/[0.03] hover:bg-white/[0.08] text-white/60 hover:text-white font-black rounded-3xl transition-all uppercase tracking-widest text-[10px] border border-white/5"
                                >
                                    Ignorar
                                </button>
                                <button
                                    onClick={handleDelete}
                                    className="py-5 bg-rose-600 hover:bg-rose-500 text-white font-black rounded-3xl transition-all uppercase tracking-widest text-[10px] shadow-2xl shadow-rose-900/40"
                                >
                                    Eliminar Ahora
                                </button>
                            </div>
                        </motion.div>
                    </div>
                )}
            </AnimatePresence>
        </motion.div>
    );
}
