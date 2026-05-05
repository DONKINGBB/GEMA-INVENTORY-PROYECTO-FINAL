import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { User, Mail, MapPin, Phone, Lock, ArrowRight, Sparkles, Eye, EyeOff } from 'lucide-react';
import api from '../services/api';
import { useTheme } from '../context/ThemeContext';
import toast from 'react-hot-toast';
import { motion } from 'framer-motion';
import logo from '../assets/ic_logo_cuadrado_bb.png';

export default function Register() {
    const [formData, setFormData] = useState({
        nombre: '',
        correo: '',
        contrasena: '',
        direccion: '',
        telefono: '',
        lada: '+52'
    });
    const [showPassword, setShowPassword] = useState(false);
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
            params.append('telefono', `${formData.lada}${formData.telefono}`);

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
                toast.success('¡Bienvenido a GEMA INVENTORY!');
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
        <div className="min-h-screen flex items-center justify-center p-4 relative overflow-hidden">
            {/* Background Mesh */}
            <div className="bg-mesh" />

            <motion.div 
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6, ease: "easeOut" }}
                className="w-full max-w-[600px] my-10"
            >
                <div className="glass-card rounded-[2.5rem] p-8 md:p-12 relative overflow-hidden group">
                    {/* Decorative Elements */}
                    <div className="absolute -top-24 -right-24 w-64 h-64 bg-blue-500/10 rounded-full blur-3xl group-hover:bg-blue-500/20 transition-all duration-700" />
                    <div className="absolute -bottom-24 -left-24 w-64 h-64 bg-indigo-500/10 rounded-full blur-3xl group-hover:bg-indigo-500/20 transition-all duration-700" />

                    <div className="relative z-10">
                        <div className="text-center mb-10">
                            <motion.div 
                                whileHover={{ scale: 1.05, rotate: -5 }}
                                className="w-20 h-20 mb-6 relative mx-auto"
                            >
                                <div className="absolute inset-0 bg-blue-500/20 blur-xl rounded-3xl" />
                                <img 
                                    src={logo} 
                                    alt="GEMA Inventory Logo" 
                                    className="w-full h-full object-contain relative z-10 brightness-200 contrast-125 drop-shadow-[0_0_15px_rgba(59,130,246,0.5)]" 
                                />
                            </motion.div>
                            <h1 className="text-4xl font-black text-slate-800 dark:text-white tracking-tight flex items-center justify-center gap-3 uppercase">
                                Únete a GEMA INVENTORY <Sparkles className="text-blue-500" size={32} />
                            </h1>
                            <p className="text-slate-500 dark:text-slate-400 mt-2 font-medium">Moderniza la gestión de tu inventario hoy</p>
                        </div>

                        <form onSubmit={handleSubmit} className="space-y-6">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div className="space-y-1.5 md:col-span-2">
                                    <label className="text-sm font-bold text-slate-700 dark:text-slate-300 ml-1">Nombre Completo</label>
                                    <div className="relative group">
                                        <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none transition-colors group-focus-within:text-blue-500">
                                            <User size={18} className="text-slate-400" />
                                        </div>
                                        <input
                                            type="text"
                                            name="nombre"
                                            required
                                            className="glass-input w-full pl-12 pr-4 py-4 rounded-2xl text-slate-900 dark:text-white placeholder:text-slate-400/70"
                                            placeholder="ej. Juan Pérez"
                                            value={formData.nombre}
                                            onChange={handleChange}
                                        />
                                    </div>
                                </div>
                                
                                <div className="space-y-1.5">
                                    <label className="text-sm font-bold text-slate-700 dark:text-slate-300 ml-1">Email Profesional</label>
                                    <div className="relative group">
                                        <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none transition-colors group-focus-within:text-blue-500">
                                            <Mail size={18} className="text-slate-400" />
                                        </div>
                                        <input
                                            type="email"
                                            name="correo"
                                            required
                                            className="glass-input w-full pl-12 pr-4 py-4 rounded-2xl text-slate-900 dark:text-white placeholder:text-slate-400/70"
                                            placeholder="tu@negocio.com"
                                            value={formData.correo}
                                            onChange={handleChange}
                                        />
                                    </div>
                                </div>

                                <div className="space-y-1.5">
                                    <label className="text-sm font-bold text-slate-700 dark:text-slate-300 ml-1">Contraseña</label>
                                    <div className="relative group">
                                        <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none transition-colors group-focus-within:text-blue-500">
                                            <Lock size={18} className="text-slate-400" />
                                        </div>
                                        <input
                                            type={showPassword ? "text" : "password"}
                                            name="contrasena"
                                            required
                                            minLength="6"
                                            className="glass-input w-full pl-12 pr-12 py-4 rounded-2xl text-slate-900 dark:text-white placeholder:text-slate-400/70"
                                            placeholder="••••••••"
                                            value={formData.contrasena}
                                            onChange={handleChange}
                                        />
                                        <button 
                                            type="button"
                                            onClick={() => setShowPassword(!showPassword)}
                                            className="absolute inset-y-0 right-0 pr-4 flex items-center text-slate-400 hover:text-blue-500 transition-colors"
                                        >
                                            {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                                        </button>
                                    </div>
                                </div>

                                <div className="space-y-1.5">
                                    <label className="text-sm font-bold text-slate-700 dark:text-slate-300 ml-1">Teléfono</label>
                                    <div className="flex gap-2">
                                        <select 
                                            name="lada"
                                            value={formData.lada}
                                            onChange={handleChange}
                                            className="glass-input w-24 px-2 py-4 rounded-2xl text-slate-900 dark:text-white font-bold"
                                        >
                                            <option value="+52">🇲🇽 +52</option>
                                            <option value="+1">🇺🇸 +1</option>
                                            <option value="+57">🇨🇴 +57</option>
                                            <option value="+34">🇪🇸 +34</option>
                                            <option value="+54">🇦🇷 +54</option>
                                        </select>
                                        <div className="relative group flex-1">
                                            <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none transition-colors group-focus-within:text-blue-500">
                                                <Phone size={18} className="text-slate-400" />
                                            </div>
                                            <input
                                                type="tel"
                                                name="telefono"
                                                required
                                                className="glass-input w-full pl-12 pr-4 py-4 rounded-2xl text-slate-900 dark:text-white placeholder:text-slate-400/70"
                                                placeholder="1234567890"
                                                value={formData.telefono}
                                                onChange={handleChange}
                                            />
                                        </div>
                                    </div>
                                </div>

                                <div className="space-y-1.5">
                                    <label className="text-sm font-bold text-slate-700 dark:text-slate-300 ml-1">Dirección</label>
                                    <div className="relative group">
                                        <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none transition-colors group-focus-within:text-blue-500">
                                            <MapPin size={18} className="text-slate-400" />
                                        </div>
                                        <input
                                            type="text"
                                            name="direccion"
                                            required
                                            className="glass-input w-full pl-12 pr-4 py-4 rounded-2xl text-slate-900 dark:text-white placeholder:text-slate-400/70"
                                            placeholder="Calle, Ciudad, País"
                                            value={formData.direccion}
                                            onChange={handleChange}
                                        />
                                    </div>
                                </div>
                            </div>

                            <motion.button
                                whileHover={{ scale: 1.01 }}
                                whileTap={{ scale: 0.99 }}
                                type="submit"
                                disabled={loading}
                                className="w-full py-4 bg-blue-600 dark:bg-blue-500 text-white font-bold rounded-2xl shadow-lg shadow-blue-500/25 transition-all disabled:opacity-70 flex items-center justify-center gap-3 mt-4"
                            >
                                {loading ? (
                                    <div className="w-6 h-6 border-3 border-white/30 border-t-white rounded-full animate-spin" />
                                ) : (
                                    <>Comenzar con GEMA INVENTORY <ArrowRight size={20} /></>
                                )}
                            </motion.button>
                        </form>

                        <div className="mt-10 text-center">
                            <p className="text-slate-500 dark:text-slate-400 text-sm font-medium">
                                ¿Ya tienes una cuenta?{' '}
                                <Link to="/login" className="text-blue-600 dark:text-blue-400 font-bold hover:underline">
                                    Inicia Sesión
                                </Link>
                            </p>
                        </div>
                    </div>
                </div>
                
                <p className="text-center mt-8 text-slate-400 dark:text-slate-500 text-xs font-medium uppercase tracking-widest">
                    JEDD AI © 2026
                </p>
            </motion.div>
        </div>
    );
}

