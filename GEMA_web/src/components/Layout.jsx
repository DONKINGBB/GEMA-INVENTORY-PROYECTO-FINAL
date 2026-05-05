import { Outlet, NavLink, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { 
    LogOut, Package, ShoppingCart, Users, Home, 
    Settings, Menu, X, TrendingUp, Bell, Sun, Moon,
    ChevronRight, Search, LayoutDashboard, Layers,
    Building2, UserPlus, Shield, Palette, ChevronDown,
    Activity, Box, Truck, Sparkles, Info
} from 'lucide-react';
import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';

export default function Layout() {
    const { user, logout } = useAuth();
    const { isDarkMode, toggleDarkMode } = useTheme();
    const navigate = useNavigate();
    const location = useLocation();
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
    const [scrolled, setScrolled] = useState(false);
    const [openSubmenu, setOpenSubmenu] = useState(null);
    const [toast, setToast] = useState(null);

    const showToast = (message, type = 'info') => {
        setToast({ message, type });
        setTimeout(() => setToast(null), 3000);
    };

    useEffect(() => {
        const handleScroll = () => setScrolled(window.scrollY > 20);
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    const navigation = [
        {
            title: 'Principal',
            items: [
                { name: 'Dashboard', path: '/app', icon: LayoutDashboard, roles: [1,2,3,4,5,6] },
                { name: 'Notificaciones', path: '/app/notifications', icon: Bell, roles: [1,2,3,4,5,6] },
            ]
        },
        {
            title: 'Operaciones',
            items: [
                { name: 'Inventario', path: '/app/inventory', icon: Package, roles: [1,2,3,4,6] },
                { name: 'Pedidos', path: '/app/orders', icon: ShoppingCart, roles: [1,2,3,4,5] },
                { name: 'Clientes', path: '/app/clients', icon: Users, roles: [1,2,3,4] },
            ]
        },
        {
            title: 'Análisis',
            items: [
                { name: 'Finanzas', path: '/app/finances', icon: TrendingUp, roles: [1,2] },
            ]
        },
        {
            title: 'Configuración',
            items: [
                { name: 'Equipo', path: '/app/settings/team', icon: UserPlus, roles: [1,2] },
                { name: 'Mi Negocio', path: '/app/settings/business', icon: Building2, roles: [1,2] },
                {
                    name: 'Catálogos',
                    icon: Layers,
                    roles: [1,2,3],
                    submenu: [
                        { name: 'Categorías', path: '/app/settings/categories', icon: Activity },
                        { name: 'Almacenes', path: '/app/settings/warehouses', icon: Box },
                        { name: 'Proveedores', path: '/app/settings/suppliers', icon: Truck },
                        { name: 'Clientes', path: '/app/settings/clients', icon: Users },
                    ]
                },
                { name: 'Configuración', path: '/app/settings', icon: Settings, roles: [1,2,3,4,5,6] },
            ]
        }
    ];

    const sidebarVariants = {
        open: { x: 0, transition: { type: 'spring', stiffness: 300, damping: 30 } },
        closed: { x: '-100%', transition: { type: 'spring', stiffness: 300, damping: 30 } }
    };

    const hasRole = (allowedRoles) => {
        if (!allowedRoles) return true;
        return allowedRoles.includes(user?.idRol);
    };

    const toggleSubmenu = (name) => {
        setOpenSubmenu(openSubmenu === name ? null : name);
    };

    const NavItem = ({ item, isMobile = false }) => {
        const isActive = location.pathname === item.path || (item.submenu && item.submenu.some(s => location.pathname === s.path));
        const hasSubmenu = !!item.submenu;

        if (!hasRole(item.roles)) return null;

        if (hasSubmenu) {
            const isSubmenuOpen = openSubmenu === item.name;
            return (
                <div className="space-y-1">
                    <button
                        onClick={() => toggleSubmenu(item.name)}
                        className={`
                            w-full flex items-center justify-between px-4 py-3 rounded-2xl transition-all duration-300 group relative overflow-hidden
                            ${isActive 
                                ? 'bg-gradient-to-r from-primary/20 to-transparent text-primary' 
                                : 'text-slate-500 dark:text-white/40 hover:text-slate-900 dark:hover:text-white hover:bg-black/5 dark:hover:bg-white/5'
                            }
                        `}
                    >
                        {isActive && <div className="absolute left-0 top-0 bottom-0 w-1 bg-primary rounded-r-full" />}
                        <div className="flex items-center gap-3">
                            <item.icon size={20} className={`transition-all duration-300 ${isActive ? 'scale-110 drop-shadow-[0_0_8px_rgba(59,130,246,0.5)]' : 'group-hover:scale-110 opacity-70 group-hover:opacity-100'}`} />
                            <span className="font-black text-[11px] uppercase tracking-widest">{item.name}</span>
                        </div>
                        <ChevronDown 
                            size={14} 
                            className={`transition-transform duration-500 ${isSubmenuOpen ? 'rotate-180 text-primary' : 'opacity-40'}`} 
                        />
                    </button>
                    <AnimatePresence>
                        {isSubmenuOpen && (
                            <motion.div
                                initial={{ height: 0, opacity: 0 }}
                                animate={{ height: 'auto', opacity: 1 }}
                                exit={{ height: 0, opacity: 0 }}
                                className="overflow-hidden pl-5 space-y-1"
                            >
                                {item.submenu.map((sub) => (
                                    <NavLink
                                        key={sub.path}
                                        to={sub.path}
                                        onClick={() => isMobile && setIsMobileMenuOpen(false)}
                                        className={({ isActive }) => `
                                            flex items-center gap-3 px-4 py-3 rounded-xl transition-all text-[10px] font-black uppercase tracking-[0.15em]
                                            ${isActive 
                                                ? 'text-primary bg-primary/10 dark:bg-primary/20 border border-primary/20 dark:border-primary/30 shadow-[0_0_15px_rgba(59,130,246,0.1)]' 
                                                : 'text-slate-400 dark:text-white/20 hover:text-slate-700 dark:hover:text-white/60 hover:bg-black/5 dark:hover:bg-white/5'
                                            }
                                        `}
                                    >
                                        <sub.icon size={12} className={isActive ? 'animate-pulse' : ''} />
                                        {sub.name}
                                    </NavLink>
                                ))}
                            </motion.div>
                        )}
                    </AnimatePresence>
                </div>
            );
        }

        return (
            <NavLink
                to={item.path}
                end={item.path === '/app'}
                onClick={() => isMobile && setIsMobileMenuOpen(false)}
                className={({ isActive }) => `
                    flex items-center gap-3 px-4 py-3.5 rounded-2xl transition-all duration-300 group relative overflow-hidden
                    ${isActive 
                        ? 'bg-primary/10 dark:bg-primary/20 backdrop-blur-md border border-primary/20 dark:border-primary/30 text-primary shadow-[0_0_25px_rgba(59,130,246,0.1)]' 
                        : 'text-slate-500 dark:text-white/50 hover:text-slate-900 dark:hover:text-white hover:bg-black/5 dark:hover:bg-white/5'
                    }
                `}
            >
                {({ isActive }) => (
                    <>
                        {isActive && <div className="absolute left-0 top-1/2 -translate-y-1/2 h-8 w-1 bg-primary rounded-r-full" />}
                        <item.icon size={20} className={`transition-all duration-300 ${isActive ? 'scale-110 drop-shadow-[0_0_8px_rgba(59,130,246,0.5)]' : 'group-hover:scale-110 opacity-70 group-hover:opacity-100'}`} />
                        <span className="font-black text-[11px] uppercase tracking-widest">{item.name}</span>
                        {isActive && (
                            <motion.div 
                                layoutId={isMobile ? "activeNavMobile" : "activeNav"} 
                                className="absolute right-4 w-1.5 h-1.5 bg-primary rounded-full shadow-[0_0_10px_rgba(59,130,246,0.8)]"
                            />
                        )}
                    </>
                )}
            </NavLink>
        );
    };

    return (
        <div className="min-h-screen flex text-slate-800 dark:text-white overflow-hidden selection:bg-primary/30 font-sans bg-slate-50 dark:bg-[#030712] transition-colors duration-500">
            {/* Sidebar */}
            <aside className="hidden lg:flex flex-col w-[340px] h-screen sticky top-0 bg-white dark:bg-gradient-to-b dark:from-slate-950 dark:to-[#020617] border-r border-black/5 dark:border-white/5 z-50 transition-colors duration-500 overflow-hidden">
                {/* Decorative background elements for sidebar */}
                <div className="absolute top-0 right-0 w-48 h-48 bg-primary/10 blur-[80px] -z-10 opacity-30" />
                <div className="absolute bottom-0 left-0 w-48 h-48 bg-indigo-600/10 blur-[80px] -z-10 opacity-30" />

                <div className="p-8">
                    <motion.div 
                        whileHover={{ scale: 1.02 }}
                        className="flex items-center gap-4 cursor-pointer group"
                        onClick={() => navigate('/app')}
                    >
                        <div className="relative">
                            <div className="absolute inset-0 bg-primary blur-lg opacity-20 group-hover:opacity-40 transition-opacity" />
                            <motion.img 
                                animate={{ 
                                    y: [0, -4, 0],
                                    rotate: [0, 2, -2, 0]
                                }}
                                transition={{ 
                                    duration: 4, 
                                    repeat: Infinity,
                                    ease: "easeInOut"
                                }}
                                src="/gema_white.svg" 
                                alt="GEMA" 
                                className="w-10 h-10 relative z-10 drop-shadow-2xl dark:invert-0 invert" 
                            />
                        </div>
                        <div className="flex flex-col">
                            <span className="text-xl font-black tracking-tighter block leading-none text-slate-900 dark:text-white">
                                GEMA <span className="text-primary italic brightness-125 drop-shadow-[0_0_8px_rgba(59,130,246,0.5)]">INVENTORY</span>
                            </span>
                            <span className="text-[9px] font-black text-slate-500 dark:text-white/40 uppercase tracking-[0.4em] mt-1.5">Intelligence v2.0</span>
                        </div>
                    </motion.div>
                </div>

                <nav className="flex-1 px-4 space-y-8 overflow-y-auto custom-scrollbar pb-10">
                    {navigation.map((section) => {
                        const visibleItems = section.items.filter(item => hasRole(item.roles));
                        if (visibleItems.length === 0) return null;
                        
                        return (
                            <div key={section.title} className="space-y-4">
                                <div className="flex items-center gap-3 px-4">
                                    <div className="h-[1px] flex-1 bg-black/[0.05] dark:bg-white/[0.03]" />
                                    <p className="text-[9px] font-black text-slate-500 dark:text-white/20 uppercase tracking-[0.4em]">{section.title}</p>
                                    <div className="h-[1px] flex-1 bg-black/[0.05] dark:bg-white/[0.03]" />
                                </div>
                                <div className="space-y-1">
                                    {section.items.map((item) => (
                                        <NavItem key={item.name} item={item} />
                                    ))}
                                </div>
                            </div>
                        );
                    })}
                </nav>

                <div className="p-6 border-t border-black/5 dark:border-white/5 bg-slate-50/50 dark:bg-black/20">
                    <div className="p-5 rounded-[2.5rem] bg-white/20 dark:bg-white/[0.02] border border-black/5 dark:border-white/5 backdrop-blur-3xl relative overflow-hidden group">
                        <div className="absolute top-0 right-0 w-20 h-20 bg-primary/5 blur-2xl group-hover:bg-primary/10 transition-colors" />
                        
                        <div className="flex items-center gap-4 mb-6">
                            <div className="relative">
                                <div className="absolute inset-0 bg-primary/20 blur-md rounded-2xl" />
                                <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-primary to-indigo-600 p-[1.5px] relative z-10 shadow-lg">
                                    <div className="w-full h-full rounded-[0.9rem] bg-slate-950 flex items-center justify-center overflow-hidden">
                                        {(user?.imagen_url || user?.imagenUrl) ? (
                                            <img 
                                                src={user.imagen_url || user.imagenUrl} 
                                                alt="Profile" 
                                                className="w-full h-full object-cover"
                                                onError={(e) => {
                                                    e.target.onerror = null;
                                                    e.target.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(user?.nombre || 'U')}&background=0D8ABC&color=fff&bold=true`;
                                                }}
                                            />
                                        ) : (
                                            <div className="w-full h-full bg-gradient-to-br from-primary/20 to-indigo-600/20 flex items-center justify-center">
                                                <span className="text-sm font-black text-primary">{user?.nombre?.charAt(0) || 'U'}</span>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                            <div className="flex-1 min-w-0">
                                <p className="text-xs font-black truncate text-slate-900 dark:text-white uppercase tracking-wider">{user?.nombre || 'Usuario'}</p>
                                <div className="flex items-center gap-1.5 mt-1">
                                    <div className="w-1.5 h-1.5 rounded-full bg-emerald-500 shadow-[0_0_10px_rgba(16,185,129,0.5)]" />
                                    <p className="text-[8px] text-slate-400 dark:text-white/30 truncate uppercase font-black tracking-[0.15em]">
                                        {user?.idRol === 1 ? 'Propietario' : user?.idRol === 2 ? 'Admin' : 'Operador'}
                                    </p>
                                </div>
                            </div>
                        </div>
                        
                        <div className="grid grid-cols-2 gap-2">
                            <button 
                                onClick={toggleDarkMode}
                                className="p-3.5 rounded-2xl bg-black/5 dark:bg-white/5 hover:bg-black/10 dark:hover:bg-white/10 text-slate-400 dark:text-white/30 hover:text-slate-900 dark:hover:text-white transition-all flex items-center justify-center border border-black/5 dark:border-white/5"
                            >
                                {isDarkMode ? <Sun size={16} /> : <Moon size={16} />}
                            </button>
                            <button 
                                onClick={() => navigate('/app/settings')}
                                className="p-3.5 rounded-2xl bg-black/5 dark:bg-white/5 hover:bg-black/10 dark:hover:bg-white/10 text-slate-400 dark:text-white/30 hover:text-slate-900 dark:hover:text-white transition-all flex items-center justify-center border border-black/5 dark:border-white/5"
                            >
                                <Settings size={16} />
                            </button>
                            <button 
                                onClick={() => { logout(); navigate('/login'); }}
                                className="col-span-2 p-4 rounded-2xl bg-rose-500/5 hover:bg-rose-500/10 text-rose-500/60 hover:text-rose-500 transition-all flex items-center justify-center gap-3 border border-rose-500/10 font-black text-[9px] tracking-[0.2em] uppercase mt-1"
                            >
                                <LogOut size={16} />
                                SALIR DEL SISTEMA
                            </button>
                        </div>
                    </div>
                    <div className="mt-6 text-center">
                        <p className="text-[8px] font-black text-slate-400 dark:text-white/10 uppercase tracking-[0.3em]">
                            © 2026 JEDD AI • All Rights Reserved
                        </p>
                    </div>
                </div>
            </aside>

            {/* Main Content */}
            <main className="flex-1 h-screen flex flex-col relative overflow-hidden bg-white dark:bg-[#020617] transition-colors duration-500">
                {/* Visual accents */}
                <div className="absolute top-0 right-0 w-[800px] h-[800px] bg-primary/20 blur-[180px] -z-10 rounded-full opacity-60 animate-pulse" />
                <div className="absolute bottom-0 left-0 w-[600px] h-[600px] bg-indigo-600/15 blur-[150px] -z-10 rounded-full opacity-50" />
                <div className="absolute top-1/2 left-1/4 w-[400px] h-[400px] bg-purple-600/10 blur-[120px] -z-10 rounded-full opacity-40" />
                <div className="absolute bottom-1/4 right-1/4 w-[300px] h-[300px] bg-blue-500/5 blur-[100px] -z-10 rounded-full opacity-30" />

                {/* Header Bar */}
                <header className={`
                    h-24 px-10 flex items-center justify-between z-40 transition-all duration-500
                    ${scrolled ? 'bg-white/80 dark:bg-slate-950/60 border-b border-black/5 dark:border-white/5 backdrop-blur-2xl shadow-2xl' : 'bg-transparent'}
                `}>
                    <div className="flex items-center gap-10 flex-1">
                        <div className="lg:hidden flex items-center gap-3 cursor-pointer" onClick={() => navigate('/app')}>
                            <img src="/gema_white.svg" alt="Logo" className="w-10 h-10 drop-shadow-2xl dark:invert-0 invert" />
                            <span className="font-black tracking-tighter text-2xl uppercase text-slate-900 dark:text-white">GEMA INVENTORY</span>
                        </div>

                        {/* Search Bar */}
                        <motion.div 
                            whileHover={{ scale: 1.01 }}
                            className="hidden md:flex flex-1 max-w-xl relative group"
                        >
                            <Search className="absolute left-6 top-1/2 -translate-y-1/2 text-slate-400 dark:text-white/30 group-focus-within:text-primary transition-colors" size={18} />
                            <input 
                                type="text" 
                                placeholder="Búsqueda inteligente en inventario..."
                                onKeyDown={(e) => {
                                    if (e.key === 'Enter') {
                                        showToast('Búsqueda en desarrollo: filtrando resultados...', 'info');
                                    }
                                }}
                                className="w-full bg-black/5 dark:bg-white/[0.05] border border-black/5 dark:border-white/10 rounded-3xl py-4 pl-16 pr-8 outline-none focus:ring-1 focus:ring-primary/30 focus:bg-white/[0.08] transition-all text-xs font-medium placeholder:text-slate-400 dark:placeholder:text-white/30 placeholder:uppercase placeholder:tracking-widest"
                            />
                        </motion.div>
                    </div>

                    <div className="flex items-center gap-5">
                        <motion.button 
                            whileHover={{ scale: 1.05, boxShadow: "0 0 30px rgba(245, 158, 11, 0.6)" }}
                            whileTap={{ scale: 0.95 }}
                            animate={{ 
                                boxShadow: ["0 0 15px rgba(245, 158, 11, 0.2)", "0 0 25px rgba(245, 158, 11, 0.5)", "0 0 15px rgba(245, 158, 11, 0.2)"]
                            }}
                            transition={{ duration: 2, repeat: Infinity }}
                            onClick={() => navigate('/app/premium')}
                            className="hidden xl:flex items-center gap-3 px-6 py-2.5 bg-gradient-to-r from-amber-400 via-yellow-500 to-amber-600 border border-amber-400/30 rounded-2xl shadow-[0_0_15px_rgba(245,158,11,0.2)] relative overflow-hidden group"
                        >
                            <motion.div 
                                animate={{ x: ['-100%', '200%'] }}
                                transition={{ duration: 2, repeat: Infinity, ease: "linear", repeatDelay: 1 }}
                                className="absolute inset-0 bg-gradient-to-r from-transparent via-white/30 to-transparent -skew-x-12"
                            />
                            <Sparkles size={14} className="text-white drop-shadow-md" />
                            <span className="text-[10px] font-black text-white uppercase tracking-widest drop-shadow-sm">Upgrade to Premium</span>
                        </motion.button>

                        <motion.button 
                            whileHover={{ scale: 1.05 }}
                            whileTap={{ scale: 0.95 }}
                            onClick={() => navigate('/app/notifications')}
                            className="w-14 h-14 rounded-[1.2rem] bg-black/5 dark:bg-white/[0.05] border border-black/5 dark:border-white/10 flex items-center justify-center text-slate-500 dark:text-white/40 hover:text-primary hover:bg-primary/10 hover:border-primary/20 transition-all relative group shadow-lg shadow-black/5"
                        >
                            <Bell size={22} className="group-hover:rotate-12 transition-transform" />
                            <span className="absolute top-4 right-4 w-2.5 h-2.5 bg-primary rounded-full border-[3px] border-white dark:border-[#020617] shadow-[0_0_15px_rgba(59,130,246,0.8)]" />
                        </motion.button>

                        <div className="lg:hidden">
                            <button 
                                onClick={() => setIsMobileMenuOpen(true)}
                                className="w-14 h-14 rounded-[1.2rem] bg-primary flex items-center justify-center shadow-2xl shadow-primary/30 active:scale-90 transition-transform"
                            >
                                <Menu size={24} />
                            </button>
                        </div>
                    </div>
                </header>

                {/* Page Content */}
                <div className="flex-1 overflow-y-auto overflow-x-hidden custom-scrollbar">
                    <div className="p-8 md:p-12 lg:p-16 max-w-[1600px] mx-auto relative">
                        <AnimatePresence mode="wait">
                            <motion.div
                                key={location.pathname}
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                exit={{ opacity: 0, y: -20 }}
                                transition={{ duration: 0.4, ease: [0.16, 1, 0.3, 1] }}
                            >
                                <Outlet />
                            </motion.div>
                        </AnimatePresence>
                    </div>
                </div>
            </main>

            {/* Mobile Sidebar Overlay */}
            <AnimatePresence>
                {isMobileMenuOpen && (
                    <>
                        <motion.div 
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            exit={{ opacity: 0 }}
                            onClick={() => setIsMobileMenuOpen(false)}
                            className="fixed inset-0 bg-black/90 backdrop-blur-xl z-[60] lg:hidden"
                        />
                        <motion.aside
                            variants={sidebarVariants}
                            initial="closed"
                            animate="open"
                            exit="closed"
                            className="fixed inset-y-0 left-0 w-80 bg-white dark:bg-[#030712] border-r border-black/5 dark:border-white/10 z-[70] lg:hidden flex flex-col transition-colors duration-500"
                        >
                            <div className="p-10 flex justify-between items-center border-b border-black/5 dark:border-white/5">
                                <div className="flex items-center gap-4">
                                    <img src="/gema_white.svg" alt="GEMA" className="w-10 h-10 dark:invert-0 invert" />
                                    <span className="text-2xl font-black tracking-tighter text-slate-900 dark:text-white uppercase">GEMA INVENTORY</span>
                                </div>
                                <button 
                                    onClick={() => setIsMobileMenuOpen(false)} 
                                    className="w-12 h-12 flex items-center justify-center rounded-2xl bg-slate-100 dark:bg-white/5 text-slate-500 dark:text-white/50"
                                >
                                    <X size={22} />
                                </button>
                            </div>

                            <nav className="flex-1 px-6 py-10 space-y-10 overflow-y-auto">
                                {navigation.map((section) => {
                                    const visibleItems = section.items.filter(item => hasRole(item.roles));
                                    if (visibleItems.length === 0) return null;

                                    return (
                                        <div key={section.title} className="space-y-5">
                                            <p className="px-4 text-[10px] font-black text-slate-400 dark:text-white/20 uppercase tracking-[0.4em]">{section.title}</p>
                                            <div className="space-y-1">
                                                {section.items.map((item) => (
                                                    <NavItem key={item.name} item={item} isMobile />
                                                ))}
                                            </div>
                                        </div>
                                    );
                                })}
                            </nav>

                            <div className="p-8 border-t border-white/5">
                                <button 
                                    onClick={() => { logout(); navigate('/login'); }}
                                    className="w-full p-5 rounded-3xl bg-rose-500/10 text-rose-500 font-black text-[10px] tracking-widest uppercase flex items-center justify-center gap-4"
                                >
                                    <LogOut size={20} />
                                    CERRAR SESIÓN
                                </button>
                            </div>
                        </motion.aside>
                    </>
                )}
            </AnimatePresence>

            {/* Global Notification Toast */}
            <AnimatePresence>
                {toast && (
                    <motion.div
                        initial={{ opacity: 0, y: 50, x: '-50%' }}
                        animate={{ opacity: 1, y: 0, x: '-50%' }}
                        exit={{ opacity: 0, y: 20, x: '-50%' }}
                        className={`fixed bottom-10 left-1/2 z-[100] px-8 py-4 rounded-3xl border shadow-2xl backdrop-blur-2xl flex items-center gap-4 min-w-[320px]
                            ${toast.type === 'premium' 
                                ? 'bg-gradient-to-r from-amber-500 to-yellow-600 border-amber-400/30 text-white' 
                                : 'bg-white/90 dark:bg-slate-900/90 border-black/5 dark:border-white/10 text-slate-800 dark:text-white'
                            }
                        `}
                    >
                        {toast.type === 'premium' ? <Sparkles size={20} /> : <Info size={20} className="text-primary" />}
                        <span className="text-xs font-black uppercase tracking-widest">{toast.message}</span>
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
}
