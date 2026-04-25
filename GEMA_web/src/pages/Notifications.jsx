import { useState, useEffect } from 'react';
import { ShoppingCart, Package, Info, Loader } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';

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
                        type: 'info',
                        title: o.nombre || `Venta #${(o.id || '').toString().slice(-5)}`,
                        message: `Total: $${parseFloat(o.total || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}`,
                        date: o.fechaPedido,
                        rawDate: new Date(o.fechaPedido || 0)
                    });
                });
                
                purchases.forEach(p => {
                    allItems.push({
                        id: p.id,
                        type: 'purchase',
                        title: 'Compra de Stock',
                        message: `${p.nombreProveedor || p.idProveedor || 'Proveedor General'} - Total: $${parseFloat(p.total || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}`,
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

    return (
        <div className="max-w-4xl mx-auto space-y-6 transition-colors">
            <div>
                <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Notificaciones</h1>
                <p className="text-gray-500 dark:text-gray-400">Mantente al tanto de la actividad de tu inventario</p>
            </div>

            <div className="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-gray-100 dark:border-slate-700 overflow-hidden transition-colors">
                {loading ? (
                    <div className="p-12 flex justify-center text-primary">
                        <Loader className="animate-spin" size={32} />
                    </div>
                ) : (
                    <div className="divide-y divide-gray-100 dark:divide-slate-700">
                        {notifications.map((n) => (
                            <div key={n.id} className="p-6 hover:bg-gray-50 dark:hover:bg-slate-700/50 transition flex gap-4 items-start">
                                <div className={`p-3 rounded-full flex-shrink-0 ${
                                    n.type === 'alert' ? 'bg-red-50 dark:bg-red-900/30 text-red-500 dark:text-red-400' :
                                    n.type === 'success' ? 'bg-green-50 dark:bg-green-900/30 text-green-500 dark:text-green-400' :
                                    n.type === 'purchase' ? 'bg-indigo-50 dark:bg-indigo-900/30 text-indigo-500 dark:text-indigo-400' :
                                    'bg-blue-50 dark:bg-blue-900/30 text-blue-500 dark:text-blue-400'
                                }`}>
                                    {n.type === 'purchase' ? <ShoppingCart size={24} /> : <Package size={24} />}
                                </div>
                                <div className="flex-1">
                                    <div className="flex justify-between items-center mb-1 gap-4">
                                        <h3 className="font-bold text-gray-900 dark:text-white">{n.title}</h3>
                                        <span className="text-xs text-gray-400 dark:text-gray-500">{n.date ? n.date.toString().substring(0, 10) : 'N/A'}</span>
                                    </div>
                                    <p className="text-gray-600 dark:text-gray-300">{n.message}</p>
                                </div>
                            </div>
                        ))}
                        {notifications.length === 0 && (
                            <div className="p-12 text-center text-gray-500 dark:text-gray-400">No hay notificaciones recientes.</div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}
