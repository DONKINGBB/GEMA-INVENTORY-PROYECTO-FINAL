
import { useAuth } from '../context/AuthContext';
import { LogOut, User, Shield, Bell, Moon, Tags, Home as HomeIcon } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function Settings() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const sections = [
        {
            title: "Cuenta",
            items: [
                { icon: User, label: "Perfil de Usuario", desc: "Administra tu información personal", path: "/settings/profile" },
                { icon: Shield, label: "Seguridad", desc: "Cambiar contraseña y 2FA", path: "/settings/security" }
            ]
        },
        {
            title: "Inventario",
            items: [
                { icon: Tags, label: "Administrar Categorías", desc: "Crear y editar categorías de productos", path: "/settings/categories" },
                { icon: HomeIcon, label: "Administrar Almacenes", desc: "Gestionar sucursales o proveedores", path: "/settings/warehouses" }
            ]
        },
        {
            title: "Preferencias",
            items: [
                { icon: Bell, label: "Notificaciones", desc: "Configura tus alertas de stock", path: "/settings/notifications" },
                { icon: Moon, label: "Apariencia", desc: "Modo oscuro y temas", path: "/settings/appearance" }
            ]
        }
    ];

    return (
        <div className="max-w-2xl mx-auto py-8">
            <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-8">Configuración</h1>

            <div className="space-y-8">
                {sections.map((section, idx) => (
                    <div key={idx} className="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-gray-100 dark:border-slate-700 overflow-hidden">
                        <div className="px-6 py-4 bg-gray-50 dark:bg-slate-900/50 border-b border-gray-100 dark:border-slate-700">
                            <h3 className="font-bold text-gray-700 dark:text-gray-300">{section.title}</h3>
                        </div>
                        <div className="divide-y divide-gray-100 dark:divide-slate-700">
                            {section.items.map((item, i) => (
                                <button 
                                    key={i} 
                                    onClick={() => item.path && navigate(item.path)}
                                    className="w-full px-6 py-4 flex items-center gap-4 hover:bg-gray-50 dark:hover:bg-slate-700/50 transition text-left"
                                >
                                    <div className="p-2 bg-blue-50 dark:bg-slate-900 text-primary dark:text-blue-400 rounded-lg">
                                        <item.icon size={20} />
                                    </div>
                                    <div>
                                        <h4 className="font-medium text-gray-900 dark:text-white">{item.label}</h4>
                                        <p className="text-sm text-gray-500 dark:text-gray-400">{item.desc}</p>
                                    </div>
                                </button>
                            ))}
                        </div>
                    </div>
                ))}

                <button
                    onClick={logout}
                    className="w-full bg-red-50 dark:bg-red-900/20 hover:bg-red-100 dark:hover:bg-red-900/40 text-red-600 dark:text-red-400 font-bold py-4 rounded-2xl flex items-center justify-center gap-2 transition"
                >
                    <LogOut size={20} />
                    Cerrar Sesión
                </button>
            </div>
        </div>
    );
}
