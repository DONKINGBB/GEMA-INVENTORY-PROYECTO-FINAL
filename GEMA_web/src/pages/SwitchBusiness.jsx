
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Building2, Check, ArrowLeft, Loader2, Plus, Users } from 'lucide-react';
import { businessService } from '../services/businessService';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

export default function SwitchBusiness() {
    const navigate = useNavigate();
    const { user, updateUser } = useAuth(); // Usamos updateUser para refrescar datos tras el switch
    const [businesses, setBusinesses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [switchingId, setSwitchingId] = useState(null);

    useEffect(() => {
        loadBusinesses();
    }, []);

    const loadBusinesses = async () => {
        try {
            const data = await businessService.getMisNegocios();
            setBusinesses(data);
        } catch (error) {
            toast.error('Error al cargar la lista de negocios');
        } finally {
            setLoading(false);
        }
    };

    const handleSwitch = async (id) => {
        if (id === user?.idNegocio) {
            toast('Ya estás en este negocio', { icon: 'ℹ️' });
            return;
        }

        setSwitchingId(id);
        try {
            const response = await businessService.switchNegocio(id);
            
            if (response.success && response.usuario) {
                // Actualizar el usuario en el contexto
                updateUser(response.usuario);
                toast.success('Cambiando de negocio...');
                
                // Redirigir para refrescar todos los datos del nuevo negocio
                setTimeout(() => {
                    window.location.href = '/app';
                }, 500);
            } else {
                throw new Error('No se pudo obtener la información del usuario');
            }
        } catch (error) {
            console.error(error);
            toast.error('Error al cambiar de negocio');
            setSwitchingId(null);
        }
    };

    const getRoleName = (roleId) => {
        const id = parseFloat(roleId);
        if (id === 1) return 'Dueño / Propietario';
        if (id === 2) return 'Administrador';
        return 'Miembro del equipo';
    };

    if (loading) {
        return (
            <div className="flex flex-col items-center justify-center min-h-[400px] gap-4">
                <Loader2 className="animate-spin text-primary" size={40} />
                <p className="text-gray-500 animate-pulse">Cargando tus negocios...</p>
            </div>
        );
    }

    return (
        <div className="max-w-2xl mx-auto py-8 px-4">
            <div className="flex items-center justify-between mb-8">
                <button 
                    onClick={() => navigate('/app/settings')}
                    className="flex items-center gap-2 text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200 transition"
                >
                    <ArrowLeft size={20} />
                    Volver
                </button>
                <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Mis Negocios</h1>
            </div>

            <div className="grid gap-4">
                {businesses.map((biz) => {
                    // Usamos loose equality o convertimos a String para evitar fallos de tipo
                    const isActive = String(biz.id) === String(user?.idNegocio);
                    const isSwitching = switchingId === biz.id;

                    return (
                        <button
                            key={biz.id}
                            onClick={() => handleSwitch(biz.id)}
                            disabled={isSwitching || isActive}
                            className={`
                                relative flex items-center gap-4 p-6 rounded-2xl text-left transition-all
                                ${isActive 
                                    ? 'bg-blue-50 dark:bg-blue-900/20 border-2 border-primary ring-4 ring-blue-50 dark:ring-blue-900/10' 
                                    : 'bg-white dark:bg-slate-800 border border-gray-100 dark:border-slate-700 hover:border-primary/50 hover:shadow-lg'
                                }
                                ${isSwitching ? 'opacity-70 cursor-wait' : ''}
                                ${!isActive && !isSwitching ? 'active:scale-95' : ''}
                            `}
                        >
                            <div className={`
                                p-3 rounded-xl transition-colors
                                ${isActive ? 'bg-primary text-white shadow-lg shadow-primary/30' : 'bg-gray-100 dark:bg-slate-700 text-gray-500 dark:text-gray-400'}
                            `}>
                                <Building2 size={24} />
                            </div>

                            <div className="flex-1">
                                <h3 className="font-bold text-lg text-gray-900 dark:text-white">
                                    {biz.nombre}
                                </h3>
                                <div className="flex items-center gap-3 mt-1">
                                    <span className="flex items-center gap-1 text-[10px] font-black uppercase tracking-wider px-2 py-0.5 rounded-lg bg-gray-100 dark:bg-slate-900 text-gray-600 dark:text-gray-400 border border-gray-200 dark:border-slate-700">
                                        <Users size={12} />
                                        {getRoleName(biz.idRol)}
                                    </span>
                                    {isActive && (
                                        <span className="text-xs font-bold text-primary dark:text-blue-400 flex items-center gap-1">
                                            <div className="w-1.5 h-1.5 rounded-full bg-primary animate-pulse" />
                                            Negocio actual
                                        </span>
                                    )}
                                </div>
                            </div>

                            {isActive && (
                                <div className="bg-primary text-white p-1 rounded-full shadow-md">
                                    <Check size={16} />
                                </div>
                            )}

                            {isSwitching && (
                                <div className="flex items-center gap-2 text-primary font-bold text-sm">
                                    <Loader2 className="animate-spin" size={20} />
                                    <span>Conectando...</span>
                                </div>
                            )}
                        </button>
                    );
                })}

                <button 
                    onClick={() => navigate('/app/settings', { state: { openBusinessModal: true } })}
                    className="flex items-center justify-center gap-2 p-6 rounded-2xl border-2 border-dashed border-gray-200 dark:border-slate-700 text-gray-500 dark:text-gray-400 hover:border-primary hover:text-primary dark:hover:text-blue-400 transition group"
                >
                    <div className="p-2 bg-gray-50 dark:bg-slate-900 rounded-full group-hover:bg-primary/10 transition">
                        <Plus size={20} />
                    </div>
                    <span className="font-semibold">Unirse o crear nuevo negocio</span>
                </button>
            </div>
        </div>
    );
}
