import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LogIn, User, Eye, EyeOff, ArrowRight } from 'lucide-react';
import api from '../services/api';
import { useTheme } from '../context/ThemeContext';
import { motion, AnimatePresence } from 'framer-motion';
import logo from '../assets/ic_logo_cuadrado_bb.png';

export default function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const { login } = useAuth();
    const { isDarkMode } = useTheme();
    const navigate = useNavigate();
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [isGoogleLoading, setIsGoogleLoading] = useState(false);

    const handleGoogleLogin = async (response) => {
        setIsGoogleLoading(true);
        setError('');
        try {
            // Decode the Google JWT to get user info
            // The credential is the second part of the JWT (base64 encoded)
            const base64Url = response.credential.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));

            const googleUser = JSON.parse(jsonPayload);

            // Send to our specific backend endpoint
            const res = await api.post('/auth/google', {
                correo: googleUser.email,
                nombre: googleUser.name
            });
            
            if (res.data.success) {
                login({
                    ...res.data.usuario,
                    token: res.data.token
                });
                navigate('/app');
            } else {
                setError(res.data.message || 'Error al iniciar sesión con Google');
            }
        } catch (err) {
            console.error('Google Login Error:', err);
            setError('Error de conexión con el servidor de autenticación social.');
        } finally {
            setIsGoogleLoading(false);
        }
    };

    useEffect(() => {
        /* global google */
        if (typeof google !== 'undefined') {
            google.accounts.id.initialize({
                client_id: "744309639828-74vmh420b16077tstm6rijrbc540ddh9.apps.googleusercontent.com",
                callback: handleGoogleLogin
            });
        }
    }, []);

    const triggerGoogleLogin = () => {
        if (typeof google !== 'undefined') {
            google.accounts.id.prompt();
        } else {
            setError('El servicio de Google no se cargó correctamente. Reintente.');
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setIsLoading(true);

        try {
            const params = new URLSearchParams();
            params.append('correo', email);
            params.append('contrasena', password);

            const response = await api.post('/auth/login', params, {
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            });

            const data = response.data;
            if (data.success) {
                const userDataToStore = {
                    ...data.usuario,
                    token: data.token
                };
                login(userDataToStore);
                navigate('/app');
            } else {
                setError(data.message || 'Credenciales inválidas');
            }
        } catch (err) {
            console.error(err);
            setError('Error al iniciar sesión. Verifica tu conexión.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center p-4 relative overflow-hidden">
            {/* Background Mesh */}
            <div className="bg-mesh" />

            <motion.div 
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6, ease: "easeOut" }}
                className="w-full max-w-[440px]"
            >
                <div className="glass-card rounded-[2.5rem] p-8 md:p-12 relative overflow-hidden group">
                    {/* Decorative Elements */}
                    <div className="absolute -top-24 -right-24 w-48 h-48 bg-blue-500/10 rounded-full blur-3xl group-hover:bg-blue-500/20 transition-all duration-700" />
                    <div className="absolute -bottom-24 -left-24 w-48 h-48 bg-indigo-500/10 rounded-full blur-3xl group-hover:bg-indigo-500/20 transition-all duration-700" />

                    <div className="relative z-10">
                        <div className="flex flex-col items-center mb-10">
                            <motion.div 
                                whileHover={{ scale: 1.05, rotate: 5 }}
                                className="w-24 h-24 mb-6 relative"
                            >
                                <div className="absolute inset-0 bg-blue-500/20 blur-xl rounded-3xl" />
                                <img 
                                    src={logo} 
                                    alt="GEMA Inventory Logo" 
                                    className="w-full h-full object-contain relative z-10 brightness-[10] grayscale drop-shadow-[0_0_15px_rgba(255,255,255,0.3)]" 
                                />
                            </motion.div>
                            <h1 className="text-4xl font-black tracking-tight text-slate-800 dark:text-white mb-2 uppercase">
                                GEMA <span className="text-blue-600 dark:text-blue-400">INVENTORY</span>
                            </h1>
                            <p className="text-slate-500 dark:text-slate-400 font-medium text-center">
                                Gestión inteligente de inventarios
                            </p>
                        </div>

                        <AnimatePresence mode="wait">
                            {error && (
                                <motion.div 
                                    initial={{ opacity: 0, height: 0 }}
                                    animate={{ opacity: 1, height: 'auto' }}
                                    exit={{ opacity: 0, height: 0 }}
                                    className="bg-red-500/10 border border-red-500/20 text-red-600 dark:text-red-400 px-4 py-3 rounded-2xl text-sm mb-6 flex items-center gap-3"
                                >
                                    <div className="w-2 h-2 rounded-full bg-red-500 animate-pulse" />
                                    {error}
                                </motion.div>
                            )}
                        </AnimatePresence>

                        <form onSubmit={handleSubmit} className="space-y-5">
                            <div className="space-y-1.5">
                                <label className="text-sm font-bold text-slate-700 dark:text-slate-300 ml-1">Usuario / Email</label>
                                <div className="relative group">
                                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                        <User className="h-5 w-5 text-slate-400 group-focus-within:text-blue-500 transition-colors" />
                                    </div>
                                    <input
                                        type="text"
                                        required
                                        className="glass-input w-full pl-12 pr-4 py-4 rounded-2xl text-slate-900 dark:text-white placeholder:text-slate-400/70"
                                        placeholder="ej. admin"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                    />
                                </div>
                            </div>

                            <div className="space-y-1.5">
                                <div className="flex justify-between items-center px-1">
                                    <label className="text-sm font-bold text-slate-700 dark:text-slate-300">Contraseña</label>
                                    <Link to="/forgot-password" size="sm" className="text-xs font-bold text-blue-600 dark:text-blue-400 hover:underline">
                                        ¿Olvidaste la clave?
                                    </Link>
                                </div>
                                <div className="relative group">
                                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                        <LogIn className="h-5 w-5 text-slate-400 group-focus-within:text-blue-500 transition-colors" />
                                    </div>
                                    <input
                                        type={showPassword ? "text" : "password"}
                                        required
                                        className="glass-input w-full pl-12 pr-12 py-4 rounded-2xl text-slate-900 dark:text-white placeholder:text-slate-400/70"
                                        placeholder="••••••••"
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                    />
                                    <button 
                                        type="button"
                                        onClick={() => setShowPassword(!showPassword)}
                                        className="absolute inset-y-0 right-0 pr-4 flex items-center text-slate-400 hover:text-blue-500 transition-colors"
                                    >
                                        {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                                    </button>
                                </div>
                            </div>

                            <motion.button
                                whileHover={{ scale: 1.02 }}
                                whileTap={{ scale: 0.98 }}
                                type="submit"
                                disabled={isLoading}
                                className="w-full relative group overflow-hidden bg-blue-600 dark:bg-blue-500 text-white font-bold py-4 rounded-2xl shadow-lg shadow-blue-500/25 transition-all disabled:opacity-70 flex items-center justify-center gap-2"
                            >
                                {isLoading ? (
                                    <div className="w-6 h-6 border-3 border-white/30 border-t-white rounded-full animate-spin" />
                                ) : (
                                    <>
                                        Entrar al sistema
                                        <ArrowRight size={20} className="group-hover:translate-x-1 transition-transform" />
                                    </>
                                )}
                            </motion.button>
                        </form>

                        <div className="relative my-8">
                            <div className="absolute inset-0 flex items-center">
                                <div className="w-full border-t border-slate-200 dark:border-white/10"></div>
                            </div>
                            <div className="relative flex justify-center text-xs uppercase">
                                <span className="bg-white dark:bg-[#0a0f1d] px-4 text-slate-500 dark:text-slate-400 font-bold tracking-widest">O</span>
                            </div>
                        </div>

                        <motion.button
                            whileHover={{ scale: 1.02, backgroundColor: "rgba(255, 255, 255, 0.05)" }}
                            whileTap={{ scale: 0.98 }}
                            type="button"
                            onClick={triggerGoogleLogin}
                            disabled={isGoogleLoading}
                            className="w-full flex items-center justify-center gap-3 py-4 rounded-2xl border border-slate-200 dark:border-white/10 text-slate-700 dark:text-white font-bold transition-all hover:shadow-lg disabled:opacity-50"
                        >
                            {isGoogleLoading ? (
                                <div className="w-5 h-5 border-2 border-slate-400 border-t-primary rounded-full animate-spin" />
                            ) : (
                                <>
                                    <svg className="w-5 h-5" viewBox="0 0 24 24">
                                        <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                                        <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                                        <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                                        <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                                    </svg>
                                    Continuar con Google
                                </>
                            )}
                        </motion.button>

                        <div className="mt-10 text-center">
                            <p className="text-slate-500 dark:text-slate-400 text-sm font-medium">
                                ¿No tienes acceso aún? {' '}
                                <Link to="/register" className="text-blue-600 dark:text-blue-400 font-bold hover:underline">
                                    Crea una cuenta
                                </Link>
                            </p>
                        </div>
                    </div>
                </div>
                
                {/* Footer Credits */}
                <p className="text-center mt-8 text-slate-400 dark:text-slate-500 text-xs font-medium uppercase tracking-widest">
                    JEDD AI © 2026
                </p>
            </motion.div>
        </div>
    );
}

