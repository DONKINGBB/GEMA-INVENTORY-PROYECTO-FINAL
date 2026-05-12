import { motion, AnimatePresence } from 'framer-motion';
import { X, LayoutGrid, Package, ChevronRight, Settings } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function WelcomeGuide({ isOpen, onClose }) {
    const navigate = useNavigate();
    const { user } = useAuth();
    const isAdmin = user?.rol?.idRol === 1 || user?.rol?.idRol === 2 || user?.idRol === 1 || user?.idRol === 2;

    const adminSteps = [
        {
            icon: <LayoutGrid className="text-blue-500" size={32} />,
            title: "Configura tus Catálogos",
            desc: "Antes de registrar productos, crea al menos una Categoría y un Almacén en la sección de configuración."
        },
        {
            icon: <Package className="text-indigo-500" size={32} />,
            title: "Registra tus Productos",
            desc: "Agrega tus productos asignándoles una categoría y define su stock inicial en los almacenes creados."
        }
    ];

    const staffSteps = [
        {
            icon: <Package className="text-blue-500" size={32} />,
            title: "Gestiona el Inventario",
            desc: "Ya puedes empezar a registrar ventas, compras y movimientos de stock en el negocio."
        },
        {
            icon: <LayoutGrid className="text-indigo-500" size={32} />,
            title: "Explora los Reportes",
            desc: "Revisa las métricas y el historial de transacciones para mantener el control."
        }
    ];

    const steps = isAdmin ? adminSteps : staffSteps;

    const handleAction = () => {
        onClose();
        if (isAdmin) {
            navigate('/app/settings');
        } else {
            navigate('/app/inventory');
        }
    };

    return (
        <AnimatePresence>
            {isOpen && (
                <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-md">
                    <motion.div
                        initial={{ opacity: 0, scale: 0.9, y: 20 }}
                        animate={{ opacity: 1, scale: 1, y: 0 }}
                        exit={{ opacity: 0, scale: 0.9, y: 20 }}
                        className="glass-card w-full max-w-xl rounded-[3rem] overflow-hidden relative shadow-2xl"
                    >
                        {/* Header Image/Pattern */}
                        <div className="h-48 bg-gradient-to-br from-blue-600 to-indigo-700 relative overflow-hidden flex items-center justify-center">
                            <div className="absolute inset-0 opacity-20 pattern-dots" />
                            <motion.div 
                                initial={{ scale: 0.5, opacity: 0 }}
                                animate={{ scale: 1, opacity: 1 }}
                                transition={{ delay: 0.2 }}
                                className="w-24 h-24 bg-white/20 backdrop-blur-xl rounded-3xl flex items-center justify-center border border-white/30"
                            >
                                <Settings className="text-white animate-spin-slow" size={48} />
                            </motion.div>
                        </div>

                        <div className="p-8 md:p-12 text-center space-y-8">
                            <div className="space-y-3">
                                <h2 className="text-3xl font-black text-slate-800 dark:text-white uppercase tracking-tight">
                                    ¡Bienvenido a GEMA!
                                </h2>
                                <p className="text-slate-500 dark:text-slate-400">
                                    Sigue estos pasos para comenzar a organizar tu inventario:
                                </p>
                            </div>

                            <div className="space-y-4">
                                {steps.map((step, idx) => (
                                    <motion.div
                                        key={idx}
                                        initial={{ opacity: 0, x: -20 }}
                                        animate={{ opacity: 1, x: 0 }}
                                        transition={{ delay: 0.3 + idx * 0.1 }}
                                        className="flex items-start gap-6 p-6 rounded-3xl bg-slate-50 dark:bg-white/5 border border-slate-100 dark:border-white/5 text-left group hover:border-blue-500/30 transition-colors"
                                    >
                                        <div className="w-14 h-14 shrink-0 rounded-2xl bg-white dark:bg-slate-800 shadow-sm flex items-center justify-center group-hover:scale-110 transition-transform">
                                            {step.icon}
                                        </div>
                                        <div className="space-y-1">
                                            <h4 className="font-bold text-slate-800 dark:text-white">{step.title}</h4>
                                            <p className="text-sm text-slate-500 dark:text-slate-400 leading-relaxed">{step.desc}</p>
                                        </div>
                                    </motion.div>
                                ))}
                            </div>

                            <div className="space-y-4 pt-4">
                                <button
                                    onClick={handleAction}
                                    className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-5 rounded-2xl shadow-xl shadow-blue-500/20 flex items-center justify-center gap-3 transition-all"
                                >
                                    {isAdmin ? 'Ir a Configuración Ahora' : 'Comenzar a Explorar'} <ChevronRight size={20} />
                                </button>
                                <button
                                    onClick={onClose}
                                    className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 font-medium transition-colors"
                                >
                                    Omitir por ahora
                                </button>
                            </div>
                        </div>

                        <button 
                            onClick={onClose}
                            className="absolute top-6 right-6 p-2 rounded-full bg-black/10 hover:bg-black/20 dark:bg-white/10 dark:hover:bg-white/20 text-white transition-colors"
                        >
                            <X size={20} />
                        </button>
                    </motion.div>
                </div>
            )}
        </AnimatePresence>
    );
}
