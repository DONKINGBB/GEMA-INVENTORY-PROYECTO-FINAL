import { useState, useEffect } from 'react';
import { DollarSign, TrendingUp, TrendingDown, FileText, Loader } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';
import { useTheme } from '../context/ThemeContext';

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
                // Estado 2 = Completado/Entregado en la lógica Android
                if (o.idEstado === 2 && o.total) {
                     totalIngresos += o.total;
                     const date = new Date(o.fechaPedido);
                     const month = safeGetMonth(o.fechaPedido);
                     if (month >= 1 && month <= 12) ingresosPorMes[month] += o.total;
                     
                     allItems.push({
                         id: o.id,
                         tipo: 'INGRESO',
                         fecha: o.fechaPedido,
                         fuente: `Venta - ${o.nombre || 'Pedido'}`,
                         referencia: o.id.toString().slice(-5),
                         monto: o.total,
                         rawDate: date
                     });
                }
            });

            purchases.forEach(p => {
                if (p.total) {
                     totalEgresos += p.total;
                     const date = new Date(p.fechaCompra);
                     const month = safeGetMonth(p.fechaCompra);
                     if (month >= 1 && month <= 12) gastosPorMes[month] += p.total;

                     allItems.push({
                         id: p.id,
                         tipo: 'GASTO',
                         fecha: p.fechaCompra,
                         fuente: `Compra - ${p.nombreProveedor || 'Stock'}`,
                         referencia: (p.id || '').toString().slice(-5),
                         monto: -p.total,
                         rawDate: date
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
                    Gastos: gastosPorMes[i]
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
        
        doc.setFontSize(18);
        doc.setTextColor(40, 40, 40);
        doc.text('Reporte Financiero GEMA', 14, 22);
        
        doc.setFontSize(11);
        doc.setTextColor(100);
        doc.text(`Generado el: ${new Date().toLocaleDateString()}`, 14, 30);
        
        doc.setFontSize(12);
        doc.setTextColor(40);
        doc.text(`Balance Neto: $${totals.neto.toLocaleString('en-US', { minimumFractionDigits: 2 })}`, 14, 40);
        doc.text(`Total Ingresos: $${totals.ingresos.toLocaleString('en-US', { minimumFractionDigits: 2 })}`, 14, 46);
        doc.text(`Total Gastos: $${totals.egresos.toLocaleString('en-US', { minimumFractionDigits: 2 })}`, 14, 52);

        const tableColumn = ["FECHA", "TIPO", "CONCEPTO", "REFERENCIA", "MONTO"];
        const tableRows = [];

        finances.forEach(f => {
            const ticketData = [
                new Date(f.fecha).toLocaleDateString(),
                f.tipo,
                f.fuente,
                f.referencia || '-',
                `$${Math.abs(f.monto).toLocaleString('en-US', { minimumFractionDigits: 2 })}`
            ];
            tableRows.push(ticketData);
        });

        autoTable(doc, {
            startY: 60,
            head: [tableColumn],
            body: tableRows,
            theme: 'striped',
            headStyles: { fillColor: [41, 128, 185] },
            styles: { fontSize: 10 }
        });

        doc.save('reporte_financiero_gema.pdf');
    };

    return (
        <div className="max-w-6xl mx-auto space-y-6 transition-colors">
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Finanzas</h1>
                    <p className="text-gray-500 dark:text-gray-400">Resumen y reportes de ingresos y egresos</p>
                </div>
                <button
                    onClick={generarPDF}
                    className="bg-accent text-white px-6 py-3 rounded-xl hover:opacity-90 transition shadow-lg flex items-center gap-2 font-medium"
                >
                    <FileText size={20} />
                    <span>Generar Reporte PDF</span>
                </button>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 lg:gap-6">
                <div className="bg-white dark:bg-slate-800 p-6 rounded-2xl shadow-sm border border-gray-100 dark:border-slate-700 flex items-center gap-4 transition-colors">
                    <div className="p-4 bg-green-50 dark:bg-green-900/30 text-green-500 dark:text-green-400 rounded-full">
                        <TrendingUp size={32} />
                    </div>
                    <div>
                        <p className="text-gray-500 dark:text-gray-400 text-sm font-medium">Total Ingresos</p>
                        <h3 className="text-xl sm:text-2xl font-bold text-gray-900 dark:text-white truncate">${totals.ingresos.toLocaleString('en-US', { minimumFractionDigits: 2 })}</h3>
                    </div>
                </div>
                <div className="bg-white dark:bg-slate-800 p-6 rounded-2xl shadow-sm border border-gray-100 dark:border-slate-700 flex items-center gap-4 transition-colors">
                    <div className="p-4 bg-red-50 dark:bg-red-900/30 text-red-500 dark:text-red-400 rounded-full">
                        <TrendingDown size={32} />
                    </div>
                    <div>
                        <p className="text-gray-500 dark:text-gray-400 text-sm font-medium">Total Egresos</p>
                        <h3 className="text-xl sm:text-2xl font-bold text-gray-900 dark:text-white truncate">${totals.egresos.toLocaleString('en-US', { minimumFractionDigits: 2 })}</h3>
                    </div>
                </div>
                <div className="bg-gradient-to-r from-primary to-accent dark:from-blue-700 dark:to-indigo-900 text-white p-6 rounded-2xl shadow-lg border border-transparent flex items-center gap-4">
                    <div className="p-4 bg-white/20 rounded-full">
                        <DollarSign size={32} />
                    </div>
                    <div>
                        <p className="text-white/80 text-sm font-medium">Balance Neto</p>
                        <h3 className="text-xl sm:text-2xl font-bold truncate">${totals.neto.toLocaleString('en-US', { minimumFractionDigits: 2 })}</h3>
                    </div>
                </div>
            </div>

            {/* Chart Section */}
            <div className="bg-white dark:bg-slate-800 rounded-2xl shadow-sm dark:shadow-lg p-6 border border-gray-100 dark:border-slate-700 transition-colors">
                 <h3 className="font-bold text-lg mb-6 text-gray-900 dark:text-white">Balance Financiero</h3>
                 <div className="h-[300px] w-full">
                     {loading ? (
                          <div className="h-full flex items-center justify-center"><Loader className="animate-spin text-primary" size={32} /></div>
                     ) : (
                           <ResponsiveContainer width="100%" height="100%">
                               <LineChart data={chartData} margin={{ top: 5, right: 20, bottom: 5, left: 0 }}>
                                   <CartesianGrid strokeDasharray="3 3" stroke={isDarkMode ? "#334155" : "#e2e8f0"} vertical={false} />
                                   <XAxis dataKey="name" stroke={isDarkMode ? "#94a3b8" : "#64748b"} fontSize={12} tickMargin={10} axisLine={false} tickLine={false} />
                                   <YAxis stroke={isDarkMode ? "#94a3b8" : "#64748b"} fontSize={12} axisLine={false} tickLine={false} tickFormatter={(value) => `$${value}`} />
                                   <Tooltip 
                                       contentStyle={{ 
                                           backgroundColor: isDarkMode ? '#1e293b' : '#fff', 
                                           borderColor: isDarkMode ? '#334155' : '#e2e8f0', 
                                           borderRadius: '8px', 
                                           color: isDarkMode ? '#f8fafc' : '#1e293b' 
                                       }}
                                       itemStyle={{ color: isDarkMode ? '#f8fafc' : '#1e293b' }}
                                       formatter={(value) => [`$${value}`, undefined]}
                                   />
                                   <Line type="monotone" dataKey="Ingresos" stroke="#22c55e" strokeWidth={3} dot={{ r: 4, strokeWidth: 2 }} activeDot={{ r: 6 }} />
                                   <Line type="monotone" dataKey="Gastos" name="Gastos (Est.)" stroke="#ef4444" strokeWidth={3} dot={{ r: 4, strokeWidth: 2 }} activeDot={{ r: 6 }} />
                               </LineChart>
                           </ResponsiveContainer>
                     )}
                 </div>
            </div>

            <div className="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-gray-100 dark:border-slate-700 overflow-hidden transition-colors">
                <div className="px-6 py-4 border-b border-gray-100 dark:border-slate-700 bg-gray-50 dark:bg-slate-900/50">
                    <h3 className="font-bold text-gray-800 dark:text-white">Historial de Movimientos Financieros</h3>
                </div>
                {loading ? (
                    <div className="p-12 flex justify-center text-primary">
                        <Loader className="animate-spin" size={32} />
                    </div>
                ) : (
                    <div className="overflow-x-auto text-center md:text-left">
                        <table className="w-full text-left text-sm text-gray-600 dark:text-gray-300 min-w-[600px]">
                            <thead className="bg-white dark:bg-slate-800 text-gray-400 dark:text-gray-500 uppercase tracking-wider text-xs font-semibold border-b border-gray-100 dark:border-slate-700">
                            <tr>
                                <th className="p-4">Fecha</th>
                                <th className="p-4">Fuente / Descripción</th>
                                <th className="p-4">Referencia</th>
                                <th className="p-4 text-right">Monto</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100 dark:divide-slate-700">
                            {finances.map((f) => (
                                <tr key={f.id} className="hover:bg-gray-50 dark:hover:bg-slate-700/50 transition-colors">
                                    <td className="p-4 text-gray-500 dark:text-gray-400">{new Date(f.fecha).toLocaleDateString()}</td>
                                    <td className="p-4 font-medium text-gray-900 dark:text-white">{f.fuente}</td>
                                    <td className="p-4 font-mono text-xs text-gray-400 dark:text-gray-500">{f.referencia || '-'}</td>
                                    <td className={`p-4 text-right font-bold ${f.monto >= 0 ? 'text-green-600 dark:text-green-400' : 'text-red-500 dark:text-red-400'}`}>
                                        {f.monto >= 0 ? '+' : '-'} ${Math.abs(f.monto).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                                    </td>
                                </tr>
                            ))}
                            {finances.length === 0 && (
                                <tr>
                                    <td colSpan="4" className="p-12 text-center text-gray-500">No hay registros financieros</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
                )}
            </div>
        </div>
    );
}
