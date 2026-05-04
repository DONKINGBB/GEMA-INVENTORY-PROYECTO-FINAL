
import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { userService } from '../services/userService';
import { uploadService } from '../services/uploadService';
import { User, MapPin, Phone, Save, Loader, ArrowLeft, Pencil } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

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
    const [message, setMessage] = useState({ type: '', text: '' });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleFileUpload = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        setUploading(true);
        setMessage({ type: '', text: '' });
        try {
            const data = await uploadService.uploadImage(file, 'profiles');
            if (data && data.url) {
                setFormData(prev => ({ ...prev, imagenUrl: data.url }));
                setMessage({ type: 'success', text: 'Imagen subida correctamente. No olvides guardar los cambios.' });
            } else {
                setMessage({ type: 'error', text: 'Error al subir la imagen.' });
            }
        } catch (err) {
            console.error('Upload error:', err);
            setMessage({ type: 'error', text: 'Error de conexión al subir la imagen.' });
        } finally {
            setUploading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setMessage({ type: '', text: '' });
        try {
            const updatedUser = await userService.updateProfile(user.id, formData);
            // Update local user state
            updateUser(updatedUser);
            setMessage({ type: 'success', text: 'Perfil actualizado correctamente' });
        } catch (error) {
            setMessage({ type: 'error', text: 'Error al actualizar el perfil' });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-2xl mx-auto">
            <button 
                onClick={(e) => {
                    e.preventDefault();
                    if (document.startViewTransition) {
                        document.startViewTransition(() => {
                            navigate('/app/settings');
                        });
                    } else {
                        navigate('/app/settings');
                    }
                }}
                className="flex items-center gap-2 text-gray-500 hover:text-gray-700 mb-6 transition"
            >
                <ArrowLeft size={20} />
                <span>Volver a Configuración</span>
            </button>

            <div className="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-gray-100 dark:border-slate-700 overflow-hidden">
                {/* Banner / Cover */}
                <div className="h-32 bg-gradient-to-r from-primary to-blue-400 dark:from-slate-700 dark:to-slate-800 relative overflow-hidden">
                    <div className="absolute inset-0 bg-white/10 dark:bg-black/10"></div>
                </div>
                <div className="px-6 pb-6 relative">
                    {/* Big Profile Picture centered and elevated */}
                    <div className="flex flex-col items-center -mt-20 mb-8">
                        <div className="relative group">
                            {/* Premium Glow Effect */}
                            <div className="absolute -inset-2 bg-gradient-to-r from-primary via-blue-500 to-purple-600 rounded-full blur-xl opacity-40 group-hover:opacity-70 transition duration-500 animate-pulse"></div>
                            
                            {/* Animated ring */}
                            <div className="absolute -inset-1 bg-gradient-to-tr from-primary to-blue-400 rounded-full animate-spin-slow opacity-0 group-hover:opacity-100 transition duration-500"></div>

                            <div 
                                style={{ viewTransitionName: 'profile-photo' }}
                                className="relative w-40 h-40 bg-white dark:bg-slate-900 rounded-full flex items-center justify-center text-primary dark:text-white font-bold shadow-2xl overflow-hidden border-[6px] border-white dark:border-slate-800 z-10 text-5xl">
                                {formData.imagenUrl ? (
                                    <img 
                                        src={formData.imagenUrl} 
                                        alt="Profile" 
                                        className="w-full h-full object-cover transform transition duration-700 group-hover:scale-110" 
                                    />
                                ) : (
                                    <span className="transform transition duration-500 group-hover:scale-110">
                                        {formData.nombre?.charAt(0)?.toUpperCase() || "U"}
                                    </span>
                                )}

                                {/* Hover Overlay */}
                                <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-center justify-center">
                                    <Pencil className="text-white" size={32} />
                                </div>
                            </div>

                            {/* Floating Pencil/Edit Button */}
                            <label className="absolute bottom-2 right-2 z-20 flex items-center justify-center w-12 h-12 bg-primary hover:bg-primary-dark text-white rounded-full shadow-2xl cursor-pointer transition-all duration-300 hover:scale-110 border-4 border-white dark:border-slate-800 group/btn">
                                {uploading ? (
                                    <Loader size={20} className="animate-spin" />
                                ) : (
                                    <Pencil size={20} className="group-hover/btn:rotate-12 transition-transform" />
                                )}
                                <input 
                                    type="file" 
                                    accept="image/*"
                                    onChange={handleFileUpload}
                                    className="hidden"
                                    disabled={uploading}
                                />
                            </label>
                        </div>

                        <div className="text-center mt-6">
                            <h2 className="text-3xl font-black text-gray-900 dark:text-white tracking-tight">
                                {formData.nombre || 'Tu Perfil'}
                            </h2>
                            <p className="text-gray-500 dark:text-gray-400 font-medium flex items-center justify-center gap-2 mt-1">
                                <User size={16} className="text-primary" /> 
                                {user?.correo || 'Configuración de cuenta'}
                            </p>
                        </div>
                    </div>
                </div>

                <form onSubmit={handleSubmit} className="p-6 space-y-6">
                    {message.text && (
                        <div className={`p-4 rounded-xl text-sm font-medium ${
                            message.type === 'success' ? 'bg-green-50 text-green-700 border border-green-100' : 'bg-red-50 text-red-700 border border-red-100'
                        }`}>
                            {message.text}
                        </div>
                    )}

                    <div className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Nombre Completo</label>
                            <div className="relative">
                                <User className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
                                <input
                                    type="text"
                                    name="nombre"
                                    value={formData.nombre}
                                    onChange={handleChange}
                                    className="w-full pl-10 pr-4 py-2.5 bg-gray-50 dark:bg-slate-900 border border-gray-200 dark:border-slate-700 rounded-xl focus:ring-2 focus:ring-primary outline-none transition dark:text-white"
                                    placeholder="Tu nombre"
                                    required
                                />
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Dirección</label>
                            <div className="relative">
                                <MapPin className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
                                <input
                                    type="text"
                                    name="direccion"
                                    value={formData.direccion}
                                    onChange={handleChange}
                                    className="w-full pl-10 pr-4 py-2.5 bg-gray-50 dark:bg-slate-900 border border-gray-200 dark:border-slate-700 rounded-xl focus:ring-2 focus:ring-primary outline-none transition dark:text-white"
                                    placeholder="Calle, Ciudad, Estado"
                                />
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Teléfono</label>
                            <div className="relative">
                                <Phone className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
                                <input
                                    type="tel"
                                    name="telefono"
                                    value={formData.telefono}
                                    onChange={handleChange}
                                    className="w-full pl-10 pr-4 py-2.5 bg-gray-50 dark:bg-slate-900 border border-gray-200 dark:border-slate-700 rounded-xl focus:ring-2 focus:ring-primary outline-none transition dark:text-white"
                                    placeholder="+52 ..."
                                />
                            </div>
                        </div>

                        {/* Image URL input field removed */}
                    </div>

                    <div className="pt-4">
                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full bg-primary hover:bg-primary-dark text-white font-bold py-3 rounded-xl shadow-lg transition flex items-center justify-center gap-2 disabled:opacity-50"
                        >
                            {loading ? <Loader className="animate-spin" size={20} /> : <Save size={20} />}
                            {loading ? 'Guardando...' : 'Guardar Cambios'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
