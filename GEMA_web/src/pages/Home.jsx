
import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { Package, TrendingUp, DollarSign, AlertTriangle, ShoppingCart, ArrowRight } from 'lucide-react';
import { dashboardService } from '../services/dashboardService';
import api from '../services/api';

export default function Home() {
    const { user } = useAuth();
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
        const fetchStats = async () => {
            if (!user?.id) return;
            try {
                const summary = await dashboardService.getSummary(user.id);
                if (summary) {
                    setStats({
                        inventoryValue: summary.valor_inventario || summary.valorInventario || 0,
                        pendingOrders: summary.pedidos_pendientes || summary.pedidosPendientes || 0,
                        lowStock: summary.productos_bajo_stock || summary.productosBajoStock || 0,
                        monthlyProfit: summary.beneficio_mes || summary.beneficioMes || 0
                    });
                }
            } catch (e) {
                console.error("Error loading stats:", e);
            } finally {
                setLoading(false);
            }
        };

        const fetchAlerts = async () => {
             if (!user?.id) return;
             try {
                const response = await api.get(`/inventario?userId=${user.id}`);
                const data = response.data || [];
                const lowStockItems = data.filter(item => 
                    (item.cantidadActual || 0) <= (item.stockMinimo || 5)
                );
                setAlerts(lowStockItems);
             } catch (e) {
                console.error("Error loading alerts:", e);
             }
        };

        const fetchActivities = async () => {
            if (!user?.id) return;
            try {
                // Fetch orders and purchases parallelly
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
                        type: 'ORDER',
                        title: o.nombre || `Venta #${(o.id || '').toString().slice(-5)}`,
                        description: `Pedido: $${o.total || 0}`,
                        timestamp: o.fechaPedido?.substring(0, 10) || '',
                        rawDate: new Date(o.fechaPedido || 0)
                    });
                });
                
                purchases.forEach(p => {
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
                setRecentActivity(allItems.slice(0, 3));
            } catch(e) {
                console.error("Error loading activities:", e);
            }
        };

        fetchStats();
        fetchAlerts();
        fetchActivities();
    }, [user]);

    return (
        <div className="max-w-6xl mx-auto space-y-8">
            <div className="mb-8 p-4">
                <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Inicio</h1>
                <p className="text-gray-500 dark:text-gray-400 mt-2">Resumen Rápido</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 xl:grid-cols-4 gap-6">

                {/* Valor Inventario */}
                <div className="bg-white dark:bg-slate-800 p-6 rounded-2xl shadow-sm border border-gray-100 dark:border-slate-700 flex flex-col justify-between h-40 transition-colors">
                    <div>
                        <p className="text-gray-500 dark:text-gray-400 text-sm font-medium mb-1">Valor Inventario</p>
                        <h3 className="text-3xl font-bold text-gray-900 dark:text-white">${stats.inventoryValue.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</h3>
                    </div>
                </div>

                {/* Pedidos Pendientes */}
                <div className="bg-white dark:bg-slate-800 p-6 rounded-2xl shadow-sm border border-gray-100 dark:border-slate-700 flex flex-col justify-between h-40 transition-colors">
                    <div>
                        <p className="text-gray-500 dark:text-gray-400 text-sm font-medium mb-1">Pedidos Pendientes</p>
                        <h3 className="text-3xl font-bold text-gray-900 dark:text-white flex items-center gap-2">
                            {stats.pendingOrders}
                        </h3>
                    </div>
                </div>

                {/* Productos Bajo Stock */}
                <div className="bg-white dark:bg-slate-800 p-6 rounded-2xl shadow-sm border border-gray-100 dark:border-slate-700 flex flex-col justify-between h-40 transition-colors">
                    <div>
                        <p className="text-gray-500 dark:text-gray-400 text-sm font-medium mb-1">Productos Bajo Stock</p>
                        <h3 className="text-3xl font-bold text-gray-900 dark:text-white">{stats.lowStock}</h3>
                    </div>
                </div>

                {/* Beneficio (Mes) */}
                <div className="bg-white dark:bg-slate-800 p-6 rounded-2xl shadow-sm border border-gray-100 dark:border-slate-700 flex flex-col justify-between h-40 transition-colors">
                    <div>
                        <p className="text-gray-500 dark:text-gray-400 text-sm font-medium mb-1">Beneficio (Mes)</p>
                        <h3 className="text-3xl font-bold text-green-600 dark:text-green-400">${stats.monthlyProfit.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</h3>
                    </div>
                </div>
            </div>

            {/* Inventory Alert Section matching App */}
            {alerts.length > 0 && (
                <div className="bg-white dark:bg-slate-800 border border-yellow-500/50 rounded-2xl p-6 shadow-sm dark:shadow-xl mt-8 transition-colors">
                    <div className="flex items-center gap-3 mb-4">
                        <AlertTriangle className="text-yellow-500" size={28} />
                        <h3 className="text-xl font-bold text-yellow-600 dark:text-yellow-500">Alerta de Stock</h3>
                    </div>
                    <p className="text-gray-500 dark:text-gray-400 mb-4">Los siguientes productos tienen stock crítico:</p>

                    <div className="space-y-2 mb-4">
                        {alerts.slice(0, 5).map(item => {
                            const isOut = (item.cantidadActual || 0) === 0;
                            return (
                                <p key={item.idInventario} className={`font-medium text-sm ${isOut ? 'text-red-500 dark:text-red-400 font-bold' : 'text-yellow-600 dark:text-yellow-500'}`}>
                                    • {item.nombreProducto} ({item.cantidadActual || 0}) {isOut ? '¡AGOTADO!' : '- Bajo Stock'}
                                </p>
                            );
                        })}
                        {alerts.length > 5 && (
                            <p className="text-sm text-gray-500 italic mt-2">
                                ... y {alerts.length - 5} items más con bajo stock
                            </p>
                        )}
                    </div>

                    <div className="text-right mt-4">
                        <a href="/inventory" className="inline-flex items-center gap-2 text-primary dark:text-blue-400 hover:text-primary-dark dark:hover:text-blue-300 font-medium text-sm transition-colors">
                            <span>Ir a Inventario</span>
                            <ArrowRight size={16} />
                        </a>
                    </div>
                </div>
            )}

            {alerts.length === 0 && !loading && (
                 <div className="bg-white dark:bg-slate-800 border border-green-500/50 rounded-2xl p-6 shadow-sm dark:shadow-xl mt-8 transition-colors">
                     <p className="text-green-600 dark:text-green-400 font-medium flex items-center gap-2">
                         <span className="w-2 h-2 rounded-full bg-green-500 dark:bg-green-400 inline-block"></span>
                         Todo en orden. Niveles de stock saludables.
                     </p>
                 </div>
            )}

            {/* Recent Activity */}
            <div className="bg-gradient-to-r from-primary to-accent rounded-3xl p-8 text-white relative overflow-hidden shadow-xl mt-8">
                <div className="relative z-10 max-w-xl">
                    <h2 className="text-2xl font-bold mb-4">Actividad Reciente</h2>
                    <p className="mb-6 text-blue-100">Resumen de tus últimas operaciones registradas.</p>
                    
                    <div className="space-y-4">
                        {recentActivity.length === 0 ? (
                            <div className="p-4 bg-white/10 rounded-xl">
                                <p className="text-blue-200">No hay actividad reciente.</p>
                            </div>
                        ) : (
                            recentActivity.map(activity => (
                                <div key={activity.id} className="bg-white/10 hover:bg-white/20 transition-colors backdrop-blur-md border border-white/20 p-4 rounded-xl flex items-center gap-4">
                                    <div className={`p-3 rounded-full ${activity.type === 'ORDER' ? 'bg-green-500/20 text-green-300' : 'bg-blue-500/20 text-blue-300'}`}>
                                        {activity.type === 'ORDER' ? <ShoppingCart size={24} /> : <Package size={24} />}
                                    </div>
                                    <div>
                                        <h4 className="font-bold text-white text-lg">{activity.title}</h4>
                                        <p className="text-sm text-blue-100">{activity.description} • {activity.timestamp}</p>
                                    </div>
                                </div>
                            ))
                        )}
                    </div>
                </div>
                <div className="absolute -right-10 -bottom-10 text-white/5 pointer-events-none">
                    <Package size={300} />
                </div>
            </div>
        </div>
    );
}
