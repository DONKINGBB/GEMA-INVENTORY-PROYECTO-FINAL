import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { orderService, clientService, warehouseService, inventoryService } from '../services/dataService';
import { ShoppingCart, Calendar, CheckCircle, Clock, ChevronRight, Plus, Trash2, X, Package, Search, ShoppingBag, User, MapPin, DollarSign, AlertCircle, Loader2, ArrowUpRight, TrendingUp, PlusCircle } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

export default function Orders() {
    const { user } = useAuth();
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    
    // UI States
    const [isAddModalOpen, setIsAddModalOpen] = useState(false);
    const [isConfirmDeleteOpen, setIsConfirmDeleteOpen] = useState(false);
    const [orderToDelete, setOrderToDelete] = useState(null);
    const [selectedOrderDetails, setSelectedOrderDetails] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    
    // Form States
    const [clients, setClients] = useState([]);
    const [warehouses, setWarehouses] = useState([]);
    const [availableProducts, setAvailableProducts] = useState([]);
    const [formData, setFormData] = useState({
        nombre: '',
        fechaLimite: '',
        idCliente: '',
        idAlmacen: ''
    });
    const [cart, setCart] = useState([]);
    const [tempProduct, setTempProduct] = useState({ id: '', cantidad: 1 });
    const [formError, setFormError] = useState('');

    const fetchOrders = async () => {
        if (!user?.id) return;
        try {
            setLoading(true);
            const data = await orderService.getAll(user.id);
            if (Array.isArray(data)) setOrders(data);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const fetchInitialData = async () => {
        if (!user?.id) return;
        try {
            const [c, w] = await Promise.all([
                clientService.getAll(user.id),
                warehouseService.getAll(user.id)
            ]);
            setClients(c || []);
            setWarehouses(w || []);
        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        fetchOrders();
        fetchInitialData();
    }, [user]);

    const handleWarehouseChange = async (wId) => {
        setFormData({ ...formData, idAlmacen: wId });
        setCart([]); // Reset cart if warehouse changes
        if (!wId) {
            setAvailableProducts([]);
            return;
        }
        try {
            const products = await inventoryService.getProductsByWarehouse(user.id, wId);
            setAvailableProducts(products || []);
        } catch (err) {
            console.error(err);
        }
    };

    const addToCart = () => {
        setFormError('');
        const prod = availableProducts.find(p => p.idProducto === tempProduct.id);
        if (!prod || !tempProduct.cantidad) return;

        // Check if enough stock
        if (tempProduct.cantidad > (prod.stockActual || 0)) {
            setFormError(`Stock insuficiente. Disponible: ${prod.stockActual || 0}`);
            return;
        }

        const existing = cart.find(item => item.idProducto === prod.idProducto);
        if (existing) {
            setCart(cart.map(item => 
                item.idProducto === prod.idProducto 
                ? { ...item, cantidad: item.cantidad + parseInt(tempProduct.cantidad) } 
                : item
            ));
        } else {
            setCart([...cart, {
                idProducto: prod.idProducto,
                nombre: prod.nombre,
                cantidad: parseInt(tempProduct.cantidad),
                precioUnitario: prod.precioVenta
            }]);
        }
        setTempProduct({ id: '', cantidad: 1 });
    };

    const removeFromCart = (id) => {
        setCart(cart.filter(item => item.idProducto !== id));
    };

    const handleSaveOrder = async () => {
        setFormError('');
        if (!formData.idCliente || !formData.idAlmacen || cart.length === 0) {
            setFormError("Completa los datos y agrega al menos un producto.");
            return;
        }

        const payload = {
            idCliente: formData.idCliente,
            idAlmacenOrigen: parseInt(formData.idAlmacen),
            nombre: formData.nombre || null,
            fechaLimite: formData.fechaLimite || null,
            detalles: cart.map(item => ({
                idProducto: item.idProducto,
                cantidad: item.cantidad,
                precioUnitario: item.precioUnitario
            }))
        };

        try {
            await orderService.create(payload, user.id);
            setIsAddModalOpen(false);
            setFormData({ nombre: '', fechaLimite: '', idCliente: '', idAlmacen: '' });
            setCart([]);
            fetchOrders();
        } catch (err) {
            setFormError("Error al guardar el pedido: " + (err.response?.data || err.message));
        }
    };

    const handleMarkDelivered = async (e, id) => {
        e.stopPropagation();
        try {
            await orderService.markDelivered(id);
            fetchOrders();
        } catch (err) {
            console.error(err);
        }
    };

    const handleDeleteClick = (e, order) => {
        e.stopPropagation();
        setOrderToDelete(order);
        setIsConfirmDeleteOpen(true);
    };

    const confirmDelete = async () => {
        if (!orderToDelete) return;
        try {
            await orderService.delete(orderToDelete.id);
            setIsConfirmDeleteOpen(false);
            setOrderToDelete(null);
            fetchOrders();
        } catch (err) {
            console.error(err);
        }
    };

    const getStatusBadge = (statusId) => {
        switch (statusId) {
            case 1: 
                return (
                    <span className="flex items-center gap-1.5 text-amber-500 bg-amber-500/10 border border-amber-500/20 px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest backdrop-blur-md">
                        <Clock size={10} className="animate-pulse" /> Pendiente
                    </span>
                );
            case 2: 
                return (
                    <span className="flex items-center gap-1.5 text-emerald-500 bg-emerald-500/10 border border-emerald-500/20 px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest backdrop-blur-md">
                        <CheckCircle size={10} /> Entregado
                    </span>
                );
            default: 
                return (
                    <span className="text-slate-500 bg-slate-500/10 border border-slate-500/20 px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest">
                        N/A
                    </span>
                );
        }
    };

    const filteredOrders = orders.filter(order => 
        (order.nombre || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
        (order.id || '').toString().includes(searchTerm)
    );

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
                            <ShoppingCart size={12} />
                            <span>Logística de Salida</span>
                        </div>
                        <h1 className="text-5xl sm:text-7xl font-black text-slate-900 dark:text-white tracking-tighter leading-none">
                            Flujo de <span className="text-primary">Pedidos</span>
                        </h1>
                        <p className="text-slate-500 dark:text-slate-400 text-lg max-w-xl">
                            Monitorea, gestiona y completa las órdenes de venta con trazabilidad total de inventario.
                        </p>
                    </div>
                    <button 
                        onClick={() => setIsAddModalOpen(true)} 
                        className="group bg-primary hover:bg-primary/90 text-white px-8 py-5 rounded-[2rem] flex items-center gap-3 transition-all shadow-2xl shadow-primary/40 font-black text-lg active:scale-95"
                    >
                        <Plus size={24} className="group-hover:rotate-90 transition-transform" />
                        <span>Nuevo Pedido</span>
                    </button>
                </div>
                
                {/* Background Decoration */}
                <div className="absolute top-0 right-0 w-1/3 h-full bg-gradient-to-l from-primary/10 to-transparent pointer-events-none" />
                <div className="absolute -bottom-24 -right-24 w-64 h-64 bg-primary/20 blur-[120px] rounded-full pointer-events-none" />
            </motion.div>

            {/* Search and Stats */}
            <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
                <motion.div variants={itemVariants} className="lg:col-span-3 relative group">
                    <Search className="absolute left-6 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-primary transition-colors" size={24} />
                    <input 
                        type="text" 
                        placeholder="Buscar por referencia, cliente o concepto..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="w-full pl-16 pr-8 py-6 rounded-[2.5rem] bg-white dark:bg-slate-900/40 border border-slate-200 dark:border-white/5 focus:border-primary focus:ring-8 focus:ring-primary/5 outline-none transition-all text-slate-900 dark:text-white text-lg placeholder:text-slate-400 dark:placeholder:text-slate-600 shadow-sm dark:shadow-none"
                    />
                </motion.div>
                <motion.div variants={itemVariants} className="bg-white dark:bg-slate-900/40 border border-slate-200 dark:border-primary/20 p-6 rounded-[2.5rem] flex items-center justify-between shadow-sm dark:shadow-none">
                    <div className="space-y-1">
                        <p className="text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Total Volumen</p>
                        <p className="text-3xl font-black text-slate-900 dark:text-white">{orders.length}</p>
                    </div>
                    <div className="p-3 bg-primary/10 text-primary rounded-2xl">
                        <TrendingUp size={24} />
                    </div>
                </motion.div>
            </div>

            {/* Orders Feed */}
            <div className="bg-white dark:bg-slate-900/40 border border-slate-200 dark:border-white/5 rounded-[2.5rem] overflow-hidden shadow-sm dark:shadow-none">
                <div className="px-8 py-6 border-b border-slate-100 dark:border-white/5 bg-slate-50/50 dark:bg-white/5 flex items-center justify-between">
                    <h3 className="font-black text-slate-900 dark:text-white uppercase tracking-widest text-xs flex items-center gap-2">
                        <ShoppingBag size={16} className="text-primary" />
                        Historial de Operaciones
                    </h3>
                    <div className="text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">
                        Live Tracking
                    </div>
                </div>

                {loading ? (
                    <div className="py-24 flex flex-col items-center justify-center space-y-6">
                        <Loader2 className="animate-spin text-primary w-16 h-16" />
                        <p className="text-slate-500 font-bold tracking-widest text-sm uppercase animate-pulse">Sincronizando órdenes...</p>
                    </div>
                ) : filteredOrders.length > 0 ? (
                    <div className="divide-y divide-slate-100 dark:divide-white/5">
                        <AnimatePresence mode="popLayout">
                            {filteredOrders.map((order, index) => (
                                <motion.div 
                                    key={order.id} 
                                    variants={itemVariants}
                                    onClick={() => setSelectedOrderDetails(order)}
                                    className="p-8 hover:bg-slate-100/60 dark:hover:bg-white/[0.02] transition-all cursor-pointer flex flex-col lg:flex-row lg:items-center justify-between gap-8 group relative overflow-hidden"
                                >
                                    <div className="flex items-start gap-6 relative z-10">
                                        <div className="w-16 h-16 rounded-2xl bg-slate-100 dark:bg-slate-900 border border-slate-200 dark:border-white/10 flex items-center justify-center text-primary group-hover:scale-110 group-hover:border-primary/30 transition-all duration-500 shadow-sm dark:shadow-none">
                                            <Package size={28} />
                                        </div>
                                        <div className="space-y-2">
                                            <div className="flex items-center gap-3">
                                                <h3 className="font-black text-2xl text-slate-900 dark:text-white group-hover:text-primary transition-colors">
                                                    {order.nombre || `Orden #${order.id.toString().substring(0, 8)}`}
                                                </h3>
                                                {getStatusBadge(order.idEstado)}
                                            </div>
                                            <div className="flex flex-wrap items-center gap-x-6 gap-y-2 text-sm text-slate-500 font-medium">
                                                <span className="flex items-center gap-2">
                                                    <Calendar size={14} /> 
                                                    {new Date(order.fechaPedido).toLocaleDateString()}
                                                </span>
                                                <span className="flex items-center gap-2">
                                                    <User size={14} /> 
                                                    {order.nombreCliente || 'Cliente Final'}
                                                </span>
                                                <span className="flex items-center gap-2">
                                                    <MapPin size={14} /> 
                                                    {order.nombreAlmacen || 'Almacén Central'}
                                                </span>
                                            </div>
                                        </div>
                                    </div>

                                    <div className="flex items-center justify-between lg:justify-end gap-12 relative z-10">
                                        <div className="text-right">
                                            <p className="text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest mb-1">Valor Transacción</p>
                                            <p className="font-black text-slate-900 dark:text-white text-3xl tracking-tighter">
                                                <span className="text-primary text-xl mr-1">$</span>
                                                {parseFloat(order.total || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                                            </p>
                                        </div>
                                        <div className="flex items-center gap-3">
                                            {order.idEstado === 1 && (
                                                <button 
                                                    onClick={(e) => handleMarkDelivered(e, order.id)}
                                                    className="p-4 bg-emerald-500/10 text-emerald-500 hover:bg-emerald-500 hover:text-white rounded-2xl transition-all shadow-lg shadow-emerald-500/0 hover:shadow-emerald-500/20"
                                                    title="Finalizar Orden"
                                                >
                                                    <CheckCircle size={22} />
                                                </button>
                                            )}
                                            <button 
                                                onClick={(e) => handleDeleteClick(e, order)}
                                                className="p-4 bg-rose-500/10 text-rose-500 hover:bg-rose-500 hover:text-white rounded-2xl transition-all shadow-lg shadow-rose-500/0 hover:shadow-rose-500/20"
                                                title="Cancelar Registro"
                                            >
                                                <Trash2 size={22} />
                                            </button>
                                            <div className="p-2 text-slate-700 group-hover:text-primary transition-colors">
                                                <ChevronRight size={24} />
                                            </div>
                                        </div>
                                    </div>
                                    
                                    {/* Subtle Gradient Hover */}
                                    <div className="absolute top-0 right-0 w-full h-full bg-gradient-to-r from-transparent to-primary/5 translate-x-full group-hover:translate-x-0 transition-transform duration-700 pointer-events-none" />
                                </motion.div>
                            ))}
                        </AnimatePresence>
                    </div>
                ) : (
                    <div className="py-32 text-center space-y-8">
                        <div className="w-32 h-32 bg-slate-100 dark:bg-white/5 rounded-[3rem] flex items-center justify-center mx-auto border border-slate-200 dark:border-white/5 shadow-xl">
                            <ShoppingCart size={56} className="text-slate-300 dark:text-slate-700" />
                        </div>
                        <div className="space-y-3 max-w-sm mx-auto">
                            <h3 className="text-3xl font-black text-slate-900 dark:text-white">Sin actividad</h3>
                            <p className="text-slate-500 text-lg">No se han registrado pedidos recientemente en este periodo.</p>
                        </div>
                        <button 
                            onClick={() => setIsAddModalOpen(true)}
                            className="bg-primary/10 hover:bg-primary/20 text-primary px-8 py-4 rounded-2xl font-black transition-all inline-flex items-center gap-3 border border-primary/20"
                        >
                            Comenzar Nueva Orden
                            <Plus size={20} />
                        </button>
                    </div>
                )}
            </div>

            {/* Modal Components (Add, Details, Delete) - Keeping current logic but upgrading visuals */}
            <AnimatePresence>
                {isAddModalOpen && (
                    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
                        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} onClick={() => setIsAddModalOpen(false)} className="absolute inset-0 bg-black/80 backdrop-blur-md" />
                        <motion.div initial={{ opacity: 0, scale: 0.9, y: 20 }} animate={{ opacity: 1, scale: 1, y: 0 }} exit={{ opacity: 0, scale: 0.9, y: 20 }} className="relative bg-white dark:bg-slate-900 w-full max-w-4xl max-h-[90vh] rounded-[2.5rem] overflow-hidden flex flex-col shadow-2xl border border-slate-200 dark:border-white/10">
                            <div className="p-10 border-b border-slate-100 dark:border-white/5 flex justify-between items-center bg-slate-50/50 dark:bg-white/5">
                                <div>
                                    <h2 className="text-4xl font-black text-slate-900 dark:text-white tracking-tighter">Nueva Orden</h2>
                                    <p className="text-slate-500 font-medium">Configuración de salida de mercancía</p>
                                </div>
                                <button onClick={() => setIsAddModalOpen(false)} className="p-4 bg-slate-100 dark:bg-white/5 hover:bg-slate-200 dark:hover:bg-white/10 rounded-2xl text-slate-500 dark:text-slate-400 transition-all">
                                    <X size={28} />
                                </button>
                            </div>

                            <div className="p-10 overflow-y-auto space-y-10 flex-1 custom-scrollbar bg-white dark:bg-slate-900/50">
                                {formError && (
                                    <div className="bg-rose-500/10 border border-rose-500/20 p-6 rounded-[1.5rem] flex items-center gap-4 text-rose-600 dark:text-rose-400 animate-pulse">
                                        <AlertCircle size={24} />
                                        <p className="font-black text-sm uppercase tracking-widest">{formError}</p>
                                    </div>
                                )}

                                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                                    <div className="space-y-3">
                                        <label className="text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest ml-1">Concepto del Pedido</label>
                                        <input type="text" className="w-full p-5 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-200 dark:border-white/5 focus:border-primary focus:ring-4 focus:ring-primary/10 outline-none text-slate-900 dark:text-white transition-all placeholder:text-slate-400" placeholder="Ej: Entrega Mayorista Q3" value={formData.nombre} onChange={e => setFormData({...formData, nombre: e.target.value})} />
                                    </div>
                                    <div className="space-y-3">
                                        <label className="text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest ml-1">Fecha de Compromiso</label>
                                        <input type="date" className="w-full p-5 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-200 dark:border-white/5 focus:border-primary focus:ring-4 focus:ring-primary/10 outline-none text-slate-900 dark:text-white transition-all [color-scheme:light] dark:[color-scheme:dark]" value={formData.fechaLimite} onChange={e => setFormData({...formData, fechaLimite: e.target.value})} />
                                    </div>
                                    <div className="space-y-3">
                                        <label className="text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest ml-1">Cliente Solicitante</label>
                                        <select className="w-full p-5 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-200 dark:border-white/5 outline-none text-slate-900 dark:text-white transition-all appearance-none" value={formData.idCliente} onChange={e => setFormData({...formData, idCliente: e.target.value})}>
                                            <option value="" className="bg-white dark:bg-slate-900 text-slate-900 dark:text-white">Seleccionar Cliente</option>
                                            {clients.map(c => <option key={c.idCliente} value={c.idCliente} className="bg-white dark:bg-slate-900 text-slate-900 dark:text-white">{c.nombre}</option>)}
                                        </select>
                                    </div>
                                    <div className="space-y-3">
                                        <label className="text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest ml-1">Almacén de Despacho</label>
                                        <select className="w-full p-5 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-200 dark:border-white/5 outline-none text-slate-900 dark:text-white transition-all appearance-none" value={formData.idAlmacen} onChange={e => handleWarehouseChange(e.target.value)}>
                                            <option value="" className="bg-white dark:bg-slate-900 text-slate-900 dark:text-white">Seleccionar Almacén</option>
                                            {warehouses.map(w => <option key={w.idAlmacen} value={w.idAlmacen} className="bg-white dark:bg-slate-900 text-slate-900 dark:text-white">{w.nombre}</option>)}
                                        </select>
                                    </div>
                                </div>

                                <div className="bg-slate-50 dark:bg-primary/5 p-8 rounded-[2rem] border border-slate-200 dark:border-primary/20 space-y-8">
                                    <h3 className="font-black text-slate-900 dark:text-white text-xl flex items-center gap-3">
                                        <PlusCircle size={24} className="text-primary" />
                                        Configuración de Items
                                    </h3>

                                    <div className="flex flex-col md:flex-row gap-4">
                                        <select className="flex-1 p-5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-white/10 outline-none text-slate-900 dark:text-white disabled:opacity-30 transition-all appearance-none" value={tempProduct.id} onChange={e => setTempProduct({...tempProduct, id: e.target.value})} disabled={!formData.idAlmacen}>
                                            <option value="" className="bg-white dark:bg-slate-900 text-slate-900 dark:text-white">Buscar Producto...</option>
                                            {availableProducts.map(p => <option key={p.idProducto} value={p.idProducto} className="bg-white dark:bg-slate-900 text-slate-900 dark:text-white">{p.nombre} (${p.precioVenta}) • Stock: {p.stockActual || 0}</option>)}
                                        </select>
                                        <div className="flex gap-4">
                                            <input type="number" className="w-32 p-5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-white/10 outline-none text-slate-900 dark:text-white text-center font-black" placeholder="CANT" min="1" value={tempProduct.cantidad} onChange={e => setTempProduct({...tempProduct, cantidad: e.target.value})} />
                                            <button onClick={addToCart} className="bg-primary text-white px-8 rounded-2xl hover:bg-primary/90 transition shadow-xl shadow-primary/20 active:scale-95">
                                                <Plus size={32} />
                                            </button>
                                        </div>
                                    </div>

                                    <div className="space-y-3">
                                        <AnimatePresence>
                                            {cart.map((item) => (
                                                <motion.div key={item.idProducto} initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, scale: 0.9 }} className="flex items-center justify-between p-5 bg-white dark:bg-white/5 border border-slate-100 dark:border-white/5 rounded-2xl hover:border-primary/30 transition-all shadow-sm">
                                                    <div className="flex items-center gap-5">
                                                        <div className="w-12 h-12 bg-primary/10 dark:bg-primary/20 text-primary rounded-xl flex items-center justify-center font-black text-lg">
                                                            {item.cantidad}
                                                        </div>
                                                        <div>
                                                            <div className="font-black text-slate-900 dark:text-white text-lg">{item.nombre}</div>
                                                            <div className="text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Unit: ${item.precioUnitario}</div>
                                                        </div>
                                                    </div>
                                                    <div className="flex items-center gap-8">
                                                        <div className="text-xl font-black text-primary">
                                                            ${(item.cantidad * item.precioUnitario).toLocaleString()}
                                                        </div>
                                                        <button onClick={() => removeFromCart(item.idProducto)} className="p-3 bg-rose-500/10 text-rose-500 hover:bg-rose-500 hover:text-white rounded-xl transition-all">
                                                            <Trash2 size={18} />
                                                        </button>
                                                    </div>
                                                </motion.div>
                                            ))}
                                        </AnimatePresence>
                                        {cart.length === 0 && <div className="py-12 text-center text-slate-400 dark:text-slate-600 border-2 border-dashed border-slate-200 dark:border-white/5 rounded-[2rem] font-black uppercase tracking-[0.2em] text-xs">Lista de carga vacía</div>}
                                    </div>
                                </div>
                            </div>

                            <div className="p-10 border-t border-slate-100 dark:border-white/5 bg-slate-50/50 dark:bg-white/5 flex flex-col sm:flex-row items-center justify-between gap-8">
                                <div className="text-right sm:text-left">
                                    <p className="text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest mb-1">Monto Total Estimado</p>
                                    <p className="text-5xl font-black text-slate-900 dark:text-white tracking-tighter">
                                        <span className="text-primary text-3xl mr-2">$</span>
                                        {cart.reduce((sum, item) => sum + (item.cantidad * item.precioUnitario), 0).toLocaleString()}
                                    </p>
                                </div>
                                <div className="flex gap-4 w-full sm:w-auto">
                                    <button onClick={() => setIsAddModalOpen(false)} className="flex-1 sm:px-10 py-5 text-slate-500 dark:text-slate-400 font-black rounded-2xl hover:bg-slate-100 dark:hover:bg-white/5 transition uppercase tracking-widest text-xs">CANCELAR</button>
                                    <button onClick={handleSaveOrder} disabled={cart.length === 0} className="flex-1 sm:px-12 py-5 bg-primary text-white font-black rounded-[1.5rem] hover:bg-primary/90 transition shadow-2xl shadow-primary/30 disabled:opacity-20 active:scale-95 text-lg">PROCESAR ORDEN</button>
                                </div>
                            </div>
                        </motion.div>
                    </div>
                )}
            </AnimatePresence>

            {/* Modal - Confirmar Eliminación */}
            <AnimatePresence>
                {isConfirmDeleteOpen && (
                    <div className="fixed inset-0 z-[110] flex items-center justify-center p-4">
                        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="absolute inset-0 bg-black/90 backdrop-blur-xl" />
                        <motion.div initial={{ opacity: 0, scale: 0.9 }} animate={{ opacity: 1, scale: 1 }} exit={{ opacity: 0, scale: 0.9 }} className="relative bg-white dark:bg-slate-900 p-12 max-w-md w-full text-center rounded-[3rem] shadow-2xl border border-rose-500/20">
                            <div className="w-24 h-24 bg-rose-500/10 text-rose-500 rounded-[2.5rem] flex items-center justify-center mx-auto mb-8 border border-rose-500/20 shadow-2xl shadow-rose-500/20">
                                <Trash2 size={48} className="animate-pulse" />
                            </div>
                            <h3 className="text-4xl font-black text-slate-900 dark:text-white mb-4 tracking-tighter">¿Anular Orden?</h3>
                            <p className="text-slate-500 dark:text-slate-400 mb-10 text-lg leading-relaxed">
                                Esta operación marcará la orden como cancelada y no podrá ser reactivada.
                            </p>
                            <div className="flex flex-col sm:flex-row gap-4">
                                <button onClick={() => setIsConfirmDeleteOpen(false)} className="flex-1 py-5 bg-slate-100 dark:bg-white/5 text-slate-600 dark:text-white font-black rounded-2xl hover:bg-slate-200 dark:hover:bg-white/10 transition uppercase tracking-widest text-xs border border-slate-200 dark:border-white/5">CANCELAR</button>
                                <button onClick={confirmDelete} className="flex-1 py-5 bg-rose-600 text-white font-black rounded-2xl hover:bg-rose-700 transition shadow-2xl shadow-rose-600/30 active:scale-95 text-xs tracking-widest">ANULAR REGISTRO</button>
                            </div>
                        </motion.div>
                    </div>
                )}
            </AnimatePresence>

            {/* Order Details Drawer/Modal */}
            <AnimatePresence>
                {selectedOrderDetails && (
                    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
                        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} onClick={() => setSelectedOrderDetails(null)} className="absolute inset-0 bg-black/80 backdrop-blur-md" />
                        <motion.div initial={{ opacity: 0, x: 100 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: 100 }} className="relative bg-white dark:bg-slate-950 w-full max-w-xl shadow-2xl overflow-hidden border-l border-slate-200 dark:border-white/10 ml-auto h-[100vh] rounded-none">
                            <div className="p-10 border-b border-slate-100 dark:border-white/5 flex justify-between items-center bg-slate-50/50 dark:bg-white/5">
                                <div>
                                    <h2 className="text-3xl font-black text-slate-900 dark:text-white tracking-tighter">Ficha de Pedido</h2>
                                    <p className="text-slate-500 font-medium">Información detallada de la transacción</p>
                                </div>
                                <button onClick={() => setSelectedOrderDetails(null)} className="p-4 bg-slate-100 dark:bg-white/5 hover:bg-slate-200 dark:hover:bg-white/10 rounded-2xl text-slate-500 dark:text-slate-400 transition-all">
                                    <X size={28} />
                                </button>
                            </div>
                            
                            <div className="p-10 space-y-12 overflow-y-auto h-[calc(100vh-200px)] custom-scrollbar">
                                <div className="grid grid-cols-2 gap-6">
                                    <div className="p-6 bg-slate-50 dark:bg-white/5 rounded-3xl border border-slate-200 dark:border-white/5 space-y-1">
                                        <p className="text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Estado de Orden</p>
                                        <div className="pt-2">{getStatusBadge(selectedOrderDetails.idEstado)}</div>
                                    </div>
                                    <div className="p-6 bg-slate-50 dark:bg-white/5 rounded-3xl border border-slate-200 dark:border-white/5 space-y-1">
                                        <p className="text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Fecha Registro</p>
                                        <p className="text-xl font-black text-slate-900 dark:text-white">{new Date(selectedOrderDetails.fechaPedido).toLocaleDateString()}</p>
                                    </div>
                                </div>

                                <div className="space-y-6">
                                    <h4 className="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em] flex items-center gap-3">
                                        <Package size={16} className="text-primary" />
                                        Items Despachados
                                    </h4>
                                    <div className="space-y-4">
                                        {selectedOrderDetails.detalles?.map((d, i) => (
                                            <div key={i} className="flex justify-between items-center p-6 bg-slate-50 dark:bg-white/5 rounded-3xl border border-transparent hover:border-slate-200 dark:hover:border-white/10 transition-all group shadow-sm">
                                                <div className="flex items-center gap-5">
                                                    <div className="w-12 h-12 bg-slate-200 dark:bg-slate-900 rounded-2xl flex items-center justify-center font-black text-primary border border-slate-300 dark:border-white/5">
                                                        {d.cantidad}
                                                    </div>
                                                    <div>
                                                        <span className="text-lg text-slate-900 dark:text-white font-black block">{d.nombreProducto || `Producto ID: ${d.idProducto}`}</span>
                                                        <span className="text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Unit: ${d.precioUnitario}</span>
                                                    </div>
                                                </div>
                                                <span className="text-xl font-black text-slate-900 dark:text-white group-hover:text-primary transition-colors">
                                                    ${(d.cantidad * d.precioUnitario).toLocaleString()}
                                                </span>
                                            </div>
                                        ))}
                                    </div>
                                </div>

                                <div className="p-8 bg-primary/5 dark:bg-primary/10 rounded-[2.5rem] border border-primary/10 dark:border-primary/20 flex justify-between items-center shadow-inner">
                                    <div>
                                        <p className="text-[10px] font-black text-primary uppercase tracking-widest mb-1">Monto Total de Operación</p>
                                        <p className="text-4xl font-black text-slate-900 dark:text-white tracking-tighter">
                                            <span className="text-primary text-2xl mr-2">$</span>
                                            {parseFloat(selectedOrderDetails.total || 0).toLocaleString()}
                                        </p>
                                    </div>
                                    <div className="p-5 bg-white dark:bg-white/10 rounded-3xl shadow-sm">
                                        <DollarSign size={32} className="text-primary" />
                                    </div>
                                </div>
                            </div>
                            
                            <div className="absolute bottom-0 left-0 w-full p-10 bg-slate-50 dark:bg-slate-950 border-t border-slate-100 dark:border-white/5 flex gap-4">
                                <button onClick={() => setSelectedOrderDetails(null)} className="flex-1 py-5 bg-white dark:bg-white/5 text-slate-600 dark:text-white font-black rounded-2xl hover:bg-slate-100 dark:hover:bg-white/10 border border-slate-200 dark:border-white/5 transition uppercase tracking-widest text-xs">CERRAR</button>
                                {selectedOrderDetails.idEstado === 1 && (
                                    <button onClick={(e) => { handleMarkDelivered(e, selectedOrderDetails.id); setSelectedOrderDetails(null); }} className="flex-1 py-5 bg-emerald-600 text-white font-black rounded-2xl hover:bg-emerald-700 transition shadow-2xl shadow-emerald-600/30 uppercase tracking-widest text-xs">COMPLETAR ENTREGA</button>
                                )}
                            </div>
                        </motion.div>
                    </div>
                )}
            </AnimatePresence>
        </motion.div>
    );
}
