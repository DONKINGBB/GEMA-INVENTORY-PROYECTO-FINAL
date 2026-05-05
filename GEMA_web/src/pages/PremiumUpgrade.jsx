import { motion } from 'framer-motion';
import { 
    Sparkles, ShieldCheck, Zap, Globe, BarChart3, 
    Smartphone, Clock, Rocket, ArrowRight, Star,
    CheckCircle2, Cpu, Database, Layout
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function PremiumUpgrade() {
    const navigate = useNavigate();

    const benefits = [
        {
            icon: Globe,
            title: "Sincronización Global",
            desc: "Accede a tu inventario desde cualquier lugar del mundo con redundancia en la nube de alta disponibilidad.",
            color: "from-blue-400 to-indigo-600"
        },
        {
            icon: BarChart3,
            title: "Analítica Predictiva",
            desc: "IA avanzada que predice tendencias de consumo y te sugiere órdenes de compra inteligentes.",
            color: "from-purple-400 to-pink-600"
        },
        {
            icon: ShieldCheck,
            title: "Seguridad Grado Bancario",
            desc: "Encriptación AES-256 de extremo a extremo y respaldos automáticos en tiempo real.",
            color: "from-emerald-400 to-teal-600"
        },
        {
            icon: Smartphone,
            title: "App Mobile Exclusiva",
            desc: "Funciones premium en iOS y Android con escaneo de código de barras offline y alertas push.",
            color: "from-amber-400 to-orange-600"
        }
    ];

    const upcomingFeatures = [
        { icon: Cpu, title: "Motor de IA GEMA v2", desc: "Optimización de stock mediante redes neuronales." },
        { icon: Database, title: "API Empresarial", desc: "Conecta tu inventario con cualquier ERP o sistema externo." },
        { icon: Layout, title: "Dashboards Custom", desc: "Crea tus propios paneles de control con widgets dinámicos." }
    ];

    const containerVariants = {
        hidden: { opacity: 0 },
        visible: {
            opacity: 1,
            transition: {
                staggerChildren: 0.15
            }
        }
    };

    const itemVariants = {
        hidden: { y: 30, opacity: 0 },
        visible: { 
            y: 0, 
            opacity: 1,
            transition: { type: 'spring', stiffness: 200, damping: 20 }
        }
    };

    return (
        <motion.div 
            initial="hidden"
            animate="visible"
            variants={containerVariants}
            className="relative min-h-screen pb-32"
        >
            {/* Animated Mesh Background */}
            <div className="absolute inset-0 -z-10 overflow-hidden pointer-events-none">
                <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-primary/10 blur-[120px] rounded-full animate-pulse" />
                <div className="absolute bottom-[-10%] right-[-10%] w-[50%] h-[50%] bg-purple-500/5 blur-[150px] rounded-full" />
            </div>

            {/* Header Section */}
            <div className="relative z-10 text-center mb-24 pt-12">
                <motion.div
                    variants={itemVariants}
                    className="inline-flex items-center gap-2 px-6 py-2.5 rounded-full bg-amber-400/10 border border-amber-400/20 text-amber-500 mb-10 shadow-lg shadow-amber-500/5"
                >
                    <Star size={16} className="fill-current" />
                    <span className="text-[10px] font-black uppercase tracking-[0.4em]">Experiencia Elite GEMA</span>
                </motion.div>
                
                <motion.h1 
                    variants={itemVariants}
                    className="text-6xl md:text-8xl font-black tracking-tighter text-slate-900 dark:text-white mb-8 leading-[0.85]"
                >
                    GEMA <span className="text-transparent bg-clip-text bg-gradient-to-r from-amber-200 via-yellow-400 to-amber-600 drop-shadow-2xl">PREMIUM</span>
                </motion.h1>
                
                <motion.p 
                    variants={itemVariants}
                    className="text-xl text-slate-500 dark:text-white/60 max-w-3xl mx-auto font-medium leading-relaxed"
                >
                    Redefine la gestión de tu negocio con la tecnología más avanzada del mercado. 
                    Inteligencia artificial, seguridad inquebrantable y control total en la palma de tu mano.
                </motion.p>
            </div>

            {/* Coming Soon Hero Section */}
            <motion.div 
                variants={itemVariants}
                className="relative aspect-[21/9] rounded-[4rem] overflow-hidden border border-black/5 dark:border-white/10 shadow-[0_50px_100px_-20px_rgba(0,0,0,0.2)] mb-32 group"
            >
                <div className="absolute inset-0 bg-gradient-to-r from-slate-950 via-slate-950/60 to-transparent z-10" />
                <img 
                    src="/premium_features_showcase_1778015074676.png" 
                    alt="Premium Showcase" 
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-[2000ms] ease-out"
                />
                
                <div className="absolute inset-0 z-20 flex flex-col justify-center p-12 md:p-24">
                    <motion.div
                        initial={{ x: -50, opacity: 0 }}
                        whileInView={{ x: 0, opacity: 1 }}
                        transition={{ delay: 0.3 }}
                        className="max-w-2xl"
                    >
                        <div className="flex items-center gap-3 text-amber-400 mb-6">
                            <div className="w-12 h-[2px] bg-amber-400" />
                            <span className="text-sm font-black uppercase tracking-[0.4em]">Próximamente 2026</span>
                        </div>
                        <h2 className="text-5xl md:text-7xl font-black text-white mb-8 tracking-tighter leading-[0.9]">
                            Lanzamiento <br />
                            <span className="text-primary brightness-125">GEMA 2.0</span>
                        </h2>
                        <p className="text-white/70 text-lg md:text-xl mb-12 font-medium leading-relaxed">
                            Estamos construyendo el futuro del comercio. Únete a los más de 5,000 negocios que esperan la revolución digital.
                        </p>
                        
                        <div className="flex flex-wrap gap-5">
                            <motion.button 
                                whileHover={{ scale: 1.05 }}
                                whileTap={{ scale: 0.95 }}
                                className="px-10 py-5 bg-white text-slate-950 rounded-2xl font-black text-[11px] uppercase tracking-widest shadow-2xl hover:shadow-white/20 transition-all"
                            >
                                Notificarme al Lanzamiento
                            </motion.button>
                            <motion.button 
                                whileHover={{ scale: 1.05 }}
                                whileTap={{ scale: 0.95 }}
                                onClick={() => navigate('/app')}
                                className="px-10 py-5 bg-white/10 backdrop-blur-2xl border border-white/20 text-white rounded-2xl font-black text-[11px] uppercase tracking-widest hover:bg-white/20 transition-all"
                            >
                                Volver al Dashboard
                            </motion.button>
                        </div>
                    </motion.div>
                </div>

                {/* Status Badges */}
                <div className="absolute top-10 right-10 z-20 flex gap-4">
                    <div className="px-4 py-2 bg-emerald-500/20 backdrop-blur-xl border border-emerald-500/30 rounded-xl flex items-center gap-2">
                        <div className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
                        <span className="text-[10px] font-black text-white uppercase tracking-widest">Servidores Online</span>
                    </div>
                </div>
            </motion.div>

            {/* Core Benefits Grid */}
            <div className="mb-40">
                <div className="flex items-center gap-4 mb-16 px-4">
                    <div className="h-px flex-1 bg-gradient-to-r from-transparent to-slate-200 dark:to-white/10" />
                    <h2 className="text-2xl font-black text-slate-900 dark:text-white tracking-tighter uppercase">Ventajas Exclusivas</h2>
                    <div className="h-px flex-1 bg-gradient-to-l from-transparent to-slate-200 dark:to-white/10" />
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
                    {benefits.map((benefit, idx) => (
                        <motion.div
                            key={idx}
                            variants={itemVariants}
                            whileHover={{ y: -12, scale: 1.02 }}
                            className="p-10 rounded-[3rem] bg-white dark:bg-white/[0.02] border border-slate-100 dark:border-white/5 shadow-xl relative overflow-hidden group transition-all duration-500"
                        >
                            <div className={`absolute -right-8 -top-8 w-32 h-32 bg-gradient-to-br ${benefit.color} opacity-[0.03] blur-3xl group-hover:opacity-[0.08] transition-opacity`} />
                            
                            <div className={`w-16 h-16 rounded-[1.5rem] bg-gradient-to-br ${benefit.color} flex items-center justify-center text-white mb-10 shadow-2xl shadow-indigo-500/20 group-hover:rotate-12 transition-transform duration-500`}>
                                <benefit.icon size={32} />
                            </div>
                            
                            <h3 className="text-xl font-black text-slate-900 dark:text-white mb-4 tracking-tight uppercase">
                                {benefit.title}
                            </h3>
                            <p className="text-sm text-slate-500 dark:text-white/40 leading-relaxed font-medium">
                                {benefit.desc}
                            </p>
                        </motion.div>
                    ))}
                </div>
            </div>

            {/* Feature Teasers */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-40">
                {upcomingFeatures.map((f, i) => (
                    <motion.div
                        key={i}
                        variants={itemVariants}
                        className="p-8 rounded-[2.5rem] border border-black/5 dark:border-white/5 bg-slate-50 dark:bg-white/[0.01] flex items-center gap-6 group hover:border-primary/30 transition-all"
                    >
                        <div className="w-14 h-14 rounded-2xl bg-primary/10 flex items-center justify-center text-primary group-hover:bg-primary group-hover:text-white transition-all duration-500">
                            <f.icon size={24} />
                        </div>
                        <div>
                            <h4 className="font-black text-slate-900 dark:text-white text-sm uppercase tracking-tight mb-1">{f.title}</h4>
                            <p className="text-xs text-slate-500 dark:text-white/30 font-medium">{f.desc}</p>
                        </div>
                    </motion.div>
                ))}
            </div>

            {/* Pricing Comparison Immersive */}
            <div className="mb-40">
                <div className="text-center mb-20">
                    <span className="text-primary font-black uppercase tracking-[0.5em] text-[10px] mb-4 block">Inversión Inteligente</span>
                    <h2 className="text-5xl font-black text-slate-900 dark:text-white tracking-tighter uppercase">Planes de <span className="text-slate-400">Crecimiento</span></h2>
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-10 max-w-7xl mx-auto">
                    {/* Basic */}
                    <motion.div 
                        variants={itemVariants}
                        whileHover={{ y: -10 }}
                        className="p-12 rounded-[3.5rem] bg-white dark:bg-white/[0.02] border border-slate-100 dark:border-white/5 shadow-2xl relative flex flex-col"
                    >
                        <h3 className="text-2xl font-black text-slate-900 dark:text-white mb-2 uppercase tracking-tight">Free</h3>
                        <p className="text-slate-500 dark:text-white/40 text-sm mb-10 font-medium italic">Perfecto para empezar</p>
                        <div className="text-5xl font-black text-slate-900 dark:text-white mb-12">$0 <span className="text-sm text-slate-400 font-bold uppercase tracking-widest">/ Mes</span></div>
                        
                        <div className="space-y-6 mb-12 flex-1">
                            {['Hasta 100 Productos', '1 Almacén Local', 'Reportes Básicos'].map((feat, i) => (
                                <div key={i} className="flex items-center gap-4 text-[11px] font-black text-slate-600 dark:text-white/40 uppercase tracking-widest">
                                    <CheckCircle2 size={16} className="text-slate-300 dark:text-white/10" />
                                    {feat}
                                </div>
                            ))}
                        </div>
                        
                        <button className="w-full py-5 rounded-2xl bg-slate-100 dark:bg-white/5 text-slate-500 dark:text-white/20 font-black text-[10px] uppercase tracking-widest cursor-default">
                            Plan Actual
                        </button>
                    </motion.div>

                    {/* Pro - The Ultimate Choice */}
                    <motion.div 
                        variants={itemVariants}
                        whileHover={{ y: -15, scale: 1.02 }}
                        className="p-14 rounded-[4rem] bg-gradient-to-br from-primary via-blue-700 to-indigo-900 shadow-[0_50px_100px_-20px_rgba(59,130,246,0.3)] relative overflow-hidden flex flex-col group z-20"
                    >
                        <motion.div 
                            animate={{ 
                                x: ['-200%', '300%'],
                                transition: { duration: 4, repeat: Infinity, ease: "linear", repeatDelay: 1 }
                            }}
                            className="absolute inset-0 bg-gradient-to-r from-transparent via-white/20 to-transparent -skew-x-12 z-0"
                        />
                        
                        <div className="relative z-10 h-full flex flex-col">
                            <div className="absolute -top-6 -right-6 px-6 py-2 bg-amber-400 text-slate-950 rounded-full text-[10px] font-black uppercase tracking-widest shadow-2xl">Más Popular</div>
                            
                            <h3 className="text-3xl font-black text-white mb-2 uppercase tracking-tight">GEMA Pro</h3>
                            <p className="text-white/60 text-sm mb-12 font-black uppercase tracking-widest">Potencia Ilimitada</p>
                            
                            <div className="text-7xl font-black text-white mb-12 tracking-tighter flex items-start">
                                <span className="text-2xl mt-4 mr-1">$</span>29
                            </div>
                            
                            <div className="space-y-7 mb-14 flex-1">
                                {['Inventario Ilimitado', 'Multialmacén Cloud', 'IA Predictiva v2', 'Soporte Prioritario', 'Personalización Total'].map((feat, i) => (
                                    <div key={i} className="flex items-center gap-4 text-[12px] font-black text-white uppercase tracking-widest">
                                        <div className="w-5 h-5 rounded-full bg-white/20 flex items-center justify-center">
                                            <CheckCircle2 size={12} className="text-white" />
                                        </div>
                                        {feat}
                                    </div>
                                ))}
                            </div>
                            
                            <motion.button 
                                whileHover={{ scale: 1.05 }}
                                whileTap={{ scale: 0.95 }}
                                className="w-full py-6 rounded-3xl bg-white text-primary font-black text-[12px] uppercase tracking-[0.25em] shadow-2xl transition-all"
                            >
                                Adquirir Premium
                            </motion.button>
                        </div>
                    </motion.div>

                    {/* Enterprise */}
                    <motion.div 
                        variants={itemVariants}
                        whileHover={{ y: -10 }}
                        className="p-12 rounded-[3.5rem] bg-white dark:bg-white/[0.02] border border-slate-100 dark:border-white/5 shadow-2xl relative flex flex-col"
                    >
                        <h3 className="text-2xl font-black text-slate-900 dark:text-white mb-2 uppercase tracking-tight">Elite</h3>
                        <p className="text-slate-500 dark:text-white/40 text-sm mb-10 font-medium italic">Soluciones Corporativas</p>
                        <div className="text-5xl font-black text-slate-900 dark:text-white mb-12">Custom</div>
                        
                        <div className="space-y-6 mb-12 flex-1">
                            {['Servidor Dedicado', 'On-Premise Option', 'Gerente de Cuenta 24/7', 'API White-label'].map((feat, i) => (
                                <div key={i} className="flex items-center gap-4 text-[11px] font-black text-slate-600 dark:text-white/40 uppercase tracking-widest">
                                    <CheckCircle2 size={16} className="text-slate-300 dark:text-white/10" />
                                    {feat}
                                </div>
                            ))}
                        </div>
                        
                        <button className="w-full py-5 rounded-2xl border-2 border-slate-200 dark:border-white/10 text-slate-900 dark:text-white font-black text-[10px] uppercase tracking-widest hover:bg-slate-50 dark:hover:bg-white/5 transition-all">
                            Hablar con Ventas
                        </button>
                    </motion.div>
                </div>
            </div>

            {/* Final Interactive CTA */}
            <motion.div 
                whileInView={{ opacity: 1, scale: 1 }}
                initial={{ opacity: 0, scale: 0.9 }}
                viewport={{ once: true }}
                className="relative p-24 rounded-[5rem] bg-slate-950 text-center overflow-hidden shadow-3xl group"
            >
                <div className="absolute inset-0 bg-gradient-to-br from-primary/20 via-transparent to-purple-600/20 opacity-50 group-hover:scale-110 transition-transform duration-[3000ms]" />
                <div className="absolute inset-0 bg-[url('https://www.transparenttextures.com/patterns/carbon-fibre.png')] opacity-20" />
                
                <div className="relative z-10 flex flex-col items-center">
                    <motion.div 
                        animate={{ y: [0, -10, 0] }}
                        transition={{ duration: 4, repeat: Infinity }}
                        className="w-24 h-24 bg-white/5 rounded-full flex items-center justify-center mb-10 border border-white/10 backdrop-blur-xl"
                    >
                        <Rocket size={48} className="text-primary brightness-150" />
                    </motion.div>
                    
                    <h2 className="text-6xl md:text-8xl font-black text-white mb-10 tracking-tighter uppercase leading-[0.8] max-w-4xl">
                        EL FUTURO ES <span className="text-transparent bg-clip-text bg-gradient-to-r from-primary to-purple-400">AHORA</span>
                    </h2>
                    
                    <p className="text-white/40 text-xl max-w-2xl mx-auto mb-16 font-medium leading-relaxed uppercase tracking-widest">
                        No te quedes atrás. La transformación digital de tu negocio comienza hoy con GEMA.
                    </p>
                    
                    <motion.button 
                        whileHover={{ scale: 1.1, boxShadow: "0 0 50px rgba(59,130,246,0.5)" }}
                        whileTap={{ scale: 0.95 }}
                        className="px-16 py-8 bg-primary text-white rounded-[2.5rem] font-black text-sm uppercase tracking-[0.4em] shadow-2xl transition-all"
                    >
                        Solicitar Acceso Beta Privado
                    </motion.button>
                </div>
            </motion.div>
        </motion.div>
    );
}

