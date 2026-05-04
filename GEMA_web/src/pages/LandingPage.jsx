import { Link } from 'react-router-dom';
import { useTheme } from '../context/ThemeContext';
import { Package, TrendingUp, Users, Download, ArrowRight, ShieldCheck, Zap } from 'lucide-react';

export default function LandingPage() {
    const { isDarkMode } = useTheme();

    return (
        <div className={`min-h-screen font-sans ${isDarkMode ? 'dark bg-[#0f172a] text-white' : 'bg-gray-50 text-gray-900'} transition-colors duration-300`}>
            {/* Navbar */}
            <nav className="fixed w-full z-50 top-0 px-6 py-4 backdrop-blur-md bg-white/70 dark:bg-slate-900/70 border-b border-gray-200 dark:border-slate-800">
                <div className="max-w-7xl mx-auto flex justify-between items-center">
                    <div className="flex items-center gap-3">
                        <img src={isDarkMode ? "/gema_white.svg" : "/src/assets/ic_logo_cuadrado_bb.png"} alt="GEMA" className="w-10 h-10 object-contain" />
                        <span className="text-xl font-bold tracking-tight text-primary dark:text-white">GEMA Inventory</span>
                    </div>
                    <div className="flex items-center gap-4">
                        <Link to="/login" className="text-sm font-semibold hover:text-accent dark:hover:text-blue-400 transition-colors">
                            Iniciar Sesión
                        </Link>
                        <Link to="/app" className="bg-primary hover:bg-primary-dark dark:bg-blue-600 dark:hover:bg-blue-700 text-white px-5 py-2.5 rounded-full text-sm font-bold shadow-lg transition-transform hover:scale-105">
                            Probar en Web
                        </Link>
                    </div>
                </div>
            </nav>

            {/* Hero Section */}
            <section className="relative pt-32 pb-20 lg:pt-48 lg:pb-32 overflow-hidden">
                <div className="absolute inset-0 z-0">
                    <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-accent/20 dark:bg-blue-500/20 rounded-full blur-3xl opacity-50 animate-pulse"></div>
                    <div className="absolute bottom-1/4 right-1/4 w-[30rem] h-[30rem] bg-primary/10 dark:bg-indigo-500/10 rounded-full blur-3xl opacity-50"></div>
                </div>
                
                <div className="max-w-7xl mx-auto px-6 relative z-10 text-center">
                    <h1 className="text-5xl md:text-7xl font-extrabold tracking-tight mb-8 leading-tight">
                        Tu inventario, <br className="hidden md:block" />
                        <span className="text-transparent bg-clip-text bg-gradient-to-r from-primary to-accent dark:from-blue-400 dark:to-indigo-400">
                            inteligente y en tiempo real
                        </span>
                    </h1>
                    <p className="text-lg md:text-xl text-gray-600 dark:text-gray-300 mb-12 max-w-3xl mx-auto leading-relaxed">
                        GEMA Inventory es la solución completa para gestionar tus productos, ventas y clientes desde cualquier lugar. Disponible en la palma de tu mano o desde tu navegador.
                    </p>
                    
                    <div className="flex flex-col sm:flex-row items-center justify-center gap-4 sm:gap-6">
                        <Link to="/app" className="w-full sm:w-auto flex items-center justify-center gap-2 bg-primary dark:bg-blue-600 hover:bg-primary-dark dark:hover:bg-blue-700 text-white px-8 py-4 rounded-full text-lg font-bold shadow-xl shadow-primary/20 dark:shadow-blue-900/20 transition-all hover:-translate-y-1">
                            Probar en Web <ArrowRight size={20} />
                        </Link>
                        <a href="#descarga" className="w-full sm:w-auto flex items-center justify-center gap-2 bg-white dark:bg-slate-800 border-2 border-gray-200 dark:border-slate-700 hover:border-gray-300 dark:hover:border-slate-600 text-gray-900 dark:text-white px-8 py-4 rounded-full text-lg font-bold shadow-md transition-all hover:-translate-y-1">
                            Descargar App <Download size={20} />
                        </a>
                    </div>
                </div>
            </section>

            {/* Features Section */}
            <section className="py-20 bg-white/50 dark:bg-slate-900/50 backdrop-blur-sm border-y border-gray-100 dark:border-slate-800">
                <div className="max-w-7xl mx-auto px-6">
                    <div className="text-center mb-16">
                        <h2 className="text-3xl md:text-4xl font-bold mb-4">Todo lo que necesitas para crecer</h2>
                        <p className="text-gray-500 dark:text-gray-400 max-w-2xl mx-auto">Diseñado para pequeñas y medianas empresas que buscan eficiencia y control absoluto.</p>
                    </div>

                    <div className="grid md:grid-cols-3 gap-8">
                        {/* Feature 1 */}
                        <div className="bg-white dark:bg-slate-800 p-8 rounded-3xl shadow-sm border border-gray-100 dark:border-slate-700 hover:shadow-xl transition-shadow">
                            <div className="w-14 h-14 bg-blue-100 dark:bg-blue-900/50 rounded-2xl flex items-center justify-center mb-6 text-blue-600 dark:text-blue-400">
                                <Package size={28} />
                            </div>
                            <h3 className="text-xl font-bold mb-3">Control de Stock</h3>
                            <p className="text-gray-600 dark:text-gray-400">Mantén tu inventario actualizado al instante. Recibe alertas de stock bajo y gestiona múltiples almacenes.</p>
                        </div>
                        {/* Feature 2 */}
                        <div className="bg-white dark:bg-slate-800 p-8 rounded-3xl shadow-sm border border-gray-100 dark:border-slate-700 hover:shadow-xl transition-shadow">
                            <div className="w-14 h-14 bg-green-100 dark:bg-green-900/50 rounded-2xl flex items-center justify-center mb-6 text-green-600 dark:text-green-400">
                                <TrendingUp size={28} />
                            </div>
                            <h3 className="text-xl font-bold mb-3">Finanzas Claras</h3>
                            <p className="text-gray-600 dark:text-gray-400">Visualiza tus ingresos, gastos y márgenes de ganancia con gráficos detallados en tiempo real.</p>
                        </div>
                        {/* Feature 3 */}
                        <div className="bg-white dark:bg-slate-800 p-8 rounded-3xl shadow-sm border border-gray-100 dark:border-slate-700 hover:shadow-xl transition-shadow">
                            <div className="w-14 h-14 bg-purple-100 dark:bg-purple-900/50 rounded-2xl flex items-center justify-center mb-6 text-purple-600 dark:text-purple-400">
                                <Users size={28} />
                            </div>
                            <h3 className="text-xl font-bold mb-3">Gestión de Clientes</h3>
                            <p className="text-gray-600 dark:text-gray-400">Registra tus clientes, historial de compras y preferencias para ofrecer un servicio personalizado.</p>
                        </div>
                    </div>
                </div>
            </section>

            {/* Platform Choice Section */}
            <section id="descarga" className="py-24 relative">
                <div className="max-w-7xl mx-auto px-6">
                    <div className="bg-primary dark:bg-slate-800 rounded-[3rem] overflow-hidden shadow-2xl relative">
                        <div className="absolute top-0 right-0 w-[40rem] h-[40rem] bg-white/5 rounded-full blur-3xl -mr-48 -mt-48"></div>
                        
                        <div className="relative z-10 grid lg:grid-cols-2 gap-12 p-12 md:p-20 items-center">
                            <div className="text-white">
                                <h2 className="text-4xl md:text-5xl font-bold mb-6 leading-tight">Lleva tu negocio <br/> a todas partes</h2>
                                <p className="text-primary-100 dark:text-gray-300 text-lg mb-8 leading-relaxed">
                                    Disfruta de la experiencia nativa en tu dispositivo Android o accede desde cualquier computadora con nuestra aplicación web responsiva. Todos tus datos sincronizados al instante.
                                </p>
                                
                                <div className="flex flex-col sm:flex-row gap-4">
                                    <button className="flex items-center justify-center gap-3 bg-white text-primary dark:bg-blue-600 dark:text-white px-8 py-4 rounded-full font-bold shadow-lg hover:bg-gray-50 transition-colors">
                                        <Download size={24} />
                                        Descargar APK
                                    </button>
                                </div>
                                <p className="text-white/60 text-sm mt-4">* Requiere Android 8.0 o superior</p>
                            </div>
                            
                            <div className="relative">
                                {/* Decoraciones */}
                                <div className="absolute inset-0 bg-gradient-to-tr from-white/10 to-transparent rounded-3xl transform rotate-3"></div>
                                <div className="relative bg-white/10 backdrop-blur-md border border-white/20 p-8 rounded-3xl shadow-2xl">
                                    <div className="flex items-center gap-4 mb-6">
                                        <ShieldCheck className="text-green-400" size={32} />
                                        <div>
                                            <h4 className="text-white font-bold text-lg">Seguridad Total</h4>
                                            <p className="text-white/70 text-sm">Tus datos en la nube</p>
                                        </div>
                                    </div>
                                    <div className="flex items-center gap-4 mb-6">
                                        <Zap className="text-yellow-400" size={32} />
                                        <div>
                                            <h4 className="text-white font-bold text-lg">Sincronización Rápida</h4>
                                            <p className="text-white/70 text-sm">Actualizaciones en vivo</p>
                                        </div>
                                    </div>
                                    <div className="mt-8 pt-8 border-t border-white/10">
                                        <Link to="/app" className="flex items-center justify-center gap-2 w-full bg-accent dark:bg-indigo-600 hover:bg-accent-light dark:hover:bg-indigo-500 text-white px-6 py-4 rounded-xl font-bold transition-colors">
                                            Abrir Web App <ArrowRight size={20} />
                                        </Link>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* Footer */}
            <footer className="bg-white dark:bg-[#0b1121] py-8 border-t border-gray-200 dark:border-slate-800">
                <div className="max-w-7xl mx-auto px-6 text-center">
                    <p className="text-gray-500 dark:text-gray-400 font-medium">© 2026 GEMA Inventory. Todos los derechos reservados.</p>
                </div>
            </footer>
        </div>
    );
}
