
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
            <div className="flex items-center gap-4 mb-8">
                <button 
                    onClick={() => navigate('/app/settings')}
                    className="p-3 bg-white dark:bg-white/5 hover:bg-slate-50 dark:hover:bg-white/10 rounded-2xl transition-all text-slate-900 dark:text-white border border-slate-200 dark:border-white/10 shadow-sm dark:shadow-none active:scale-95"
                >
                    <ArrowLeft size={24} />
                </button>
                <div>
                    <h1 className="text-3xl font-black text-slate-900 dark:text-white">
                        Mis Negocios
                    </h1>
                    <p className="text-slate-500 dark:text-slate-400 text-sm">Gestiona tus diferentes organizaciones</p>
                </div>
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
                                relative flex items-center gap-4 p-6 rounded-[2rem] text-left transition-all
                                ${isActive 
                                    ? 'bg-primary/5 dark:bg-primary/10 border-2 border-primary ring-4 ring-primary/5 dark:ring-primary/5' 
                                    : 'bg-white dark:bg-slate-900/40 backdrop-blur-xl border border-slate-200 dark:border-white/5 hover:border-primary/50 hover:shadow-xl hover:shadow-black/5 dark:hover:shadow-none'
                                }
                                ${isSwitching ? 'opacity-70 cursor-wait' : ''}
                                ${!isActive && !isSwitching ? 'active:scale-95' : ''}
                            `}
                        >
                            <div className={`
                                p-3 rounded-2xl transition-colors
                                ${isActive ? 'bg-primary text-white shadow-lg shadow-primary/30' : 'bg-slate-100 dark:bg-white/5 text-slate-500 dark:text-slate-400'}
                            `}>
                                <Building2 size={24} />
                            </div>

                            <div className="flex-1">
                                <h3 className="font-black text-lg text-slate-900 dark:text-white leading-tight">
                                    {biz.nombre}
                                </h3>
                                <div className="flex items-center gap-3 mt-1.5">
                                    <span className="flex items-center gap-1 text-[9px] font-black uppercase tracking-wider px-2 py-0.5 rounded-lg bg-slate-100 dark:bg-slate-900 text-slate-600 dark:text-slate-400 border border-slate-200 dark:border-slate-800">
                                        <Users size={12} />
                                        {getRoleName(biz.idRol)}
                                    </span>
                                    {isActive && (
                                        <span className="text-[10px] font-black text-primary uppercase tracking-tighter flex items-center gap-1.5">
                                            <div className="w-1.5 h-1.5 rounded-full bg-primary animate-pulse shadow-[0_0_8px_rgba(59,130,246,0.5)]" />
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
                    className="flex items-center justify-center gap-3 p-6 rounded-[2rem] border-2 border-dashed border-slate-200 dark:border-slate-800 text-slate-500 dark:text-slate-400 hover:border-primary/50 hover:bg-primary/5 hover:text-primary transition-all group"
                >
                    <div className="p-2 bg-slate-50 dark:bg-slate-900 rounded-full group-hover:bg-primary/10 transition-colors">
                        <Plus size={20} />
                    </div>
                    <span className="font-black uppercase tracking-widest text-sm">Añadir nuevo negocio</span>
                </button>
            </div>
        </div>
    );
}
