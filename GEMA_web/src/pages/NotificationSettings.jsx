
import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { userService } from '../services/userService';
import { Bell, Save, Loader2, ArrowLeft, ToggleLeft, ToggleRight, Box, ShoppingCart, RefreshCw, ChevronRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import toast from 'react-hot-toast';

export default function NotificationSettings() {
    const { user, login } = useAuth();
    const navigate = useNavigate();
    const [settings, setSettings] = useState({
        notifyLowStock: user?.notifyLowStock || false,
        notifyNewOrders: user?.notifyNewOrders || false,
        notifyInventoryChanges: user?.notifyInventoryChanges || false
    });
    const [loading, setLoading] = useState(false);

    const toggleSetting = (name) => {
        setSettings(prev => ({ ...prev, [name]: !prev[name] }));
    };

    const handleSubmit = async () => {
        setLoading(true);
        try {
            const updatedUser = await userService.updateNotifications(user.id, settings);
            login({ ...user, ...updatedUser });
            toast.success('Preferencias guardadas correctamente');
        } catch (error) {
            toast.error('Error al guardar preferencias');
        } finally {
            setLoading(false);
        }
    };

    const items = [
        { 
            id: 'notifyLowStock', 
            label: 'Stock Bajo', 
            desc: 'Recibir avisos cuando un producto esté por debajo del mínimo.',
            icon: Box,
            color: 'text-orange-400',
            bgColor: 'bg-orange-500/10',
            borderColor: 'border-orange-500/20'
        },
        { 
            id: 'notifyNewOrders', 
            label: 'Nuevos Pedidos', 
            desc: 'Notificar cuando se registre una nueva orden de cliente.',
            icon: ShoppingCart,
            color: 'text-blue-400',
            bgColor: 'bg-blue-500/10',
            borderColor: 'border-blue-500/20'
        },
        { 
            id: 'notifyInventoryChanges', 
            label: 'Cambios de Inventario', 
            desc: 'Alertas sobre entradas y salidas manuales de stock.',
            icon: RefreshCw,
            color: 'text-emerald-400',
            bgColor: 'bg-emerald-500/10',
            borderColor: 'border-emerald-500/20'
        }
    ];

    return (
        <motion.div 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="max-w-2xl mx-auto space-y-6 pb-20 sm:pb-0"
        >
            {/* Header */}
            <div className="flex items-center gap-4 mb-2">
                <button 
                    onClick={() => navigate('/app/settings')}
                    className="p-3 bg-white dark:bg-white/5 hover:bg-slate-50 dark:hover:bg-white/10 rounded-2xl transition-all text-slate-900 dark:text-white border border-slate-200 dark:border-white/10 shadow-sm dark:shadow-none active:scale-95"
                >
                    <ArrowLeft size={24} />
                </button>
                <div>
                    <h1 className="text-3xl font-black text-slate-900 dark:text-white">
                        Notificaciones
                    </h1>
                    <p className="text-slate-500 dark:text-slate-400 text-sm">Controla cómo y cuándo recibes alertas</p>
                </div>
            </div>

            <div className="bg-white dark:bg-slate-900/40 backdrop-blur-xl border border-slate-200 dark:border-white/5 rounded-[2.5rem] overflow-hidden shadow-sm dark:shadow-none">
                <div className="p-8 space-y-8">
                    <div className="flex items-center gap-4 p-4 bg-primary/5 dark:bg-primary/10 rounded-2xl border border-primary/10 dark:border-primary/20">
                        <div className="p-2 bg-primary/20 rounded-xl text-primary">
                            <Bell size={20} />
                        </div>
                        <div>
                            <h3 className="text-slate-900 dark:text-white font-bold text-sm">Alertas del Sistema</h3>
                            <p className="text-slate-500 dark:text-slate-400 text-xs">Configura tus preferencias de avisos automáticos.</p>
                        </div>
                    </div>

                    <div className="space-y-4">
                        {items.map((item, index) => (
                            <motion.div 
                                key={item.id}
                                initial={{ opacity: 0, x: -20 }}
                                animate={{ opacity: 1, x: 0 }}
                                transition={{ delay: index * 0.1 }}
                                onClick={() => toggleSetting(item.id)}
                                className="group flex items-center justify-between p-4 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-100 dark:border-white/5 hover:bg-white dark:hover:bg-white/10 hover:border-slate-200 dark:hover:border-white/10 transition-all cursor-pointer shadow-sm dark:shadow-none"
                            >
                                <div className="flex gap-4 items-center">
                                    <div className={`p-3 rounded-xl ${item.bgColor} ${item.color} border ${item.borderColor} group-hover:scale-110 transition-transform`}>
                                        <item.icon size={22} />
                                    </div>
                                    <div>
                                        <h4 className="font-bold text-slate-900 dark:text-white group-hover:text-primary transition-colors">{item.label}</h4>
                                        <p className="text-xs text-slate-500 dark:text-slate-400 max-w-[200px] sm:max-w-xs">{item.desc}</p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-3">
                                    <button 
                                        className={`transition-all duration-300 ${settings[item.id] ? 'text-primary' : 'text-slate-400 dark:text-slate-600'}`}
                                    >
                                        {settings[item.id] ? (
                                            <ToggleRight size={44} strokeWidth={1.5} className="drop-shadow-[0_0_8px_rgba(59,130,246,0.5)]" />
                                        ) : (
                                            <ToggleLeft size={44} strokeWidth={1.5} />
                                        )}
                                    </button>
                                </div>
                            </motion.div>
                        ))}
                    </div>

                    <div className="pt-4 border-t border-slate-100 dark:border-white/5">
                        <button
                            onClick={handleSubmit}
                            disabled={loading}
                            className="w-full bg-primary hover:bg-primary/90 text-white font-black py-4 rounded-2xl shadow-xl shadow-primary/20 transition-all hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-3 disabled:opacity-50"
                        >
                            {loading ? <Loader2 className="animate-spin" size={24} /> : <Save size={24} />}
                            {loading ? 'GUARDANDO...' : 'GUARDAR PREFERENCIAS'}
                        </button>
                    </div>
                </div>
            </div>

            {/* Extra Info */}
            <div className="bg-indigo-500/5 dark:bg-indigo-500/10 border border-indigo-500/10 dark:border-indigo-500/20 rounded-3xl p-6 flex gap-4 items-start shadow-sm shadow-indigo-500/5 dark:shadow-none">
                <div className="p-2 bg-indigo-500/20 rounded-xl text-indigo-500 dark:text-indigo-400">
                    <Bell size={20} />
                </div>
                <div className="space-y-1">
                    <h4 className="text-slate-900 dark:text-white font-bold text-sm">¿Sabías que?</h4>
                    <p className="text-slate-600 dark:text-slate-400 text-xs leading-relaxed">
                        Estas notificaciones también se sincronizan con tu dispositivo móvil para que nunca pierdas de vista tu inventario.
                    </p>
                </div>
            </div>
        </motion.div>
    );
}
