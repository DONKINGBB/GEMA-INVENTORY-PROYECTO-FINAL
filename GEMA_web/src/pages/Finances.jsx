import { useState, useEffect } from 'react';
import { DollarSign, TrendingUp, TrendingDown, FileText, Loader2, Calendar, ArrowUpRight, ArrowDownRight, CreditCard, Activity, BarChart3, PieChart as PieIcon, Download } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, AreaChart, Area, BarChart, Bar } from 'recharts';
import { jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';
import { useTheme } from '../context/ThemeContext';
import { motion, AnimatePresence } from 'framer-motion';

export default function Finances() {
    const { user } = useAuth();
    const { isDarkMode } = useTheme();
    const [finances, setFinances] = useState([]);
    const [chartData, setChartData] = useState([]);
    const [totals, setTotals] = useState({ ingresos: 0, egresos: 0, neto: 0 });
    const [loading, setLoading] = useState(true);

    const fetchFinances = async () => {
        if (!user?.id) return;
        try {
            setLoading(true);
            const [ordersRes, purchasesRes] = await Promise.all([
                   api.get(`/pedidos?userId=${user.id}`).catch(() => ({ data: [] })),
                   api.get(`/compra?userId=${user.id}`).catch(() => ({ data: [] }))
            ]);
            
            const orders = ordersRes.data || [];
            const purchases = purchasesRes.data || [];

            let totalIngresos = 0;
            let totalEgresos = 0;
            const ingresosPorMes = new Array(13).fill(0);
            const gastosPorMes = new Array(13).fill(0);

            const safeGetMonth = (dateVal) => {
                if (!dateVal) return new Date().getMonth() + 1;
                if (typeof dateVal === 'string' && dateVal.includes('-')) {
                    const parts = dateVal.split('-');
                    if (parts.length >= 2) {
                        const m = parseInt(parts[1], 10);
                        if (!isNaN(m) && m >= 1 && m <= 12) return m;
                    }
                }
                const d = new Date(dateVal);
                return isNaN(d.getTime()) ? new Date().getMonth() + 1 : d.getMonth() + 1;
            };
            
            const allItems = [];

            orders.forEach(o => {
                if (o.idEstado === 2 && o.total) {
                     totalIngresos += o.total;
                     const month = safeGetMonth(o.fechaPedido);
                     if (month >= 1 && month <= 12) ingresosPorMes[month] += o.total;
                     
                     allItems.push({
                         id: o.id,
                         tipo: 'INGRESO',
                         fecha: o.fechaPedido,
                         fuente: `Venta - ${o.nombre || 'Pedido'}`,
                         referencia: o.id.toString().slice(-5),
                         monto: o.total,
                         rawDate: new Date(o.fechaPedido)
                     });
                }
            });

            purchases.forEach(p => {
                if (p.total) {
                     totalEgresos += p.total;
                     const month = safeGetMonth(p.fechaCompra);
                     if (month >= 1 && month <= 12) gastosPorMes[month] += p.total;

                     allItems.push({
                         id: p.id,
                         tipo: 'GASTO',
                         fecha: p.fechaCompra,
                         fuente: `Compra - ${p.nombreProveedor || 'Stock'}`,
                         referencia: (p.id || '').toString().slice(-5),
                         monto: -p.total,
                         rawDate: new Date(p.fechaCompra)
                     });
                }
            });

            setTotals({
                ingresos: totalIngresos,
                egresos: totalEgresos,
                neto: totalIngresos - totalEgresos
            });

            const monthNames = ["", "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"];
            const newChartData = [];
            for (let i = 1; i <= 12; i++) {
                newChartData.push({
                    name: monthNames[i],
                    Ingresos: ingresosPorMes[i],
                    Gastos: gastosPorMes[i],
                    Neto: ingresosPorMes[i] - gastosPorMes[i]
                });
            }
            setChartData(newChartData);

            allItems.sort((a, b) => b.rawDate - a.rawDate);
            setFinances(allItems);

        } catch (error) {
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchFinances();
    }, [user]);

    const generarPDF = () => {
        const doc = new jsPDF();
        const pageWidth = doc.internal.pageSize.width;
        
        // --- Header Configuration ---
        doc.setFillColor(59, 130, 246); // Primary Blue
        doc.rect(0, 0, pageWidth, 40, 'F');
        
        doc.setFontSize(24);
        doc.setTextColor(255, 255, 255);
        doc.setFont('helvetica', 'bold');
        doc.text('GEMA INVENTORY', 14, 25);
        
        doc.setFontSize(12);
        doc.setFont('helvetica', 'normal');
        doc.text('REPORTE FINANCIERO ESTRATÉGICO', 14, 33);
        
        // --- User and Date Info ---
        doc.setTextColor(50, 50, 50);
        doc.setFontSize(10);
        doc.setFont('helvetica', 'bold');
        doc.text('INFORMACIÓN DEL EMISOR', 14, 50);
        
        doc.setFont('helvetica', 'normal');
        doc.setTextColor(100, 100, 100);
        doc.text(`Responsable: ${user?.nombre || user?.correo || 'Usuario GEMA'}`, 14, 56);
        doc.text(`Email: ${user?.correo || 'N/A'}`, 14, 61);
        doc.text(`Fecha de Emisión: ${new Date().toLocaleString('es-MX', { dateStyle: 'long', timeStyle: 'short' })}`, 14, 66);

        // --- Executive Summary Box ---
        doc.setFillColor(248, 250, 252);
        doc.roundedRect(140, 48, 56, 25, 3, 3, 'F');
        
        doc.setFontSize(8);
        doc.setTextColor(100, 100, 100);
        doc.text('BALANCE NETO ACTUAL', 145, 55);
        
        doc.setFontSize(14);
        const balanceColor = totals.neto >= 0 ? [16, 185, 129] : [244, 63, 94];
        doc.setTextColor(balanceColor[0], balanceColor[1], balanceColor[2]);
        doc.setFont('helvetica', 'bold');
        doc.text(`$${totals.neto.toLocaleString('en-US', { minimumFractionDigits: 2 })}`, 145, 65);

        // --- Financial Highlights ---
        doc.setDrawColor(226, 232, 240);
        doc.setLineWidth(0.5);
        doc.line(14, 75, 196, 75);

        doc.setFontSize(11);
        doc.setTextColor(40, 40, 40);
        doc.setFont('helvetica', 'bold');
        doc.text('Métricas de Rendimiento:', 14, 85);
        
        doc.setFont('helvetica', 'normal');
        doc.text(`Total Ingresos Operativos:`, 14, 93);
        doc.setTextColor(16, 185, 129);
        doc.text(`$${totals.ingresos.toLocaleString('en-US', { minimumFractionDigits: 2 })}`, 70, 93);
        
        doc.setTextColor(40, 40, 40);
        doc.text(`Total Gastos de Operación:`, 14, 100);
        doc.setTextColor(244, 63, 94);
        doc.text(`$${totals.egresos.toLocaleString('en-US', { minimumFractionDigits: 2 })}`, 70, 100);

        // --- Data Table ---
        const tableColumn = ["FECHA", "TIPO", "CONCEPTO / ENTIDAD", "REFERENCIA", "MONTO"];
        const tableRows = finances.map(f => [
            new Date(f.fecha).toLocaleDateString('es-MX'),
            f.tipo,
            f.fuente,
            f.referencia ? `REF-${f.referencia}` : '-',
            { 
                content: `$${Math.abs(f.monto).toLocaleString('en-US', { minimumFractionDigits: 2 })}`,
                styles: { textColor: f.monto >= 0 ? [16, 185, 129] : [244, 63, 94], fontStyle: 'bold' }
            }
        ]);

        autoTable(doc, {
            startY: 110,
            head: [tableColumn],
            body: tableRows,
            theme: 'striped',
            headStyles: { 
                fillColor: [30, 41, 59], 
                textColor: 255, 
                fontSize: 10, 
                fontStyle: 'bold',
                halign: 'center',
                cellPadding: 4
            },
            bodyStyles: { 
                fontSize: 9, 
                cellPadding: 4,
                textColor: [50, 50, 50]
            },
            alternateRowStyles: { 
                fillColor: [248, 250, 252] 
            },
            columnStyles: {
                0: { halign: 'center' },
                1: { halign: 'center' },
                4: { halign: 'right' }
            },
            margin: { top: 20 },
            didDrawPage: (data) => {
                // Footer
                const str = `Página ${doc.internal.getNumberOfPages()}`;
                doc.setFontSize(8);
                doc.setTextColor(150);
                doc.text(str, pageWidth - 25, doc.internal.pageSize.height - 10);
                doc.text('GEMA Inventory System - Reporte Confidencial', 14, doc.internal.pageSize.height - 10);
            }
        });

        doc.save(`GEMA_Reporte_Financiero_${new Date().toISOString().split('T')[0]}.pdf`);
    };

    const containerVariants = {
        hidden: { opacity: 0 },
        visible: {
            opacity: 1,
            transition: { staggerChildren: 0.1 }
        }
    };

    const itemVariants = {
        hidden: { y: 20, opacity: 0 },
        visible: { y: 0, opacity: 1 }
    };

    return (
        <motion.div 
            initial="hidden"
            animate="visible"
            variants={containerVariants}
            className="max-w-7xl mx-auto space-y-8 pb-20 sm:pb-0"
        >
            {/* Ultra-Premium Header */}
            <motion.div variants={itemVariants} className="relative overflow-hidden bg-white dark:bg-slate-900/40 border border-slate-200 dark:border-white/5 p-8 sm:p-12 rounded-[2.5rem] shadow-sm dark:shadow-none">
                <div className="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-8">
                    <div className="space-y-4">
                        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-primary/10 border border-primary/20 text-primary text-[10px] font-black uppercase tracking-widest">
                            <Activity size={12} />
                            <span>Análisis de Patrimonio</span>
                        </div>
                        <h1 className="text-5xl sm:text-7xl font-black text-slate-900 dark:text-white tracking-tighter leading-none">
                            Dashboard <span className="text-primary">Financiero</span>
                        </h1>
                        <p className="text-slate-500 dark:text-slate-400 text-lg max-w-xl">
                            Visualiza el rendimiento económico de tu negocio, flujo de caja y balances operativos en tiempo real.
                        </p>
                    </div>
                    <button
                        onClick={generarPDF}
                        className="group bg-primary hover:bg-primary/90 text-white px-8 py-5 rounded-[2rem] flex items-center gap-3 transition-all shadow-2xl shadow-primary/40 font-black text-lg active:scale-95"
                    >
                        <Download size={24} className="group-hover:translate-y-1 transition-transform" />
                        <span>Exportar Informe</span>
                    </button>
                </div>
                
                {/* Background Decoration */}
                <div className="absolute top-0 right-0 w-1/3 h-full bg-gradient-to-l from-primary/10 to-transparent pointer-events-none" />
                <div className="absolute -bottom-24 -right-24 w-64 h-64 bg-primary/20 blur-[120px] rounded-full pointer-events-none" />
            </motion.div>

            {/* Premium Stats Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                <motion.div variants={itemVariants} className="bg-white dark:bg-slate-900/40 border border-slate-200 dark:border-emerald-500/10 p-8 group relative overflow-hidden rounded-[2.5rem] shadow-sm dark:shadow-none">
                    <div className="relative z-10 flex items-center justify-between mb-8">
                        <div className="p-4 bg-emerald-500/10 text-emerald-500 rounded-[1.5rem] group-hover:scale-110 transition-transform duration-500">
                            <TrendingUp size={32} />
                        </div>
                        <div className="text-right">
                            <span className="text-[10px] font-black text-emerald-500 bg-emerald-500/10 px-3 py-1 rounded-full uppercase tracking-widest">Entradas</span>
                        </div>
                    </div>
                    <p className="text-slate-500 dark:text-slate-400 font-bold uppercase tracking-[0.2em] text-[10px] mb-2">Ingresos Operativos</p>
                    <h3 className="text-4xl font-black text-slate-900 dark:text-white tracking-tighter">
                        <span className="text-emerald-500 text-2xl mr-1">$</span>
                        {totals.ingresos.toLocaleString('en-US', { minimumFractionDigits: 2 })}
                    </h3>
                    <div className="absolute -bottom-4 -right-4 w-32 h-32 bg-emerald-500/5 blur-[40px] rounded-full" />
                </motion.div>

                <motion.div variants={itemVariants} className="bg-white dark:bg-slate-900/40 border border-slate-200 dark:border-rose-500/10 p-8 group relative overflow-hidden rounded-[2.5rem] shadow-sm dark:shadow-none">
                    <div className="relative z-10 flex items-center justify-between mb-8">
                        <div className="p-4 bg-rose-500/10 text-rose-500 rounded-[1.5rem] group-hover:scale-110 transition-transform duration-500">
                            <TrendingDown size={32} />
                        </div>
                        <div className="text-right">
                            <span className="text-[10px] font-black text-rose-500 bg-rose-500/10 px-3 py-1 rounded-full uppercase tracking-widest">Salidas</span>
                        </div>
                    </div>
                    <p className="text-slate-500 dark:text-slate-400 font-bold uppercase tracking-[0.2em] text-[10px] mb-2">Gastos de Operación</p>
                    <h3 className="text-4xl font-black text-slate-900 dark:text-white tracking-tighter">
                        <span className="text-rose-500 text-2xl mr-1">$</span>
                        {totals.egresos.toLocaleString('en-US', { minimumFractionDigits: 2 })}
                    </h3>
                    <div className="absolute -bottom-4 -right-4 w-32 h-32 bg-rose-500/5 blur-[40px] rounded-full" />
                </motion.div>

                <motion.div variants={itemVariants} className="bg-white dark:bg-slate-900/40 border border-slate-200 dark:border-primary/20 p-8 bg-gradient-to-br from-primary/5 dark:from-primary/20 to-transparent group relative overflow-hidden rounded-[2.5rem] shadow-sm dark:shadow-none">
                    <div className="relative z-10 flex items-center justify-between mb-8">
                        <div className="p-4 bg-primary/20 text-primary rounded-[1.5rem] group-hover:scale-110 transition-transform duration-500">
                            <DollarSign size={32} />
                        </div>
                        <div className="text-right">
                            <span className="text-[10px] font-black text-primary bg-primary/10 px-3 py-1 rounded-full uppercase tracking-widest">Patrimonio</span>
                        </div>
                    </div>
                    <p className="text-slate-500 dark:text-slate-200 font-bold uppercase tracking-[0.2em] text-[10px] mb-2">Balance Neto</p>
                    <h3 className="text-4xl font-black text-slate-900 dark:text-white tracking-tighter">
                        <span className="text-primary text-2xl mr-1">$</span>
                        {totals.neto.toLocaleString('en-US', { minimumFractionDigits: 2 })}
                    </h3>
                    <div className="absolute -bottom-4 -right-4 w-32 h-32 bg-primary/20 blur-[40px] rounded-full" />
                </motion.div>
            </div>

            {/* Premium Chart Section */}
            <motion.div variants={itemVariants} className="bg-white dark:bg-slate-900/40 border border-slate-200 dark:border-white/5 p-10 space-y-10 relative overflow-hidden rounded-[2.5rem] shadow-sm dark:shadow-none">
                 <div className="relative z-10 flex flex-col sm:flex-row items-center justify-between gap-6">
                    <div>
                        <h3 className="font-black text-3xl text-slate-900 dark:text-white tracking-tighter flex items-center gap-3">
                            <BarChart3 size={28} className="text-primary" />
                            Rendimiento Anual
                        </h3>
                        <p className="text-slate-500 text-sm mt-1">Comparativa mensual de flujo de caja</p>
                    </div>
                    <div className="flex items-center gap-8 text-[10px] font-black uppercase tracking-[0.2em] text-slate-500">
                        <div className="flex items-center gap-2"><div className="w-3 h-3 rounded-full bg-emerald-500 shadow-lg shadow-emerald-500/50" /> Ingresos</div>
                        <div className="flex items-center gap-2"><div className="w-3 h-3 rounded-full bg-rose-500 shadow-lg shadow-rose-500/50" /> Gastos</div>
                    </div>
                 </div>
                 
                 <div className="h-[400px] w-full relative z-10">
                     {loading ? (
                          <div className="h-full flex flex-col items-center justify-center space-y-6">
                              <Loader2 className="animate-spin text-primary" size={64} />
                              <p className="text-slate-500 font-black tracking-widest text-xs uppercase animate-pulse">Procesando Inteligencia Financiera...</p>
                          </div>
                     ) : (
                           <ResponsiveContainer width="100%" height="100%">
                               <AreaChart data={chartData} margin={{ top: 20, right: 20, bottom: 0, left: 0 }}>
                                   <defs>
                                       <linearGradient id="colorIngresos" x1="0" y1="0" x2="0" y2="1">
                                           <stop offset="5%" stopColor="#10b981" stopOpacity={0.4}/>
                                           <stop offset="95%" stopColor="#10b981" stopOpacity={0}/>
                                       </linearGradient>
                                       <linearGradient id="colorGastos" x1="0" y1="0" x2="0" y2="1">
                                           <stop offset="5%" stopColor="#f43f5e" stopOpacity={0.4}/>
                                           <stop offset="95%" stopColor="#f43f5e" stopOpacity={0}/>
                                       </linearGradient>
                                   </defs>
                                   <CartesianGrid strokeDasharray="3 3" stroke={isDarkMode ? "#1e293b" : "#e2e8f0"} vertical={false} opacity={0.3} />
                                   <XAxis 
                                        dataKey="name" 
                                        stroke="#475569" 
                                        fontSize={10} 
                                        tickMargin={15} 
                                        axisLine={false} 
                                        tickLine={false} 
                                        fontFamily="Inter"
                                        fontWeight="900"
                                    />
                                   <YAxis 
                                        stroke="#475569" 
                                        fontSize={10} 
                                        axisLine={false} 
                                        tickLine={false} 
                                        tickFormatter={(value) => `$${value}`} 
                                        fontFamily="Inter"
                                        fontWeight="900"
                                    />
                                   <Tooltip 
                                       cursor={{ stroke: 'rgba(0,0,0,0.05)', strokeWidth: 2 }}
                                       contentStyle={{ 
                                           backgroundColor: isDarkMode ? 'rgba(15, 23, 42, 0.95)' : 'rgba(255, 255, 255, 0.95)', 
                                           borderColor: isDarkMode ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.1)', 
                                           borderRadius: '24px', 
                                           boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.5)',
                                           backdropFilter: 'blur(16px)',
                                           border: '1px solid rgba(255,255,255,0.1)',
                                           padding: '20px'
                                       }}
                                       itemStyle={{ padding: '4px 0', fontSize: '12px', fontWeight: '900', textTransform: 'uppercase', color: isDarkMode ? '#f8fafc' : '#0f172a' }}
                                       labelStyle={{ color: '#64748b', marginBottom: '8px', fontSize: '10px', fontWeight: '900', textTransform: 'uppercase', letterSpacing: '0.1em' }}
                                       formatter={(value) => [`$${value.toLocaleString()}`, undefined]}
                                   />
                                   <Area type="monotone" dataKey="Ingresos" stroke="#10b981" strokeWidth={4} fillOpacity={1} fill="url(#colorIngresos)" animationDuration={2000} />
                                   <Area type="monotone" dataKey="Gastos" stroke="#f43f5e" strokeWidth={4} fillOpacity={1} fill="url(#colorGastos)" animationDuration={2000} />
                               </AreaChart>
                           </ResponsiveContainer>
                     )}
                 </div>
                 
                 {/* Decorative background logo */}
                 <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 opacity-[0.02] pointer-events-none">
                    <PieIcon size={400} />
                 </div>
            </motion.div>

            {/* Premium Transactions Table */}
            <motion.div variants={itemVariants} className="bg-white dark:bg-slate-900/40 border border-slate-200 dark:border-white/5 overflow-hidden rounded-[2.5rem] shadow-sm dark:shadow-none">
                <div className="px-10 py-8 border-b border-slate-100 dark:border-white/5 bg-slate-50/50 dark:bg-white/5 flex flex-col sm:flex-row items-center justify-between gap-6">
                    <div className="flex items-center gap-4">
                        <div className="p-3 bg-primary/10 text-primary rounded-2xl">
                            <CreditCard size={24} />
                        </div>
                        <div>
                            <h3 className="font-black text-2xl text-slate-900 dark:text-white tracking-tighter uppercase">Historial Maestro</h3>
                            <p className="text-slate-500 text-xs font-bold uppercase tracking-widest mt-0.5">Últimos movimientos verificados</p>
                        </div>
                    </div>
                    <div className="px-4 py-2 bg-black/5 dark:bg-slate-900 border border-black/5 dark:border-white/10 rounded-full text-[10px] font-black text-slate-500 uppercase tracking-widest">
                        Total {finances.length} Registros
                    </div>
                </div>
                
                {loading ? (
                    <div className="p-32 flex flex-col items-center justify-center space-y-6">
                        <div className="w-16 h-16 border-4 border-primary/20 border-t-primary rounded-full animate-spin"></div>
                        <p className="text-slate-600 font-black tracking-widest text-[10px] uppercase animate-pulse">Sincronizando Ledger...</p>
                    </div>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="w-full text-left">
                            <thead>
                                <tr className="text-slate-400 dark:text-slate-500 uppercase tracking-[0.2em] text-[10px] font-black border-b border-slate-100 dark:border-white/5 bg-slate-50 dark:bg-slate-950/50">
                                    <th className="px-10 py-6">Fecha Valor</th>
                                    <th className="px-10 py-6">Concepto / Entidad</th>
                                    <th className="px-10 py-6">Referencia</th>
                                    <th className="px-10 py-6 text-right">Impacto Neto</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 dark:divide-white/5">
                                <AnimatePresence>
                                    {finances.map((f, idx) => (
                                        <motion.tr 
                                            key={f.id} 
                                            initial={{ opacity: 0 }}
                                            animate={{ opacity: 1 }}
                                            transition={{ delay: idx * 0.02 }}
                                            className="hover:bg-slate-50 dark:hover:bg-white/[0.02] transition-all group"
                                        >
                                            <td className="px-10 py-8">
                                                <div className="flex items-center gap-3">
                                                    <div className="p-2 bg-black/5 dark:bg-slate-900 rounded-xl text-slate-400 dark:text-slate-600 group-hover:text-primary transition-colors">
                                                        <Calendar size={16} />
                                                    </div>
                                                    <span className="text-slate-600 dark:text-slate-400 font-black text-xs tracking-widest">
                                                        {new Date(f.fecha).toLocaleDateString('es-MX', { day: '2-digit', month: 'short', year: 'numeric' })}
                                                    </span>
                                                </div>
                                            </td>
                                            <td className="px-10 py-8">
                                                <div className="font-black text-slate-900 dark:text-white text-lg group-hover:text-primary transition-colors tracking-tight">
                                                    {f.fuente}
                                                </div>
                                                <div className="flex items-center gap-2 mt-1">
                                                    <span className={`w-2 h-2 rounded-full ${f.tipo === 'INGRESO' ? 'bg-emerald-500' : 'bg-rose-500'}`} />
                                                    <span className="text-[9px] text-slate-500 uppercase font-black tracking-[0.1em]">
                                                        {f.tipo === 'INGRESO' ? 'Entrada de Capital' : 'Egresos de Activos'}
                                                    </span>
                                                </div>
                                            </td>
                                            <td className="px-10 py-8">
                                                <span className="px-4 py-1.5 bg-black/5 dark:bg-slate-900 border border-black/5 dark:border-white/5 rounded-full text-[10px] font-black font-mono text-slate-500 group-hover:text-primary transition-colors tracking-widest">
                                                    REF-{f.referencia || 'N/A'}
                                                </span>
                                            </td>
                                            <td className="px-10 py-8 text-right">
                                                <div className={`text-2xl font-black flex items-center justify-end gap-2 tracking-tighter ${f.monto >= 0 ? 'text-emerald-500' : 'text-rose-500'}`}>
                                                    {f.monto >= 0 ? <ArrowUpRight size={20} /> : <ArrowDownRight size={20} />}
                                                    ${Math.abs(f.monto).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                                                </div>
                                            </td>
                                        </motion.tr>
                                    ))}
                                </AnimatePresence>
                                
                                {finances.length === 0 && (
                                    <tr>
                                        <td colSpan="4" className="py-32 text-center">
                                            <div className="flex flex-col items-center space-y-6 opacity-20 group">
                                                <div className="w-24 h-24 bg-slate-100 dark:bg-white/5 rounded-[2.5rem] flex items-center justify-center border border-slate-200 dark:border-white/5 group-hover:scale-110 transition-transform duration-700">
                                                    <CreditCard size={48} className="text-slate-900 dark:text-white" />
                                                </div>
                                                <p className="text-xl font-black uppercase tracking-[0.3em] text-slate-900 dark:text-white">Cero Actividad</p>
                                            </div>
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                )}
            </motion.div>
        </motion.div>
    );
}
