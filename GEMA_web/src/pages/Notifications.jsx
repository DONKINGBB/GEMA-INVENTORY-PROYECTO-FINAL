import { useState, useEffect } from 'react';
import { ShoppingCart, Package, Info, Loader2, Bell, Calendar, ArrowUpRight, ShoppingBag, Truck, Activity, Sparkles, X, ChevronRight } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import { motion, AnimatePresence } from 'framer-motion';

export default function Notifications() {
    const { user } = useAuth();
    const [notifications, setNotifications] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const fetchNotifications = async () => {
             if (!user?.id) return;
             setLoading(true);
             try {
                const [ordersRes, purchasesRes] = await Promise.all([
                   api.get(`/pedidos?userId=${user.id}`).catch(() => ({ data: [] })),
                   api.get(`/compra?userId=${user.id}`).catch(() => ({ data: [] }))
                ]);
                
                const orders = ordersRes.data || [];
                const purchases = purchasesRes.data || [];

                const allItems = [];
                orders.forEach(o => {
                    allItems.push({
                        id: o.id,
                        type: 'sale',
                        title: o.nombre || `Venta #${(o.id || '').toString().slice(-5)}`,
                        message: `Operación de salida completada por un valor de $${parseFloat(o.total || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}.`,
                        date: o.fechaPedido,
                        rawDate: new Date(o.fechaPedido || 0)
                    });
                });
                
                purchases.forEach(p => {
                    allItems.push({
                        id: p.id,
                        type: 'purchase',
                        title: 'Abastecimiento de Stock',
                        message: `Entrada de mercancía desde ${p.nombreProveedor || p.idProveedor || 'Proveedor General'}. Inversión total: $${parseFloat(p.total || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}.`,
                        date: p.fechaCompra,
                        rawDate: new Date(p.fechaCompra || 0)
                    });
                });

                allItems.sort((a, b) => b.rawDate - a.rawDate);
                setNotifications(allItems);
             } catch (error) {
                console.error("Error fetching notifications", error);
             } finally {
                setLoading(false);
             }
        };

        fetchNotifications();
    }, [user]);

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
            className="max-w-4xl mx-auto space-y-8 pb-20 sm:pb-0"
        >
            {/* Ultra-Premium Header */}
            <motion.div variants={itemVariants} className="relative overflow-hidden bg-white dark:bg-slate-900/40 border border-slate-200 dark:border-white/5 p-10 sm:p-14 rounded-[2.5rem] shadow-sm dark:shadow-none">
                <div className="relative z-10 flex flex-col items-center text-center space-y-6">
                    <div className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-primary/10 border border-primary/20 text-primary text-[10px] font-black uppercase tracking-[0.2em]">
                        <Activity size={12} className="animate-pulse" />
                        <span>Monitoreo en Tiempo Real</span>
                    </div>
                    <h1 className="text-6xl sm:text-8xl font-black text-slate-900 dark:text-white tracking-tighter leading-none">
                        Centro de <br /> <span className="text-primary">Alertas</span>
                    </h1>
                    <p className="text-slate-500 dark:text-slate-400 text-lg max-w-lg mx-auto font-medium">
                        Historial unificado de operaciones de entrada y salida para una trazabilidad total.
                    </p>
                </div>
                
                {/* Background Decoration */}
                <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-full h-full bg-gradient-to-b from-primary/10 via-transparent to-transparent pointer-events-none" />
                <div className="absolute -top-24 -left-24 w-64 h-64 bg-primary/10 blur-[100px] rounded-full pointer-events-none" />
                <div className="absolute -bottom-24 -right-24 w-64 h-64 bg-primary/10 blur-[100px] rounded-full pointer-events-none" />
            </motion.div>

            {/* Notifications Feed */}
            <div className="bg-white dark:bg-slate-900/40 border border-slate-200 dark:border-white/5 rounded-[2.5rem] overflow-hidden shadow-sm dark:shadow-none">
                <div className="px-10 py-6 border-b border-slate-100 dark:border-white/5 bg-slate-50/50 dark:bg-white/5 flex items-center justify-between">
                    <h3 className="font-black text-slate-900 dark:text-white uppercase tracking-widest text-xs flex items-center gap-2">
                        <Bell size={16} className="text-primary" />
                        Actividad Reciente
                    </h3>
                    <div className="px-3 py-1 bg-slate-100 dark:bg-slate-900 border border-slate-200 dark:border-white/5 rounded-full text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">
                        Total {notifications.length} Eventos
                    </div>
                </div>

                {loading ? (
                    <div className="py-32 flex flex-col items-center justify-center space-y-6">
                        <Loader2 className="animate-spin text-primary w-16 h-16" />
                        <p className="text-slate-500 font-black tracking-widest text-[10px] uppercase animate-pulse">Sincronizando Feed de Datos...</p>
                    </div>
                ) : notifications.length > 0 ? (
                    <div className="divide-y divide-slate-100 dark:divide-white/5">
                        <AnimatePresence mode="popLayout">
                            {notifications.map((n, idx) => (
                                <motion.div 
                                    key={n.id} 
                                    variants={itemVariants}
                                    className="p-10 hover:bg-slate-100/60 dark:hover:bg-white/[0.02] transition-all flex flex-col sm:flex-row gap-8 items-start group relative overflow-hidden"
                                >
                                    <div className={`p-5 rounded-[1.5rem] flex-shrink-0 transition-all duration-500 group-hover:scale-110 shadow-2xl dark:shadow-none ${
                                        n.type === 'purchase' ? 'bg-amber-500/10 text-amber-500 border border-amber-500/20' :
                                        'bg-primary/10 text-primary border border-primary/20'
                                    }`}>
                                        {n.type === 'purchase' ? <Truck size={32} /> : <ShoppingBag size={32} />}
                                    </div>
                                    <div className="flex-1 space-y-2 relative z-10">
                                        <div className="flex flex-wrap justify-between items-center gap-4">
                                            <div className="space-y-1">
                                                <h3 className="font-black text-2xl text-slate-900 dark:text-white group-hover:text-primary transition-colors tracking-tight">
                                                    {n.title}
                                                </h3>
                                                <div className="flex items-center gap-3">
                                                    <span className={`px-3 py-0.5 rounded-full text-[9px] font-black uppercase tracking-widest ${
                                                        n.type === 'purchase' ? 'bg-amber-500/10 text-amber-500' : 'bg-primary/10 text-primary'
                                                    }`}>
                                                        {n.type === 'purchase' ? 'Entrada / Compra' : 'Salida / Venta'}
                                                    </span>
                                                    <div className="flex items-center gap-1.5 text-slate-400 dark:text-slate-500 text-[10px] font-black uppercase tracking-widest">
                                                        <Calendar size={12} />
                                                        {n.date ? new Date(n.date).toLocaleDateString('es-MX', { day: '2-digit', month: 'short', year: 'numeric' }) : 'N/A'}
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="p-2 text-slate-200 dark:text-slate-800 group-hover:text-primary transition-colors">
                                                <ChevronRight size={24} />
                                            </div>
                                        </div>
                                        <p className="text-slate-500 dark:text-slate-400 text-lg font-medium leading-relaxed max-w-2xl">
                                            {n.message}
                                        </p>
                                    </div>
                                    
                                    {/* Subtle Hover Gradient */}
                                    <div className="absolute top-0 right-0 h-full w-full bg-gradient-to-r from-transparent to-primary/5 translate-x-full group-hover:translate-x-0 transition-transform duration-700 pointer-events-none" />
                                </motion.div>
                            ))}
                        </AnimatePresence>
                    </div>
                ) : (
                    <div className="py-40 text-center space-y-8">
                        <div className="w-32 h-32 bg-slate-100 dark:bg-white/5 rounded-[3rem] flex items-center justify-center mx-auto border border-slate-200 dark:border-white/5 shadow-xl relative">
                            <Bell size={56} className="text-slate-300 dark:text-slate-800" />
                            <div className="absolute -top-2 -right-2 w-8 h-8 bg-white dark:bg-slate-900 border border-slate-200 dark:border-white/5 rounded-full flex items-center justify-center shadow-md">
                                <X size={16} className="text-slate-400 dark:text-slate-600" />
                            </div>
                        </div>
                        <div className="space-y-3 max-w-sm mx-auto">
                            <h3 className="text-3xl font-black text-slate-900 dark:text-white tracking-tighter">Bandeja Vacía</h3>
                            <p className="text-slate-500 text-lg">No hay eventos recientes registrados. Tu flujo logístico está al día.</p>
                        </div>
                        <button 
                            onClick={() => window.location.reload()}
                            className="bg-primary/10 hover:bg-primary/20 text-primary px-8 py-4 rounded-2xl font-black transition-all inline-flex items-center gap-3 border border-primary/20 uppercase tracking-widest text-[10px]"
                        >
                            Refrescar Centro
                            <Sparkles size={16} />
                        </button>
                    </div>
                )}
            </div>

            {/* Footer Tip */}
            <motion.div variants={itemVariants} className="p-8 bg-slate-50 dark:bg-white/[0.02] border border-slate-200 dark:border-white/5 rounded-[2.5rem] flex items-start gap-5 shadow-sm dark:shadow-none">
                <div className="p-3 bg-primary/10 text-primary rounded-2xl">
                    <Info size={24} />
                </div>
                <div className="space-y-1">
                    <p className="text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Sugerencia de Inteligencia</p>
                    <p className="text-slate-500 dark:text-slate-400 font-medium">Las alertas se generan automáticamente cada vez que se completa una venta o se procesa una compra de stock. Puedes configurar alertas de bajo inventario en los ajustes de cada producto.</p>
                </div>
            </motion.div>
        </motion.div>
    );
}
