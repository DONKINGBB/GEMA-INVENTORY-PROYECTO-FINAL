
import { useTheme } from '../context/ThemeContext';
import { Moon, Sun, ArrowLeft, Check, Palette } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function AppearanceSettings() {
    const { isDarkMode, toggleDarkMode } = useTheme();
    const navigate = useNavigate();

    const themes = [
        { id: 'light', name: 'Claro', icon: Sun, dark: false },
        { id: 'dark', name: 'Oscuro', icon: Moon, dark: true }
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
                        <Palette className="text-primary dark:text-blue-400" />
                        Apariencia
                    </h2>
                    <p className="text-sm text-gray-500 dark:text-gray-400">Personaliza el aspecto visual del panel</p>
                </div>

                <div className="p-6 space-y-8">
                    <div className="grid grid-cols-2 gap-4">
                        {themes.map((theme) => (
                            <button
                                key={theme.id}
                                onClick={() => {
                                    if (isDarkMode !== theme.dark) toggleDarkMode();
                                }}
                                className={`relative group p-4 rounded-2xl border-2 transition-all duration-200 text-left ${
                                    isDarkMode === theme.dark 
                                    ? 'border-primary bg-blue-50/50 dark:border-blue-400 dark:bg-blue-900/20' 
                                    : 'border-gray-100 hover:border-gray-200 bg-gray-50 dark:border-slate-700 dark:bg-slate-900'
                                }`}
                            >
                                <div className={`p-3 rounded-xl mb-4 inline-block ${
                                    isDarkMode === theme.dark 
                                    ? 'bg-primary text-white dark:bg-blue-400' 
                                    : 'bg-white text-gray-400 dark:bg-slate-800'
                                }`}>
                                    <theme.icon size={24} />
                                </div>
                                <h3 className={`font-bold transition-colors ${
                                    isDarkMode === theme.dark ? 'text-primary dark:text-blue-400' : 'text-gray-600 dark:text-gray-400'
                                }`}>
                                    Tema {theme.name}
                                </h3>
                                
                                {isDarkMode === theme.dark && (
                                    <div className="absolute top-4 right-4 text-primary dark:text-blue-400 animate-in zoom-in duration-300">
                                        <Check size={20} strokeWidth={3} />
                                    </div>
                                )}
                            </button>
                        ))}
                    </div>

                    <div className="bg-gray-50 dark:bg-slate-900 p-6 rounded-2xl border border-dashed border-gray-200 dark:border-slate-700">
                        <h4 className="font-bold text-gray-700 dark:text-gray-300 mb-2">Vista Previa</h4>
                        <div className="space-y-3">
                            <div className="h-4 w-3/4 bg-gray-200 dark:bg-slate-800 rounded-full"></div>
                            <div className="h-4 w-1/2 bg-gray-200 dark:bg-slate-800 rounded-full"></div>
                            <div className="flex gap-2 pt-2">
                                <div className="h-8 w-24 bg-primary dark:bg-blue-400 rounded-lg"></div>
                                <div className="h-8 w-24 bg-gray-200 dark:bg-slate-800 rounded-lg"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
