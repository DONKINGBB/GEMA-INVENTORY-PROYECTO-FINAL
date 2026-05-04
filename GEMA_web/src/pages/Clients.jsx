import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { clientService } from '../services/dataService';
import { Users, Phone, MapPin, Mail, Search, PlusCircle, Loader, X, Edit, Trash2 } from 'lucide-react';

export default function Clients() {
    const { user } = useAuth();
    const [clients, setClients] = useState([]);
    const [loading, setLoading] = useState(true);

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
            alert("Error al guardar cliente");
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
            alert("Error al eliminar cliente");
        }
    };

    return (
        <div className="space-y-6 transition-colors">
            <div className="flex justify-between items-center bg-white dark:bg-slate-800 p-6 rounded-2xl shadow-sm border border-gray-100 dark:border-slate-700 transition-colors">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Clientes</h1>
                    <p className="text-gray-500 dark:text-gray-400">Directorio de clientes y contactos</p>
                </div>
                <button 
                    onClick={() => handleOpenModal()} 
                    className="bg-primary text-white px-5 py-2.5 rounded-lg flex items-center gap-2 transition shadow-md hover:shadow-lg font-medium hover:bg-primary-dark"
                >
                    <PlusCircle size={20} />
                    <span>Nuevo Cliente</span>
                </button>
            </div>

            {loading ? (
                <div className="flex justify-center p-12"><Loader className="animate-spin text-primary w-10 h-10" /></div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
                    {clients.map(client => (
                        <div key={client.idCliente || client.id} className="bg-white dark:bg-slate-800 p-6 rounded-2xl shadow-sm border border-gray-100 dark:border-slate-700 hover:shadow-md transition group flex flex-col h-full transition-colors">
                            <div className="flex items-start justify-between mb-4">
                                <div className="flex items-center gap-4">
                                    <div className="w-14 h-14 rounded-xl bg-gradient-to-br from-accent/20 to-primary/10 dark:from-blue-900/40 dark:to-slate-900/30 flex items-center justify-center text-primary dark:text-blue-400 font-bold text-xl shadow-inner border border-primary/20 dark:border-blue-500/20">
                                        {client.nombre?.charAt(0)?.toUpperCase() || "C"}
                                    </div>
                                    <div>
                                        <h3 className="font-bold text-lg text-gray-900 dark:text-white group-hover:text-primary dark:group-hover:text-blue-400 transition-colors">{client.nombre}</h3>
                                        <span className="text-[10px] px-2.5 py-1 bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400 rounded-full font-semibold tracking-wide uppercase mt-1 inline-block">Cliente</span>
                                    </div>
                                </div>
                                <button onClick={() => confirmDelete(client)} className="text-gray-300 dark:text-slate-600 hover:text-red-500 transition-colors p-1" title="Eliminar Cliente">
                                    <Trash2 size={18} />
                                </button>
                            </div>

                            <div className="space-y-2.5 text-sm text-gray-600 dark:text-gray-300 flex-1 my-4">
                                <div className="flex items-center gap-3 p-2.5 rounded-lg hover:bg-gray-50 dark:hover:bg-slate-700/50 transition-colors border border-transparent hover:border-gray-100 dark:hover:border-slate-600">
                                    <div className="bg-blue-50 dark:bg-blue-900/30 p-1.5 rounded-md"><Phone size={16} className="text-primary dark:text-blue-400" /></div>
                                    <span className="font-medium text-gray-800 dark:text-gray-200">{client.contacto || "Sin contacto"}</span>
                                </div>
                                <div className="flex items-center gap-3 p-2.5 rounded-lg hover:bg-gray-50 dark:hover:bg-slate-700/50 transition-colors border border-transparent hover:border-gray-100 dark:hover:border-slate-600">
                                    <div className="bg-blue-50 dark:bg-blue-900/30 p-1.5 rounded-md"><MapPin size={16} className="text-primary dark:text-blue-400" /></div>
                                    <span className="line-clamp-2 text-gray-700 dark:text-gray-300 leading-relaxed text-sm">{client.direccion || "Sin dirección"}</span>
                                </div>
                            </div>

                            <div className="mt-auto pt-4 border-t border-gray-100 dark:border-slate-700 flex gap-3">
                                <button className="flex-1 py-2.5 text-primary dark:text-blue-400 font-semibold hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition text-xs uppercase tracking-wider border border-blue-100 dark:border-blue-900/50">
                                    Ver Historial
                                </button>
                                <button onClick={() => handleOpenModal(client)} className="flex-1 py-2.5 text-gray-600 dark:text-gray-300 font-semibold hover:bg-gray-100 dark:hover:bg-slate-700 rounded-lg transition text-xs uppercase tracking-wider border border-gray-200 dark:border-slate-600">
                                    <span className="flex items-center justify-center gap-1.5"><Edit size={14}/> Editar</span>
                                </button>
                            </div>
                        </div>
                    ))}
                    {clients.length === 0 && (
                        <div className="col-span-full bg-white dark:bg-slate-800 rounded-2xl p-16 border border-gray-200 dark:border-slate-700 border-dashed flex flex-col items-center justify-center text-center transition-colors">
                            <div className="bg-blue-50 dark:bg-blue-900/30 p-4 rounded-full mb-4">
                                <Users size={32} className="text-primary dark:text-blue-400" />
                            </div>
                            <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2">No hay clientes</h3>
                            <p className="text-gray-500 dark:text-gray-400 max-w-sm">Aún no has registrado ningún cliente. Agrega uno nuevo para empezar a gestionar tus contactos.</p>
                            <button onClick={() => handleOpenModal()} className="mt-6 bg-white dark:bg-slate-800 border-2 border-primary dark:border-blue-500 text-primary dark:text-blue-400 hover:bg-primary dark:hover:bg-blue-500 hover:text-white dark:hover:text-white px-6 py-2 rounded-lg font-bold transition-colors">
                                Crear mi primer cliente
                            </button>
                        </div>
                    )}
                </div>
            )}

            {isModalOpen && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                    <div className="absolute inset-0 bg-black/40 backdrop-blur-sm" onClick={handleCloseModal}></div>
                    <div className="bg-white dark:bg-slate-800 rounded-2xl shadow-2xl w-full max-w-md relative z-10 overflow-hidden flex flex-col max-h-[90vh] border border-gray-100 dark:border-slate-700 transition-colors">
                        <div className="p-6 border-b border-gray-100 dark:border-slate-700 flex justify-between items-center bg-gray-50/50 dark:bg-slate-900/50">
                            <h2 className="text-xl font-bold text-gray-800 dark:text-white">
                                {editingClient ? 'Editar Cliente' : 'Nuevo Cliente'}
                            </h2>
                            <button onClick={handleCloseModal} className="text-gray-400 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/30 p-2 rounded-lg transition-colors">
                                <X size={20} />
                            </button>
                        </div>
                        <div className="p-6 overflow-y-auto">
                            <form id="clientForm" onSubmit={handleSave} className="space-y-4">
                                <div>
                                    <label className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-1">Nombre Completo *</label>
                                    <input 
                                        type="text" 
                                        required 
                                        value={formData.nombre}
                                        onChange={e => setFormData({...formData, nombre: e.target.value})}
                                        className="w-full px-4 py-2.5 bg-gray-50 dark:bg-slate-700 border border-gray-200 dark:border-slate-600 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary dark:focus:border-blue-500 text-gray-900 dark:text-white transition-all outline-none"
                                        placeholder="Ej. Juan Pérez"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-1">Contacto (Tel o Email)</label>
                                    <input 
                                        type="text" 
                                        value={formData.contacto}
                                        onChange={e => setFormData({...formData, contacto: e.target.value})}
                                        className="w-full px-4 py-2.5 bg-gray-50 dark:bg-slate-700 border border-gray-200 dark:border-slate-600 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary dark:focus:border-blue-500 text-gray-900 dark:text-white transition-all outline-none"
                                        placeholder="Ej. 555-0192 o juan@correo.com"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-1">Dirección</label>
                                    <textarea 
                                        rows="3"
                                        value={formData.direccion}
                                        onChange={e => setFormData({...formData, direccion: e.target.value})}
                                        className="w-full px-4 py-2.5 bg-gray-50 dark:bg-slate-700 border border-gray-200 dark:border-slate-600 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary dark:focus:border-blue-500 text-gray-900 dark:text-white transition-all resize-none outline-none"
                                        placeholder="Ej. Calle Principal #123"
                                    />
                                </div>
                            </form>
                        </div>
                        <div className="p-6 border-t border-gray-100 dark:border-slate-700 bg-gray-50 dark:bg-slate-900/50 flex gap-3">
                            <button 
                                type="button" 
                                onClick={handleCloseModal}
                                className="flex-1 px-4 py-2.5 border border-gray-300 dark:border-slate-600 text-gray-700 dark:text-gray-300 font-bold rounded-xl hover:bg-gray-100 dark:hover:bg-slate-700 transition shadow-sm"
                            >
                                Cancelar
                            </button>
                            <button 
                                type="submit" 
                                form="clientForm"
                                disabled={isSubmitting}
                                className="flex-1 px-4 py-2.5 bg-primary dark:bg-blue-600 text-white font-bold rounded-xl hover:bg-primary-dark dark:hover:bg-blue-700 transition shadow-md disabled:opacity-50"
                            >
                                {isSubmitting ? 'Guardando...' : 'Guardar Cliente'}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {clientToDelete && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                    <div className="absolute inset-0 bg-black/40 backdrop-blur-sm" onClick={() => setClientToDelete(null)}></div>
                    <div className="bg-white dark:bg-slate-800 rounded-2xl shadow-2xl w-full max-w-sm relative z-10 overflow-hidden transform transition-all scale-100 p-6 text-center border border-gray-100 dark:border-slate-700">
                        <div className="mx-auto flex items-center justify-center h-16 w-16 rounded-full bg-red-100 dark:bg-red-900/30 mb-6">
                            <Trash2 className="h-8 w-8 text-red-600 dark:text-red-400" />
                        </div>
                        <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2">¿Eliminar Cliente?</h3>
                        <p className="text-gray-500 dark:text-gray-400 mb-8">
                            Estás a punto de eliminar a <span className="font-bold text-gray-700 dark:text-gray-300">{clientToDelete.nombre}</span>. Esta acción no se puede deshacer.
                        </p>
                        <div className="flex gap-3">
                            <button
                                type="button"
                                onClick={() => setClientToDelete(null)}
                                className="flex-1 py-2.5 bg-gray-100 dark:bg-slate-700 text-gray-700 dark:text-gray-300 font-bold rounded-xl hover:bg-gray-200 dark:hover:bg-slate-600 transition"
                            >
                                Cancelar
                            </button>
                            <button
                                type="button"
                                onClick={handleDelete}
                                className="flex-1 py-2.5 bg-red-600 text-white font-bold rounded-xl hover:bg-red-700 transition shadow-md"
                            >
                                Sí, eliminar
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
