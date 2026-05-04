
import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { userService } from '../services/userService';
import { UserPlus, Trash2, Shield, User, Loader2, ArrowLeft, Search, Mail, UserCircle, Edit3, X, Check } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';

const ROLES = [
    { id: 1, name: 'Propietario', color: 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400' },
    { id: 2, name: 'Administrador', color: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400' },
    { id: 3, name: 'Supervisor', color: 'bg-indigo-100 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-400' },
    { id: 4, name: 'Vendedor', color: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400' },
    { id: 5, name: 'Repartidor', color: 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400' },
    { id: 6, name: 'Almacenista', color: 'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-400' },
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

    // Form states for adding/editing
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
            password: '', // Password stays empty unless changing
            idRol: user.idRol
        });
        setShowEditModal(true);
    };

    const filteredTeam = team.filter(u => 
        u.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
        u.user.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const getRoleInfo = (id) => ROLES.find(r => r.id === id) || ROLES[5];

    // Check if current user has permission to manage team
    const canManage = currentUser?.idRol <= 2; // Owner and Admin

    if (loading) {
        return (
            <div className="flex flex-col items-center justify-center min-h-[60vh]">
                <Loader2 className="w-12 h-12 text-primary animate-spin" />
                <p className="mt-4 text-gray-500 font-medium">Cargando equipo...</p>
            </div>
        );
    }

    return (
        <div className="max-w-6xl mx-auto py-8 px-4">
            {/* Header */}
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
                <div className="flex items-center gap-4">
                    <button 
                        onClick={() => navigate('/app/settings')}
                        className="p-2 hover:bg-gray-100 dark:hover:bg-slate-800 rounded-full transition"
                    >
                        <ArrowLeft size={24} />
                    </button>
                    <div>
                        <h1 className="text-3xl font-black text-gray-900 dark:text-white tracking-tight">Gestión de Equipo</h1>
                        <p className="text-gray-500 dark:text-gray-400">Administra los miembros y permisos de tu negocio</p>
                    </div>
                </div>

                {canManage && (
                    <button 
                        onClick={() => {
                            setFormData({ nombre: '', user: '', password: '', idRol: 6 });
                            setShowAddModal(true);
                        }}
                        className="flex items-center gap-2 bg-primary hover:bg-blue-600 text-white px-6 py-3 rounded-2xl font-bold shadow-lg shadow-primary/20 transition-all hover:scale-105 active:scale-95"
                    >
                        <UserPlus size={20} />
                        Añadir Miembro
                    </button>
                )}
            </div>

            {/* Search and Stats */}
            <div className="grid grid-cols-1 lg:grid-cols-4 gap-6 mb-8">
                <div className="lg:col-span-3 relative">
                    <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
                    <input 
                        type="text" 
                        placeholder="Buscar por nombre o correo..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="w-full pl-12 pr-4 py-4 rounded-3xl bg-white dark:bg-slate-800 border border-gray-100 dark:border-slate-700 shadow-sm focus:ring-2 focus:ring-primary outline-none transition dark:text-white"
                    />
                </div>
                <div className="bg-white dark:bg-slate-800 p-4 rounded-3xl border border-gray-100 dark:border-slate-700 flex items-center justify-center gap-4 shadow-sm">
                    <div className="p-3 bg-blue-50 dark:bg-blue-900/20 text-primary rounded-2xl font-black text-xl">
                        {team.length}
                    </div>
                    <span className="font-bold text-gray-500 dark:text-gray-400">Total Miembros</span>
                </div>
            </div>

            {/* Team List */}
            <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
                {filteredTeam.map((member) => (
                    <div 
                        key={member.id}
                        className="group bg-white dark:bg-slate-800 rounded-[2.5rem] p-6 border border-gray-100 dark:border-slate-700 shadow-sm hover:shadow-xl transition-all duration-300 relative overflow-hidden"
                    >
                        {/* Status bar */}
                        <div className={`absolute top-0 left-0 w-full h-2 ${getRoleInfo(member.idRol).color.split(' ')[0]}`} />

                        <div className="flex items-start justify-between mb-4">
                            <div className="relative">
                                {member.imagenUrl ? (
                                    <img 
                                        src={member.imagenUrl} 
                                        alt={member.nombre} 
                                        className="w-16 h-16 rounded-3xl object-cover ring-4 ring-gray-50 dark:ring-slate-900 shadow-lg"
                                    />
                                ) : (
                                    <div className="w-16 h-16 rounded-3xl bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white text-2xl font-black shadow-lg">
                                        {member.nombre.charAt(0)}
                                    </div>
                                )}
                                <div className="absolute -bottom-1 -right-1 p-1.5 bg-white dark:bg-slate-800 rounded-xl shadow-md border border-gray-100 dark:border-slate-700">
                                    <Shield size={14} className="text-primary" />
                                </div>
                            </div>

                            {canManage && member.idRol !== 1 && (
                                <div className="flex gap-2">
                                    <button 
                                        onClick={() => openEditModal(member)}
                                        className="p-2 text-gray-400 hover:text-blue-500 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-xl transition-colors"
                                    >
                                        <Edit3 size={18} />
                                    </button>
                                    <button 
                                        onClick={() => handleDeleteUser(member.id, member.nombre)}
                                        className="p-2 text-gray-400 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-xl transition-colors"
                                    >
                                        <Trash2 size={18} />
                                    </button>
                                </div>
                            )}
                        </div>

                        <div className="space-y-3">
                            <div>
                                <h3 className="text-xl font-black text-gray-900 dark:text-white truncate">
                                    {member.nombre}
                                    {member.id === currentUser?.id && (
                                        <span className="ml-2 text-xs font-bold text-primary bg-blue-50 dark:bg-blue-900/30 px-2 py-1 rounded-lg">TÚ</span>
                                    )}
                                </h3>
                                <div className="flex items-center gap-2 text-gray-500 dark:text-gray-400 text-sm mt-1">
                                    <Mail size={14} />
                                    <span className="truncate">{member.user}</span>
                                </div>
                            </div>

                            <div className="flex flex-wrap gap-2">
                                <span className={`px-4 py-1.5 rounded-xl text-xs font-black uppercase tracking-wider ${getRoleInfo(member.idRol).color}`}>
                                    {getRoleInfo(member.idRol).name}
                                </span>
                            </div>
                        </div>

                        {/* Background Decoration */}
                        <div className="absolute -bottom-4 -right-4 opacity-[0.03] dark:opacity-[0.05] group-hover:scale-125 transition-transform duration-500 pointer-events-none">
                            <UserCircle size={120} />
                        </div>
                    </div>
                ))}
            </div>

            {/* Empty State */}
            {filteredTeam.length === 0 && (
                <div className="bg-white dark:bg-slate-800 rounded-[3rem] p-20 text-center border-2 border-dashed border-gray-200 dark:border-slate-700">
                    <div className="w-24 h-24 bg-gray-50 dark:bg-slate-900 rounded-[2rem] flex items-center justify-center mx-auto mb-6">
                        <UserCircle size={48} className="text-gray-300" />
                    </div>
                    <h3 className="text-2xl font-black text-gray-900 dark:text-white mb-2">No se encontraron miembros</h3>
                    <p className="text-gray-500 dark:text-gray-400 max-w-sm mx-auto">
                        {searchTerm ? 'Prueba con otros términos de búsqueda' : 'Empieza por añadir a tu primer colaborador'}
                    </p>
                </div>
            )}

            {/* Modal Components */}
            {(showAddModal || showEditModal) && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-in fade-in duration-200">
                    <div className="bg-white dark:bg-slate-800 rounded-[2.5rem] shadow-2xl w-full max-w-lg overflow-hidden animate-in zoom-in-95 duration-200">
                        <div className="px-8 py-6 border-b border-gray-100 dark:border-slate-700 flex justify-between items-center bg-gray-50 dark:bg-slate-900/50">
                            <div>
                                <h3 className="text-2xl font-black text-gray-900 dark:text-white">
                                    {showAddModal ? 'Nuevo Miembro' : 'Editar Permisos'}
                                </h3>
                                <p className="text-sm text-gray-500">Configura el acceso para tu equipo</p>
                            </div>
                            <button 
                                onClick={() => { setShowAddModal(false); setShowEditModal(false); }} 
                                className="p-3 hover:bg-white dark:hover:bg-slate-800 rounded-2xl transition shadow-sm border border-transparent hover:border-gray-200 dark:hover:border-slate-700"
                            >
                                <X size={20} className="text-gray-500" />
                            </button>
                        </div>

                        <form onSubmit={showAddModal ? handleAddUser : handleUpdateUser} className="p-8 space-y-6">
                            <div className="space-y-4">
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    <div>
                                        <label className="block text-sm font-bold text-gray-700 dark:text-gray-300 mb-2 px-1">Nombre Completo</label>
                                        <div className="relative">
                                            <User className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
                                            <input 
                                                type="text"
                                                required
                                                placeholder="Nombre"
                                                value={formData.nombre}
                                                onChange={(e) => setFormData({...formData, nombre: e.target.value})}
                                                className="w-full pl-12 pr-4 py-3.5 rounded-2xl bg-gray-50 dark:bg-slate-900 border border-gray-100 dark:border-slate-700 focus:ring-2 focus:ring-primary outline-none transition dark:text-white font-medium"
                                            />
                                        </div>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-bold text-gray-700 dark:text-gray-300 mb-2 px-1">Correo Electrónico</label>
                                        <div className="relative">
                                            <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
                                            <input 
                                                type="email"
                                                required
                                                disabled={showEditModal}
                                                placeholder="email@ejemplo.com"
                                                value={formData.user}
                                                onChange={(e) => setFormData({...formData, user: e.target.value})}
                                                className="w-full pl-12 pr-4 py-3.5 rounded-2xl bg-gray-50 dark:bg-slate-900 border border-gray-100 dark:border-slate-700 focus:ring-2 focus:ring-primary outline-none transition dark:text-white font-medium disabled:opacity-50"
                                            />
                                        </div>
                                    </div>
                                </div>

                                {showAddModal && (
                                    <div>
                                        <label className="block text-sm font-bold text-gray-700 dark:text-gray-300 mb-2 px-1">Contraseña Temporal</label>
                                        <input 
                                            type="password"
                                            required
                                            placeholder="Min. 6 caracteres"
                                            value={formData.password}
                                            onChange={(e) => setFormData({...formData, password: e.target.value})}
                                            className="w-full px-5 py-3.5 rounded-2xl bg-gray-50 dark:bg-slate-900 border border-gray-100 dark:border-slate-700 focus:ring-2 focus:ring-primary outline-none transition dark:text-white font-medium"
                                        />
                                    </div>
                                )}

                                <div>
                                    <label className="block text-sm font-bold text-gray-700 dark:text-gray-300 mb-2 px-1">Rol en el Negocio</label>
                                    <div className="grid grid-cols-2 gap-3">
                                        {ROLES.filter(r => r.id !== 1).map((role) => (
                                            <button
                                                key={role.id}
                                                type="button"
                                                onClick={() => setFormData({...formData, idRol: role.id})}
                                                className={`flex items-center justify-between p-4 rounded-2xl border-2 transition-all ${
                                                    formData.idRol === role.id 
                                                        ? 'border-primary bg-blue-50 dark:bg-blue-900/20' 
                                                        : 'border-gray-100 dark:border-slate-700 hover:border-gray-200 dark:hover:border-slate-600 bg-white dark:bg-slate-900'
                                                }`}
                                            >
                                                <div className="text-left">
                                                    <span className="block font-black text-gray-900 dark:text-white text-sm">{role.name}</span>
                                                    <span className="text-[10px] text-gray-500 uppercase font-bold tracking-tighter">Acceso Nivel {role.id}</span>
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
                                className="w-full py-4 bg-primary hover:bg-blue-600 text-white font-black rounded-2xl shadow-xl shadow-primary/30 transition-all hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-3 disabled:opacity-50"
                            >
                                {submitting ? <Loader2 className="animate-spin" size={24} /> : (
                                    <>
                                        {showAddModal ? <UserPlus size={24} /> : <Check size={24} />}
                                        {showAddModal ? 'AÑADIR AL EQUIPO' : 'GUARDAR CAMBIOS'}
                                    </>
                                )}
                            </button>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}
