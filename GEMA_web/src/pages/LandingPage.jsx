import { Link } from 'react-router-dom';
import { useTheme } from '../context/ThemeContext';
import { Package, TrendingUp, Users, Download, ArrowRight, ShieldCheck, Zap, ChevronRight, CheckCircle2, Globe, Sparkles, Star } from 'lucide-react';
import { motion, useScroll, useTransform } from 'framer-motion';

const fadeInUp = {
    hidden: { opacity: 0, y: 30 },
    visible: { opacity: 1, y: 0, transition: { duration: 0.8, ease: [0.16, 1, 0.3, 1] } }
};

const staggerContainer = {
    hidden: { opacity: 0 },
    visible: {
        opacity: 1,
        transition: {
            staggerChildren: 0.2
        }
    }
};

const FloatingElement = ({ children, delay = 0, duration = 4 }) => (
    <motion.div
        animate={{ y: [0, -15, 0] }}
        transition={{ duration, repeat: Infinity, delay, ease: "easeInOut" }}
    >
        {children}
    </motion.div>
);

export default function LandingPage() {
    const { isDarkMode } = useTheme();
    const { scrollY } = useScroll();
    const y1 = useTransform(scrollY, [0, 500], [0, 200]);
    const opacity = useTransform(scrollY, [0, 300], [1, 0]);

    return (
        <div className={`min-h-screen font-sans selection:bg-primary/30 ${isDarkMode ? 'dark bg-[#030712] text-white' : 'bg-slate-50 text-slate-900'} transition-colors duration-500 overflow-x-hidden`}>
            {/* Ambient Premium Background */}
            <div className="fixed inset-0 z-0 pointer-events-none">
                <motion.div 
                    animate={{ 
                        scale: [1, 1.2, 1],
                        opacity: [0.3, 0.5, 0.3],
                        rotate: [0, 90, 0]
                    }}
                    transition={{ duration: 15, repeat: Infinity }}
                    className="absolute top-[-20%] left-[-10%] w-[60%] h-[60%] bg-primary/30 rounded-full blur-[150px]" 
                />
                <motion.div 
                    animate={{ 
                        scale: [1, 1.3, 1],
                        opacity: [0.2, 0.4, 0.2],
                        rotate: [0, -90, 0]
                    }}
                    transition={{ duration: 20, repeat: Infinity }}
                    className="absolute bottom-[-10%] right-[-10%] w-[50%] h-[50%] bg-blue-600/20 rounded-full blur-[150px]" 
                />
                <div className="absolute top-[40%] left-[30%] w-[30%] h-[30%] bg-indigo-500/10 rounded-full blur-[120px]" />
                <div className="absolute top-[10%] right-[10%] w-[25%] h-[25%] bg-purple-500/10 rounded-full blur-[100px] animate-pulse" />
            </div>

            {/* Navbar */}
            <nav className="fixed w-full z-50 top-0 px-6 py-6 transition-all duration-300 backdrop-blur-2xl bg-white/40 dark:bg-slate-950/40 border-b border-white/5">
                <div className="max-w-7xl mx-auto flex justify-between items-center">
                    <motion.div 
                        initial={{ opacity: 0, x: -20 }}
                        animate={{ opacity: 1, x: 0 }}
                        className="flex items-center gap-3 group cursor-pointer"
                    >
                        <div className="relative">
                            <div className="absolute inset-0 bg-primary blur-lg opacity-40 group-hover:opacity-100 transition-opacity" />
                            <img src="/gema_white.svg" alt="GEMA Logo" className="w-10 h-10 relative z-10 drop-shadow-2xl" />
                        </div>
                        <span className="text-2xl font-black tracking-tighter text-slate-900 dark:text-white uppercase flex items-center">
                            GEMA <span className="text-primary italic ml-2">INVENTORY</span>
                        </span>
                    </motion.div>
                    
                    <motion.div 
                        initial={{ opacity: 0, x: 20 }}
                        animate={{ opacity: 1, x: 0 }}
                        className="flex items-center gap-10"
                    >
                        <div className="hidden md:flex items-center gap-8">
                            <a href="#features" className="text-xs font-black uppercase tracking-widest text-slate-500 hover:text-primary transition-colors">Funciones</a>
                            <a href="#app" className="text-xs font-black uppercase tracking-widest text-slate-500 hover:text-primary transition-colors">Móvil</a>
                        </div>
                        <div className="flex items-center gap-4">
                            <Link to="/login" className="px-5 py-2.5 text-xs font-black uppercase tracking-widest text-slate-600 dark:text-slate-400 hover:text-primary transition-colors">
                                Entrar
                            </Link>
                            <Link to="/app" className="relative group">
                                <div className="absolute inset-0 bg-primary blur-md opacity-40 group-hover:opacity-80 transition-opacity" />
                                <div className="relative bg-primary hover:bg-primary/90 text-white px-8 py-3 rounded-2xl text-xs font-black uppercase tracking-widest shadow-xl transition-all hover:-translate-y-1 active:scale-95">
                                    ABRIR APP
                                </div>
                            </Link>
                        </div>
                    </motion.div>
                </div>
            </nav>

            {/* Hero Section */}
            <section className="relative pt-48 pb-20 lg:pt-64 lg:pb-32">
                <div className="max-w-7xl mx-auto px-6 relative z-10 text-center">
                    <motion.div
                        initial={{ opacity: 0, scale: 0.9 }}
                        animate={{ opacity: 1, scale: 1 }}
                        className="inline-flex items-center gap-2 px-6 py-2.5 rounded-full bg-white/5 backdrop-blur-xl border border-white/10 text-white text-[10px] font-black tracking-[0.2em] uppercase mb-10 shadow-2xl"
                    >
                        <Sparkles size={14} className="animate-pulse" /> Revolución en Inventarios
                    </motion.div>
                    
                    <motion.h1 
                        initial={{ opacity: 0, y: 30 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.2, duration: 1, ease: [0.16, 1, 0.3, 1] }}
                        className="text-7xl md:text-[10rem] font-black tracking-tighter mb-10 leading-[0.85] text-slate-900 dark:text-white"
                    >
                        Gestiona <br className="hidden md:block" />
                        <span className="text-transparent bg-clip-text bg-gradient-to-r from-primary via-blue-400 to-indigo-500 drop-shadow-[0_0_30px_rgba(59,130,246,0.3)] saturate-150">
                            sin límites.
                        </span>
                    </motion.h1>
                    
                    <motion.p 
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.4, duration: 1 }}
                        className="text-lg md:text-2xl text-slate-500 dark:text-slate-400 mb-16 max-w-3xl mx-auto font-medium leading-relaxed px-4"
                    >
                        GEMA Inventory es el cerebro digital para tu negocio. 
                        Sincronización en tiempo real, inteligencia operativa y diseño premium.
                    </motion.p>
                    
                    <motion.div 
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.6, duration: 1 }}
                        className="flex flex-col sm:flex-row items-center justify-center gap-6"
                    >
                        <Link to="/app" className="group relative w-full sm:w-auto">
                            <div className="absolute inset-0 bg-white dark:bg-white blur-xl opacity-20 group-hover:opacity-40 transition-opacity" />
                            <div className="relative flex items-center justify-center gap-4 bg-slate-950 dark:bg-white text-white dark:text-slate-950 px-12 py-6 rounded-[2rem] text-sm font-black uppercase tracking-widest shadow-2xl transition-all hover:-translate-y-1.5 hover:shadow-primary/40">
                                EMPEZAR AHORA <ChevronRight size={20} className="group-hover:translate-x-1 transition-transform" />
                            </div>
                        </Link>
                        <a href="#app" className="group w-full sm:w-auto flex items-center justify-center gap-4 bg-white/5 dark:bg-slate-800/20 backdrop-blur-3xl border border-white/10 text-slate-800 dark:text-white px-12 py-6 rounded-[2rem] text-sm font-black uppercase tracking-widest transition-all hover:bg-white/10 dark:hover:bg-slate-800/40 hover:-translate-y-1">
                            <Download size={20} /> APP MÓVIL
                        </a>
                    </motion.div>

                    {/* Preview Dashboard */}
                    <motion.div 
                        style={{ y: y1 }}
                        initial={{ opacity: 0, y: 100 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.8, duration: 1.2 }}
                        className="mt-32 relative group"
                    >
                        <div className="absolute inset-0 bg-gradient-to-t from-[#030712] via-transparent to-transparent z-10 pointer-events-none" />
                        <div className="absolute -inset-4 bg-primary/20 blur-3xl rounded-[3rem] opacity-0 group-hover:opacity-40 transition-opacity duration-1000" />
                        <div className="relative glass-card p-2 rounded-[2.5rem] border-white/10 shadow-2xl overflow-hidden backdrop-blur-3xl">
                            <img 
                                src="https://images.unsplash.com/photo-1460925895917-afdab827c52f?q=80&w=2426&auto=format&fit=crop" 
                                alt="Dashboard GEMA" 
                                className="w-full h-auto rounded-[2.3rem] shadow-2xl grayscale-[20%] group-hover:grayscale-0 transition-all duration-1000"
                            />
                        </div>
                    </motion.div>
                </div>
            </section>

            {/* Features Section */}
            <section id="features" className="py-40 relative overflow-hidden bg-slate-950/20">
                <div className="max-w-7xl mx-auto px-6">
                    <div className="text-center mb-24">
                        <h2 className="text-4xl md:text-5xl font-black mb-6">Potencia tu Negocio</h2>
                        <p className="text-slate-500 max-w-xl mx-auto font-medium">Herramientas diseñadas para escalar tu operación sin complicaciones técnicas.</p>
                    </div>

                    <motion.div 
                        variants={staggerContainer}
                        initial="hidden"
                        whileInView="visible"
                        viewport={{ once: true }}
                        className="grid md:grid-cols-3 gap-10"
                    >
                        {[
                            { icon: Package, title: "Stock Inteligente", desc: "Predicciones de inventario basadas en patrones de venta.", color: "primary" },
                            { icon: TrendingUp, title: "Métricas Reales", desc: "Análisis financiero profundo en tiempo real.", color: "emerald-500" },
                            { icon: Users, title: "Equipo Conectado", desc: "Roles granulares y colaboración instantánea.", color: "indigo-500" }
                        ].map((item, i) => (
                            <motion.div 
                                key={i}
                                variants={fadeInUp} 
                                className="glass-card p-12 group hover:bg-white/5 transition-all duration-500 relative overflow-hidden"
                            >
                                <div className={`absolute top-0 right-0 w-32 h-32 bg-${item.color}/5 rounded-full blur-3xl -mr-16 -mt-16 group-hover:scale-150 transition-transform duration-700`} />
                                <div className={`w-16 h-16 bg-${item.color}/10 rounded-3xl flex items-center justify-center mb-8 text-${item.color} group-hover:scale-110 group-hover:rotate-6 transition-all duration-500`}>
                                    <item.icon size={32} />
                                </div>
                                <h3 className="text-2xl font-black mb-4">{item.title}</h3>
                                <p className="text-slate-500 dark:text-slate-400 leading-relaxed font-medium">
                                    {item.desc}
                                </p>
                            </motion.div>
                        ))}
                    </motion.div>
                </div>
            </section>

            {/* App Promotion Section */}
            <section id="app" className="py-40 relative">
                <div className="max-w-7xl mx-auto px-6">
                    <div className="relative bg-gradient-to-br from-indigo-900 to-blue-900 rounded-[4rem] overflow-hidden shadow-[0_0_100px_rgba(59,130,246,0.2)]">
                        <div className="absolute inset-0 bg-[url('https://www.transparenttextures.com/patterns/carbon-fibre.png')] opacity-20" />
                        <div className="absolute inset-0 bg-gradient-to-r from-primary/40 to-transparent" />
                        
                        <div className="relative z-10 grid lg:grid-cols-2 gap-20 p-16 md:p-32 items-center">
                            <motion.div 
                                initial={{ opacity: 0, x: -50 }}
                                whileInView={{ opacity: 1, x: 0 }}
                                viewport={{ once: true }}
                            >
                                <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-white/10 text-white text-[10px] font-black tracking-widest uppercase mb-8">
                                    <Star size={12} className="fill-yellow-400 text-yellow-400" /> App Exclusiva
                                </div>
                                <h2 className="text-6xl md:text-8xl font-black text-white mb-10 leading-[0.85] tracking-tighter">
                                    Tu negocio, <br/> en tu bolsillo.
                                </h2>
                                <p className="text-white/70 text-xl md:text-2xl mb-14 leading-relaxed font-medium">
                                    Nuestra App para Android redefine la movilidad. Escaneo láser, ventas offline y notificaciones críticas al instante.
                                </p>
                                
                                <div className="flex flex-col sm:flex-row gap-6">
                                    <button className="group flex items-center justify-center gap-4 bg-white text-primary px-10 py-6 rounded-[2rem] font-black shadow-2xl hover:bg-slate-50 transition-all hover:-translate-y-1 active:scale-95">
                                        <Download size={24} /> DESCARGAR APK
                                    </button>
                                    <div className="flex flex-col justify-center">
                                        <div className="flex items-center gap-2 text-white/40 mb-1">
                                            <div className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" />
                                            <span className="text-xs font-black uppercase tracking-widest">Android 8.0+ Compatible</span>
                                        </div>
                                    </div>
                                </div>
                            </motion.div>
                            
                            <motion.div 
                                initial={{ opacity: 0, scale: 0.9 }}
                                whileInView={{ opacity: 1, scale: 1 }}
                                viewport={{ once: true }}
                                className="relative flex justify-center"
                            >
                                <FloatingElement duration={5}>
                                    <div className="w-[320px] h-[640px] bg-slate-950 rounded-[4rem] border-[12px] border-slate-900 shadow-[0_50px_100px_rgba(0,0,0,0.5)] overflow-hidden relative">
                                        <div className="absolute top-0 w-full h-10 bg-slate-900 flex justify-center items-center">
                                            <div className="w-20 h-2 bg-slate-800 rounded-full" />
                                        </div>
                                        <div className="p-8 pt-16 h-full bg-slate-950 flex flex-col items-center justify-center text-center">
                                            <div className="relative mb-10">
                                                <div className="absolute inset-0 bg-primary blur-2xl opacity-40 animate-pulse" />
                                                <div className="w-24 h-24 bg-primary/10 rounded-[2.5rem] flex items-center justify-center text-primary relative z-10">
                                                    <img src="/gema_white.svg" alt="Logo" className="w-12 h-12" />
                                                </div>
                                            </div>
                                            <h4 className="text-white font-black text-2xl mb-4 tracking-tight uppercase">GEMA Mobile</h4>
                                            <p className="text-white/30 text-sm px-4 leading-relaxed">Sincronización híbrida de última generación.</p>
                                        </div>
                                    </div>
                                </FloatingElement>

                                {/* Floating Badges */}
                                <motion.div 
                                    animate={{ y: [0, -20, 0] }}
                                    transition={{ duration: 4, repeat: Infinity }}
                                    className="absolute -right-12 top-32 glass-card p-6 bg-emerald-500/20 border-emerald-500/30 text-emerald-400 flex items-center gap-4 shadow-2xl backdrop-blur-2xl"
                                >
                                    <Zap size={24} />
                                    <span className="font-black text-sm uppercase tracking-widest">Sync en tiempo real</span>
                                </motion.div>
                                <motion.div 
                                    animate={{ y: [0, 20, 0] }}
                                    transition={{ duration: 6, repeat: Infinity, delay: 1 }}
                                    className="absolute -left-16 bottom-32 glass-card p-6 bg-blue-500/20 border-blue-500/30 text-blue-400 flex items-center gap-4 shadow-2xl backdrop-blur-2xl"
                                >
                                    <ShieldCheck size={24} />
                                    <span className="font-black text-sm uppercase tracking-widest">Seguridad de Élite</span>
                                </motion.div>
                            </motion.div>
                        </div>
                    </div>
                </div>
            </section>

            {/* Social Proof / Stats */}
            <section className="py-32 border-y border-white/5 bg-slate-900/10">
                <div className="max-w-7xl mx-auto px-6 grid md:grid-cols-4 gap-16 text-center">
                    {[
                        { val: "+150", label: "Negocios" },
                        { val: "+10k", label: "Movimientos" },
                        { val: "24/7", label: "Soporte" },
                        { val: "Global", label: "Acceso" }
                    ].map((stat, i) => (
                        <motion.div 
                            key={i}
                            initial={{ opacity: 0 }}
                            whileInView={{ opacity: 1 }}
                            transition={{ delay: i * 0.1 }}
                        >
                            <div className="text-5xl font-black text-primary mb-3 tracking-tighter">{stat.val}</div>
                            <div className="text-[10px] font-black text-slate-500 uppercase tracking-[0.3em]">{stat.label}</div>
                        </motion.div>
                    ))}
                </div>
            </section>

            {/* CTA Final */}
            <section className="py-40 text-center relative overflow-hidden">
                <div className="absolute inset-0 bg-primary/5 blur-[120px] rounded-full scale-150 opacity-20" />
                <div className="max-w-4xl mx-auto px-6 relative z-10">
                    <h2 className="text-5xl md:text-7xl font-black mb-10 leading-[0.9] tracking-tighter">¿Listo para llevar tu <br/> negocio al <span className="text-primary italic">siguiente nivel?</span></h2>
                    <Link to="/app" className="inline-flex items-center gap-4 bg-primary text-white px-16 py-8 rounded-[2.5rem] text-xl font-black uppercase tracking-widest shadow-2xl shadow-primary/40 hover:-translate-y-2 transition-all">
                        EMPEZAR GRATIS <ArrowRight size={28} />
                    </Link>
                </div>
            </section>

            {/* Footer */}
            <footer className="py-32 bg-[#030712] text-white border-t border-white/5">
                <div className="max-w-7xl mx-auto px-6">
                    <div className="grid md:grid-cols-4 gap-20 mb-24">
                        <div className="md:col-span-2">
                             <div className="flex items-center gap-3 mb-10">
                                <img src="/gema_white.svg" alt="Logo" className="w-10 h-10" />
                                <span className="text-2xl font-black tracking-tighter uppercase">GEMA <span className="text-primary italic">INVENTORY</span></span>
                            </div>
                            <p className="text-slate-500 max-w-sm mb-10 font-medium leading-relaxed text-lg">
                                La plataforma líder en gestión inteligente de inventarios. 
                                Diseñada para la velocidad, construida para la escala.
                            </p>
                            <div className="flex gap-6">
                                <div className="w-12 h-12 rounded-2xl bg-white/5 border border-white/10 flex items-center justify-center hover:bg-primary/20 hover:text-primary hover:border-primary/30 cursor-pointer transition-all duration-500">
                                    <Globe size={20} />
                                </div>
                            </div>
                        </div>
                        <div>
                            <h4 className="font-black uppercase tracking-[0.2em] text-[10px] mb-8 text-white/30">Plataforma</h4>
                            <ul className="space-y-5 text-sm font-bold text-slate-400">
                                <li><Link to="/app" className="hover:text-primary transition-colors">Web Dashboard</Link></li>
                                <li><a href="#app" className="hover:text-primary transition-colors">Android Application</a></li>
                                <li><Link to="/login" className="hover:text-primary transition-colors">Acceso Usuarios</Link></li>
                            </ul>
                        </div>
                        <div>
                            <h4 className="font-black uppercase tracking-[0.2em] text-[10px] mb-8 text-white/30">Compañía</h4>
                            <ul className="space-y-5 text-sm font-bold text-slate-400">
                                <li><a href="#" className="hover:text-primary transition-colors">Políticas de Privacidad</a></li>
                                <li><a href="#" className="hover:text-primary transition-colors">Términos de Servicio</a></li>
                            </ul>
                        </div>
                    </div>
                    <div className="pt-12 border-t border-white/5 flex flex-col md:flex-row justify-between items-center gap-10">
                        <p className="text-slate-600 text-[10px] font-black uppercase tracking-widest">© 2026 JEDD AI. Todos los derechos reservados.</p>
                        <div className="flex items-center gap-3 text-[10px] font-black text-slate-600 uppercase tracking-widest">
                            Built by experts for <span className="text-white">High Performance</span>
                        </div>
                    </div>
                </div>
            </footer>
        </div>
    );
}
