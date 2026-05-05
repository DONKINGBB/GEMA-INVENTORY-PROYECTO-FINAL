
import { useTheme } from '../context/ThemeContext';
import { Moon, Sun, ArrowLeft, Check, Palette, Monitor } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';

export default function AppearanceSettings() {
    const { isDarkMode, toggleDarkMode } = useTheme();
    const navigate = useNavigate();

    const themes = [
        { 
            id: 'light', 
            name: 'Modo Claro', 
            icon: Sun, 
            dark: false,
            desc: 'Ideal para ambientes con mucha luz natural.',
            accent: 'from-amber-400 to-orange-500'
        },
        { 
            id: 'dark', 
            name: 'Modo Oscuro', 
            icon: Moon, 
            dark: true,
            desc: 'Reduce la fatiga visual en entornos oscuros.',
            accent: 'from-indigo-500 to-purple-600'
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
                        Apariencia
                    </h1>
                    <p className="text-slate-500 dark:text-slate-400 text-sm">Personaliza cómo ves GEMA Inventory</p>
                </div>
            </div>

            <div className="bg-white dark:bg-slate-900/40 backdrop-blur-xl border border-slate-200 dark:border-white/5 rounded-[2.5rem] overflow-hidden shadow-xl shadow-black/5 dark:shadow-none">
                <div className="p-8 space-y-8">
                    <div className="flex items-center gap-4 p-4 bg-primary/5 rounded-2xl border border-primary/10">
                        <div className="p-2 bg-primary/20 rounded-xl text-primary">
                            <Palette size={20} />
                        </div>
                        <div>
                            <h3 className="text-slate-900 dark:text-white font-bold text-sm">Tema del Sistema</h3>
                            <p className="text-slate-500 dark:text-slate-400 text-xs">Selecciona el modo que mejor se adapte a tu vista.</p>
                        </div>
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                        {themes.map((theme, index) => (
                            <motion.button
                                key={theme.id}
                                initial={{ opacity: 0, scale: 0.9 }}
                                animate={{ opacity: 1, scale: 1 }}
                                transition={{ delay: index * 0.1 }}
                                onClick={() => {
                                    if (isDarkMode !== theme.dark) toggleDarkMode();
                                }}
                                className={`relative group p-6 rounded-3xl border-2 transition-all duration-500 text-left overflow-hidden ${
                                    isDarkMode === theme.dark 
                                    ? 'border-primary bg-primary/5 ring-4 ring-primary/10' 
                                    : 'border-slate-100 dark:border-white/5 bg-slate-50 dark:bg-white/5 hover:border-slate-200 dark:hover:border-white/10 hover:bg-slate-100 dark:hover:bg-white/10'
                                }`}
                            >
                                {/* Decorative Gradient */}
                                {isDarkMode === theme.dark && (
                                    <div className={`absolute -right-4 -top-4 w-24 h-24 bg-gradient-to-br ${theme.accent} opacity-20 blur-2xl rounded-full`} />
                                )}

                                <div className={`p-4 rounded-2xl mb-6 inline-block transition-transform duration-500 group-hover:scale-110 ${
                                    isDarkMode === theme.dark 
                                    ? 'bg-primary text-white shadow-lg shadow-primary/30' 
                                    : 'bg-slate-100 dark:bg-white/5 text-slate-400 dark:text-slate-500 border border-slate-200 dark:border-white/5'
                                }`}>
                                    <theme.icon size={28} />
                                </div>

                                <div className="relative z-10 space-y-1">
                                    <h3 className={`text-xl font-black transition-colors ${
                                        isDarkMode === theme.dark ? 'text-slate-900 dark:text-white' : 'text-slate-400 dark:text-slate-500'
                                    }`}>
                                        {theme.name}
                                    </h3>
                                    <p className="text-xs text-slate-500 font-medium">
                                        {theme.desc}
                                    </p>
                                </div>
                                
                                {isDarkMode === theme.dark && (
                                    <motion.div 
                                        initial={{ scale: 0 }}
                                        animate={{ scale: 1 }}
                                        className="absolute top-6 right-6 text-primary"
                                    >
                                        <div className="bg-primary/20 p-1 rounded-full">
                                            <Check size={20} strokeWidth={4} />
                                        </div>
                                    </motion.div>
                                )}
                            </motion.button>
                        ))}
                    </div>

                    {/* Preview Section */}
                    <div className="space-y-4">
                        <label className="text-xs font-black text-slate-500 uppercase tracking-widest ml-1">Vista Previa</label>
                        <div className="relative group p-8 rounded-[2rem] bg-slate-50 dark:bg-white/5 border border-slate-100 dark:border-white/5 overflow-hidden">
                            <div className="absolute inset-0 bg-gradient-to-br from-primary/5 via-transparent to-transparent opacity-50" />
                            
                            <div className="relative z-10 flex flex-col gap-4">
                                <div className="flex items-center gap-3">
                                    <div className="w-10 h-10 rounded-xl bg-primary/20 flex items-center justify-center text-primary">
                                        <Monitor size={20} />
                                    </div>
                                    <div className="space-y-1">
                                        <div className="h-3 w-32 bg-slate-200 dark:bg-white/20 rounded-full animate-pulse" />
                                        <div className="h-2 w-20 bg-slate-100 dark:bg-white/10 rounded-full" />
                                    </div>
                                </div>
                                <div className="grid grid-cols-3 gap-3">
                                    <div className="h-16 rounded-2xl bg-slate-100 dark:bg-white/5 border border-slate-200 dark:border-white/5" />
                                    <div className="h-16 rounded-2xl bg-slate-100 dark:bg-white/5 border border-slate-200 dark:border-white/5" />
                                    <div className="h-16 rounded-2xl bg-primary/20 border border-primary/20" />
                                </div>
                                <div className="h-4 w-full bg-slate-100 dark:bg-white/5 rounded-full" />
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Accent Color Hint */}
            <div className="bg-white dark:bg-slate-900/40 backdrop-blur-xl border border-slate-200 dark:border-white/5 rounded-[2rem] p-6 flex items-center justify-between shadow-xl shadow-black/5 dark:shadow-none">
                <div className="flex items-center gap-4">
                    <div className="p-3 bg-indigo-500/10 rounded-2xl text-indigo-400">
                        <Palette size={24} />
                    </div>
                    <div>
                        <p className="text-slate-900 dark:text-white font-bold">Colores de Acento</p>
                        <p className="text-xs text-slate-500">Próximamente: Personaliza el color principal del sistema.</p>
                    </div>
                </div>
                <div className="flex gap-2">
                    <div className="w-6 h-6 rounded-full bg-primary" />
                    <div className="w-6 h-6 rounded-full bg-emerald-500 opacity-30" />
                    <div className="w-6 h-6 rounded-full bg-orange-500 opacity-30" />
                </div>
            </div>
        </motion.div>
    );
}
