import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { clientService } from '../services/dataService';
import { Users, Phone, MapPin, Mail, Search, PlusCircle, Loader2, X, Edit, Trash2, UserPlus, ArrowUpRight, MessageSquare, History } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

export default function Clients() {
    const { user } = useAuth();
    const [clients, setClients] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');

    // Modal state
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingClient, setEditingClient] = useState(null);
    const [formData, setFormData] = useState({ nombre: '', contacto: '', direccion: '' });
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [clientToDelete, setClientToDelete] = useState(null);

    const fetchClients = async () => {
        if (!user?.id) return;
        try {
            setLoading(true);
            const data = await clientService.getAll(user.id);
            if (Array.isArray(data)) setClients(data);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchClients();
    }, [user]);

    const handleOpenModal = (client = null) => {
        if (client) {
            setEditingClient(client);
            setFormData({ nombre: client.nombre || '', contacto: client.contacto || '', direccion: client.direccion || '' });
        } else {
            setEditingClient(null);
            setFormData({ nombre: '', contacto: '', direccion: '' });
        }
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setEditingClient(null);
        setFormData({ nombre: '', contacto: '', direccion: '' });
    };

    const handleSave = async (e) => {
        e.preventDefault();
        try {
            setIsSubmitting(true);
            if (editingClient) {
                await clientService.update(editingClient.idCliente || editingClient.id, formData);
            } else {
                await clientService.create({ ...formData, idUsuario: user.id }, user.id);
            }
            handleCloseModal();
            await fetchClients();
        } catch (err) {
            console.error("Error saving client", err);
        } finally {
            setIsSubmitting(false);
        }
    };

    const confirmDelete = (client) => {
        setClientToDelete(client);
    };

    const handleDelete = async () => {
        if (!clientToDelete) return;
        try {
            await clientService.delete(clientToDelete.idCliente || clientToDelete.id);
            setClientToDelete(null);
            await fetchClients();
        } catch (err) {
            console.error("Error deleting client", err);
        }
    };

    const filteredClients = clients.filter(c => 
        c.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
        c.contacto?.toLowerCase().includes(searchTerm.toLowerCase())
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
            <motion.div variants={itemVariants} className="relative overflow-hidden bg-white dark:bg-slate-900/40 border border-slate-200 dark:border-white/5 p-8 sm:p-12 rounded-[2.5rem] shadow-sm dark:shadow-none">
                <div className="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-8">
                    <div className="space-y-4">
                        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-primary/10 border border-primary/20 text-primary text-[10px] font-black uppercase tracking-widest">
                            <Users size={12} />
                            <span>Relaciones Comerciales</span>
                        </div>
                        <h1 className="text-5xl sm:text-7xl font-black text-slate-900 dark:text-white tracking-tighter leading-none">
                            Cartera de <span className="text-primary">Clientes</span>
                        </h1>
                        <p className="text-slate-500 dark:text-slate-400 text-lg max-w-xl">
                            Gestiona tu base de datos de clientes, historiales de compra y canales de contacto directo.
                        </p>
                    </div>
                    <button 
                        onClick={() => handleOpenModal()} 
                        className="group bg-primary hover:bg-primary/90 text-white px-8 py-5 rounded-[2rem] flex items-center gap-3 transition-all shadow-2xl shadow-primary/40 font-black text-lg active:scale-95"
                    >
                        <UserPlus size={24} className="group-hover:rotate-12 transition-transform" />
                        <span>Nuevo Registro</span>
                    </button>
                </div>
                
                {/* Background Decoration */}
                <div className="absolute top-0 right-0 w-1/3 h-full bg-gradient-to-l from-primary/10 to-transparent pointer-events-none" />
                <div className="absolute -bottom-24 -right-24 w-64 h-64 bg-primary/20 blur-[120px] rounded-full pointer-events-none" />
            </motion.div>

            {/* Search and Filter */}
            <motion.div variants={itemVariants} className="relative group">
                <Search className="absolute left-6 top-1/2 -translate-y-1/2 text-slate-400 dark:text-slate-500 group-focus-within:text-primary transition-colors" size={24} />
                <input 
                    type="text" 
                    placeholder="Buscar clientes por nombre, teléfono o correo..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full pl-16 pr-8 py-6 rounded-[2.5rem] bg-white dark:bg-slate-900/40 border border-slate-200 dark:border-white/5 focus:border-primary focus:ring-8 focus:ring-primary/5 outline-none transition-all text-slate-900 dark:text-white text-lg placeholder:text-slate-400 dark:placeholder:text-slate-600 shadow-sm dark:shadow-none"
                />
            </motion.div>

            {loading ? (
                <div className="py-24 flex flex-col items-center justify-center space-y-6">
                    <Loader2 className="animate-spin text-primary w-16 h-16" />
                    <p className="text-slate-500 font-bold tracking-widest text-sm uppercase animate-pulse">Sincronizando directorio...</p>
                </div>
            ) : (
                <motion.div 
                    variants={containerVariants}
                    className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-8"
                >
                    <AnimatePresence>
                        {filteredClients.map((client, idx) => (
                            <motion.div 
                                key={client.idCliente || client.id}
                                variants={itemVariants}
                                exit={{ opacity: 0, scale: 0.9 }}
                                className="group bg-white dark:bg-slate-900/40 backdrop-blur-xl p-8 border border-slate-200 dark:border-white/5 hover:border-primary/20 transition-all duration-500 relative flex flex-col h-full rounded-[2.5rem] shadow-xl shadow-black/5 dark:shadow-none"
                            >
                                <div className="flex items-start justify-between mb-8">
                                    <div className="flex items-center gap-5">
                                        <div className="w-20 h-20 rounded-[2rem] bg-gradient-to-br from-primary to-indigo-600 flex items-center justify-center text-white font-black text-3xl shadow-2xl group-hover:scale-110 transition-transform duration-500 ring-4 ring-white/5">
                                            {client.nombre?.charAt(0)?.toUpperCase() || "C"}
                                        </div>
                                        <div>
                                            <h3 className="font-black text-2xl text-slate-900 dark:text-white group-hover:text-primary transition-colors leading-tight">
                                                {client.nombre}
                                            </h3>
                                            <div className="flex items-center gap-2 mt-1">
                                                <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
                                                <span className="text-[10px] font-black text-slate-500 uppercase tracking-widest">Activo</span>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="flex gap-2">
                                        <button 
                                            onClick={() => handleOpenModal(client)}
                                            className="p-3 bg-slate-100 dark:bg-white/5 hover:bg-primary/10 text-slate-500 hover:text-primary rounded-2xl transition-all border border-slate-200 dark:border-white/5"
                                        >
                                            <Edit size={18} />
                                        </button>
                                        <button 
                                            onClick={() => confirmDelete(client)} 
                                            className="p-3 bg-slate-100 dark:bg-white/5 hover:bg-rose-500/10 text-slate-500 hover:text-rose-500 rounded-2xl transition-all border border-slate-200 dark:border-white/5"
                                        >
                                            <Trash2 size={18} />
                                        </button>
                                    </div>
                                </div>

                                <div className="space-y-6 flex-1 mb-8">
                                    <div className="bg-slate-50 dark:bg-white/5 p-4 space-y-4 border border-slate-100 dark:border-white/5 rounded-2xl">
                                        <div className="flex items-center gap-4">
                                            <div className="p-2.5 bg-primary/10 text-primary rounded-xl">
                                                <Phone size={18} />
                                            </div>
                                            <div className="flex flex-col">
                                                <span className="text-[9px] font-black text-slate-500 uppercase tracking-widest">Contacto Directo</span>
                                                <span className="text-slate-900 dark:text-white font-bold">{client.contacto || "No registrado"}</span>
                                            </div>
                                        </div>
                                        <div className="flex items-start gap-4">
                                            <div className="p-2.5 bg-indigo-500/10 text-indigo-500 dark:text-indigo-400 rounded-xl">
                                                <MapPin size={18} />
                                            </div>
                                            <div className="flex flex-col">
                                                <span className="text-[9px] font-black text-slate-500 uppercase tracking-widest">Ubicación / Fiscal</span>
                                                <span className="text-slate-500 dark:text-slate-300 text-sm leading-relaxed line-clamp-2">
                                                    {client.direccion || "Sin dirección física"}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div className="grid grid-cols-2 gap-4">
                                    <button className="flex items-center justify-center gap-2 py-4 bg-slate-100 dark:bg-white/5 hover:bg-slate-200 dark:hover:bg-white/10 text-slate-900 dark:text-white rounded-2xl transition-all font-black text-[10px] uppercase tracking-widest border border-slate-200 dark:border-white/5">
                                        <History size={14} className="text-primary" />
                                        Historial
                                    </button>
                                    <button className="flex items-center justify-center gap-2 py-4 bg-primary/10 hover:bg-primary/20 text-primary rounded-2xl transition-all font-black text-[10px] uppercase tracking-widest border border-primary/20">
                                        <MessageSquare size={14} />
                                        Mensaje
                                    </button>
                                </div>
                            </motion.div>
                        ))}
                    </AnimatePresence>

                    {/* Empty State */}
                    {!loading && filteredClients.length === 0 && (
                        <div className="col-span-full bg-white dark:bg-slate-900/40 backdrop-blur-xl p-24 text-center border-2 border-dashed border-slate-200 dark:border-white/10 rounded-[3rem]">
                            <div className="w-24 h-24 bg-primary/10 rounded-[2.5rem] flex items-center justify-center mx-auto mb-8 text-primary shadow-2xl shadow-primary/20">
                                <Users size={48} />
                            </div>
                            <h3 className="text-3xl font-black text-slate-900 dark:text-white mb-4">Sin resultados</h3>
                            <p className="text-slate-500 dark:text-slate-400 max-w-md mx-auto text-lg mb-8">
                                No encontramos clientes que coincidan con tu búsqueda. ¿Deseas registrar un nuevo cliente ahora?
                            </p>
                            <button 
                                onClick={() => handleOpenModal()} 
                                className="bg-primary text-white px-8 py-4 rounded-2xl font-black transition-all hover:scale-105 active:scale-95 shadow-xl shadow-primary/25"
                            >
                                Registrar mi primer cliente
                            </button>
                        </div>
                    )}
                </motion.div>
            )}

            {/* Modal de Registro/Edición */}
            <AnimatePresence>
                {isModalOpen && (
                    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                        <motion.div 
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            exit={{ opacity: 0 }}
                            onClick={handleCloseModal}
                            className="absolute inset-0 bg-black/60 backdrop-blur-md"
                        />
                        <motion.div 
                            initial={{ scale: 0.9, opacity: 0, y: 20 }}
                            animate={{ scale: 1, opacity: 1, y: 0 }}
                            exit={{ scale: 0.9, opacity: 0, y: 20 }}
                            className="bg-white dark:bg-slate-900/90 backdrop-blur-2xl w-full max-w-lg overflow-hidden relative z-10 border border-slate-200 dark:border-white/10 shadow-2xl rounded-[2.5rem]"
                        >
                            <div className="px-10 py-8 border-b border-slate-100 dark:border-white/5 flex justify-between items-center bg-slate-50 dark:bg-white/5">
                                <div>
                                    <h2 className="text-3xl font-black text-slate-900 dark:text-white tracking-tighter">
                                        {editingClient ? 'Editar Perfil' : 'Nuevo Cliente'}
                                    </h2>
                                    <p className="text-slate-500 font-medium">Información comercial y de contacto</p>
                                </div>
                                <button onClick={handleCloseModal} className="p-3 bg-slate-100 dark:bg-white/5 hover:bg-slate-200 dark:hover:bg-white/10 rounded-2xl transition-all text-slate-400">
                                    <X size={24} />
                                </button>
                            </div>

                            <form onSubmit={handleSave} className="p-10 space-y-8">
                                <div className="space-y-6">
                                    <div className="space-y-3">
                                        <label className="text-xs font-black text-slate-500 uppercase tracking-widest ml-1">Nombre Completo *</label>
                                        <div className="relative group">
                                            <Users className="absolute left-5 top-1/2 -translate-y-1/2 text-slate-500 group-focus-within:text-primary transition-colors" size={20} />
                                            <input 
                                                type="text" 
                                                required 
                                                value={formData.nombre}
                                                onChange={e => setFormData({...formData, nombre: e.target.value})}
                                                className="w-full pl-14 pr-6 py-5 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-200 dark:border-white/10 focus:border-primary focus:ring-4 focus:ring-primary/10 outline-none transition-all text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-slate-600 font-bold"
                                                placeholder="Ej. Corporativo GEMA"
                                            />
                                        </div>
                                    </div>

                                    <div className="space-y-3">
                                        <label className="text-xs font-black text-slate-500 uppercase tracking-widest ml-1">Contacto Directo</label>
                                        <div className="relative group">
                                            <Phone className="absolute left-5 top-1/2 -translate-y-1/2 text-slate-500 group-focus-within:text-primary transition-colors" size={20} />
                                            <input 
                                                type="text" 
                                                value={formData.contacto}
                                                onChange={e => setFormData({...formData, contacto: e.target.value})}
                                                className="w-full pl-14 pr-6 py-5 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-200 dark:border-white/10 focus:border-primary focus:ring-4 focus:ring-primary/10 outline-none transition-all text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-slate-600 font-bold"
                                                placeholder="Teléfono o Correo electrónico"
                                            />
                                        </div>
                                    </div>

                                    <div className="space-y-3">
                                        <label className="text-xs font-black text-slate-500 uppercase tracking-widest ml-1">Dirección / Ubicación</label>
                                        <div className="relative group">
                                            <MapPin className="absolute left-5 top-6 text-slate-500 group-focus-within:text-primary transition-colors" size={20} />
                                            <textarea 
                                                rows="3"
                                                value={formData.direccion}
                                                onChange={e => setFormData({...formData, direccion: e.target.value})}
                                                className="w-full pl-14 pr-6 py-5 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-200 dark:border-white/10 focus:border-primary focus:ring-4 focus:ring-primary/10 outline-none transition-all text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-slate-600 resize-none font-bold"
                                                placeholder="Dirección fiscal o de entrega..."
                                            />
                                        </div>
                                    </div>
                                </div>

                                <button 
                                    type="submit" 
                                    disabled={isSubmitting}
                                    className="w-full py-6 bg-primary hover:bg-primary/90 text-white font-black rounded-[2rem] shadow-2xl shadow-primary/20 transition-all hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-3 disabled:opacity-50 text-lg"
                                >
                                    {isSubmitting ? <Loader2 className="animate-spin" size={28} /> : (
                                        <>
                                            <PlusCircle size={28} />
                                            {editingClient ? 'GUARDAR CAMBIOS' : 'REGISTRAR CLIENTE'}
                                        </>
                                    )}
                                </button>
                            </form>
                        </motion.div>
                    </div>
                )}
            </AnimatePresence>

            {/* Modal de Confirmación de Eliminación */}
            <AnimatePresence>
                {clientToDelete && (
                    <div className="fixed inset-0 z-[60] flex items-center justify-center p-4">
                        <motion.div 
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            exit={{ opacity: 0 }}
                            onClick={() => setClientToDelete(null)}
                            className="absolute inset-0 bg-black/80 backdrop-blur-xl"
                        />
                        <motion.div 
                            initial={{ scale: 0.9, opacity: 0 }}
                            animate={{ scale: 1, opacity: 1 }}
                            exit={{ scale: 0.9, opacity: 0 }}
                            className="bg-white dark:bg-slate-900 relative z-10 p-10 text-center border border-slate-200 dark:border-rose-500/20 shadow-2xl rounded-[3rem]"
                        >
                            <div className="mx-auto flex items-center justify-center h-24 w-24 rounded-[2.5rem] bg-rose-500/10 mb-8 border border-rose-500/20">
                                <Trash2 className="h-10 w-10 text-rose-500" />
                            </div>
                            <h3 className="text-3xl font-black text-slate-900 dark:text-white mb-4 tracking-tighter">¿Eliminar Cliente?</h3>
                            <p className="text-slate-500 dark:text-slate-400 mb-10 text-lg leading-relaxed">
                                Estás a punto de eliminar a <span className="font-bold text-slate-900 dark:text-white underline decoration-rose-500/50">{clientToDelete.nombre}</span>. Esta acción es irreversible.
                            </p>
                            <div className="flex flex-col sm:flex-row gap-4">
                                <button
                                    onClick={() => setClientToDelete(null)}
                                    className="flex-1 py-5 bg-slate-100 dark:bg-white/5 hover:bg-slate-200 dark:hover:bg-white/10 text-slate-900 dark:text-white font-black rounded-2xl transition-all border border-slate-200 dark:border-white/5"
                                >
                                    CANCELAR
                                </button>
                                <button
                                    onClick={handleDelete}
                                    className="flex-1 py-5 bg-rose-600 hover:bg-rose-700 text-white font-black rounded-2xl transition-all shadow-xl shadow-rose-600/25"
                                >
                                    SÍ, ELIMINAR
                                </button>
                            </div>
                        </motion.div>
                    </div>
                )}
            </AnimatePresence>
        </motion.div>
    );
}
