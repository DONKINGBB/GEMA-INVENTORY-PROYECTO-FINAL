import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Building2, UserPlus, ArrowRight, Loader2, QrCode } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import api from '../services/api';
import toast from 'react-hot-toast';

export default function Onboarding() {
    const { user, updateUser } = useAuth();
    const navigate = useNavigate();
    const [view, setView] = useState('select'); // 'select', 'create', 'join'
    const [businessName, setBusinessName] = useState('');
    const [inviteCode, setInviteCode] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const handleCreateBusiness = async (e) => {
        e.preventDefault();
        if (!businessName.trim()) return;

        setIsLoading(true);
        try {
            const res = await api.post('/negocios/create', { nombre: businessName });
            if (res.data.success) {
                toast.success('¡Negocio creado con éxito!');
                // Update local user state with the new business ID and Role (Admin = 1)
                const updatedUser = { 
                    ...user, 
                    idNegocio: res.data.negocio.idNegocio,
                    idRol: 1,
                    rol: { idRol: 1, nombre: 'ROLE_ADMIN' } 
                };
                updateUser(updatedUser);
                navigate('/app', { state: { showTutorial: true } });
            } else {
                toast.error(res.data.message || 'Error al crear el negocio');
            }
        } catch (error) {
            console.error(error);
            toast.error(error.response?.data?.message || 'Error de conexión con el servidor');
        } finally {
            setIsLoading(false);
        }
    };

    const handleJoinBusiness = async (e) => {
        e.preventDefault();
        if (!inviteCode.trim()) return;

        setIsLoading(true);
        try {
            const res = await api.post('/negocios/join', { codigoInvitacion: inviteCode });
            if (res.data.success) {
                toast.success('¡Te has unido al negocio!');
                // Update local user state
                const updatedUser = { 
                    ...user, 
                    idNegocio: res.data.negocio.idNegocio,
                    idRol: 6,
                    rol: { idRol: 6, nombre: 'ROLE_OPERARIO' }
                };
                updateUser(updatedUser);
                navigate('/app', { state: { showTutorial: true } });
            } else {
                toast.error(res.data.message || 'Código inválido');
            }
        } catch (error) {
            console.error(error);
            toast.error(error.response?.data?.message || 'Error al intentar unirse');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center p-4 relative overflow-hidden">
            <div className="bg-mesh" />
            
            <motion.div 
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                className="w-full max-w-2xl relative z-10"
            >
                <div className="glass-card rounded-[3rem] p-8 md:p-16 text-center">
                    <AnimatePresence mode="wait">
                        {view === 'select' && (
                            <motion.div
                                key="select"
                                initial={{ opacity: 0, x: 20 }}
                                animate={{ opacity: 1, x: 0 }}
                                exit={{ opacity: 0, x: -20 }}
                                className="space-y-8"
                            >
                                <div className="space-y-4">
                                    <h1 className="text-4xl md:text-5xl font-black text-slate-800 dark:text-white">
                                        ¡Bienvenido, <span className="text-blue-600 dark:text-blue-400">{user?.nombre?.split(' ')[0]}</span>!
                                    </h1>
                                    <p className="text-slate-500 dark:text-slate-400 text-lg max-w-md mx-auto">
                                        Para comenzar a gestionar tus inventarios, necesitas pertenecer a un negocio.
                                    </p>
                                </div>

                                <div className="grid md:grid-cols-2 gap-6">
                                    <button 
                                        onClick={() => setView('create')}
                                        className="group p-8 rounded-3xl border-2 border-slate-200 dark:border-white/10 hover:border-blue-500/50 hover:bg-blue-500/5 transition-all text-left"
                                    >
                                        <div className="w-14 h-14 bg-blue-500/10 rounded-2xl flex items-center justify-center mb-6 group-hover:scale-110 transition-transform">
                                            <Building2 className="text-blue-600 dark:text-blue-400" size={32} />
                                        </div>
                                        <h3 className="text-xl font-bold text-slate-800 dark:text-white mb-2">Crear mi propio negocio</h3>
                                        <p className="text-slate-500 dark:text-slate-400 text-sm">Serás el administrador y podrás invitar a tu equipo.</p>
                                    </button>

                                    <button 
                                        onClick={() => setView('join')}
                                        className="group p-8 rounded-3xl border-2 border-slate-200 dark:border-white/10 hover:border-indigo-500/50 hover:bg-indigo-500/5 transition-all text-left"
                                    >
                                        <div className="w-14 h-14 bg-indigo-500/10 rounded-2xl flex items-center justify-center mb-6 group-hover:scale-110 transition-transform">
                                            <UserPlus className="text-indigo-600 dark:text-indigo-400" size={32} />
                                        </div>
                                        <h3 className="text-xl font-bold text-slate-800 dark:text-white mb-2">Unirme a un negocio</h3>
                                        <p className="text-slate-500 dark:text-slate-400 text-sm">Usa un código de invitación para unirte a un equipo existente.</p>
                                    </button>
                                </div>
                            </motion.div>
                        )}

                        {view === 'create' && (
                            <motion.div
                                key="create"
                                initial={{ opacity: 0, x: 20 }}
                                animate={{ opacity: 1, x: 0 }}
                                exit={{ opacity: 0, x: -20 }}
                                className="max-w-md mx-auto space-y-8"
                            >
                                <button onClick={() => setView('select')} className="text-blue-500 font-bold hover:underline mb-4 inline-block">← Volver</button>
                                <div className="space-y-4">
                                    <h2 className="text-3xl font-black text-slate-800 dark:text-white uppercase">Crea tu Negocio</h2>
                                    <p className="text-slate-500 dark:text-slate-400">Dale un nombre a tu empresa para empezar.</p>
                                </div>
                                <form onSubmit={handleCreateBusiness} className="space-y-4">
                                    <input 
                                        type="text"
                                        placeholder="Nombre del Negocio (ej. Ferretería El Sol)"
                                        className="glass-input w-full p-5 rounded-2xl"
                                        value={businessName}
                                        onChange={(e) => setBusinessName(e.target.value)}
                                        required
                                    />
                                    <button 
                                        disabled={isLoading}
                                        className="w-full bg-blue-600 text-white font-bold py-5 rounded-2xl shadow-xl shadow-blue-500/20 flex items-center justify-center gap-3"
                                    >
                                        {isLoading ? <Loader2 className="animate-spin" /> : <>Configurar mi Negocio <ArrowRight size={20} /></>}
                                    </button>
                                </form>
                            </motion.div>
                        )}

                        {view === 'join' && (
                            <motion.div
                                key="join"
                                initial={{ opacity: 0, x: 20 }}
                                animate={{ opacity: 1, x: 0 }}
                                exit={{ opacity: 0, x: -20 }}
                                className="max-w-md mx-auto space-y-8"
                            >
                                <button onClick={() => setView('select')} className="text-blue-500 font-bold hover:underline mb-4 inline-block">← Volver</button>
                                <div className="space-y-4">
                                    <h2 className="text-3xl font-black text-slate-800 dark:text-white uppercase">Unirse a Equipo</h2>
                                    <p className="text-slate-500 dark:text-slate-400">Ingresa el código que te proporcionó tu administrador.</p>
                                </div>
                                <form onSubmit={handleJoinBusiness} className="space-y-4">
                                    <div className="relative">
                                        <QrCode className="absolute left-5 top-1/2 -translate-y-1/2 text-slate-400" />
                                        <input 
                                            type="text"
                                            placeholder="Código de Invitación"
                                            className="glass-input w-full p-5 pl-14 rounded-2xl uppercase tracking-widest text-center font-bold"
                                            value={inviteCode}
                                            onChange={(e) => setInviteCode(e.target.value)}
                                            required
                                        />
                                    </div>
                                    <button 
                                        disabled={isLoading}
                                        className="w-full bg-indigo-600 text-white font-bold py-5 rounded-2xl shadow-xl shadow-indigo-500/20 flex items-center justify-center gap-3"
                                    >
                                        {isLoading ? <Loader2 className="animate-spin" /> : <>Unirme al equipo <ArrowRight size={20} /></>}
                                    </button>
                                </form>
                            </motion.div>
                        )}
                    </AnimatePresence>
                </div>
            </motion.div>
        </div>
    );
}
