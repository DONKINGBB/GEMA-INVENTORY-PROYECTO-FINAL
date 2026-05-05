
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Building2, Copy, Edit2, QrCode, ArrowLeft, Check, AlertCircle, Share2, Sparkles, Loader2 } from 'lucide-react';
import { businessService } from '../services/businessService';
import { useAuth } from '../context/AuthContext';
import { motion, AnimatePresence } from 'framer-motion';
import toast from 'react-hot-toast';

export default function BusinessSettings() {
    const navigate = useNavigate();
    const { user } = useAuth();
    const [business, setBusiness] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isEditing, setIsEditing] = useState(false);
    const [newName, setNewName] = useState('');
    const [saving, setSaving] = useState(false);

    useEffect(() => {
        loadBusiness();
    }, []);

    const loadBusiness = async () => {
        try {
            const data = await businessService.getMiNegocio();
            setBusiness(data);
            setNewName(data.nombre);
        } catch (error) {
            console.error('Error loading business:', error);
            toast.error('Error al cargar la información del negocio');
        } finally {
            setLoading(false);
        }
    };

    const handleCopyCode = () => {
        if (business?.codigoInvitacion) {
            navigator.clipboard.writeText(business.codigoInvitacion);
            toast.success('Código copiado al portapapeles');
        }
    };

    const handleUpdateName = async () => {
        if (!newName.trim()) return;
        setSaving(true);
        try {
            const updated = await businessService.updateNegocio(business.idNegocio, { nombre: newName });
            setBusiness(updated);
            setIsEditing(false);
            toast.success('Nombre actualizado');
        } catch (error) {
            toast.error('Error al actualizar el nombre');
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return (
            <div className="flex flex-col items-center justify-center min-h-[60vh] space-y-4">
                <div className="w-12 h-12 border-4 border-primary/20 border-t-primary rounded-full animate-spin"></div>
                <p className="text-slate-500 font-bold tracking-widest text-xs uppercase animate-pulse">Cargando negocio...</p>
            </div>
        );
    }

    const qrUrl = business ? `https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=${business.codigoInvitacion}` : '';
    const isAdmin = user?.idRol === 1;

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
                        Mi Negocio
                    </h1>
                    <p className="text-slate-500 dark:text-slate-400 text-sm">Información de identidad y códigos de acceso</p>
                </div>
            </div>

            <div className="bg-white dark:bg-slate-900/40 backdrop-blur-xl border border-slate-200 dark:border-white/5 rounded-[2.5rem] overflow-hidden shadow-sm dark:shadow-none">
                {/* Visual Banner */}
                <div className="h-40 bg-gradient-to-br from-indigo-600/30 via-primary/20 to-blue-600/30 relative">
                    <div className="absolute inset-0 bg-[radial-gradient(circle_at_50%_0%,rgba(59,130,246,0.1),transparent)]" />
                    <div className="absolute -bottom-10 left-10 p-5 bg-white dark:bg-slate-900 rounded-[2.5rem] shadow-2xl border-4 border-white dark:border-slate-900 z-10">
                        <div className="w-16 h-16 bg-primary/10 rounded-[1.5rem] flex items-center justify-center text-primary">
                            <Building2 size={40} />
                        </div>
                    </div>
                </div>

                <div className="pt-16 pb-12 px-10">
                    <div className="flex flex-col md:flex-row justify-between items-start gap-8">
                        <div className="flex-1 w-full space-y-4">
                            <div className="flex items-center gap-4">
                                <AnimatePresence mode="wait">
                                    {isEditing ? (
                                        <motion.div 
                                            initial={{ opacity: 0, x: -10 }}
                                            animate={{ opacity: 1, x: 0 }}
                                            exit={{ opacity: 0, x: -10 }}
                                            className="flex gap-2 w-full max-w-md"
                                        >
                                            <input 
                                                type="text"
                                                value={newName}
                                                onChange={(e) => setNewName(e.target.value)}
                                                className="text-2xl font-black bg-slate-50 dark:bg-white/5 border border-primary/30 rounded-2xl px-4 py-2 w-full focus:ring-4 focus:ring-primary/10 outline-none text-slate-900 dark:text-white"
                                                autoFocus
                                            />
                                            <button 
                                                onClick={handleUpdateName}
                                                disabled={saving}
                                                className="p-3 bg-primary text-white rounded-2xl hover:bg-primary/90 transition-all shadow-lg shadow-primary/20"
                                            >
                                                {saving ? <Loader2 size={24} className="animate-spin" /> : <Check size={24} />}
                                            </button>
                                        </motion.div>
                                    ) : (
                                        <motion.div 
                                            initial={{ opacity: 0, x: 10 }}
                                            animate={{ opacity: 1, x: 0 }}
                                            exit={{ opacity: 0, x: 10 }}
                                            className="flex items-center gap-3"
                                        >
                                            <h1 className="text-4xl font-black text-slate-900 dark:text-white tracking-tight">
                                                {business?.nombre}
                                            </h1>
                                            {isAdmin && (
                                                <button 
                                                    onClick={() => setIsEditing(true)}
                                                    className="p-2.5 bg-slate-100 dark:bg-white/5 hover:bg-slate-200 dark:hover:bg-white/10 text-slate-500 hover:text-primary rounded-xl transition-all"
                                                >
                                                    <Edit2 size={18} />
                                                </button>
                                            )}
                                        </motion.div>
                                    )}
                                </AnimatePresence>
                            </div>
                            
                            <div className="flex flex-wrap gap-3">
                                <span className="flex items-center gap-2 px-4 py-1.5 bg-primary/10 text-primary text-[10px] font-black uppercase tracking-widest rounded-full border border-primary/20">
                                    <Sparkles size={12} />
                                    Plan Premium
                                </span>
                                <span className="px-4 py-1.5 bg-slate-50 dark:bg-white/5 text-slate-500 dark:text-slate-400 text-[10px] font-black uppercase tracking-widest rounded-full border border-slate-200 dark:border-white/5">
                                    ID: #{business?.idNegocio}
                                </span>
                            </div>
                        </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-10 mt-12">
                        {/* Invitation Code Section */}
                        <div className="space-y-6">
                            <div className="space-y-1">
                                <h3 className="text-sm font-black text-slate-500 dark:text-slate-400 uppercase tracking-[0.2em] flex items-center gap-2">
                                    <Share2 size={16} className="text-primary" />
                                    Código de Invitación
                                </h3>
                                <p className="text-xs text-slate-500 dark:text-slate-600 font-medium">Comparte este código para añadir nuevos colaboradores</p>
                            </div>

                            <div className="relative group">
                                <div className="absolute -inset-1 bg-gradient-to-r from-primary/30 to-indigo-600/30 rounded-[2.5rem] blur-xl opacity-0 group-hover:opacity-100 transition duration-1000"></div>
                                <div className="relative bg-slate-50 dark:bg-slate-900/50 p-8 rounded-[2.5rem] border border-slate-200 dark:border-white/5 text-center flex flex-col items-center justify-center space-y-6 shadow-sm dark:shadow-inner">
                                    <span className="text-5xl font-black tracking-[0.3em] text-slate-900 dark:text-white">
                                        {business?.codigoInvitacion}
                                    </span>
                                    
                                    <button 
                                        onClick={handleCopyCode}
                                        className="inline-flex items-center gap-2 px-6 py-3 bg-primary text-white rounded-2xl font-black text-sm shadow-xl shadow-primary/20 hover:scale-105 active:scale-95 transition-all"
                                    >
                                        <Copy size={18} />
                                        COPIAR CÓDIGO
                                    </button>
                                </div>
                            </div>

                            <div className="flex items-start gap-3 p-5 bg-blue-500/5 rounded-3xl border border-blue-500/10 text-blue-400 text-xs font-medium leading-relaxed">
                                <AlertCircle size={20} className="shrink-0 text-primary opacity-70" />
                                <p>Cualquier persona con este código podrá solicitar unirse a tu negocio. Asegúrate de compartirlo solo con personas de confianza.</p>
                            </div>
                        </div>

                        {/* QR Code Section */}
                        <div className="space-y-6">
                            <div className="space-y-1">
                                <h3 className="text-sm font-black text-slate-500 dark:text-slate-400 uppercase tracking-[0.2em] flex items-center gap-2">
                                    <QrCode size={16} className="text-primary" />
                                    Acceso Rápido QR
                                </h3>
                                <p className="text-xs text-slate-500 dark:text-slate-600 font-medium">Escaneo directo para registro de colaboradores</p>
                            </div>

                            <div className="bg-white p-8 rounded-[2.5rem] border border-slate-200 dark:border-transparent flex flex-col items-center justify-center shadow-2xl relative overflow-hidden group">
                                <div className="absolute inset-0 bg-primary/5 opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
                                <img 
                                    src={qrUrl} 
                                    alt="QR Code" 
                                    className="w-48 h-48 rounded-2xl relative z-10 transition-transform duration-700 group-hover:scale-110"
                                />
                                <div className="mt-6 flex items-center gap-2 text-slate-900 font-black text-xs tracking-widest relative z-10">
                                    <Sparkles size={14} className="text-primary" />
                                    GEMA SMART ACCESS
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </motion.div>
    );
}
