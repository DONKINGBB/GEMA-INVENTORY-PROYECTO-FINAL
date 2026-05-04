
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Building2, Copy, Edit2, QrCode, ArrowLeft, Check, AlertCircle } from 'lucide-react';
import { businessService } from '../services/businessService';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

export default function BusinessSettings() {
    const navigate = useNavigate();
    const { user, updateUserData } = useAuth();
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
            // Opcional: Actualizar datos de usuario en contexto si es necesario
        } catch (error) {
            toast.error('Error al actualizar el nombre');
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-[400px]">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
            </div>
        );
    }

    const qrUrl = business ? `https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=${business.codigoInvitacion}` : '';
    const isAdmin = user?.idRol === 1;

    return (
        <div className="max-w-2xl mx-auto py-8 px-4">
            <button 
                onClick={() => navigate('/app/settings')}
                className="flex items-center gap-2 text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200 mb-6 transition"
            >
                <ArrowLeft size={20} />
                Volver a Ajustes
            </button>

            <div className="bg-white dark:bg-slate-800 rounded-3xl shadow-xl overflow-hidden border border-gray-100 dark:border-slate-700">
                {/* Header Banner */}
                <div className="h-32 bg-gradient-to-r from-blue-600 to-indigo-700 relative">
                    <div className="absolute -bottom-12 left-8 p-4 bg-white dark:bg-slate-800 rounded-2xl shadow-lg border-4 border-white dark:border-slate-800">
                        <Building2 size={48} className="text-blue-600 dark:text-blue-400" />
                    </div>
                </div>

                <div className="pt-16 pb-8 px-8">
                    <div className="flex justify-between items-start mb-8">
                        <div className="flex-1">
                            {isEditing ? (
                                <div className="flex gap-2">
                                    <input 
                                        type="text"
                                        value={newName}
                                        onChange={(e) => setNewName(e.target.value)}
                                        className="text-2xl font-bold bg-gray-50 dark:bg-slate-900 border-none rounded-lg px-2 py-1 w-full focus:ring-2 focus:ring-primary outline-none text-gray-900 dark:text-white"
                                        autoFocus
                                    />
                                    <button 
                                        onClick={handleUpdateName}
                                        disabled={saving}
                                        className="p-2 bg-green-100 text-green-600 rounded-lg hover:bg-green-200 transition"
                                    >
                                        <Check size={20} />
                                    </button>
                                </div>
                            ) : (
                                <div className="flex items-center gap-3">
                                    <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
                                        {business?.nombre}
                                    </h1>
                                    {isAdmin && (
                                        <button 
                                            onClick={() => setIsEditing(true)}
                                            className="p-2 text-gray-400 hover:text-primary transition"
                                        >
                                            <Edit2 size={18} />
                                        </button>
                                    )}
                                </div>
                            )}
                            <p className="text-gray-500 dark:text-gray-400 mt-1">
                                {isAdmin ? 'Propietario / Administrador' : 'Colaborador'}
                            </p>
                        </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                        {/* Invitation Code Section */}
                        <div className="space-y-4">
                            <h3 className="font-semibold text-gray-700 dark:text-gray-300 flex items-center gap-2">
                                <QrCode size={18} />
                                Código de Invitación
                            </h3>
                            <div className="bg-gray-50 dark:bg-slate-900/50 p-6 rounded-2xl border border-dashed border-gray-200 dark:border-slate-700 text-center group">
                                <span className="text-4xl font-black tracking-widest text-primary dark:text-blue-400 block mb-4">
                                    {business?.codigoInvitacion}
                                </span>
                                <button 
                                    onClick={handleCopyCode}
                                    className="inline-flex items-center gap-2 px-4 py-2 bg-white dark:bg-slate-800 text-gray-700 dark:text-gray-200 rounded-xl shadow-sm hover:shadow-md transition border border-gray-100 dark:border-slate-700"
                                >
                                    <Copy size={16} />
                                    Copiar Código
                                </button>
                            </div>
                            <div className="flex items-start gap-2 p-4 bg-blue-50 dark:bg-blue-900/20 rounded-xl text-blue-700 dark:text-blue-300 text-sm">
                                <AlertCircle size={16} className="mt-0.5 shrink-0" />
                                <p>Comparte este código con tu equipo para que puedan unirse a tu inventario.</p>
                            </div>
                        </div>

                        {/* QR Code Section */}
                        <div className="flex flex-col items-center justify-center p-6 bg-gray-50 dark:bg-slate-900/50 rounded-2xl border border-gray-100 dark:border-slate-700">
                            <img 
                                src={qrUrl} 
                                alt="QR Code" 
                                className="w-48 h-48 rounded-xl shadow-lg mb-4 bg-white p-2"
                            />
                            <p className="text-sm text-gray-500 dark:text-gray-400 text-center">
                                Escanea para unirte instantáneamente
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
