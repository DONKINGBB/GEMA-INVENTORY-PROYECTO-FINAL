
import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { userService } from '../services/userService';
import { Shield, Lock, Save, Loader2, ArrowLeft, AlertCircle, Fingerprint, Sparkles } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import toast from 'react-hot-toast';

export default function SecuritySettings() {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        password: '',
        confirmPassword: ''
    });
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (formData.password !== formData.confirmPassword) {
            toast.error('Las contraseñas no coinciden');
            return;
        }

        setLoading(true);
        try {
            const userData = {
                user: user.user,
                nombre: user.nombre,
                password: formData.password,
                idRol: user.idRol
            };
            await userService.changePassword(user.id, userData);
            toast.success('Contraseña actualizada correctamente');
            setFormData({ password: '', confirmPassword: '' });
        } catch (error) {
            toast.error('Error al actualizar la contraseña');
        } finally {
            setLoading(false);
        }
    };

    return (
        <motion.div 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="max-w-4xl mx-auto space-y-6 pb-20 sm:pb-0"
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
                        Seguridad
                    </h1>
                    <p className="text-slate-500 dark:text-slate-400 text-sm">Protección de cuenta y accesos</p>
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-12 gap-6">
                {/* Left Column - Form */}
                <div className="md:col-span-7">
                    <div className="bg-white dark:bg-slate-900/40 backdrop-blur-xl border border-slate-200 dark:border-white/5 rounded-[2.5rem] overflow-hidden shadow-sm dark:shadow-none">
                        <div className="p-8 border-b border-slate-100 dark:border-white/5 bg-slate-50/50 dark:bg-white/5">
                            <h2 className="text-xl font-black text-slate-900 dark:text-white flex items-center gap-3">
                                <div className="p-2 bg-primary/10 rounded-xl text-primary">
                                    <Lock size={20} />
                                </div>
                                Cambiar Contraseña
                            </h2>
                        </div>

                        <form onSubmit={handleSubmit} className="p-8 space-y-6">
                            <div className="space-y-5">
                                <div className="space-y-2">
                                    <label className="text-xs font-black text-slate-500 dark:text-slate-400 uppercase tracking-widest ml-1">Nueva Contraseña</label>
                                    <div className="relative group">
                                        <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 dark:text-slate-500 group-focus-within:text-primary transition-colors" size={20} />
                                        <input
                                            type="password"
                                            name="password"
                                            value={formData.password}
                                            onChange={handleChange}
                                            className="w-full pl-12 pr-4 py-4 bg-slate-50 dark:bg-white/5 border border-slate-200 dark:border-white/10 rounded-2xl focus:ring-4 focus:ring-primary/10 focus:border-primary/30 outline-none transition-all text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-slate-600 font-medium"
                                            placeholder="Introduce tu nueva contraseña"
                                            required
                                            minLength={6}
                                        />
                                    </div>
                                </div>

                                <div className="space-y-2">
                                    <label className="text-xs font-black text-slate-500 dark:text-slate-400 uppercase tracking-widest ml-1">Confirmar Contraseña</label>
                                    <div className="relative group">
                                        <Shield className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 dark:text-slate-500 group-focus-within:text-primary transition-colors" size={20} />
                                        <input
                                            type="password"
                                            name="confirmPassword"
                                            value={formData.confirmPassword}
                                            onChange={handleChange}
                                            className="w-full pl-12 pr-4 py-4 bg-slate-50 dark:bg-white/5 border border-slate-200 dark:border-white/10 rounded-2xl focus:ring-4 focus:ring-primary/10 focus:border-primary/30 outline-none transition-all text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-slate-600 font-medium"
                                            placeholder="Repite la contraseña"
                                            required
                                        />
                                    </div>
                                </div>
                            </div>

                            <button
                                type="submit"
                                disabled={loading}
                                className="w-full bg-primary hover:bg-primary/90 text-white font-black py-4 rounded-2xl shadow-xl shadow-primary/20 hover:scale-[1.02] active:scale-[0.98] transition-all flex items-center justify-center gap-3 disabled:opacity-50"
                            >
                                {loading ? <Loader2 className="animate-spin" size={22} /> : <Save size={22} />}
                                {loading ? 'ACTUALIZANDO...' : 'ACTUALIZAR CONTRASEÑA'}
                            </button>
                        </form>
                    </div>
                </div>

                {/* Right Column - Info */}
                <div className="md:col-span-5 space-y-6">
                    <div className="bg-white dark:bg-slate-900/40 backdrop-blur-xl border border-slate-200 dark:border-white/5 rounded-[2.5rem] p-8 shadow-sm dark:shadow-none space-y-6">
                        <div className="w-16 h-16 bg-blue-500/10 rounded-3xl flex items-center justify-center text-blue-500 dark:text-blue-400">
                            <Fingerprint size={32} />
                        </div>
                        <div className="space-y-2">
                            <h3 className="text-lg font-black text-slate-900 dark:text-white">Recomendaciones</h3>
                            <p className="text-sm text-slate-500 dark:text-slate-400 leading-relaxed">
                                Para una cuenta segura, utiliza al menos 8 caracteres, incluyendo mayúsculas, números y símbolos especiales.
                            </p>
                        </div>
                        <div className="pt-4 border-t border-slate-100 dark:border-white/5">
                            <div className="flex items-center gap-3 text-xs font-black text-primary dark:text-primary uppercase tracking-widest">
                                <Sparkles size={14} />
                                GEMA Security Suite
                            </div>
                        </div>
                    </div>

                    <div className="bg-gradient-to-br from-primary/10 to-indigo-600/10 dark:from-primary/20 dark:to-indigo-600/20 p-8 rounded-[2.5rem] border border-primary/10 dark:border-primary/20 shadow-sm dark:shadow-none space-y-4">
                        <div className="flex items-center gap-3 text-primary">
                            <AlertCircle size={24} />
                            <h4 className="font-black text-sm uppercase tracking-widest">Aviso Importante</h4>
                        </div>
                        <p className="text-xs text-slate-600 dark:text-slate-300 font-medium leading-relaxed">
                            Si cambias tu contraseña, se cerrarán todas las sesiones activas en otros dispositivos para garantizar la seguridad de tu inventario.
                        </p>
                    </div>
                </div>
            </div>
        </motion.div>
    );
}
