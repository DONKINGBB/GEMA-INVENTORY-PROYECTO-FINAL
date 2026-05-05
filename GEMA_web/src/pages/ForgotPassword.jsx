import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Mail, ArrowLeft, ShieldCheck, Lock, Loader2, KeyRound, CheckCircle2 } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import api from '../services/api';
import toast from 'react-hot-toast';

export default function ForgotPassword() {
    const navigate = useNavigate();
    const [step, setStep] = useState(1); // 1: Request, 2: Reset, 3: Success
    const [email, setEmail] = useState('');
    const [code, setCode] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [loading, setLoading] = useState(false);

    const handleRequestCode = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const params = new URLSearchParams();
            params.append('email', email);

            await api.post('/auth/forgot-password', params, {
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            });

            toast.success('Código enviado con éxito');
            setStep(2);
        } catch (err) {
            console.error(err);
            toast.error(err.response?.data?.message || 'Error al solicitar recuperación. Verifica tu correo.');
        } finally {
            setLoading(false);
        }
    };

    const handleResetPassword = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const params = new URLSearchParams();
            params.append('email', email);
            params.append('code', code);
            params.append('newPassword', newPassword);

            await api.post('/auth/reset-password', params, {
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            });

            toast.success('Contraseña restablecida con éxito');
            setStep(3);
            setTimeout(() => navigate('/login'), 3000);
        } catch (err) {
            console.error(err);
            toast.error(err.response?.data?.message || 'Código incorrecto o error en el proceso.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center p-4 relative overflow-hidden">
            {/* Background elements are handled by index.css body styles, 
                but we can add page-specific glows if needed */}
            
            <motion.div 
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                className="glass-card p-8 md:p-10 w-full max-w-md relative z-10"
            >
                <div className="flex justify-between items-center mb-8">
                    <Link 
                        to="/login" 
                        className="p-2 bg-white/10 hover:bg-white/20 rounded-xl transition-all text-white/70 hover:text-white"
                    >
                        <ArrowLeft size={20} />
                    </Link>
                    <div className="flex gap-2">
                        {[1, 2, 3].map((s) => (
                            <div 
                                key={s}
                                className={`h-1.5 rounded-full transition-all duration-500 ${
                                    step >= s 
                                        ? 'w-8 bg-blue-500 shadow-[0_0_10px_rgba(59,130,246,0.5)]' 
                                        : 'w-4 bg-white/10'
                                }`}
                            />
                        ))}
                    </div>
                </div>

                <AnimatePresence mode="wait">
                    {step === 1 && (
                        <motion.div
                            key="step1"
                            initial={{ opacity: 0, x: 20 }}
                            animate={{ opacity: 1, x: 0 }}
                            exit={{ opacity: 0, x: -20 }}
                            className="space-y-6"
                        >
                            <div className="text-center">
                                <div className="inline-flex p-4 rounded-3xl bg-blue-500/20 text-blue-400 mb-6">
                                    <KeyRound size={32} />
                                </div>
                                <h1 className="text-3xl font-bold text-white tracking-tight">Recuperar Acceso</h1>
                                <p className="text-white/60 mt-3">
                                    Ingresa tu correo y te enviaremos un código de seguridad.
                                </p>
                            </div>

                            <form onSubmit={handleRequestCode} className="space-y-5">
                                <div className="space-y-2">
                                    <label className="text-sm font-medium text-white/70 ml-1">Correo Electrónico</label>
                                    <div className="relative group">
                                        <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-white/40 group-focus-within:text-blue-400 transition-colors" size={20} />
                                        <input
                                            type="email"
                                            value={email}
                                            onChange={(e) => setEmail(e.target.value)}
                                            placeholder="tu@correo.com"
                                            className="glass-input w-full pl-12 pr-4 py-4"
                                            required
                                        />
                                    </div>
                                </div>

                                <button
                                    type="submit"
                                    disabled={loading}
                                    className="w-full py-4 bg-blue-600 hover:bg-blue-500 text-white font-bold rounded-2xl shadow-lg shadow-blue-900/20 transition-all hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-3 disabled:opacity-50"
                                >
                                    {loading ? <Loader2 size={22} className="animate-spin" /> : 'ENVIAR CÓDIGO'}
                                </button>
                            </form>
                        </motion.div>
                    )}

                    {step === 2 && (
                        <motion.div
                            key="step2"
                            initial={{ opacity: 0, x: 20 }}
                            animate={{ opacity: 1, x: 0 }}
                            exit={{ opacity: 0, x: -20 }}
                            className="space-y-6"
                        >
                            <div className="text-center">
                                <div className="inline-flex p-4 rounded-3xl bg-blue-500/20 text-blue-400 mb-6">
                                    <ShieldCheck size={32} />
                                </div>
                                <h1 className="text-3xl font-bold text-white tracking-tight">Verificación</h1>
                                <p className="text-white/60 mt-3">
                                    Hemos enviado un código a <br/>
                                    <span className="text-blue-400 font-semibold">{email}</span>
                                </p>
                            </div>

                            <form onSubmit={handleResetPassword} className="space-y-5">
                                <div className="space-y-4">
                                    <div className="space-y-2">
                                        <label className="text-sm font-medium text-white/70 ml-1">Código de Seguridad</label>
                                        <div className="relative group">
                                            <KeyRound className="absolute left-4 top-1/2 -translate-y-1/2 text-white/40 group-focus-within:text-blue-400 transition-colors" size={20} />
                                            <input
                                                type="text"
                                                value={code}
                                                onChange={(e) => setCode(e.target.value)}
                                                placeholder="000000"
                                                className="glass-input w-full pl-12 pr-4 py-4 text-center tracking-[0.5em] font-mono"
                                                required
                                            />
                                        </div>
                                    </div>

                                    <div className="space-y-2">
                                        <label className="text-sm font-medium text-white/70 ml-1">Nueva Contraseña</label>
                                        <div className="relative group">
                                            <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-white/40 group-focus-within:text-blue-400 transition-colors" size={20} />
                                            <input
                                                type="password"
                                                value={newPassword}
                                                onChange={(e) => setNewPassword(e.target.value)}
                                                placeholder="••••••••"
                                                className="glass-input w-full pl-12 pr-4 py-4"
                                                required
                                            />
                                        </div>
                                    </div>
                                </div>

                                <button
                                    type="submit"
                                    disabled={loading}
                                    className="w-full py-4 bg-blue-600 hover:bg-blue-500 text-white font-bold rounded-2xl shadow-lg shadow-blue-900/20 transition-all hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-3 disabled:opacity-50"
                                >
                                    {loading ? <Loader2 size={22} className="animate-spin" /> : 'ACTUALIZAR CONTRASEÑA'}
                                </button>

                                <button
                                    type="button"
                                    onClick={() => setStep(1)}
                                    className="w-full text-sm text-white/40 hover:text-white transition-colors"
                                >
                                    ¿No recibiste el código? Reintentar
                                </button>
                            </form>
                        </motion.div>
                    )}

                    {step === 3 && (
                        <motion.div
                            key="step3"
                            initial={{ opacity: 0, scale: 0.9 }}
                            animate={{ opacity: 1, scale: 1 }}
                            className="text-center space-y-6"
                        >
                            <div className="inline-flex p-6 rounded-full bg-green-500/20 text-green-400 mb-2">
                                <CheckCircle2 size={64} />
                            </div>
                            <div>
                                <h1 className="text-3xl font-bold text-white tracking-tight">¡Todo listo!</h1>
                                <p className="text-white/60 mt-3">
                                    Tu contraseña ha sido actualizada con éxito.
                                    Serás redirigido en unos segundos.
                                </p>
                            </div>
                            <Link 
                                to="/login" 
                                className="block w-full py-4 bg-white/10 hover:bg-white/20 text-white font-bold rounded-2xl transition-all"
                            >
                                IR AL LOGIN
                            </Link>
                        </motion.div>
                    )}
                </AnimatePresence>
            </motion.div>
        </div>
    );
}
