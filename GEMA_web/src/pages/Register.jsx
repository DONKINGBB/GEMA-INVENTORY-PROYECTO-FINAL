import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { User, Mail, MapPin, Phone, Lock, Loader2, ArrowRight, Sparkles } from 'lucide-react';
import api from '../services/api';
import { useTheme } from '../context/ThemeContext';
import toast from 'react-hot-toast';

export default function Register() {
    const [formData, setFormData] = useState({
        nombre: '',
        correo: '',
        contrasena: '',
        direccion: '',
        telefono: ''
    });
    const { login } = useAuth();
    const { isDarkMode } = useTheme();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const params = new URLSearchParams();
            params.append('nombre', formData.nombre);
            params.append('correo', formData.correo);
            params.append('contrasena', formData.contrasena);
            params.append('direccion', formData.direccion);
            params.append('telefono', formData.telefono);

            const response = await api.post('/auth/register', params, {
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            });

            const data = response.data;
            if (data.success) {
                const userDataToStore = {
                    ...data.usuario,
                    token: data.token
                };
                login(userDataToStore);
                toast.success('¡Bienvenido a GEMA!');
                navigate('/app');
            } else {
                toast.error(data.message || 'Error al registrarse');
            }
        } catch (err) {
            console.error(err);
            toast.error(err.response?.data?.message || 'Error al registrarse. Intente nuevamente.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-[#f8fafc] dark:bg-[#0f172a] p-4 transition-colors duration-500 overflow-hidden relative">
            {/* Background elements */}
            <div className="absolute top-[-10%] right-[-10%] w-[50%] h-[50%] bg-blue-500/10 rounded-full blur-[120px] animate-pulse" />
            <div className="absolute bottom-[-10%] left-[-10%] w-[50%] h-[50%] bg-primary/10 rounded-full blur-[120px] animate-pulse delay-700" />

            <div className="glass dark:bg-slate-800/40 p-8 md:p-10 rounded-[2.5rem] shadow-2xl w-full max-w-xl border border-white/20 dark:border-slate-700/50 relative z-10 animate-in fade-in zoom-in-95 duration-500 my-8">
                <div className="text-center mb-8">
                    <div className="flex justify-center mb-6">
                        <div className="p-4 bg-white/50 dark:bg-slate-900/50 rounded-3xl shadow-lg backdrop-blur-md border border-white/20">
                            <img 
                                src={isDarkMode ? "/gema_white.svg" : "/src/assets/ic_logo_cuadrado_bb.png"} 
                                alt="GEMA Logo" 
                                className="w-12 h-12 object-contain" 
                            />
                        </div>
                    </div>
                    <h1 className="text-3xl font-black text-gray-900 dark:text-white tracking-tight flex items-center justify-center gap-2">
                        Únete a GEMA <Sparkles className="text-yellow-400" size={24} />
                    </h1>
                    <p className="text-gray-500 dark:text-gray-400 mt-2 font-medium">Control inteligente para tu negocio</p>
                </div>

                <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-5 animate-in slide-up">
                    <div className="space-y-2 md:col-span-2">
                        <label className="text-xs font-black text-gray-700 dark:text-gray-300 ml-1 uppercase tracking-wider">Nombre Completo</label>
                        <div className="relative group">
                            <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none transition-colors group-focus-within:text-primary">
                                <User size={18} className="text-gray-400" />
                            </div>
                            <input
                                type="text"
                                name="nombre"
                                required
                                className="w-full pl-12 pr-4 py-3.5 rounded-2xl bg-gray-50/50 dark:bg-slate-900/50 border border-gray-100 dark:border-slate-700 focus:ring-2 focus:ring-primary outline-none transition-all dark:text-white font-medium"
                                placeholder="Tu nombre"
                                value={formData.nombre}
                                onChange={handleChange}
                            />
                        </div>
                    </div>
                    
                    <div className="space-y-2">
                        <label className="text-xs font-black text-gray-700 dark:text-gray-300 ml-1 uppercase tracking-wider">Correo Electrónico</label>
                        <div className="relative group">
                            <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none transition-colors group-focus-within:text-primary">
                                <Mail size={18} className="text-gray-400" />
                            </div>
                            <input
                                type="email"
                                name="correo"
                                required
                                className="w-full pl-12 pr-4 py-3.5 rounded-2xl bg-gray-50/50 dark:bg-slate-900/50 border border-gray-100 dark:border-slate-700 focus:ring-2 focus:ring-primary outline-none transition-all dark:text-white font-medium"
                                placeholder="tu@correo.com"
                                value={formData.correo}
                                onChange={handleChange}
                            />
                        </div>
                    </div>

                    <div className="space-y-2">
                        <label className="text-xs font-black text-gray-700 dark:text-gray-300 ml-1 uppercase tracking-wider">Contraseña</label>
                        <div className="relative group">
                            <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none transition-colors group-focus-within:text-primary">
                                <Lock size={18} className="text-gray-400" />
                            </div>
                            <input
                                type="password"
                                name="contrasena"
                                required
                                minLength="6"
                                className="w-full pl-12 pr-4 py-3.5 rounded-2xl bg-gray-50/50 dark:bg-slate-900/50 border border-gray-100 dark:border-slate-700 focus:ring-2 focus:ring-primary outline-none transition-all dark:text-white font-medium"
                                placeholder="••••••••"
                                value={formData.contrasena}
                                onChange={handleChange}
                            />
                        </div>
                    </div>

                    <div className="space-y-2">
                        <label className="text-xs font-black text-gray-700 dark:text-gray-300 ml-1 uppercase tracking-wider">Teléfono</label>
                        <div className="relative group">
                            <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none transition-colors group-focus-within:text-primary">
                                <Phone size={18} className="text-gray-400" />
                            </div>
                            <input
                                type="tel"
                                name="telefono"
                                required
                                className="w-full pl-12 pr-4 py-3.5 rounded-2xl bg-gray-50/50 dark:bg-slate-900/50 border border-gray-100 dark:border-slate-700 focus:ring-2 focus:ring-primary outline-none transition-all dark:text-white font-medium"
                                placeholder="Tu teléfono"
                                value={formData.telefono}
                                onChange={handleChange}
                            />
                        </div>
                    </div>

                    <div className="space-y-2">
                        <label className="text-xs font-black text-gray-700 dark:text-gray-300 ml-1 uppercase tracking-wider">Dirección</label>
                        <div className="relative group">
                            <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none transition-colors group-focus-within:text-primary">
                                <MapPin size={18} className="text-gray-400" />
                            </div>
                            <input
                                type="text"
                                name="direccion"
                                required
                                className="w-full pl-12 pr-4 py-3.5 rounded-2xl bg-gray-50/50 dark:bg-slate-900/50 border border-gray-100 dark:border-slate-700 focus:ring-2 focus:ring-primary outline-none transition-all dark:text-white font-medium"
                                placeholder="Tu dirección"
                                value={formData.direccion}
                                onChange={handleChange}
                            />
                        </div>
                    </div>

                    <div className="md:col-span-2 pt-4">
                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full py-4 bg-primary hover:bg-blue-600 text-white font-black rounded-2xl shadow-xl shadow-primary/30 transition-all hover:scale-[1.01] active:scale-[0.99] flex items-center justify-center gap-3 disabled:opacity-50"
                        >
                            {loading ? (
                                <Loader2 className="animate-spin" size={24} />
                            ) : (
                                <>COMENZAR AHORA <ArrowRight size={20} /></>
                            )}
                        </button>
                    </div>
                </form>

                <div className="mt-8 text-center">
                    <p className="text-sm text-gray-500 dark:text-gray-400 font-medium">
                        ¿Ya tienes una cuenta?{' '}
                        <Link to="/login" className="text-primary dark:text-blue-400 font-black hover:underline underline-offset-4 decoration-2">
                            Inicia Sesión
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
}
