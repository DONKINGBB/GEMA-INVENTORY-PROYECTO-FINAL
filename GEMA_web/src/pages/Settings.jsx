
import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { LogOut, User, Shield, Bell, Moon, Tags, Home as HomeIcon, Truck, Building2, LayoutGrid, PlusCircle, UserPlus, X, Loader2, Users } from 'lucide-react';
import { useNavigate, useLocation } from 'react-router-dom';
import { businessService } from '../services/businessService';
import toast from 'react-hot-toast';

export default function Settings() {
    const { user, logout, updateUser } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    
    const [showChoiceModal, setShowChoiceModal] = useState(false);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [showJoinModal, setShowJoinModal] = useState(false);
    
    const [businessName, setBusinessName] = useState('');
    const [inviteCode, setInviteCode] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (location.state?.openBusinessModal) {
            setShowChoiceModal(true);
            // Clear state so it doesn't reopen on refresh
            window.history.replaceState({}, document.title);
        }
    }, [location]);

    const handleCreateBusiness = async (e) => {
        e.preventDefault();
        if (!businessName.trim()) return;
        setLoading(true);
        try {
            const response = await businessService.createNegocio({ nombre: businessName });
            if (response.success && response.negocio) {
                updateUser({ 
                    idNegocio: response.negocio.idNegocio,
                    idRol: 1 // Creador es Propietario
                });
            }
            toast.success('¡Negocio creado con éxito!');
            setShowCreateModal(false);
            // Refresh app to apply changes
            setTimeout(() => {
                window.location.href = '/app';
            }, 500);
        } catch (error) {
            if (error.response?.status === 409) {
                toast.error('Este nombre ya está en uso');
            } else {
                toast.error('Error al crear el negocio');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleJoinBusiness = async (e) => {
        e.preventDefault();
        if (!inviteCode.trim()) return;
        setLoading(true);
        try {
            const response = await businessService.joinNegocio({ codigoInvitacion: inviteCode, userId: user?.id });
            if (response.success && response.negocio) {
                updateUser({ 
                    idNegocio: response.negocio.idNegocio,
                    idRol: 6 // Rol base al unirse
                });
            }
            toast.success('¡Te has unido al negocio!');
            setShowJoinModal(false);
            setTimeout(() => {
                window.location.href = '/app';
            }, 500);
        } catch (error) {
            toast.error('Código inválido o error al unirse');
        } finally {
            setLoading(false);
        }
    };

    const sections = [
        {
            title: "Cuenta",
            items: [
                { icon: User, label: "Perfil de Usuario", desc: "Administra tu información personal", path: "/app/settings/profile" },
                { icon: Shield, label: "Seguridad", desc: "Cambiar contraseña y 2FA", path: "/app/settings/security" }
            ]
        },
        {
            title: "Negocio",
            items: [
                { icon: Building2, label: "Mi Negocio", desc: "Información y código de invitación", path: "/app/settings/business" },
                { icon: Users, label: "Gestión de Equipo", desc: "Controla quién tiene acceso y sus roles", path: "/app/settings/team" },
                { icon: LayoutGrid, label: "Cambiar de Negocio", desc: "Alternar entre tus inventarios", path: "/app/settings/switch-business" },
                { 
                    icon: PlusCircle, 
                    label: "Nuevo / Unirse", 
                    desc: "Crea o únete a una organización", 
                    action: () => setShowChoiceModal(true) 
                }
            ]
        },
        {
            title: "Inventario",
            items: [
                { icon: Tags, label: "Administrar Categorías", desc: "Crear y editar categorías de productos", path: "/app/settings/categories" },
                { icon: HomeIcon, label: "Administrar Almacenes", desc: "Gestionar sucursales internas", path: "/app/settings/warehouses" },
                { icon: Truck, label: "Administrar Proveedores", desc: "Gestionar proveedores externos", path: "/app/settings/suppliers" }
            ]
        },
        {
            title: "Preferencias",
            items: [
                { icon: Bell, label: "Notificaciones", desc: "Configura tus alertas de stock", path: "/app/settings/notifications" },
                { icon: Moon, label: "Apariencia", desc: "Modo oscuro y temas", path: "/app/settings/appearance" }
            ]
        }
    ];

    const Modal = ({ show, onClose, title, children }) => {
        if (!show) return null;
        return (
            <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-in fade-in duration-200">
                <div className="bg-white dark:bg-slate-800 rounded-3xl shadow-2xl w-full max-w-md overflow-hidden animate-in zoom-in-95 duration-200">
                    <div className="px-6 py-4 border-b border-gray-100 dark:border-slate-700 flex justify-between items-center">
                        <h3 className="text-xl font-bold text-gray-900 dark:text-white">{title}</h3>
                        <button onClick={onClose} className="p-2 hover:bg-gray-100 dark:hover:bg-slate-700 rounded-full transition">
                            <X size={20} className="text-gray-500" />
                        </button>
                    </div>
                    <div className="p-6">
                        {children}
                    </div>
                </div>
            </div>
        );
    };

    return (
        <div className="max-w-2xl mx-auto py-8">
            <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-8 px-4 md:px-0">Configuración</h1>

            <div className="space-y-8 px-4 md:px-0">
                {sections.map((section, idx) => (
                    <div key={idx} className="bg-white dark:bg-slate-800 rounded-3xl shadow-sm border border-gray-100 dark:border-slate-700 overflow-hidden">
                        <div className="px-6 py-4 bg-gray-50 dark:bg-slate-900/30 border-b border-gray-100 dark:border-slate-700">
                            <h3 className="font-bold text-gray-700 dark:text-gray-300 uppercase tracking-wider text-xs">{section.title}</h3>
                        </div>
                        <div className="divide-y divide-gray-100 dark:divide-slate-700">
                            {section.items.map((item, i) => (
                                <button 
                                    key={i} 
                                    onClick={(e) => {
                                        e.preventDefault();
                                        if (item.action) {
                                            item.action();
                                        } else if (item.path) {
                                            if (document.startViewTransition) {
                                                document.startViewTransition(() => {
                                                    navigate(item.path);
                                                });
                                            } else {
                                                navigate(item.path);
                                            }
                                        }
                                    }}
                                    className="w-full px-6 py-5 flex items-center gap-4 hover:bg-gray-50 dark:hover:bg-slate-700/50 transition text-left group"
                                >
                                    <div className="p-3 bg-blue-50 dark:bg-slate-900 text-primary dark:text-blue-400 rounded-2xl group-hover:scale-110 transition-transform">
                                        <item.icon size={22} />
                                    </div>
                                    <div className="flex-1">
                                        <h4 className="font-bold text-gray-900 dark:text-white leading-tight">{item.label}</h4>
                                        <p className="text-sm text-gray-500 dark:text-gray-400 mt-0.5">{item.desc}</p>
                                    </div>
                                </button>
                            ))}
                        </div>
                    </div>
                ))}

                <button
                    onClick={logout}
                    className="w-full bg-red-50 dark:bg-red-900/10 hover:bg-red-100 dark:hover:bg-red-900/20 text-red-600 dark:text-red-400 font-black py-5 rounded-3xl flex items-center justify-center gap-2 transition-all hover:gap-4 border border-red-100 dark:border-red-900/30"
                >
                    <LogOut size={22} />
                    CERRAR SESIÓN
                </button>
            </div>

            {/* Modal de Elección */}
            <Modal 
                show={showChoiceModal} 
                onClose={() => setShowChoiceModal(false)}
                title="✨ Gestión de Negocio"
            >
                <div className="grid gap-4">
                    <button 
                        onClick={() => { setShowChoiceModal(false); setShowCreateModal(true); }}
                        className="flex items-center gap-4 p-4 bg-blue-50 dark:bg-blue-900/20 rounded-2xl border border-blue-100 dark:border-blue-900/30 hover:shadow-md transition text-left"
                    >
                        <div className="p-3 bg-primary text-white rounded-xl">
                            <PlusCircle size={24} />
                        </div>
                        <div>
                            <h4 className="font-bold text-gray-900 dark:text-white">Crear Negocio</h4>
                            <p className="text-sm text-gray-500 dark:text-gray-400">Inicia tu propia organización</p>
                        </div>
                    </button>
                    
                    <button 
                        onClick={() => { setShowChoiceModal(false); setShowJoinModal(true); }}
                        className="flex items-center gap-4 p-4 bg-purple-50 dark:bg-purple-900/20 rounded-2xl border border-purple-100 dark:border-purple-900/30 hover:shadow-md transition text-left"
                    >
                        <div className="p-3 bg-purple-600 text-white rounded-xl">
                            <UserPlus size={24} />
                        </div>
                        <div>
                            <h4 className="font-bold text-gray-900 dark:text-white">Unirse a uno</h4>
                            <p className="text-sm text-gray-500 dark:text-gray-400">Usa un código de invitación</p>
                        </div>
                    </button>
                </div>
            </Modal>

            {/* Modal Crear */}
            <Modal 
                show={showCreateModal} 
                onClose={() => setShowCreateModal(false)}
                title="Nuevo Negocio"
            >
                <form onSubmit={handleCreateBusiness} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Nombre Comercial</label>
                        <input 
                            type="text"
                            required
                            placeholder="Ej. Mi Tienda Increíble"
                            value={businessName}
                            onChange={(e) => setBusinessName(e.target.value)}
                            className="w-full px-4 py-3 rounded-xl bg-gray-50 dark:bg-slate-900 border border-gray-200 dark:border-slate-700 focus:ring-2 focus:ring-primary outline-none transition dark:text-white"
                        />
                    </div>
                    <button 
                        type="submit" 
                        disabled={loading}
                        className="w-full py-4 bg-primary text-white font-bold rounded-xl shadow-lg hover:shadow-primary/30 transition flex items-center justify-center gap-2"
                    >
                        {loading ? <Loader2 className="animate-spin" size={20} /> : 'Crear Negocio'}
                    </button>
                </form>
            </Modal>

            {/* Modal Unirse */}
            <Modal 
                show={showJoinModal} 
                onClose={() => setShowJoinModal(false)}
                title="Unirse a Negocio"
            >
                <form onSubmit={handleJoinBusiness} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Código de Invitación</label>
                        <input 
                            type="text"
                            required
                            maxLength={8}
                            placeholder="Código de 8 caracteres"
                            value={inviteCode}
                            onChange={(e) => setInviteCode(e.target.value)}
                            className="w-full px-4 py-3 rounded-xl bg-gray-50 dark:bg-slate-900 border border-gray-200 dark:border-slate-700 focus:ring-2 focus:ring-primary outline-none transition uppercase tracking-widest text-center text-xl font-bold dark:text-white"
                        />
                    </div>
                    <button 
                        type="submit" 
                        disabled={loading}
                        className="w-full py-4 bg-primary text-white font-bold rounded-xl shadow-lg hover:shadow-primary/30 transition flex items-center justify-center gap-2"
                    >
                        {loading ? <Loader2 className="animate-spin" size={20} /> : 'Unirse ahora'}
                    </button>
                </form>
            </Modal>
        </div>
    );
}
