
import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { userService } from '../services/userService';
import { Bell, Save, Loader, ArrowLeft, ToggleLeft, ToggleRight, Box, ShoppingCart, RefreshCw } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function NotificationSettings() {
    const { user, login } = useAuth();
    const navigate = useNavigate();
    const [settings, setSettings] = useState({
        notifyLowStock: user?.notifyLowStock || false,
        notifyNewOrders: user?.notifyNewOrders || false,
        notifyInventoryChanges: user?.notifyInventoryChanges || false
    });
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState({ type: '', text: '' });

    const toggleSetting = (name) => {
        setSettings(prev => ({ ...prev, [name]: !prev[name] }));
    };

    const handleSubmit = async () => {
        setLoading(true);
        setMessage({ type: '', text: '' });
        try {
            const updatedUser = await userService.updateNotifications(user.id, settings);
            login({ ...user, ...updatedUser });
            setMessage({ type: 'success', text: 'Preferencias guardadas correctamente' });
        } catch (error) {
            setMessage({ type: 'error', text: 'Error al guardar preferencias' });
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
            color: 'text-orange-500 bg-orange-50'
        },
        { 
            id: 'notifyNewOrders', 
            label: 'Nuevos Pedidos', 
            desc: 'Notificar cuando se registre una nueva orden de cliente.',
            icon: ShoppingCart,
            color: 'text-blue-500 bg-blue-50'
        },
        { 
            id: 'notifyInventoryChanges', 
            label: 'Cambios de Inventario', 
            desc: 'Alertas sobre entradas y salidas manuales de stock.',
            icon: RefreshCw,
            color: 'text-green-500 bg-green-50'
        }
    ];

    return (
        <div className="max-w-2xl mx-auto">
            <button 
                onClick={() => navigate('/settings')}
                className="flex items-center gap-2 text-gray-500 hover:text-gray-700 mb-6 transition"
            >
                <ArrowLeft size={20} />
                <span>Volver a Configuración</span>
            </button>

            <div className="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-gray-100 dark:border-slate-700 overflow-hidden">
                <div className="p-6 border-b border-gray-100 dark:border-slate-700">
                    <h2 className="text-xl font-bold text-gray-900 dark:text-white flex items-center gap-2">
                        <Bell className="text-primary dark:text-blue-400" />
                        Notificaciones
                    </h2>
                    <p className="text-sm text-gray-500 dark:text-gray-400">Configura qué alertas deseas recibir</p>
                </div>

                <div className="p-6 space-y-6">
                    {message.text && (
                        <div className={`p-4 rounded-xl text-sm font-medium ${
                            message.type === 'success' ? 'bg-green-50 text-green-700 border border-green-100' : 'bg-red-50 text-red-700 border border-red-100'
                        }`}>
                            {message.text}
                        </div>
                    )}

                    <div className="divide-y divide-gray-100 dark:divide-slate-700">
                        {items.map((item) => (
                            <div key={item.id} className="py-4 flex items-center justify-between">
                                <div className="flex gap-4">
                                    <div className={`p-2 rounded-lg ${item.color} dark:bg-slate-900`}>
                                        <item.icon size={20} />
                                    </div>
                                    <div>
                                        <h4 className="font-medium text-gray-900 dark:text-white">{item.label}</h4>
                                        <p className="text-xs text-gray-500 dark:text-gray-400 max-w-xs">{item.desc}</p>
                                    </div>
                                </div>
                                <button 
                                    onClick={() => toggleSetting(item.id)}
                                    className={`transition-colors duration-200 p-1 rounded-full ${settings[item.id] ? 'text-primary dark:text-blue-400 scale-110' : 'text-gray-300'}`}
                                >
                                    {settings[item.id] ? <ToggleRight size={40} /> : <ToggleLeft size={40} />}
                                </button>
                            </div>
                        ))}
                    </div>

                    <div className="pt-4 border-t border-gray-100 dark:border-slate-700">
                        <button
                            onClick={handleSubmit}
                            disabled={loading}
                            className="w-full bg-primary hover:bg-primary-dark text-white font-bold py-3 rounded-xl shadow-lg transition flex items-center justify-center gap-2 disabled:opacity-50"
                        >
                            {loading ? <Loader className="animate-spin" size={20} /> : <Save size={20} />}
                            {loading ? 'Guardando...' : 'Guardar Preferencias'}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}
