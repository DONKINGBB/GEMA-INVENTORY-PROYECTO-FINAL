import { useState, useEffect } from 'react';
import { ArrowLeft, Plus, Edit, Trash2, Loader2, MapPin, Phone, Truck, Search, Sparkles, TrendingUp, ShieldCheck, Globe } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { supplierService } from '../services/inventoryService';
import { useAuth } from '../context/AuthContext';
import { motion, AnimatePresence } from 'framer-motion';
import toast from 'react-hot-toast';

export default function SupplierSettings() {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [suppliers, setSuppliers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingSupplier, setEditingSupplier] = useState(null);
    const [formData, setFormData] = useState({ nombre: '', contacto: '', direccion: '' });
    const [saving, setSaving] = useState(false);
    const [itemToDelete, setItemToDelete] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');

    const fetchSuppliers = async () => {
        if (!user?.id) return;
        try {
            setLoading(true);
            const data = await supplierService.getAll(user.id);
            setSuppliers(data || []);
        } catch (err) {
            console.error(err);
            toast.error('Error al cargar proveedores');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchSuppliers();
    }, []);

    const handleCreate = () => {
        setEditingSupplier(null);
        setFormData({ nombre: '', contacto: '', direccion: '' });
        setIsModalOpen(true);
    };

    const handleEdit = (supplier) => {
        setEditingSupplier(supplier);
        setFormData({ 
            nombre: supplier.nombre || '', 
            contacto: supplier.contacto || '', 
            direccion: supplier.direccion || '' 
        });
        setIsModalOpen(true);
    };

    const confirmDelete = (sup) => {
        setItemToDelete(sup);
    };

    const handleDelete = async () => {
        if (!itemToDelete) return;
        try {
            await supplierService.delete(itemToDelete.idProveedor || itemToDelete.id);
            toast.success('Proveedor eliminado');
            setItemToDelete(null);
            await fetchSuppliers();
        } catch (error) {
            console.error("Error al eliminar proveedor", error);
            toast.error("No se pudo eliminar el proveedor");
        }
    };

    const handleSave = async (e) => {
        e.preventDefault();
        setSaving(true);
        try {
            if (editingSupplier) {
                await supplierService.update(editingSupplier.idProveedor || editingSupplier.id, formData);
                toast.success('Proveedor actualizado');
            } else {
                await supplierService.create({ ...formData, idUsuario: user.id }, user.id);
                toast.success('Proveedor creado');
            }
            setIsModalOpen(false);
            await fetchSuppliers();
        } catch (err) {
            toast.error("Error al guardar");
        } finally {
            setSaving(false);
        }
    };

    const filteredSuppliers = suppliers.filter(s => 
        s.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
        s.direccion?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const containerVariants = {
        hidden: { opacity: 0 },
        visible: {
            opacity: 1,
            transition: { staggerChildren: 0.1 }
        }
    };

    const itemVariants = {
        hidden: { y: 20, opacity: 0 },
        visible: { y: 0, opacity: 1 }
    };

    return (
        <motion.div 
            initial="hidden"
            animate="visible"
            variants={containerVariants}
            className="max-w-7xl mx-auto space-y-8 pb-20 sm:pb-0"
        >
            {/* Ultra-Premium Header */}
            <motion.div variants={itemVariants} className="relative overflow-hidden bg-white dark:bg-slate-900/40 backdrop-blur-xl border border-slate-200 dark:border-white/5 rounded-[3rem] p-8 sm:p-12 shadow-xl shadow-black/5 dark:shadow-none">
                <div className="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-8">
                    <div className="space-y-4">
                        <div className="flex items-center gap-4">
                            <button 
                                onClick={() => navigate('/app/settings')}
                                className="p-4 bg-white dark:bg-white/5 hover:bg-slate-50 dark:hover:bg-white/10 rounded-2xl transition-all text-slate-900 dark:text-white border border-slate-200 dark:border-white/5 shadow-sm dark:shadow-none active:scale-95"
                            >
                                <ArrowLeft size={24} />
                            </button>
                            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-primary/10 border border-primary/20 text-primary text-[10px] font-black uppercase tracking-widest">
                                <Truck size={12} />
                                <span>Cadena de Suministro</span>
                            </div>
                        </div>
                        <h1 className="text-5xl sm:text-7xl font-black text-slate-900 dark:text-white tracking-tighter leading-none">
                            Gestión de <span className="text-primary">Proveedores</span>
                        </h1>
                        <p className="text-slate-400 text-lg max-w-xl">
                            Administra tu red de abastecimiento y socios logísticos con precisión y trazabilidad.
                        </p>
                    </div>
                    <button 
                        onClick={handleCreate} 
                        className="group bg-primary hover:bg-primary/90 text-white px-8 py-5 rounded-[2rem] flex items-center gap-3 transition-all shadow-2xl shadow-primary/40 font-black text-lg active:scale-95"
                    >
                        <Plus size={24} className="group-hover:rotate-90 transition-transform" />
                        <span>Nuevo Proveedor</span>
                    </button>
                </div>
                
                {/* Background Decoration */}
                <div className="absolute top-0 right-0 w-1/3 h-full bg-gradient-to-l from-primary/10 to-transparent pointer-events-none" />
                <div className="absolute -bottom-24 -right-24 w-64 h-64 bg-primary/20 blur-[120px] rounded-full pointer-events-none" />
            </motion.div>

            {/* Search and Quick Metrics */}
            <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
                <motion.div variants={itemVariants} className="lg:col-span-3 relative group">
                    <Search className="absolute left-6 top-1/2 -translate-y-1/2 text-slate-500 group-focus-within:text-primary transition-colors" size={24} />
                    <input 
                        type="text" 
                        placeholder="Buscar por nombre, dirección o contacto..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="w-full pl-16 pr-8 py-6 rounded-[2.5rem] bg-white dark:bg-slate-900/40 backdrop-blur-xl border border-slate-200 dark:border-white/5 focus:border-primary focus:ring-8 focus:ring-primary/5 outline-none transition-all text-slate-900 dark:text-white text-lg placeholder:text-slate-400 dark:placeholder:text-slate-600 shadow-xl shadow-black/5 dark:shadow-none"
                    />
                </motion.div>
                <motion.div variants={itemVariants} className="bg-white dark:bg-slate-900/40 backdrop-blur-xl border border-slate-200 dark:border-primary/20 rounded-[2.5rem] p-6 flex items-center justify-between shadow-xl shadow-black/5 dark:shadow-none">
                    <div className="space-y-1">
                        <p className="text-[10px] font-black text-slate-500 uppercase tracking-widest">Activos</p>
                        <p className="text-3xl font-black text-slate-900 dark:text-white">{suppliers.length}</p>
                    </div>
                    <div className="p-3 bg-primary/10 text-primary rounded-2xl">
                        <Globe size={24} />
                    </div>
                </motion.div>
            </div>

            {/* Grid of Suppliers (Modern Card View) */}
            {loading ? (
                <div className="py-24 flex flex-col items-center justify-center space-y-6">
                    <Loader2 className="animate-spin text-primary w-16 h-16" />
                    <p className="text-slate-500 font-black tracking-widest text-sm uppercase animate-pulse">Sincronizando Proveedores...</p>
                </div>
            ) : filteredSuppliers.length > 0 ? (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                    <AnimatePresence mode="popLayout">
                        {filteredSuppliers.map((p, idx) => (
                            <motion.div 
                                key={p.idProveedor || p.id}
                                variants={itemVariants}
                                className="bg-white dark:bg-slate-900/40 backdrop-blur-xl border border-slate-200 dark:border-white/5 rounded-[2.5rem] group hover:border-primary/30 transition-all duration-500 relative overflow-hidden shadow-xl shadow-black/5 dark:shadow-none"
                            >
                                <div className="p-8 space-y-6 relative z-10">
                                    <div className="flex justify-between items-start">
                                        <div className="w-16 h-16 bg-slate-100 dark:bg-slate-900 border border-slate-200 dark:border-white/10 rounded-2xl flex items-center justify-center text-primary group-hover:scale-110 group-hover:border-primary/30 transition-all duration-500 shadow-2xl">
                                            <Truck size={28} />
                                        </div>
                                        <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                            <button 
                                                onClick={() => handleEdit(p)}
                                                className="p-3 bg-slate-100 dark:bg-white/5 hover:bg-primary/20 text-slate-400 hover:text-primary rounded-xl transition-all border border-slate-200 dark:border-white/5"
                                            >
                                                <Edit size={18} />
                                            </button>
                                            <button 
                                                onClick={() => confirmDelete(p)}
                                                className="p-3 bg-slate-100 dark:bg-white/5 hover:bg-rose-500/20 text-slate-400 hover:text-rose-500 rounded-xl transition-all border border-slate-200 dark:border-white/5"
                                            >
                                                <Trash2 size={18} />
                                            </button>
                                        </div>
                                    </div>

                                    <div className="space-y-2">
                                        <h3 className="text-2xl font-black text-slate-900 dark:text-white group-hover:text-primary transition-colors tracking-tight">
                                            {p.nombre}
                                        </h3>
                                        <div className="flex items-center gap-2">
                                            <ShieldCheck size={14} className="text-emerald-500" />
                                            <span className="text-[10px] font-black text-emerald-500 uppercase tracking-widest">Socio Verificado</span>
                                        </div>
                                    </div>

                                    <div className="space-y-4 pt-4 border-t border-slate-100 dark:border-white/5">
                                        <div className="flex items-center gap-4 text-slate-500 dark:text-slate-400 group-hover:text-slate-900 dark:group-hover:text-white transition-colors">
                                            <div className="p-2 bg-slate-100 dark:bg-white/5 rounded-lg">
                                                <Phone size={16} />
                                            </div>
                                            <span className="text-sm font-bold tracking-tight">{p.contacto || 'Sin contacto'}</span>
                                        </div>
                                        <div className="flex items-center gap-4 text-slate-500 dark:text-slate-400 group-hover:text-slate-900 dark:group-hover:text-white transition-colors">
                                            <div className="p-2 bg-slate-100 dark:bg-white/5 rounded-lg">
                                                <MapPin size={16} />
                                            </div>
                                            <span className="text-sm font-bold tracking-tight line-clamp-1">{p.direccion || 'Sin dirección'}</span>
                                        </div>
                                    </div>
                                </div>
                                
                                {/* Subtle Hover Background */}
                                <div className="absolute top-0 right-0 w-full h-full bg-gradient-to-br from-primary/5 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-700 pointer-events-none" />
                            </motion.div>
                        ))}
                    </AnimatePresence>
                </div>
            ) : (
                <div className="py-32 text-center space-y-8">
                    <div className="w-32 h-32 bg-slate-100 dark:bg-white/5 rounded-[3rem] flex items-center justify-center mx-auto border border-slate-200 dark:border-white/5 shadow-2xl">
                        <Truck size={56} className="text-slate-400 dark:text-slate-700" />
                    </div>
                    <div className="space-y-2">
                        <h3 className="text-3xl font-black text-slate-900 dark:text-white">Sin socios</h3>
                        <p className="text-slate-500 text-lg">No se han registrado proveedores en tu base de datos todavía.</p>
                    </div>
                    <button 
                        onClick={handleCreate}
                        className="bg-primary/10 hover:bg-primary/20 text-primary px-8 py-4 rounded-2xl font-black transition-all inline-flex items-center gap-3 border border-primary/20"
                    >
                        Registrar Primer Proveedor
                        <Plus size={20} />
                    </button>
                </div>
            )}

            {/* Modal Components - Upgraded UI */}
            <AnimatePresence>
                {isModalOpen && (
                    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
                        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} onClick={() => setIsModalOpen(false)} className="absolute inset-0 bg-black/80 backdrop-blur-md" />
                        <motion.div initial={{ opacity: 0, scale: 0.9, y: 20 }} animate={{ opacity: 1, scale: 1, y: 0 }} exit={{ opacity: 0, scale: 0.9, y: 20 }} className="relative bg-white dark:bg-slate-900/90 backdrop-blur-2xl w-full max-w-md overflow-hidden flex flex-col shadow-2xl border border-slate-200 dark:border-white/10 rounded-[2.5rem]">
                            <div className="p-10 border-b border-slate-100 dark:border-white/5 bg-slate-50 dark:bg-white/5 flex justify-between items-center">
                                <div>
                                    <h2 className="text-3xl font-black text-slate-900 dark:text-white tracking-tighter">
                                        {editingSupplier ? 'Editar Socio' : 'Nuevo Socio'}
                                    </h2>
                                    <p className="text-slate-500 font-medium text-sm mt-1">Configuración de proveedor logístico</p>
                                </div>
                                <button onClick={() => setIsModalOpen(false)} className="p-4 bg-slate-200/50 dark:bg-white/5 hover:bg-slate-200 dark:hover:bg-white/10 rounded-2xl text-slate-500 dark:text-slate-400 transition-all">
                                    <Sparkles size={24} className="text-primary" />
                                </button>
                            </div>

                            <form onSubmit={handleSave} className="p-10 space-y-8 bg-white dark:bg-slate-900/50">
                                <div className="space-y-6">
                                    <div className="space-y-3">
                                        <label className="text-[10px] font-black text-slate-500 uppercase tracking-widest ml-1">Razón Social / Nombre</label>
                                        <div className="relative group">
                                            <Truck className="absolute left-5 top-1/2 -translate-y-1/2 text-slate-400 dark:text-slate-600 group-focus-within:text-primary transition-colors" size={20} />
                                            <input type="text" required value={formData.nombre} onChange={(e) => setFormData({ ...formData, nombre: e.target.value })} className="w-full pl-14 pr-6 py-5 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-200 dark:border-white/5 focus:border-primary focus:ring-4 focus:ring-primary/10 outline-none text-slate-900 dark:text-white transition-all font-bold placeholder:text-slate-400 dark:placeholder:text-slate-700" placeholder="Ej: Importaciones Phoenix S.A." />
                                        </div>
                                    </div>

                                    <div className="space-y-3">
                                        <label className="text-[10px] font-black text-slate-500 uppercase tracking-widest ml-1">Contacto Principal</label>
                                        <div className="relative group">
                                            <Phone className="absolute left-5 top-1/2 -translate-y-1/2 text-slate-400 dark:text-slate-600 group-focus-within:text-primary transition-colors" size={20} />
                                            <input type="text" value={formData.contacto} onChange={(e) => setFormData({ ...formData, contacto: e.target.value })} className="w-full pl-14 pr-6 py-5 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-200 dark:border-white/5 focus:border-primary focus:ring-4 focus:ring-primary/10 outline-none text-slate-900 dark:text-white transition-all font-bold placeholder:text-slate-400 dark:placeholder:text-slate-700" placeholder="Ej: +52 1 55 5555 5555" />
                                        </div>
                                    </div>

                                    <div className="space-y-3">
                                        <label className="text-[10px] font-black text-slate-500 uppercase tracking-widest ml-1">Centro de Distribución / Fiscal</label>
                                        <div className="relative group">
                                            <MapPin className="absolute left-5 top-1/2 -translate-y-1/2 text-slate-400 dark:text-slate-600 group-focus-within:text-primary transition-colors" size={20} />
                                            <input type="text" value={formData.direccion} onChange={(e) => setFormData({ ...formData, direccion: e.target.value })} className="w-full pl-14 pr-6 py-5 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-200 dark:border-white/5 focus:border-primary focus:ring-4 focus:ring-primary/10 outline-none text-slate-900 dark:text-white transition-all font-bold placeholder:text-slate-400 dark:placeholder:text-slate-700" placeholder="Calle, Número, Ciudad, CP" />
                                        </div>
                                    </div>
                                </div>

                                <div className="flex gap-4 pt-4">
                                    <button type="button" onClick={() => setIsModalOpen(false)} className="flex-1 py-5 text-slate-500 dark:text-slate-400 font-black rounded-2xl hover:bg-slate-100 dark:hover:bg-white/5 transition uppercase tracking-widest text-[10px]">CANCELAR</button>
                                    <button type="submit" disabled={saving} className="flex-[2] py-5 bg-primary text-white font-black rounded-[1.5rem] hover:bg-primary/90 transition shadow-2xl shadow-primary/30 flex items-center justify-center gap-3 active:scale-95 text-xs tracking-[0.2em]">
                                        {saving ? <Loader2 size={20} className="animate-spin" /> : (editingSupplier ? 'ACTUALIZAR DATOS' : 'CREAR SOCIO')}
                                    </button>
                                </div>
                            </form>
                        </motion.div>
                    </div>
                )}
            </AnimatePresence>

            {/* Delete Confirmation - Upgraded UI */}
            <AnimatePresence>
                {itemToDelete && (
                    <div className="fixed inset-0 z-[110] flex items-center justify-center p-4">
                        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="absolute inset-0 bg-black/90 backdrop-blur-xl" />
                        <motion.div initial={{ opacity: 0, scale: 0.9 }} animate={{ opacity: 1, scale: 1 }} exit={{ opacity: 0, scale: 0.9 }} className="relative bg-white dark:bg-slate-900 p-12 max-w-md w-full text-center shadow-2xl border border-rose-500/20 rounded-[3rem]">
                            <div className="w-24 h-24 bg-rose-500/10 text-rose-500 rounded-[2.5rem] flex items-center justify-center mx-auto mb-8 border border-rose-500/20 shadow-2xl shadow-rose-500/20">
                                <Trash2 size={48} className="animate-pulse" />
                            </div>
                            <h3 className="text-4xl font-black text-slate-900 dark:text-white mb-4 tracking-tighter">¿Dar de baja?</h3>
                            <p className="text-slate-500 dark:text-slate-400 mb-10 text-lg leading-relaxed">
                                Estás a punto de eliminar a <span className="text-slate-900 dark:text-white font-black">"{itemToDelete.nombre}"</span>. Esta operación es irreversible.
                            </p>
                            <div className="flex flex-col sm:flex-row gap-4">
                                <button onClick={() => setItemToDelete(null)} className="flex-1 py-5 bg-slate-100 dark:bg-white/5 text-slate-900 dark:text-white font-black rounded-2xl hover:bg-slate-200 dark:hover:bg-white/10 transition uppercase tracking-widest text-[10px] border border-slate-200 dark:border-white/5">CANCELAR</button>
                                <button onClick={handleDelete} className="flex-1 py-5 bg-rose-600 text-white font-black rounded-2xl hover:bg-rose-700 transition shadow-2xl shadow-rose-600/30 active:scale-95 text-[10px] tracking-widest">CONFIRMAR BAJA</button>
                            </div>
                        </motion.div>
                    </div>
                )}
            </AnimatePresence>
        </motion.div>
    );
}
