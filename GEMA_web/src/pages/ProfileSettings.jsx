
import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { userService } from '../services/userService';
import { uploadService } from '../services/uploadService';
import { User, MapPin, Phone, Save, Loader2, ArrowLeft, Pencil, Camera, Mail, ShieldCheck } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import toast from 'react-hot-toast';

export default function ProfileSettings() {
    const { user, updateUser } = useAuth();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        nombre: user?.nombre || '',
        direccion: user?.direccion || '',
        telefono: user?.telefono || '',
        imagenUrl: user?.imagen_url || user?.imagenUrl || ''
    });
    const [loading, setLoading] = useState(false);
    const [uploading, setUploading] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleFileUpload = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        setUploading(true);
        try {
            const data = await uploadService.uploadImage(file, 'profiles');
            if (data && data.url) {
                setFormData(prev => ({ ...prev, imagenUrl: data.url }));
                toast.success('Imagen subida correctamente. No olvides guardar los cambios.');
            } else {
                toast.error('Error al subir la imagen.');
            }
        } catch (err) {
            console.error('Upload error:', err);
            toast.error('Error de conexión al subir la imagen.');
        } finally {
            setUploading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const updatedUser = await userService.updateProfile(user.id, formData);
            updateUser(updatedUser);
            toast.success('Perfil actualizado correctamente');
        } catch (error) {
            toast.error('Error al actualizar el perfil');
        } finally {
            setLoading(false);
        }
    };

    return (
        <motion.div 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="max-w-3xl mx-auto space-y-6 pb-20 sm:pb-0"
        >
            {/* Header / Navigation */}
            <div className="flex items-center gap-4 mb-2">
                <button 
                    onClick={() => navigate('/app/settings')}
                    className="p-3 bg-white dark:bg-white/5 hover:bg-slate-50 dark:hover:bg-white/10 rounded-2xl transition-all text-slate-900 dark:text-white border border-slate-200 dark:border-white/10 shadow-sm dark:shadow-none active:scale-95"
                >
                    <ArrowLeft size={24} />
                </button>
                <div>
                    <h1 className="text-3xl font-black text-slate-900 dark:text-white">
                        Mi Perfil
                    </h1>
                    <p className="text-slate-500 dark:text-slate-400 text-sm">Gestiona tu información personal y cuenta</p>
                </div>
            </div>

            <div className="bg-white dark:bg-slate-900/40 backdrop-blur-xl border border-slate-200 dark:border-white/5 rounded-[2.5rem] overflow-hidden shadow-sm dark:shadow-none">
                {/* Banner / Decor */}
                <div className="h-40 bg-gradient-to-br from-primary/30 via-indigo-600/20 to-purple-600/30 relative">
                    <div className="absolute inset-0 bg-[radial-gradient(circle_at_50%_120%,rgba(59,130,246,0.1),transparent)]" />
                </div>

                <div className="px-8 pb-10 relative">
                    {/* Profile Header Section */}
                    <div className="flex flex-col md:flex-row items-center md:items-end gap-6 -mt-16 mb-10">
                        <div className="relative group">
                            {/* Animated ring */}
                            <div className="absolute -inset-1 bg-gradient-to-tr from-primary via-indigo-500 to-purple-600 rounded-[2.5rem] animate-spin-slow opacity-50 group-hover:opacity-100 transition duration-700 blur-sm"></div>
                            
                            <div className="relative w-40 h-40 bg-slate-100 dark:bg-slate-900 rounded-[2.5rem] flex items-center justify-center text-slate-900 dark:text-white font-black shadow-2xl overflow-hidden border-[6px] border-white dark:border-slate-900 z-10 text-5xl">
                                {formData.imagenUrl ? (
                                    <img 
                                        src={formData.imagenUrl} 
                                        alt="Profile" 
                                        className="w-full h-full object-cover transition duration-700 group-hover:scale-110" 
                                    />
                                ) : (
                                    <div className="w-full h-full bg-gradient-to-br from-primary to-indigo-700 flex items-center justify-center">
                                        {formData.nombre?.charAt(0)?.toUpperCase() || "U"}
                                    </div>
                                )}

                                {/* Hover Overlay */}
                                <div className="absolute inset-0 bg-black/60 opacity-0 group-hover:opacity-100 transition-all duration-300 flex flex-col items-center justify-center gap-2">
                                    <Camera size={32} className="text-white" />
                                    <span className="text-[10px] font-black uppercase tracking-widest text-white">Cambiar Foto</span>
                                </div>
                                
                                <input 
                                    type="file" 
                                    accept="image/*"
                                    onChange={handleFileUpload}
                                    className="absolute inset-0 opacity-0 cursor-pointer z-20"
                                    disabled={uploading}
                                />
                            </div>

                            {uploading && (
                                <div className="absolute inset-0 z-30 flex items-center justify-center bg-black/60 rounded-[2.5rem] backdrop-blur-sm">
                                    <Loader2 size={40} className="text-white animate-spin" />
                                </div>
                            )}
                        </div>

                        <div className="flex-1 text-center md:text-left space-y-1">
                            <div className="flex items-center justify-center md:justify-start gap-2">
                                <h2 className="text-3xl font-black text-slate-900 dark:text-white tracking-tight">
                                    {formData.nombre || 'Sin Nombre'}
                                </h2>
                                <div className="bg-primary/20 p-1 rounded-lg border border-primary/20">
                                    <ShieldCheck size={18} className="text-primary" />
                                </div>
                            </div>
                            <p className="text-slate-500 dark:text-slate-400 flex items-center justify-center md:justify-start gap-2">
                                <Mail size={16} className="text-slate-400 dark:text-slate-600" />
                                {user?.correo || user?.user || 'cuenta@negocio.com'}
                            </p>
                        </div>
                    </div>

                    <form onSubmit={handleSubmit} className="space-y-8">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            {/* Full Name */}
                            <div className="space-y-2">
                                <label className="text-xs font-black text-slate-500 uppercase tracking-widest ml-1">Nombre Completo</label>
                                <div className="relative group">
                                    <User className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-primary transition-colors" size={18} />
                                    <input
                                        type="text"
                                        name="nombre"
                                        value={formData.nombre}
                                        onChange={handleChange}
                                        className="w-full pl-12 pr-5 py-4 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-100 dark:border-white/5 focus:border-primary focus:ring-4 focus:ring-primary/10 outline-none transition-all text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-slate-600 font-medium"
                                        placeholder="Tu nombre completo"
                                        required
                                    />
                                </div>
                            </div>

                            {/* Phone */}
                            <div className="space-y-2">
                                <label className="text-xs font-black text-slate-500 uppercase tracking-widest ml-1">Teléfono de Contacto</label>
                                <div className="relative group">
                                    <Phone className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-primary transition-colors" size={18} />
                                    <input
                                        type="tel"
                                        name="telefono"
                                        value={formData.telefono}
                                        onChange={handleChange}
                                        className="w-full pl-12 pr-5 py-4 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-100 dark:border-white/5 focus:border-primary focus:ring-4 focus:ring-primary/10 outline-none transition-all text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-slate-600 font-medium"
                                        placeholder="+52 ..."
                                    />
                                </div>
                            </div>

                            {/* Address */}
                            <div className="space-y-2 md:col-span-2">
                                <label className="text-xs font-black text-slate-500 uppercase tracking-widest ml-1">Dirección o Ubicación</label>
                                <div className="relative group">
                                    <MapPin className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-primary transition-colors" size={18} />
                                    <input
                                        type="text"
                                        name="direccion"
                                        value={formData.direccion}
                                        onChange={handleChange}
                                        className="w-full pl-12 pr-5 py-4 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-100 dark:border-white/5 focus:border-primary focus:ring-4 focus:ring-primary/10 outline-none transition-all text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-slate-600 font-medium"
                                        placeholder="Calle, Ciudad, Estado"
                                    />
                                </div>
                            </div>
                        </div>

                        <div className="pt-6 flex flex-col sm:flex-row gap-4">
                            <button
                                type="submit"
                                disabled={loading || uploading}
                                className="flex-1 bg-primary hover:bg-primary/90 text-white font-black py-5 rounded-[1.5rem] shadow-xl shadow-primary/20 transition-all hover:scale-[1.02] active:scale-[0.98] flex items-center justify-center gap-3 disabled:opacity-50"
                            >
                                {loading ? <Loader2 className="animate-spin" size={24} /> : <Save size={24} />}
                                {loading ? 'GUARDANDO CAMBIOS...' : 'GUARDAR PERFIL'}
                            </button>
                            
                            <button
                                type="button"
                                onClick={() => navigate('/app/settings/security')}
                                className="sm:w-1/3 bg-slate-50 dark:bg-white/5 hover:bg-slate-100 dark:hover:bg-white/10 text-slate-900 dark:text-white font-bold py-5 rounded-[1.5rem] border border-slate-200 dark:border-white/5 transition-all flex items-center justify-center gap-2"
                            >
                                <Pencil size={18} className="text-slate-400 dark:text-slate-500" />
                                Seguridad
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            {/* Account Info Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="bg-white dark:bg-slate-900/40 backdrop-blur-xl border border-slate-200 dark:border-white/5 rounded-[1.5rem] p-6 flex items-center gap-4 shadow-sm dark:shadow-none">
                    <div className="p-3 bg-primary/10 rounded-2xl text-primary border border-primary/20">
                        <ShieldCheck size={24} />
                    </div>
                    <div>
                        <p className="text-xs font-black text-slate-500 uppercase tracking-widest">Estado de Cuenta</p>
                        <p className="text-slate-900 dark:text-white font-bold">Verificada y Activa</p>
                    </div>
                </div>
                <div className="bg-white dark:bg-slate-900/40 backdrop-blur-xl border border-slate-200 dark:border-white/5 rounded-[1.5rem] p-6 flex items-center gap-4 shadow-sm dark:shadow-none">
                    <div className="p-3 bg-indigo-500/10 rounded-2xl text-indigo-500 border border-indigo-500/20">
                        <User size={24} />
                    </div>
                    <div>
                        <p className="text-xs font-black text-slate-500 uppercase tracking-widest">Tipo de Usuario</p>
                        <p className="text-slate-900 dark:text-white font-bold">{user?.idRol === 1 ? 'Propietario del Negocio' : 'Miembro del Equipo'}</p>
                    </div>
                </div>
            </div>
        </motion.div>
    );
}
