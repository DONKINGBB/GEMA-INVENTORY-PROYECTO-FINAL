import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { 
    Package, TrendingUp, DollarSign, AlertTriangle, 
    ShoppingCart, ArrowRight, Activity, Calendar,
    ChevronRight, Wallet, Box, Users, Plus, Target,
    Sparkles, ArrowUpRight, ArrowDownRight, Zap
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { dashboardService } from '../services/dashboardService';
import api from '../services/api';
import { 
    AreaChart, Area, XAxis, YAxis, CartesianGrid, 
    Tooltip, ResponsiveContainer, BarChart, Bar,
    Cell
} from 'recharts';
import WelcomeGuide from '../components/WelcomeGuide';
import { useLocation } from 'react-router-dom';

const MOCK_CHART_DATA = [
    { name: 'Lun', ventas: 4200, compras: 2400 },
    { name: 'Mar', ventas: 3800, compras: 3100 },
    { name: 'Mie', ventas: 5200, compras: 2800 },
    { name: 'Jue', ventas: 4800, compras: 3908 },
    { name: 'Vie', ventas: 6100, compras: 4200 },
    { name: 'Sab', ventas: 5900, compras: 3800 },
    { name: 'Dom', ventas: 7400, compras: 4300 },
];

export default function Home() {
    const { user } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const isAdmin = user?.idRol === 1 || user?.idRol === 2;
    const [showTutorial, setShowTutorial] = useState(location.state?.showTutorial || false);
    const [stats, setStats] = useState({
        inventoryValue: 0,
        pendingOrders: 0,
        lowStock: 0,
        monthlyProfit: 0
    });
    const [loading, setLoading] = useState(true);
    const [alerts, setAlerts] = useState([]);
    const [recentActivity, setRecentActivity] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            if (!user?.id) return;
            setLoading(true);
            try {
                const [summary, inventoryRes, ordersRes, purchasesRes] = await Promise.all([
                    dashboardService.getSummary(user.id),
                    api.get(`/inventario?userId=${user.id}`),
                    api.get(`/pedidos?userId=${user.id}`).catch(() => ({ data: [] })),
                    api.get(`/compra?userId=${user.id}`).catch(() => ({ data: [] }))
                ]);

                if (summary) {
                    setStats({
                        inventoryValue: summary.valor_inventario || summary.valorInventario || 0,
                        pendingOrders: summary.pedidos_pendientes || summary.pedidosPendientes || 0,
                        lowStock: summary.productos_bajo_stock || summary.productosBajoStock || 0,
                        monthlyProfit: summary.beneficio_mes || summary.beneficioMes || 0
                    });
                }

                const invData = inventoryRes.data || [];
                setAlerts(invData.filter(item => (item.cantidadActual || 0) <= (item.stockMinimo || 5)));

                const allItems = [];
                (ordersRes.data || []).forEach(o => {
                    allItems.push({
                        id: o.id,
                        type: 'ORDER',
                        title: o.nombre || `Venta #${(o.id || '').toString().slice(-5)}`,
                        description: `Pedido: $${o.total || 0}`,
                        timestamp: o.fechaPedido?.substring(0, 10) || '',
                        rawDate: new Date(o.fechaPedido || 0)
                    });
                });
                
                (purchasesRes.data || []).forEach(p => {
                    allItems.push({
                        id: p.id,
                        type: 'PURCHASE',
                        title: 'Compra de Stock',
                        description: `${p.nombreProveedor || p.idProveedor || 'Proveedor'} - $${p.total || 0}`,
                        timestamp: p.fechaCompra?.substring(0, 10) || '',
                        rawDate: new Date(p.fechaCompra || 0)
                    });
                });

                allItems.sort((a, b) => b.rawDate - a.rawDate);
                setRecentActivity(allItems.slice(0, 5));

            } catch (e) {
                console.error("Error loading dashboard data:", e);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [user]);

    const containerVariants = {
        hidden: { opacity: 0 },
        visible: {
            opacity: 1,
            transition: { staggerChildren: 0.1, delayChildren: 0.2 }
        }
    };

    const itemVariants = {
        hidden: { y: 30, opacity: 0 },
        visible: { 
            y: 0, 
            opacity: 1,
            transition: { type: 'spring', stiffness: 300, damping: 24 }
        }
    };

    const CustomTooltip = ({ active, payload, label }) => {
        if (active && payload && payload.length) {
            return (
                <div className="bg-white/90 dark:bg-slate-900/90 backdrop-blur-2xl border border-black/5 dark:border-white/10 p-5 rounded-2xl shadow-2xl">
                    <p className="text-[10px] font-black uppercase tracking-[0.2em] text-slate-400 dark:text-white/40 mb-3">{label}</p>
                    {payload.map((entry, index) => (
                        <div key={index} className="flex items-center justify-between gap-8 py-1">
                            <div className="flex items-center gap-2">
                                <div className="w-2 h-2 rounded-full" style={{ backgroundColor: entry.color }} />
                                <span className="text-xs font-bold text-slate-600 dark:text-white/80">{entry.name}:</span>
                            </div>
                            <span className="text-xs font-black text-slate-900 dark:text-white">${entry.value.toLocaleString()}</span>
                        </div>
                    ))}
                </div>
            );
        }
        return null;
    };

    return (
        <motion.div 
            variants={containerVariants}
            initial="hidden"
            animate="visible"
            className="space-y-12 pb-24"
        >
            <WelcomeGuide 
                isOpen={showTutorial} 
                onClose={() => setShowTutorial(false)} 
            />
            {/* Background Glows */}
            <div className="absolute top-0 right-0 w-[600px] h-[600px] bg-primary/5 blur-[120px] -z-10 rounded-full" />
            <div className="absolute bottom-0 left-0 w-[400px] h-[400px] bg-purple-600/5 blur-[100px] -z-10 rounded-full" />

            {/* Header Section */}
            <div className="flex flex-col md:flex-row md:items-end justify-between gap-8">
                <motion.div variants={itemVariants} className="space-y-3">
                    <div className="flex items-center gap-3">
                        <div className="w-10 h-[1px] bg-primary" />
                        <span className="text-[10px] font-black text-primary uppercase tracking-[0.4em]">Panel de Control</span>
                    </div>
                    <h1 className="text-5xl md:text-7xl font-black text-slate-900 dark:text-white tracking-tighter leading-[0.9]">
                        GEMA <span className="text-primary italic">HUB</span>
                    </h1>
                    <p className="text-slate-500 dark:text-white/40 font-medium flex items-center gap-2 text-sm">
                        <Zap size={14} className="text-primary fill-primary/20" />
                        Hola, <span className="text-slate-900 dark:text-white font-bold">{user?.nombre?.split(' ')[0] || 'Usuario'}</span>. Aquí está el estado actual de tu negocio.
                    </p>
                </motion.div>
                
                <motion.div variants={itemVariants} className="flex flex-wrap items-center gap-4">
                    {/* Luxurious Premium Button */}
                    <motion.button
                        whileHover={{ scale: 1.05, y: -2 }}
                        whileTap={{ scale: 0.95 }}
                        onClick={() => {
                            import('react-hot-toast').then(t => t.default.success('Cargando Experiencia Premium...'));
                            navigate('/app/premium');
                        }}
                        className="relative group overflow-hidden px-8 py-4 rounded-2xl font-black text-[10px] uppercase tracking-[0.2em] transition-all duration-500"
                    >
                        <div className="absolute inset-0 bg-gradient-to-r from-amber-200 via-yellow-400 to-amber-500 group-hover:scale-110 transition-transform duration-500" />
                        <div className="absolute inset-0 opacity-0 group-hover:opacity-100 bg-[radial-gradient(circle_at_center,_rgba(255,255,255,0.8)_0%,_transparent_70%)] transition-opacity duration-500" />
                        <div className="relative z-10 flex items-center gap-3 text-amber-950">
                            <Sparkles size={16} className="animate-pulse" />
                            <span>Plan Premium</span>
                        </div>
                    </motion.button>

                    <div className="flex flex-col items-end hidden sm:flex">
                        <span className="text-[9px] font-black text-slate-400 dark:text-white/20 uppercase tracking-[0.3em] mb-1">Última actualización</span>
                        <div className="flex items-center gap-3 bg-white/50 dark:bg-white/[0.02] border border-black/5 dark:border-white/5 backdrop-blur-xl rounded-2xl px-6 py-4 shadow-xl">
                            <Calendar className="text-slate-700 dark:text-white/70" size={16} />
                            <span className="text-sm font-black text-slate-700 dark:text-white/70 uppercase tracking-widest">
                                {new Date().toLocaleDateString('es-MX', { day: 'numeric', month: 'long' })}
                            </span>
                        </div>
                    </div>
                    
                    <div className="flex items-center gap-3 h-full pt-4 md:pt-0">
                        <motion.button 
                            whileHover={{ scale: 1.05 }}
                            whileTap={{ scale: 0.95 }}
                            onClick={() => navigate('/app/orders')}
                            className="bg-primary hover:bg-primary/90 text-white px-8 py-4 rounded-2xl text-[10px] font-black uppercase tracking-[0.2em] shadow-2xl shadow-primary/30 flex items-center gap-3 transition-all"
                        >
                            <Plus size={16} /> Nueva Venta
                        </motion.button>
                        <motion.button 
                            whileHover={{ scale: 1.05 }}
                            whileTap={{ scale: 0.95 }}
                            onClick={() => navigate('/app/inventory')}
                            className="bg-white/5 dark:bg-white/[0.02] hover:bg-black/5 dark:hover:bg-white/10 text-slate-900 dark:text-white border border-black/5 dark:border-white/10 px-8 py-4 rounded-2xl text-[10px] font-black uppercase tracking-[0.2em] flex items-center gap-3 transition-all"
                        >
                            <Box size={16} /> Añadir Stock
                        </motion.button>
                    </div>
                </motion.div>
            </div>

            {/* Stats Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                {[
                    { label: 'Capital Total', value: `$${stats.inventoryValue.toLocaleString()}`, icon: Wallet, color: 'from-blue-500 to-indigo-600', trend: '+12.5%', show: true, status: 'up' },
                    { label: 'Pedidos Activos', value: stats.pendingOrders, icon: ShoppingCart, color: 'from-primary to-blue-400', trend: 'En curso', show: true, status: 'neutral' },
                    { label: 'Alerta Stock', value: stats.lowStock, icon: Box, color: 'from-rose-500 to-orange-600', trend: 'Revisar', alert: stats.lowStock > 0, show: true, status: 'down' },
                    { label: 'Profit Mensual', value: `$${stats.monthlyProfit.toLocaleString()}`, icon: TrendingUp, color: 'from-emerald-400 to-teal-600', trend: '+24.1%', show: isAdmin, status: 'up' },
                ].filter(s => s.show).map((stat, i) => (
                    <motion.div 
                        key={i}
                        variants={itemVariants}
                        onClick={() => {
                            if (stat.label.includes('Capital') || stat.label.includes('Stock')) navigate('/app/inventory');
                            if (stat.label.includes('Pedidos')) navigate('/app/orders');
                            if (stat.label.includes('Profit')) navigate('/app/finances');
                        }}
                        className="glass-card p-8 relative overflow-hidden group border-slate-100 dark:border-white/5 bg-white dark:bg-slate-900/40 min-h-[220px] flex flex-col justify-between cursor-pointer shadow-sm dark:shadow-none hover:-translate-y-2 transition-all duration-500"
                    >
                        <div className={`absolute -top-12 -right-12 w-40 h-40 bg-gradient-to-br ${stat.color} opacity-5 blur-3xl group-hover:opacity-20 transition-all duration-1000`} />
                        
                        <div className="flex justify-between items-start relative z-10">
                            <div className={`p-4 rounded-3xl bg-gradient-to-br ${stat.color} shadow-2xl shadow-black/60 group-hover:rotate-12 transition-all duration-500`}>
                                <stat.icon size={22} className="text-white" />
                            </div>
                            <div className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full text-[9px] font-black uppercase tracking-widest border backdrop-blur-xl ${
                                stat.status === 'up' ? 'text-emerald-500 border-emerald-500/20 bg-emerald-400/5' : 
                                stat.status === 'down' ? 'text-rose-500 border-rose-500/20 bg-rose-400/5' : 
                                'text-slate-400 dark:text-white/30 border-slate-200 dark:border-white/10 bg-slate-50 dark:bg-white/5'
                            }`}>
                                {stat.status === 'up' && <ArrowUpRight size={10} />}
                                {stat.status === 'down' && <ArrowDownRight size={10} />}
                                {stat.trend}
                            </div>
                        </div>
                        
                        <div className="relative z-10 mt-8">
                            <p className="text-slate-400 dark:text-white/20 text-[9px] font-black uppercase tracking-[0.4em] mb-2">{stat.label}</p>
                            <h3 className="text-4xl font-black text-slate-900 dark:text-white tracking-tighter group-hover:text-primary transition-colors duration-500">{stat.value}</h3>
                        </div>
                    </motion.div>
                ))}
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                {/* Main Chart Area */}
                <motion.div variants={itemVariants} className="lg:col-span-2 space-y-8">
                    <div className="glass-card p-10 border-slate-100 dark:border-white/5 bg-white dark:bg-white/[0.01] relative overflow-hidden group shadow-sm dark:shadow-none">
                        <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-primary/50 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-1000" />
                        
                        <div className="flex flex-col md:flex-row md:items-center justify-between mb-12 gap-6">
                            <div>
                                <h2 className="text-3xl font-black text-slate-900 dark:text-white tracking-tighter">Métricas de Operación</h2>
                                <p className="text-slate-400 dark:text-white/20 text-[10px] font-black uppercase tracking-[0.3em] mt-2">Seguimiento de ingresos y egresos de stock</p>
                            </div>
                            <div className="flex items-center gap-6 bg-slate-100 dark:bg-white/[0.02] p-3 rounded-2xl border border-slate-200 dark:border-white/5 shadow-inner shadow-black/5 dark:shadow-none">
                                <div className="flex items-center gap-3">
                                    <div className="w-2.5 h-2.5 rounded-full bg-primary shadow-[0_0_10px_rgba(59,130,246,0.8)]" />
                                    <span className="text-[10px] font-black text-slate-500 dark:text-white/50 uppercase tracking-widest">Ingresos</span>
                                </div>
                                <div className="flex items-center gap-3">
                                    <div className="w-2.5 h-2.5 rounded-full bg-slate-900 dark:bg-indigo-900 border border-slate-400/30 dark:border-indigo-400/30" />
                                    <span className="text-[10px] font-black text-slate-500 dark:text-white/50 uppercase tracking-widest">Egresos</span>
                                </div>
                            </div>
                        </div>

                        <div className="h-[350px] w-full">
                            <ResponsiveContainer width="100%" height="100%">
                                <AreaChart data={MOCK_CHART_DATA} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
                                    <defs>
                                        <linearGradient id="colorVentas" x1="0" y1="0" x2="0" y2="1">
                                            <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.4}/>
                                            <stop offset="95%" stopColor="#3b82f6" stopOpacity={0}/>
                                        </linearGradient>
                                    </defs>
                                    <CartesianGrid strokeDasharray="3 3" stroke="#88888810" vertical={false} />
                                    <XAxis 
                                        dataKey="name" 
                                        axisLine={false} 
                                        tickLine={false} 
                                        tick={{fill: '#88888850', fontSize: 11, fontWeight: 900}}
                                        dy={15}
                                    />
                                    <YAxis 
                                        axisLine={false} 
                                        tickLine={false} 
                                        tick={{fill: '#88888850', fontSize: 11, fontWeight: 900}}
                                        dx={-10}
                                    />
                                    <Tooltip content={<CustomTooltip />} />
                                    <Area 
                                        type="monotone" 
                                        dataKey="ventas" 
                                        name="Ingresos"
                                        stroke="#3b82f6" 
                                        strokeWidth={4}
                                        fillOpacity={1} 
                                        fill="url(#colorVentas)" 
                                        animationDuration={2000}
                                    />
                                    <Area 
                                        type="monotone" 
                                        dataKey="compras" 
                                        name="Egresos"
                                        stroke="#1e1b4b" 
                                        strokeWidth={2}
                                        fill="transparent"
                                        strokeDasharray="8 8"
                                        animationDuration={2500}
                                    />
                                </AreaChart>
                            </ResponsiveContainer>
                        </div>
                    </div>

                    {/* Recent Activity List */}
                    <div className="glass-card overflow-hidden border-slate-100 dark:border-white/5 bg-white dark:bg-transparent relative shadow-sm dark:shadow-none">
                        <div className="p-10 border-b border-slate-100 dark:border-white/5 flex items-center justify-between bg-slate-50/30 dark:bg-white/[0.01]">
                            <div className="flex items-center gap-5">
                                <div className="p-4 bg-primary/10 rounded-[1.5rem] text-primary shadow-[0_0_20px_rgba(59,130,246,0.1)]">
                                    <Activity size={22} />
                                </div>
                                <div>
                                    <h2 className="text-2xl font-black text-slate-900 dark:text-white tracking-tighter">Flujo de Transacciones</h2>
                                    <p className="text-slate-400 dark:text-white/20 text-[9px] font-black uppercase tracking-[0.3em] mt-1">Historial en tiempo real</p>
                                </div>
                            </div>
                            <button className="text-[9px] font-black text-primary hover:text-white transition-all uppercase tracking-[0.3em] px-6 py-3 bg-primary/5 hover:bg-primary rounded-2xl border border-primary/10 shadow-lg shadow-primary/5">Explorar todo</button>
                        </div>
                        <div className="divide-y divide-slate-100 dark:divide-white/[0.03]">
                            {recentActivity.length > 0 ? (
                                recentActivity.map((activity, i) => (
                                    <motion.div 
                                        key={i} 
                                        whileHover={{ backgroundColor: 'rgba(255,255,255,0.02)' }}
                                        className="p-8 flex items-center gap-6 group cursor-pointer transition-colors"
                                    >
                                        <div className={`w-14 h-14 rounded-2xl flex items-center justify-center transition-all duration-500 group-hover:rotate-[360deg] ${
                                            activity.type === 'ORDER' 
                                                ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' 
                                                : 'bg-primary/10 text-primary border border-primary/20'
                                        }`}>
                                            {activity.type === 'ORDER' ? <ShoppingCart size={22} /> : <Box size={22} />}
                                        </div>
                                        <div className="flex-1">
                                            <h4 className="font-black text-slate-900 dark:text-white text-lg tracking-tight group-hover:text-primary transition-colors">{activity.title}</h4>
                                            <div className="flex items-center gap-3 mt-1.5">
                                                <span className="text-[10px] text-slate-400 dark:text-white/30 font-black uppercase tracking-widest">{activity.description}</span>
                                                <div className="w-1 h-1 rounded-full bg-black/10 dark:bg-white/10" />
                                                <span className="text-[10px] text-primary/50 font-black uppercase tracking-widest">{activity.timestamp}</span>
                                            </div>
                                        </div>
                                        <div className="w-12 h-12 rounded-full border border-slate-100 dark:border-white/5 flex items-center justify-center text-slate-300 dark:text-white/10 group-hover:text-primary group-hover:border-primary/30 group-hover:bg-primary/5 transition-all">
                                            <ChevronRight size={20} />
                                        </div>
                                    </motion.div>
                                ))
                            ) : (
                                <div className="p-24 text-center">
                                    <div className="w-20 h-20 bg-slate-50 dark:bg-white/[0.02] rounded-full flex items-center justify-center mx-auto mb-6 border border-slate-100 dark:border-white/5">
                                        <Activity size={32} className="text-slate-300 dark:text-white/10" />
                                    </div>
                                    <p className="font-black uppercase tracking-[0.4em] text-[10px] text-slate-400 dark:text-white/20">Sin transacciones registradas</p>
                                </div>
                            )}
                        </div>
                    </div>
                </motion.div>

                {/* Right Column: Alerts & Goals */}
                <div className="space-y-8">
                    {/* Stock Alerts */}
                    <motion.div variants={itemVariants} className="glass-card p-10 border-rose-500/10 bg-white/80 dark:bg-slate-900/40 backdrop-blur-xl relative overflow-hidden shadow-sm dark:shadow-none">
                        <div className="absolute -top-10 -left-10 w-32 h-32 bg-rose-500/5 blur-3xl" />
                        
                        <div className="flex items-center gap-5 mb-10">
                            <div className="p-4 bg-rose-500/10 rounded-2xl text-rose-500 shadow-lg shadow-rose-500/10">
                                <AlertTriangle size={22} />
                            </div>
                            <div>
                                <h2 className="text-2xl font-black text-slate-900 dark:text-white tracking-tighter">Alertas</h2>
                                <p className="text-rose-500/50 text-[9px] font-black uppercase tracking-[0.3em] mt-1">Crítico: {alerts.length} ítems</p>
                            </div>
                        </div>
                        
                        <div className="space-y-4">
                            {alerts.length > 0 ? (
                                alerts.slice(0, 4).map((item, i) => (
                                    <motion.div 
                                        key={i} 
                                        onClick={() => navigate('/app/inventory', { state: { editProduct: item } })}
                                        className="flex items-center justify-between p-5 rounded-3xl bg-white/5 dark:bg-white/[0.02] border border-black/5 dark:border-white/5 group hover:border-rose-500/30 hover:bg-rose-500/[0.02] hover:translate-x-1 transition-all cursor-pointer"
                                    >
                                        <div className="flex flex-col">
                                            <span className="text-sm font-black text-slate-900 dark:text-white group-hover:text-rose-400 transition-colors uppercase tracking-tight">{item.nombreProducto}</span>
                                            <div className="flex items-center gap-2 mt-2">
                                                <div className="px-2 py-0.5 rounded-md bg-rose-500/10 border border-rose-500/20 text-[8px] font-black text-rose-500 uppercase">Quedan {item.cantidadActual}</div>
                                                <span className="text-[8px] text-slate-400 dark:text-white/20 font-black uppercase">Stock Mín: {item.stockMinimo}</span>
                                            </div>
                                        </div>
                                        <div className="w-10 h-10 rounded-2xl border border-slate-100 dark:border-white/5 flex items-center justify-center text-slate-300 dark:text-white/10 group-hover:bg-rose-500 group-hover:text-white group-hover:border-rose-500 transition-all">
                                            <ArrowRight size={16} />
                                        </div>
                                    </motion.div>
                                ))
                            ) : (
                                <div className="py-16 text-center bg-emerald-500/5 rounded-[3rem] border border-emerald-500/10 group">
                                    <div className="w-14 h-14 bg-emerald-500/10 rounded-full flex items-center justify-center mx-auto mb-4 group-hover:scale-110 transition-transform">
                                        <Zap size={24} className="text-emerald-500 fill-emerald-500/20" />
                                    </div>
                                    <p className="text-[10px] font-black uppercase tracking-[0.4em] text-emerald-500/60">Salud Óptima</p>
                                </div>
                            )}
                        </div>
                    </motion.div>

                    {/* Monthly Goal Card */}
                    <motion.div variants={itemVariants} className="glass-card p-10 bg-gradient-to-br from-primary via-blue-700 to-indigo-800 border-none shadow-2xl shadow-primary/20 relative overflow-hidden group">
                        <div className="absolute top-0 right-0 w-48 h-48 bg-white/10 rounded-full blur-[70px] -mr-20 -mt-20 group-hover:scale-125 transition-transform duration-1000" />
                        <div className="relative z-10">
                            <div className="flex items-center justify-between mb-8">
                                <div className="flex items-center gap-3 text-white/60">
                                    <Target size={20} />
                                    <h4 className="text-[10px] font-black uppercase tracking-[0.4em]">Objetivo Q2</h4>
                                </div>
                                <Sparkles size={16} className="text-white/40 animate-pulse" />
                            </div>
                            
                            <div className="flex items-end gap-3 mb-10">
                                <span className="text-7xl font-black text-white tracking-tighter leading-none">88<span className="text-3xl text-white/50">%</span></span>
                                <div className="mb-2 p-2 bg-white/20 rounded-xl">
                                    <TrendingUp size={18} className="text-white" />
                                </div>
                            </div>

                            <div className="space-y-5">
                                <div className="w-full h-3.5 bg-black/30 rounded-full overflow-hidden p-1">
                                    <motion.div 
                                        initial={{ width: 0 }}
                                        animate={{ width: '88%' }}
                                        transition={{ duration: 2, ease: [0.16, 1, 0.3, 1] }}
                                        className="h-full bg-white rounded-full shadow-[0_0_25px_rgba(255,255,255,0.7)]" 
                                    />
                                </div>
                                <div className="flex justify-between items-center px-1">
                                    <p className="text-white/50 text-[9px] font-black uppercase tracking-[0.2em]">Faltan $8,450</p>
                                    <p className="text-white font-black text-[9px] uppercase tracking-[0.2em]">Meta: $100k</p>
                                </div>
                            </div>
                        </div>
                    </motion.div>

                    {/* Quick Actions */}
                    <motion.div variants={itemVariants} className="grid grid-cols-2 gap-4">
                        <motion.button 
                            onClick={() => navigate('/app/orders')}
                            className="flex flex-col items-center justify-center p-8 rounded-[2.5rem] bg-white/5 dark:bg-white/[0.02] border border-black/5 dark:border-white/5 group hover:-translate-y-1 hover:bg-blue-500/5 transition-all duration-300"
                        >
                            <div className="w-14 h-14 bg-primary/10 rounded-2xl flex items-center justify-center text-primary group-hover:bg-primary group-hover:text-white shadow-lg transition-all duration-500">
                                <Plus size={24} />
                            </div>
                            <span className="text-[9px] font-black text-slate-400 dark:text-white/30 group-hover:text-white dark:group-hover:text-white uppercase tracking-[0.3em] mt-5">Nueva Venta</span>
                        </motion.button>
                        <motion.button 
                            onClick={() => navigate('/app/inventory')}
                            className="flex flex-col items-center justify-center p-8 rounded-[2.5rem] bg-white/5 dark:bg-white/[0.02] border border-black/5 dark:border-white/5 group hover:-translate-y-1 hover:bg-purple-500/5 transition-all duration-300"
                        >
                            <div className="w-14 h-14 bg-purple-500/10 rounded-2xl flex items-center justify-center text-purple-400 group-hover:bg-purple-500 group-hover:text-white shadow-lg transition-all duration-500">
                                <Box size={24} />
                            </div>
                            <span className="text-[9px] font-black text-slate-400 dark:text-white/30 group-hover:text-white dark:group-hover:text-white uppercase tracking-[0.3em] mt-5">Añadir Stock</span>
                        </motion.button>
                    </motion.div>
                </div>
            </div>
        </motion.div>
    );
}

