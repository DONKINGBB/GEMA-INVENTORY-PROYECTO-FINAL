import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Mail, ArrowLeft, ShieldCheck, Lock, Loader2, KeyRound, CheckCircle2 } from 'lucide-react';
import api from '../services/api';
import { useTheme } from '../context/ThemeContext';
import toast from 'react-hot-toast';

export default function ForgotPassword() {
    const navigate = useNavigate();
    const [step, setStep] = useState(1); // 1: Request, 2: Reset, 3: Success
    const [email, setEmail] = useState('');
    const [code, setCode] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const { isDarkMode } = useTheme();
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
        <div className="min-h-screen flex items-center justify-center bg-[#f8fafc] dark:bg-[#0f172a] p-4 transition-colors duration-500 overflow-hidden relative">
            {/* Animated Background Elements */}
            <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-primary/10 rounded-full blur-[120px] animate-pulse" />
            <div className="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] bg-blue-500/10 rounded-full blur-[120px] animate-pulse delay-700" />

            <div className="glass dark:bg-slate-800/40 p-8 md:p-10 rounded-[2.5rem] shadow-2xl w-full max-w-md border border-white/20 dark:border-slate-700/50 relative z-10 animate-in fade-in zoom-in-95 duration-500">
                
                <div className="flex justify-between items-center mb-8">
                    <Link to="/login" className="p-2.5 bg-gray-50 dark:bg-slate-900/50 hover:bg-white dark:hover:bg-slate-800 rounded-2xl transition-all shadow-sm border border-transparent hover:border-gray-200 dark:hover:border-slate-700 text-gray-500 hover:text-primary dark:text-gray-400">
                        <ArrowLeft size={20} />
                    </Link>
                    <div className="flex gap-1.5">
                        <div className={`w-8 h-1.5 rounded-full transition-all duration-300 ${step >= 1 ? 'bg-primary shadow-[0_0_10px_rgba(59,130,246,0.5)]' : 'bg-gray-200 dark:bg-slate-700'}`} />
                        <div className={`w-8 h-1.5 rounded-full transition-all duration-300 ${step >= 2 ? 'bg-primary shadow-[0_0_10px_rgba(59,130,246,0.5)]' : 'bg-gray-200 dark:bg-slate-700'}`} />
                        <div className={`w-8 h-1.5 rounded-full transition-all duration-300 ${step >= 3 ? 'bg-green-500 shadow-[0_0_10px_rgba(34,197,94,0.5)]' : 'bg-gray-200 dark:bg-slate-700'}`} />
                    </div>
                </div>

                <div className="text-center mb-10">
                    <div className="inline-flex p-4 rounded-[2rem] bg-blue-50 dark:bg-blue-900/20 text-primary dark:text-blue-400 mb-6 shadow-inner ring-1 ring-blue-100 dark:ring-blue-900/30">
                        {step === 1 && <KeyRound size={32} className="animate-bounce" />}
                        {step === 2 && <ShieldCheck size={32} className="animate-pulse" />}
                        {step === 3 && <CheckCircle2 size={32} className="text-green-500" />}
                    </div>
                    
                    {step === 1 && (
                        <div className="animate-in slide-up">
                            <h1 className="text-3xl font-black text-gray-900 dark:text-white tracking-tight">Recuperar Acceso</h1>
                            <p className="text-gray-500 dark:text-gray-400 mt-3 font-medium px-4">
                                Ingresa tu correo y te enviaremos un código de seguridad.
                            </p>
                        </div>
                    )}

                    {step === 2 && (
                        <div className="animate-in slide-up">
                            <h1 className="text-3xl font-black text-gray-900 dark:text-white tracking-tight">Verificación</h1>
                            <p className="text-gray-500 dark:text-gray-400 mt-3 font-medium">
                                Hemos enviado un código a <br/>
                                <span className="text-primary font-bold">{email}</span>
                            </p>
                        </div>
                    )}

                    {step === 3 && (
                        <div className="animate-in slide-up">
                            <h1 className="text-3xl font-black text-gray-900 dark:text-white tracking-tight">¡Todo listo!</h1>
                            <p className="text-gray-500 dark:text-gray-400 mt-3 font-medium">
                                Tu contraseña ha sido actualizada con éxito.
                            </p>
                        </div>
                    )}
                </div>

                {step === 1 && (
                    <form onSubmit={handleRequestCode} className="space-y-5 animate-in slide-up delay-100">
                        <div className="space-y-2">
                            <label className="text-sm font-bold text-gray-700 dark:text-gray-300 ml-1">Correo Electrónico</label>
                            <div className="relative group">
                                <div className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-primary transition-colors">
                                    <Mail size={20} />
                                </div>
                                <input
                                    type="email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    placeholder="ejemplo@correo.com"
                                    className="w-full pl-12 pr-5 py-4 bg-gray-50 dark:bg-slate-900/50 border border-gray-200 dark:border-slate-700 rounded-2xl outline-none focus:ring-2 focus:ring-primary focus:border-transparent dark:text-white transition-all font-medium"
                                    required
                                />
                            </div>
                        </div>

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full py-4 bg-primary hover:bg-primary-dark text-white font-black rounded-2xl shadow-[0_10px_20px_-5px_rgba(59,130,246,0.3)] transition-all hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-3 disabled:opacity-70 disabled:cursor-not-allowed group"
                        >
                            {loading ? <Loader2 size={22} className="animate-spin" /> : <span>ENVIAR CÓDIGO</span>}
                        </button>
                    </form>
                )}

                {step === 2 && (
                    <form onSubmit={handleResetPassword} className="space-y-5 animate-in slide-up delay-100">
                        <div className="space-y-4">
                            <div className="space-y-2">
                                <label className="text-sm font-bold text-gray-700 dark:text-gray-300 ml-1">Código de Seguridad</label>
                                <div className="relative group">
                                    <div className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-primary transition-colors">
                                        <KeyRound size={20} />
                                    </div>
                                    <input
                                        type="text"
                                        value={code}
                                        onChange={(e) => setCode(e.target.value)}
                                        placeholder="Ingresa el código"
                                        className="w-full pl-12 pr-5 py-4 bg-gray-50 dark:bg-slate-900/50 border border-gray-200 dark:border-slate-700 rounded-2xl outline-none focus:ring-2 focus:ring-primary focus:border-transparent dark:text-white transition-all font-medium tracking-[0.2em] text-center"
                                        required
                                    />
                                </div>
                            </div>

                            <div className="space-y-2">
                                <label className="text-sm font-bold text-gray-700 dark:text-gray-300 ml-1">Nueva Contraseña</label>
                                <div className="relative group">
                                    <div className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-primary transition-colors">
                                        <Lock size={20} />
                                    </div>
                                    <input
                                        type="password"
                                        value={newPassword}
                                        onChange={(e) => setNewPassword(e.target.value)}
                                        placeholder="••••••••"
                                        className="w-full pl-12 pr-5 py-4 bg-gray-50 dark:bg-slate-900/50 border border-gray-200 dark:border-slate-700 rounded-2xl outline-none focus:ring-2 focus:ring-primary focus:border-transparent dark:text-white transition-all font-medium"
                                        required
                                    />
                                </div>
                            </div>
                        </div>

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full py-4 bg-primary hover:bg-primary-dark text-white font-black rounded-2xl shadow-[0_10px_20px_-5px_rgba(59,130,246,0.3)] transition-all hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-3 disabled:opacity-70"
                        >
                            {loading ? <Loader2 size={22} className="animate-spin" /> : <span>ACTUALIZAR CONTRASEÑA</span>}
                        </button>

                        <button
                            type="button"
                            onClick={() => setStep(1)}
                            className="w-full py-2 text-sm font-bold text-gray-500 hover:text-primary transition-colors"
                        >
                            ¿No recibiste el código? Volver a intentar
                        </button>
                    </form>
                )}

                {step === 3 && (
                    <div className="space-y-6 text-center animate-in slide-up">
                        <div className="p-4 bg-green-50 dark:bg-green-900/20 text-green-600 dark:text-green-400 rounded-2xl font-bold">
                            Tu acceso ha sido restaurado con éxito.
                        </div>
                        <Link to="/login" className="block w-full py-4 bg-gray-900 dark:bg-slate-700 hover:bg-black text-white font-black rounded-2xl transition-all hover:scale-[1.02] active:scale-[0.98] text-center">
                            IR AL INICIO DE SESIÓN
                        </Link>
                    </div>
                )}
            </div>
        </div>
    );
}
