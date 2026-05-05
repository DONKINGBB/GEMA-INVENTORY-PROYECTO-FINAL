
import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { userService } from '../services/userService';
import { UserPlus, Trash2, Shield, User, Loader2, ArrowLeft, Search, Mail, UserCircle, Edit3, X, Check, MoreVertical, BadgeCheck, ShieldAlert } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { motion, AnimatePresence } from 'framer-motion';

const ROLES = [
    { id: 1, name: 'Propietario', color: 'bg-purple-500/20 text-purple-400 border-purple-500/20' },
    { id: 2, name: 'Administrador', color: 'bg-blue-500/20 text-blue-400 border-blue-500/20' },
    { id: 3, name: 'Supervisor', color: 'bg-indigo-500/20 text-indigo-400 border-indigo-500/20' },
    { id: 4, name: 'Vendedor', color: 'bg-emerald-500/20 text-emerald-400 border-emerald-500/20' },
    { id: 5, name: 'Repartidor', color: 'bg-orange-500/20 text-orange-400 border-orange-500/20' },
    { id: 6, name: 'Almacenista', color: 'bg-amber-500/20 text-amber-400 border-amber-500/20' },
];

export default function ManageTeam() {
    const { user: currentUser } = useAuth();
    const navigate = useNavigate();
    const [team, setTeam] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');
    const [showAddModal, setShowAddModal] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);
    const [submitting, setSubmitting] = useState(false);

    const [formData, setFormData] = useState({
        nombre: '',
        user: '',
        password: '',
        idRol: 6
    });

    useEffect(() => {
        fetchTeam();
    }, []);

    const fetchTeam = async () => {
        try {
            setLoading(true);
            const data = await userService.getTeam();
            setTeam(data);
        } catch (error) {
            toast.error('Error al cargar el equipo');
        } finally {
            setLoading(false);
        }
    };

    const handleAddUser = async (e) => {
        e.preventDefault();
        setSubmitting(true);
        try {
            await userService.addUser(formData);
            toast.success('Miembro añadido al equipo');
            setShowAddModal(false);
            setFormData({ nombre: '', user: '', password: '', idRol: 6 });
            fetchTeam();
        } catch (error) {
            toast.error(error.response?.data?.message || 'Error al añadir miembro');
        } finally {
            setSubmitting(false);
        }
    };

    const handleUpdateUser = async (e) => {
        e.preventDefault();
        setSubmitting(true);
        try {
            await userService.updateUser(selectedUser.id, formData);
            toast.success('Usuario actualizado');
            setShowEditModal(false);
            fetchTeam();
        } catch (error) {
            toast.error(error.response?.data?.message || 'Error al actualizar');
        } finally {
            setSubmitting(false);
        }
    };

    const handleDeleteUser = async (userId, userName) => {
        if (!window.confirm(`¿Estás seguro de que quieres eliminar a ${userName} del equipo?`)) return;
        
        try {
            await userService.deleteUser(userId);
            toast.success('Usuario eliminado');
            fetchTeam();
        } catch (error) {
            toast.error(error.response?.data?.message || 'No tienes permisos suficientes');
        }
    };

    const openEditModal = (user) => {
        setSelectedUser(user);
        setFormData({
            nombre: user.nombre,
            user: user.user,
            password: '', 
            idRol: user.idRol
        });
        setShowEditModal(true);
    };

    const filteredTeam = team.filter(u => 
        u.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
        u.user.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const getRoleInfo = (id) => ROLES.find(r => r.id === id) || ROLES[5];
    const canManage = currentUser?.idRol <= 2;

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
            className="max-w-6xl mx-auto space-y-8 pb-20 sm:pb-0"
        >
            {/* Header */}
            <motion.div variants={itemVariants} className="bg-white dark:bg-slate-900/40 backdrop-blur-xl border border-slate-200 dark:border-white/5 rounded-[2.5rem] p-6 flex flex-col md:flex-row md:items-center justify-between gap-4 shadow-xl shadow-black/5 dark:shadow-none">
                <div className="flex items-center gap-4">
                    <button 
                        onClick={() => navigate('/app/settings')}
                        className="p-3 bg-white dark:bg-white/5 hover:bg-slate-50 dark:hover:bg-white/10 rounded-2xl transition-all text-slate-900 dark:text-white border border-slate-200 dark:border-white/10 shadow-sm dark:shadow-none active:scale-95"
                    >
                        <ArrowLeft size={24} />
                    </button>
                    <div>
                        <h1 className="text-3xl font-black text-slate-900 dark:text-white">
                            Gestión de Equipo
                        </h1>
                        <p className="text-slate-500 dark:text-slate-400">Control de acceso y roles del negocio</p>
                    </div>
                </div>

                {canManage && (
                    <button 
                        onClick={() => {
                            setFormData({ nombre: '', user: '', password: '', idRol: 6 });
                            setShowAddModal(true);
                        }}
                        className="bg-primary hover:bg-primary/90 text-white px-6 py-4 rounded-2xl font-black shadow-lg shadow-primary/20 transition-all flex items-center gap-2 active:scale-95"
                    >
                        <UserPlus size={20} />
                        Añadir Miembro
                    </button>
                )}
            </motion.div>

            {/* Search and Stats */}
            <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
                <motion.div variants={itemVariants} className="lg:col-span-3 relative group">
                    <Search className="absolute left-5 top-1/2 -translate-y-1/2 text-slate-500 group-focus-within:text-primary transition-colors" size={20} />
                    <input 
                        type="text" 
                        placeholder="Buscar por nombre o correo..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="w-full pl-14 pr-6 py-5 rounded-[2rem] bg-white dark:bg-slate-900/40 backdrop-blur-xl border border-slate-200 dark:border-white/5 focus:border-primary focus:ring-4 focus:ring-primary/10 outline-none transition-all text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-slate-600 shadow-xl shadow-black/5 dark:shadow-none"
                    />
                </motion.div>
                <motion.div variants={itemVariants} className="bg-white dark:bg-slate-900/40 backdrop-blur-xl border border-slate-200 dark:border-primary/20 rounded-[2rem] p-5 flex items-center justify-center gap-4 shadow-xl shadow-black/5 dark:shadow-none">
                    <div className="p-3 bg-primary/20 text-primary rounded-2xl font-black text-2xl min-w-[3.5rem] text-center">
                        {team.length}
                    </div>
                    <span className="font-bold text-slate-500 dark:text-slate-400 leading-tight">Total de<br/>Miembros</span>
                </motion.div>
            </div>

            {/* Team List */}
            {loading ? (
                <div className="py-20 flex flex-col items-center justify-center space-y-4">
                    <div className="w-12 h-12 border-4 border-primary/20 border-t-primary rounded-full animate-spin"></div>
                    <p className="text-slate-500 animate-pulse font-bold tracking-widest text-xs uppercase">Sincronizando equipo...</p>
                </div>
            ) : (
                <motion.div 
                    variants={containerVariants}
                    className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6"
                >
                    <AnimatePresence>
                        {filteredTeam.map((member) => (
                            <motion.div 
                                key={member.id}
                                variants={itemVariants}
                                exit={{ opacity: 0, scale: 0.9 }}
                                className="group bg-white dark:bg-slate-900/40 backdrop-blur-xl p-6 border border-slate-200 dark:border-white/5 hover:border-primary/20 transition-all duration-500 relative overflow-hidden rounded-[2.5rem] shadow-xl shadow-black/5 dark:shadow-none"
                            >
                                {/* Role accent */}
                                <div className={`absolute top-0 left-0 w-1 h-full ${getRoleInfo(member.idRol).color.split(' ')[0].replace('/20', '')}`} />

                                <div className="flex items-start justify-between mb-6">
                                    <div className="relative">
                                        {member.imagenUrl ? (
                                            <img 
                                                src={member.imagenUrl} 
                                                alt={member.nombre} 
                                                className="w-16 h-16 rounded-[1.5rem] object-cover ring-2 ring-slate-100 dark:ring-white/10 shadow-2xl"
                                            />
                                        ) : (
                                            <div className="w-16 h-16 rounded-[1.5rem] bg-gradient-to-br from-primary to-indigo-600 flex items-center justify-center text-white text-2xl font-black shadow-2xl">
                                                {member.nombre.charAt(0)}
                                            </div>
                                        )}
                                        <div className="absolute -bottom-2 -right-2 p-1.5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-white/10 rounded-xl shadow-xl">
                                            <BadgeCheck size={14} className="text-primary" />
                                        </div>
                                    </div>

                                    {canManage && member.idRol !== 1 && (
                                        <div className="flex gap-1">
                                            <button 
                                                onClick={() => openEditModal(member)}
                                                className="p-2 text-slate-500 hover:text-primary hover:bg-primary/10 rounded-xl transition-all"
                                            >
                                                <Edit3 size={18} />
                                            </button>
                                            <button 
                                                onClick={() => handleDeleteUser(member.id, member.nombre)}
                                                className="p-2 text-slate-500 hover:text-rose-500 hover:bg-rose-500/5 rounded-xl transition-all"
                                            >
                                                <Trash2 size={18} />
                                            </button>
                                        </div>
                                    )}
                                </div>

                                <div className="space-y-4">
                                    <div>
                                        <h3 className="text-xl font-bold text-slate-900 dark:text-white truncate flex items-center gap-2">
                                            {member.nombre}
                                            {member.id === currentUser?.id && (
                                                <span className="text-[10px] font-black text-primary bg-primary/10 px-2 py-0.5 rounded-full border border-primary/20 uppercase tracking-tighter">Tú</span>
                                            )}
                                        </h3>
                                        <div className="flex items-center gap-2 text-slate-500 text-sm mt-1">
                                            <Mail size={14} className="text-slate-600" />
                                            <span className="truncate">{member.user}</span>
                                        </div>
                                    </div>

                                    <div className="flex items-center gap-2">
                                        <span className={`px-4 py-1.5 rounded-xl text-[10px] font-black uppercase tracking-widest border ${getRoleInfo(member.idRol).color}`}>
                                            {getRoleInfo(member.idRol).name}
                                        </span>
                                    </div>
                                </div>

                                {/* Background Decoration */}
                                <div className="absolute -bottom-6 -right-6 opacity-[0.03] group-hover:scale-125 transition-transform duration-700 pointer-events-none">
                                    <UserCircle size={140} />
                                </div>
                            </motion.div>
                        ))}
                    </AnimatePresence>
                </motion.div>
            )}

            {/* Empty State */}
            {!loading && filteredTeam.length === 0 && (
                <motion.div variants={itemVariants} className="bg-white dark:bg-slate-900/40 backdrop-blur-xl p-20 text-center border-2 border-dashed border-slate-200 dark:border-white/10 rounded-[3rem]">
                    <div className="w-24 h-24 bg-slate-100 dark:bg-white/5 rounded-[2.5rem] flex items-center justify-center mx-auto mb-6">
                        <UserCircle size={48} className="text-slate-400 dark:text-slate-700" />
                    </div>
                    <h3 className="text-2xl font-bold text-slate-900 dark:text-white mb-2">No se encontraron miembros</h3>
                    <p className="text-slate-400 max-w-sm mx-auto">
                        {searchTerm ? 'Prueba con otros términos de búsqueda' : 'Empieza por añadir a tu primer colaborador para gestionar tu inventario en equipo'}
                    </p>
                </motion.div>
            )}

            {/* Modal Components */}
            <AnimatePresence>
                {(showAddModal || showEditModal) && (
                    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                        <motion.div 
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            exit={{ opacity: 0 }}
                            onClick={() => { setShowAddModal(false); setShowEditModal(false); }}
                            className="absolute inset-0 bg-black/60 backdrop-blur-md"
                        />
                        <motion.div 
                            initial={{ scale: 0.9, opacity: 0, y: 20 }}
                            animate={{ scale: 1, opacity: 1, y: 0 }}
                            exit={{ scale: 0.9, opacity: 0, y: 20 }}
                            className="bg-white dark:bg-slate-900/90 backdrop-blur-2xl w-full max-w-lg overflow-hidden relative z-10 border border-slate-200 dark:border-white/10 shadow-2xl rounded-[2.5rem]"
                        >
                            <div className="px-8 py-6 border-b border-slate-100 dark:border-white/5 flex justify-between items-center bg-slate-50 dark:bg-white/5">
                                <div>
                                    <h3 className="text-2xl font-black text-slate-900 dark:text-white">
                                        {showAddModal ? 'Nuevo Miembro' : 'Editar Permisos'}
                                    </h3>
                                    <p className="text-sm text-slate-500">Configura el nivel de acceso del colaborador</p>
                                </div>
                                <button 
                                    onClick={() => { setShowAddModal(false); setShowEditModal(false); }} 
                                    className="p-3 bg-slate-100 dark:bg-white/5 hover:bg-slate-200 dark:hover:bg-white/10 rounded-2xl transition-all text-slate-400"
                                >
                                    <X size={20} />
                                </button>
                            </div>

                            <form onSubmit={showAddModal ? handleAddUser : handleUpdateUser} className="p-8 space-y-8">
                                <div className="space-y-6">
                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                        <div className="space-y-2">
                                            <label className="text-xs font-black text-slate-500 uppercase tracking-widest ml-1">Nombre Completo</label>
                                            <div className="relative group">
                                                <User className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 group-focus-within:text-primary transition-colors" size={18} />
                                                <input 
                                                    type="text"
                                                    required
                                                    placeholder="Nombre"
                                                    value={formData.nombre}
                                                    onChange={(e) => setFormData({...formData, nombre: e.target.value})}
                                                    className="w-full pl-12 pr-5 py-4 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-200 dark:border-white/5 focus:border-primary focus:ring-4 focus:ring-primary/10 outline-none transition-all text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-slate-600 font-bold"
                                                />
                                            </div>
                                        </div>
                                        <div className="space-y-2">
                                            <label className="text-xs font-black text-slate-500 uppercase tracking-widest ml-1">Correo Electrónico</label>
                                            <div className="relative group">
                                                <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 group-focus-within:text-primary transition-colors" size={18} />
                                                <input 
                                                    type="email"
                                                    required
                                                    disabled={showEditModal}
                                                    placeholder="email@ejemplo.com"
                                                    value={formData.user}
                                                    onChange={(e) => setFormData({...formData, user: e.target.value})}
                                                    className="w-full pl-12 pr-5 py-4 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-200 dark:border-white/5 focus:border-primary focus:ring-4 focus:ring-primary/10 outline-none transition-all text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-slate-600 disabled:opacity-40 font-bold"
                                                />
                                            </div>
                                        </div>
                                    </div>

                                    {showAddModal && (
                                        <div className="space-y-2">
                                            <label className="text-xs font-black text-slate-500 uppercase tracking-widest ml-1">Contraseña Temporal</label>
                                            <div className="relative group">
                                                <ShieldAlert className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500 group-focus-within:text-primary transition-colors" size={18} />
                                                <input 
                                                    type="password"
                                                    required
                                                    placeholder="Min. 6 caracteres"
                                                    value={formData.password}
                                                    onChange={(e) => setFormData({...formData, password: e.target.value})}
                                                    className="w-full pl-12 pr-5 py-4 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-200 dark:border-white/5 focus:border-primary focus:ring-4 focus:ring-primary/10 outline-none transition-all text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-slate-600 font-bold"
                                                />
                                            </div>
                                        </div>
                                    )}

                                    <div className="space-y-3">
                                        <label className="text-xs font-black text-slate-500 uppercase tracking-widest ml-1">Rol y Permisos</label>
                                        <div className="grid grid-cols-2 gap-3">
                                            {ROLES.filter(r => r.id !== 1).map((role) => (
                                                <button
                                                    key={role.id}
                                                    type="button"
                                                    onClick={() => setFormData({...formData, idRol: role.id})}
                                                    className={`flex items-center justify-between p-4 rounded-2xl border transition-all ${
                                                        formData.idRol === role.id 
                                                            ? 'border-primary bg-primary/10' 
                                                            : 'border-slate-200 dark:border-white/5 hover:border-slate-300 dark:hover:border-white/20 bg-slate-50 dark:bg-white/5'
                                                    }`}
                                                >
                                                    <div className="text-left">
                                                        <span className={`block font-bold text-sm ${formData.idRol === role.id ? 'text-primary' : 'text-slate-900 dark:text-white'}`}>{role.name}</span>
                                                        <span className="text-[9px] text-slate-500 uppercase font-black tracking-widest">Nivel {role.id}</span>
                                                    </div>
                                                    {formData.idRol === role.id && (
                                                        <div className="p-1 bg-primary text-white rounded-lg">
                                                            <Check size={14} />
                                                        </div>
                                                    )}
                                                </button>
                                            ))}
                                        </div>
                                    </div>
                                </div>

                                <button 
                                    type="submit" 
                                    disabled={submitting}
                                    className="w-full py-5 bg-primary hover:bg-primary/90 text-white font-black rounded-[1.5rem] shadow-xl shadow-primary/20 transition-all hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-3 disabled:opacity-50"
                                >
                                    {submitting ? <Loader2 className="animate-spin" size={24} /> : (
                                        <>
                                            {showAddModal ? <UserPlus size={24} /> : <Check size={24} />}
                                            {showAddModal ? 'AÑADIR AL EQUIPO' : 'GUARDAR CAMBIOS'}
                                        </>
                                    )}
                                </button>
                            </form>
                        </motion.div>
                    </div>
                )}
            </AnimatePresence>
        </motion.div>
    );
}
