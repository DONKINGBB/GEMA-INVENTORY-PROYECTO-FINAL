
import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { LogOut, User, Shield, Bell, Moon, Tags, Home as HomeIcon, Truck, Building2, LayoutGrid, PlusCircle, UserPlus, X, Loader2, Users, ChevronRight, Settings as SettingsIcon } from 'lucide-react';
import { useNavigate, useLocation } from 'react-router-dom';
import { businessService } from '../services/businessService';
import toast from 'react-hot-toast';
import { motion, AnimatePresence } from 'framer-motion';

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
                    idRol: 1 
                });
            }
            toast.success('¡Negocio creado con éxito!');
            setShowCreateModal(false);
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
                    idRol: 6 
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
                { icon: User, label: "Perfil de Usuario", desc: "Administra tu información personal", path: "/app/settings/profile", color: "text-blue-400" },
                { icon: Shield, label: "Seguridad", desc: "Cambiar contraseña y 2FA", path: "/app/settings/security", color: "text-indigo-400" }
            ]
        },
        {
            title: "Negocio",
            items: [
                { icon: Building2, label: "Mi Negocio", desc: "Información y código de invitación", path: "/app/settings/business", color: "text-emerald-400" },
                { icon: Users, label: "Gestión de Equipo", desc: "Controla quién tiene acceso y sus roles", path: "/app/settings/team", color: "text-purple-400" },
                { icon: LayoutGrid, label: "Cambiar de Negocio", desc: "Alternar entre tus inventarios", path: "/app/settings/switch-business", color: "text-amber-400" },
                { 
                    icon: PlusCircle, 
                    label: "Nuevo / Unirse", 
                    desc: "Crea o únete a una organización", 
                    action: () => setShowChoiceModal(true),
                    color: "text-rose-400"
                }
            ]
        },
        {
            title: "Inventario",
            items: [
                { icon: Tags, label: "Administrar Categorías", desc: "Crear y editar categorías de productos", path: "/app/settings/categories", color: "text-sky-400" },
                { icon: HomeIcon, label: "Administrar Almacenes", desc: "Gestionar sucursales internas", path: "/app/settings/warehouses", color: "text-cyan-400" },
                { icon: Truck, label: "Administrar Proveedores", desc: "Gestionar proveedores externos", path: "/app/settings/suppliers", color: "text-teal-400" }
            ]
        },
        {
            title: "Preferencias",
            items: [
                { icon: Bell, label: "Notificaciones", desc: "Configura tus alertas de stock", path: "/app/settings/notifications", color: "text-pink-400" },
                { icon: Moon, label: "Apariencia", desc: "Modo oscuro y temas", path: "/app/settings/appearance", color: "text-violet-400" }
            ]
        }
    ];

    const Modal = ({ show, onClose, title, children }) => (
        <AnimatePresence>
            {show && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                    <motion.div 
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        onClick={onClose}
                        className="absolute inset-0 bg-black/60 backdrop-blur-md"
                    />
                    <motion.div 
                        initial={{ scale: 0.9, opacity: 0, y: 20 }}
                        animate={{ scale: 1, opacity: 1, y: 0 }}
                        exit={{ scale: 0.9, opacity: 0, y: 20 }}
                        className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-white/10 w-full max-w-md overflow-hidden relative z-10 rounded-[2rem] shadow-2xl"
                    >
                        <div className="px-6 py-4 border-b border-slate-100 dark:border-white/5 flex justify-between items-center bg-slate-50 dark:bg-white/5">
                            <h3 className="text-xl font-bold text-slate-900 dark:text-white">{title}</h3>
                            <button onClick={onClose} className="p-2 hover:bg-slate-200 dark:hover:bg-white/10 rounded-full transition-colors text-slate-400">
                                <X size={20} />
                            </button>
                        </div>
                        <div className="p-6">
                            {children}
                        </div>
                    </motion.div>
                </div>
            )}
        </AnimatePresence>
    );

    const containerVariants = {
        hidden: { opacity: 0 },
        visible: {
            opacity: 1,
            transition: { staggerChildren: 0.05 }
        }
    };

    const sectionVariants = {
        hidden: { y: 20, opacity: 0 },
        visible: { y: 0, opacity: 1 }
    };

    return (
        <motion.div 
            initial="hidden"
            animate="visible"
            variants={containerVariants}
            className="max-w-2xl mx-auto space-y-8 pb-20 sm:pb-0"
        >
            <motion.div variants={sectionVariants} className="px-4 md:px-0">
                <div className="flex items-center gap-3 mb-2">
                    <div className="p-2 bg-primary/10 dark:bg-primary/20 rounded-lg text-primary">
                        <SettingsIcon size={24} />
                    </div>
                    <h1 className="text-3xl font-black text-slate-900 dark:text-white">
                        Configuración
                    </h1>
                </div>
                <p className="text-slate-500 dark:text-slate-400">Personaliza tu experiencia y gestiona tu organización</p>
            </motion.div>

            <div className="space-y-6 px-4 md:px-0">
                {sections.map((section, idx) => (
                    <motion.div 
                        key={idx} 
                        variants={sectionVariants}
                        className="bg-white dark:bg-slate-900/40 backdrop-blur-xl rounded-[2rem] overflow-hidden border border-slate-200 dark:border-white/5 shadow-xl shadow-black/5 dark:shadow-none"
                    >
                        <div className="px-6 py-4 bg-slate-50/50 dark:bg-white/5 border-b border-slate-200 dark:border-white/5">
                            <h3 className="font-black text-slate-500 uppercase tracking-[0.2em] text-[10px]">{section.title}</h3>
                        </div>
                        <div className="divide-y divide-slate-100 dark:divide-white/5">
                            {section.items.map((item, i) => (
                                <button 
                                    key={i} 
                                    onClick={(e) => {
                                        e.preventDefault();
                                        if (item.action) {
                                            item.action();
                                        } else if (item.path) {
                                            navigate(item.path);
                                        }
                                    }}
                                    className="w-full px-6 py-5 flex items-center gap-4 hover:bg-slate-50 dark:hover:bg-white/5 transition-all text-left group relative"
                                >
                                    <div className={`p-3 bg-slate-100 dark:bg-white/5 ${item.color} rounded-2xl group-hover:scale-110 group-hover:bg-white/10 transition-all duration-300`}>
                                        <item.icon size={22} />
                                    </div>
                                    <div className="flex-1">
                                        <h4 className="font-bold text-slate-900 dark:text-white group-hover:text-primary transition-colors leading-tight">{item.label}</h4>
                                        <p className="text-xs text-slate-500 mt-0.5 group-hover:text-slate-600 dark:group-hover:text-slate-400 transition-colors">{item.desc}</p>
                                    </div>
                                    <ChevronRight size={18} className="text-slate-400 dark:text-slate-600 group-hover:text-slate-900 dark:group-hover:text-white group-hover:translate-x-1 transition-all" />
                                </button>
                            ))}
                        </div>
                    </motion.div>
                ))}

                <motion.button
                    variants={sectionVariants}
                    onClick={logout}
                    whileHover={{ scale: 1.01 }}
                    whileTap={{ scale: 0.98 }}
                    className="w-full bg-rose-500/10 hover:bg-rose-500/20 text-rose-500 font-black py-5 rounded-3xl flex items-center justify-center gap-2 transition-all border border-rose-500/20 shadow-lg shadow-rose-500/5"
                >
                    <LogOut size={22} />
                    CERRAR SESIÓN
                </motion.button>
            </div>

            {/* Modal de Elección */}
            <Modal 
                show={showChoiceModal} 
                onClose={() => setShowChoiceModal(false)}
                title="Gestión de Negocio"
            >
                <div className="grid gap-4">
                    <button 
                        onClick={() => { setShowChoiceModal(false); setShowCreateModal(true); }}
                        className="flex items-center gap-4 p-5 bg-primary/10 rounded-2xl border border-primary/20 hover:bg-primary/20 transition-all text-left group"
                    >
                        <div className="p-3 bg-primary text-white rounded-xl group-hover:scale-110 transition-transform">
                            <PlusCircle size={24} />
                        </div>
                        <div>
                            <h4 className="font-bold text-slate-900 dark:text-white">Crear Negocio</h4>
                            <p className="text-xs text-slate-500 dark:text-slate-400">Inicia tu propia organización</p>
                        </div>
                    </button>
                    
                    <button 
                        onClick={() => { setShowChoiceModal(false); setShowJoinModal(true); }}
                        className="flex items-center gap-4 p-5 bg-purple-500/10 rounded-2xl border border-purple-500/20 hover:bg-purple-500/20 transition-all text-left group"
                    >
                        <div className="p-3 bg-purple-600 text-white rounded-xl group-hover:scale-110 transition-transform">
                            <UserPlus size={24} />
                        </div>
                        <div>
                            <h4 className="font-bold text-slate-900 dark:text-white">Unirse a uno</h4>
                            <p className="text-xs text-slate-500 dark:text-slate-400">Usa un código de invitación</p>
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
                <form onSubmit={handleCreateBusiness} className="space-y-5">
                    <div>
                        <label className="block text-xs font-black text-slate-500 uppercase tracking-widest mb-2 ml-1">Nombre Comercial</label>
                        <input 
                            type="text"
                            required
                            placeholder="Ej. Mi Tienda Increíble"
                            value={businessName}
                            onChange={(e) => setBusinessName(e.target.value)}
                            className="w-full px-5 py-4 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-200 dark:border-white/10 focus:border-primary focus:ring-4 focus:ring-primary/20 outline-none transition-all text-slate-900 dark:text-white placeholder:text-slate-400"
                        />
                    </div>
                    <button 
                        type="submit" 
                        disabled={loading}
                        className="w-full py-4 bg-primary text-white font-black rounded-2xl shadow-xl shadow-primary/20 hover:shadow-primary/40 transition-all flex items-center justify-center gap-2 disabled:opacity-50 active:scale-[0.98]"
                    >
                        {loading ? <Loader2 className="animate-spin" size={20} /> : 'CREAR NEGOCIO'}
                    </button>
                </form>
            </Modal>

            {/* Modal Unirse */}
            <Modal 
                show={showJoinModal} 
                onClose={() => setShowJoinModal(false)}
                title="Unirse a Negocio"
            >
                <form onSubmit={handleJoinBusiness} className="space-y-5">
                    <div>
                        <label className="block text-xs font-black text-slate-500 uppercase tracking-widest mb-2 ml-1">Código de Invitación</label>
                        <input 
                            type="text"
                            required
                            maxLength={8}
                            placeholder="CÓDIGO"
                            value={inviteCode}
                            onChange={(e) => setInviteCode(e.target.value)}
                            className="w-full px-5 py-6 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-200 dark:border-white/10 focus:border-primary focus:ring-4 focus:ring-primary/20 outline-none transition-all text-slate-900 dark:text-white placeholder:text-slate-300 text-center text-3xl font-black uppercase tracking-[0.3em]"
                        />
                        <p className="text-[10px] text-center text-slate-500 mt-3 uppercase tracking-tighter">Pide el código al administrador de la organización</p>
                    </div>
                    <button 
                        type="submit" 
                        disabled={loading}
                        className="w-full py-4 bg-primary text-white font-black rounded-2xl shadow-xl shadow-primary/20 hover:shadow-primary/40 transition-all flex items-center justify-center gap-2 disabled:opacity-50 active:scale-[0.98]"
                    >
                        {loading ? <Loader2 className="animate-spin" size={20} /> : 'UNIRSE AHORA'}
                    </button>
                </form>
            </Modal>
        </motion.div>
    );
}
