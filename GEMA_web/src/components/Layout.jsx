
import { Outlet, NavLink, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { LogOut, Package, ShoppingCart, Users, Home, Settings, Menu, X, TrendingUp, Bell, Sun, Moon } from 'lucide-react';
import { useState } from 'react';

export default function Layout() {
    const { user, logout } = useAuth();
    const { isDarkMode, toggleDarkMode } = useTheme();
    const navigate = useNavigate();
    const location = useLocation();
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

    const toggleMobileMenu = () => setIsMobileMenuOpen(!isMobileMenuOpen);
    const closeMobileMenu = () => setIsMobileMenuOpen(false);

    const navItems = [
        { name: 'Inicio', path: '/app', icon: Home },
        { name: 'Inventario', path: '/app/inventory', icon: Package },
        { name: 'Pedidos', path: '/app/orders', icon: ShoppingCart },
        { name: 'Clientes', path: '/app/clients', icon: Users },
        { name: 'Finanzas', path: '/app/finances', icon: TrendingUp },
        { name: 'Notificaciones', path: '/app/notifications', icon: Bell },
    ];

    return (
        <div className="min-h-screen bg-light-gray-bg dark:bg-[#0f172a] flex flex-col md:flex-row font-sans transition-colors duration-200">
            {/* Sidebar for Desktop */}
            <aside className="hidden md:flex flex-col w-64 bg-primary dark:bg-slate-900 text-white shadow-xl z-20 transition-colors">
                <div className="p-6 flex items-center justify-center border-b border-white/10">
                    <img src="/gema_white.svg" alt="GEMA" className="w-20 h-20 object-contain rounded-xl p-1" />
                </div>

                <nav className="flex-1 py-6 px-3 space-y-2">
                    {navItems.map((item) => (
                        <NavLink
                            key={item.path}
                            to={item.path}
                            end={item.path === '/app'}
                            className={({ isActive }) =>
                                `flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 group ${isActive
                                    ? 'bg-accent dark:bg-blue-600 text-white shadow-lg translate-x-2'
                                    : 'text-gray-300 hover:bg-white/10 hover:text-white'
                                }`
                            }
                        >
                            <item.icon size={20} className="group-hover:scale-110 transition-transform" />
                            <span className="font-medium">{item.name}</span>
                        </NavLink>
                    ))}
                </nav>

                <div className="p-4 border-t border-white/10 bg-primary-dark/50 dark:bg-slate-950/50">
                    <button 
                        onClick={toggleDarkMode}
                        className="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-gray-400 hover:text-white transition-all duration-200 mb-2 hover:bg-white/5"
                    >
                        {isDarkMode ? <Sun size={20} /> : <Moon size={20} />}
                        <span className="font-medium">{isDarkMode ? 'Modo Claro' : 'Modo Oscuro'}</span>
                    </button>
                    <NavLink
                        to="/app/settings"
                        className={({ isActive }) =>
                            `flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 mb-2 ${isActive ? 'bg-white/10 text-white' : 'text-gray-400 hover:text-white'
                            }`
                        }
                    >
                        <Settings size={20} />
                        <span className="font-medium">Configuración</span>
                    </NavLink>
                    <button 
                        onClick={() => {
                            logout();
                            navigate('/login');
                        }}
                        className="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-gray-400 hover:text-red-400 transition-all duration-200 hover:bg-red-900/10"
                    >
                        <LogOut size={20} />
                        <span className="font-medium">Cerrar Sesión</span>
                    </button>
                </div>
            </aside>

            {/* Mobile Header */}
            <div className="md:hidden bg-primary dark:bg-slate-900 text-white p-4 flex justify-between items-center shadow-md z-30 sticky top-0 transition-colors">
                <div className="flex items-center gap-2">
                    <img src="/gema_white.svg" alt="GEMA INVENTORY" className="w-8 h-8 object-contain" />
                    <h1 className="text-lg font-bold">GEMA INVENTORY</h1>
                </div>
                <div className="flex items-center gap-2">
                    <button onClick={toggleDarkMode} className="p-2 text-gray-300">
                        {isDarkMode ? <Sun size={20} /> : <Moon size={20} />}
                    </button>
                    <button onClick={toggleMobileMenu} className="p-2">
                        {isMobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
                    </button>
                </div>
            </div>

            {/* Mobile Menu Overlay */}
            {isMobileMenuOpen && (
                <div className="md:hidden fixed inset-0 bg-primary dark:bg-slate-900 z-20 pt-20 px-4 animate-in fade-in slide-in-from-top-10 duration-200">
                    <nav className="space-y-2">
                        {navItems.map((item) => (
                            <NavLink
                                key={item.path}
                                to={item.path}
                                end={item.path === '/app'}
                                onClick={closeMobileMenu}
                                className={({ isActive }) =>
                                    `flex items-center gap-4 px-4 py-4 rounded-xl text-lg ${isActive ? 'bg-accent dark:bg-blue-600 text-white' : 'text-gray-300'
                                    }`
                                }
                            >
                                <item.icon size={24} />
                                <span className="font-medium">{item.name}</span>
                            </NavLink>
                        ))}
                        <div className="h-px bg-white/10 my-4"></div>
                        <NavLink
                            to="/app/settings"
                            onClick={closeMobileMenu}
                            className={({ isActive }) =>
                                `flex items-center gap-4 px-4 py-4 rounded-xl text-lg ${isActive ? 'bg-white/10 text-white' : 'text-gray-400'
                                }`
                            }
                        >
                            <Settings size={24} />
                            <span className="font-medium">Configuración</span>
                        </NavLink>
                        <button 
                            onClick={() => {
                                logout();
                                navigate('/login');
                            }}
                            className="w-full flex items-center gap-4 px-4 py-4 rounded-xl text-lg text-red-400 hover:bg-white/5 transition-colors mt-auto"
                        >
                            <LogOut size={24} />
                            <span className="font-medium">Cerrar Sesión</span>
                        </button>
                    </nav>
                </div>
            )}

            {/* Main Content Area */}
            <main className="flex-1 flex flex-col h-[calc(100vh-64px)] md:h-screen overflow-hidden">
                {/* Header Bar */}
                <header className="bg-white dark:bg-slate-800 border-b border-gray-200 dark:border-slate-700 px-8 py-4 flex justify-between items-center shadow-sm z-10 hidden md:flex transition-colors">
                    <div className="flex items-center gap-3">
                        <img src={isDarkMode ? "/gema_white.svg" : "/src/assets/ic_logo_cuadrado_bb.png"} alt="Logo" className="w-8 h-8 object-contain" />
                        <h2 className="text-xl font-bold text-gray-800 dark:text-gray-100 tracking-wide font-sans">
                            GEMA INVENTORY
                        </h2>
                    </div>
                    <div className="flex items-center gap-4">
                        <div className="text-right">
                            <p className="text-sm font-bold text-gray-900 dark:text-white font-sans">{user?.nombre || "Usuario"}</p>
                            <p className="text-xs text-gray-500 dark:text-gray-400">{user?.correo || "Admin"}</p>
                        </div>
                        <div className="relative group cursor-pointer">
                            {/* Aro de luz giratorio */}
                            <div className="absolute -inset-1 bg-gradient-to-r from-primary via-blue-400 to-primary dark:from-blue-400 dark:via-purple-500 dark:to-blue-400 rounded-full blur opacity-75 group-hover:opacity-100 transition duration-1000 group-hover:duration-200 animate-spin-slow"></div>
                            
                            {/* Contenedor de la foto - hidden when on profile page to allow view transition */}
                            <div 
                                style={{ 
                                    viewTransitionName: 'profile-photo',
                                    visibility: location.pathname === '/app/settings/profile' ? 'hidden' : 'visible'
                                }}
                                className="relative w-11 h-11 bg-white dark:bg-slate-800 rounded-full flex items-center justify-center text-primary dark:text-white font-bold shadow-inner overflow-hidden border-2 border-white dark:border-slate-800 z-10">
                                {user?.imagen_url || user?.imagenUrl ? (
                                    <img src={user?.imagen_url || user?.imagenUrl} alt="Profile" className="w-full h-full object-cover" />
                                ) : (
                                    <span>{user?.nombre?.charAt(0)?.toUpperCase() || "U"}</span>
                                )}
                            </div>
                        </div>
                    </div>
                </header>

                {/* Scrollable Content */}
                <div className="flex-1 overflow-auto bg-light-gray-bg dark:bg-[#0f172a] p-4 md:p-8 relative transition-colors">
                    <Outlet />
                </div>
            </main>
        </div>
    );
}
