
import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { orderService, clientService, warehouseService, inventoryService } from '../services/dataService';
import { ShoppingCart, Calendar, CheckCircle, Clock, ChevronRight, Plus, Trash2, X, Package, Search } from 'lucide-react';

export default function Orders() {
    const { user } = useAuth();
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    
    // UI States
    const [isAddModalOpen, setIsAddModalOpen] = useState(false);
    const [isConfirmDeleteOpen, setIsConfirmDeleteOpen] = useState(false);
    const [orderToDelete, setOrderToDelete] = useState(null);
    const [selectedOrderDetails, setSelectedOrderDetails] = useState(null);
    
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
            alert("Error al guardar el pedido: " + (err.response?.data || err.message));
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
            case 1: return <span className="flex items-center gap-1 text-yellow-600 bg-yellow-100 px-3 py-1 rounded-full text-xs font-bold"><Clock size={12} /> Pendiente</span>;
            case 2: return <span className="flex items-center gap-1 text-green-600 bg-green-100 px-3 py-1 rounded-full text-xs font-bold"><CheckCircle size={12} /> Completado</span>;
            default: return <span className="text-gray-600 bg-gray-100 px-2 py-1 rounded text-xs">Desconocido</span>;
        }
    };

    return (
        <div className="space-y-6 pb-20 sm:pb-0 transition-colors">
            <div className="flex justify-between items-center bg-white dark:bg-slate-800 p-6 rounded-2xl shadow-sm border border-gray-100 dark:border-slate-700 transition-colors">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Pedidos</h1>
                    <p className="text-gray-500 dark:text-gray-400">Gestión de ventas y entregas</p>
                </div>
                <button 
                    onClick={() => setIsAddModalOpen(true)}
                    className="bg-primary hover:bg-primary-dark text-white px-4 py-2 rounded-xl flex items-center gap-2 transition shadow-lg shadow-primary/20"
                >
                    <Plus size={20} />
                    <span className="hidden sm:inline">Nuevo Pedido</span>
                </button>
            </div>

            <div className="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-gray-100 dark:border-slate-700 overflow-hidden transition-colors">
                {loading ? (
                    <div className="p-12 flex justify-center text-primary">
                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
                    </div>
                ) : orders.length > 0 ? (
                    <div className="divide-y divide-gray-100">
                        {orders.map(order => (
                            <div 
                                key={order.id} 
                                onClick={() => setSelectedOrderDetails(order)}
                                className="p-6 hover:bg-gray-50 dark:hover:bg-slate-700/30 transition cursor-pointer flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-gray-50 dark:border-slate-700 last:border-0"
                            >
                                <div className="flex items-start gap-4">
                                    <div className="p-3 bg-blue-50 dark:bg-blue-900/30 text-primary dark:text-blue-400 rounded-xl">
                                        <ShoppingCart size={24} />
                                    </div>
                                    <div className="flex-1">
                                        <h3 className="font-bold text-gray-900 dark:text-white">{order.nombre || `Pedido #${order.id.toString().substring(0, 8)}`}</h3>
                                        <div className="flex flex-wrap items-center gap-x-4 gap-y-1 mt-1 text-sm text-gray-500 dark:text-gray-400">
                                            <span className="flex items-center gap-1"><Calendar size={14} /> {new Date(order.fechaPedido).toLocaleDateString()}</span>
                                            <span>•</span>
                                            <span className="flex items-center gap-1"><Package size={14} /> {order.detalles?.length || 0} prod.</span>
                                            {order.fechaLimite && (
                                                <>
                                                    <span>•</span>
                                                    <span className="text-orange-600 font-medium">Vence: {order.fechaLimite}</span>
                                                </>
                                            )}
                                        </div>
                                    </div>
                                </div>

                                <div className="flex items-center justify-between sm:justify-end gap-6 border-t dark:border-slate-700 sm:border-t-0 pt-4 sm:pt-0">
                                    <div className="text-right">
                                        <p className="font-bold text-gray-900 dark:text-white text-lg">${parseFloat(order.total || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}</p>
                                        <div className="flex justify-end mt-1">{getStatusBadge(order.idEstado)}</div>
                                    </div>
                                    <div className="flex items-center gap-2">
                                        {order.idEstado === 1 && (
                                            <button 
                                                onClick={(e) => handleMarkDelivered(e, order.id)}
                                                className="p-2 text-green-600 hover:bg-green-50 rounded-lg transition"
                                                title="Marcar como entregado"
                                            >
                                                <CheckCircle size={20} />
                                            </button>
                                        )}
                                        <button 
                                            onClick={(e) => handleDeleteClick(e, order)}
                                            className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition"
                                            title="Eliminar pedido"
                                        >
                                            <Trash2 size={20} />
                                        </button>
                                        <ChevronRight className="text-gray-300" />
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                ) : (
                    <div className="p-12 text-center text-gray-400">
                        <ShoppingCart size={48} className="mx-auto mb-4 opacity-20" />
                        <p>No tienes pedidos recientes.</p>
                        <button 
                            onClick={() => setIsAddModalOpen(true)}
                            className="mt-4 text-primary font-medium hover:underline"
                        >
                            Crear tu primer pedido
                        </button>
                    </div>
                )}
            </div>

            {/* Modal - Nuevo Pedido */}
            {isAddModalOpen && (
                <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4 transition-all">
                    <div className="bg-white dark:bg-slate-800 rounded-3xl w-full max-w-2xl max-h-[90vh] overflow-hidden flex flex-col shadow-2xl border border-gray-100 dark:border-slate-700">
                        <div className="p-6 border-b border-gray-100 dark:border-slate-700 flex justify-between items-center">
                            <h2 className="text-2xl font-bold text-gray-900 dark:text-white">Crear Nuevo Pedido</h2>
                            <button onClick={() => setIsAddModalOpen(false)} className="p-2 hover:bg-gray-100 dark:hover:bg-slate-700 rounded-full text-gray-500">
                                <X size={24} />
                            </button>
                        </div>

                        <div className="p-6 overflow-y-auto space-y-6 flex-1">
                            {formError && (
                                <div className="bg-red-50 border-l-4 border-red-500 p-4 rounded-r-xl flex items-center justify-between text-red-700 animate-in fade-in slide-in-from-top-2">
                                    <div className="flex items-center gap-3">
                                        <X size={18} className="bg-red-500 text-white rounded-full p-0.5" />
                                        <p className="font-medium">{formError}</p>
                                    </div>
                                    <button onClick={() => setFormError('')} className="text-red-400 hover:text-red-600 transition">
                                        <X size={20} />
                                    </button>
                                </div>
                            )}

                            {/* Cabecera del Pedido */}
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div className="space-y-1">
                                    <label className="text-sm font-semibold text-gray-700 dark:text-gray-300">Nombre del Pedido (Opcional)</label>
                                    <input 
                                        type="text" 
                                        className="w-full p-3 bg-gray-50 dark:bg-slate-700 border border-gray-200 dark:border-slate-600 rounded-xl focus:ring-2 focus:ring-primary/20 outline-none text-gray-900 dark:text-white"
                                        placeholder="Ej: Entrega Lunes"
                                        value={formData.nombre}
                                        onChange={e => setFormData({...formData, nombre: e.target.value})}
                                    />
                                </div>
                                <div className="space-y-1">
                                    <label className="text-sm font-semibold text-gray-700 dark:text-gray-300">Fecha Límite (Opcional)</label>
                                    <input 
                                        type="date" 
                                        className="w-full p-3 bg-gray-50 dark:bg-slate-700 border border-gray-200 dark:border-slate-600 rounded-xl focus:ring-2 focus:ring-primary/20 outline-none text-gray-900 dark:text-white"
                                        value={formData.fechaLimite}
                                        onChange={e => setFormData({...formData, fechaLimite: e.target.value})}
                                    />
                                </div>
                            </div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div className="space-y-1">
                                    <label className="text-sm font-semibold text-gray-700 dark:text-gray-300">Cliente</label>
                                    <select 
                                        className="w-full p-3 bg-gray-50 dark:bg-slate-700 border border-gray-200 dark:border-slate-600 rounded-xl outline-none text-gray-900 dark:text-white"
                                        value={formData.idCliente}
                                        onChange={e => setFormData({...formData, idCliente: e.target.value})}
                                    >
                                        <option value="">Selecciona un cliente</option>
                                        {clients.map(c => <option key={c.idCliente} value={c.idCliente}>{c.nombre}</option>)}
                                    </select>
                                </div>
                                <div className="space-y-1">
                                    <label className="text-sm font-semibold text-gray-700 dark:text-gray-300">Almacén de Origen</label>
                                    <select 
                                        className="w-full p-3 bg-gray-50 dark:bg-slate-700 border border-gray-200 dark:border-slate-600 rounded-xl outline-none text-gray-900 dark:text-white"
                                        value={formData.idAlmacen}
                                        onChange={e => handleWarehouseChange(e.target.value)}
                                    >
                                        <option value="">Selecciona un almacén</option>
                                        {warehouses.map(w => <option key={w.idAlmacen} value={w.idAlmacen}>{w.nombre}</option>)}
                                    </select>
                                </div>
                            </div>

                            <hr />

                            {/* Agregar Productos */}
                            <div className="space-y-4">
                                <h3 className="font-bold text-gray-900 flex items-center gap-2">
                                    <Package size={18} />
                                    Productos del Pedido
                                </h3>

                                <div className="flex flex-col sm:flex-row gap-2">
                                    <select 
                                        className="flex-1 p-3 bg-gray-50 border border-gray-200 rounded-xl outline-none"
                                        value={tempProduct.id}
                                        onChange={e => setTempProduct({...tempProduct, id: e.target.value})}
                                        disabled={!formData.idAlmacen}
                                    >
                                        <option value="">Seleccionar Producto</option>
                                        {availableProducts.map(p => (
                                            <option key={p.idProducto} value={p.idProducto}>
                                                {p.nombre} (${p.precioVenta}) - Stock: {p.stockActual || 0}
                                            </option>
                                        ))}
                                    </select>
                                    <input 
                                        type="number" 
                                        className="w-24 p-3 bg-gray-50 border border-gray-200 rounded-xl outline-none"
                                        placeholder="Cant."
                                        min="1"
                                        value={tempProduct.cantidad}
                                        onChange={e => setTempProduct({...tempProduct, cantidad: e.target.value})}
                                    />
                                    <button 
                                        onClick={addToCart}
                                        className="bg-primary text-white p-3 rounded-xl hover:bg-primary-dark transition"
                                    >
                                        <Plus size={20} />
                                    </button>
                                </div>

                                {/* Tabla del Carrito */}
                                {cart.length > 0 ? (
                                    <div className="border border-gray-100 rounded-2xl overflow-hidden">
                                        <table className="w-full text-sm">
                                            <thead className="bg-gray-50 border-b border-gray-100">
                                                <tr>
                                                    <th className="text-left p-3 font-semibold text-gray-600">Producto</th>
                                                    <th className="text-center p-3 font-semibold text-gray-600">Cant.</th>
                                                    <th className="text-right p-3 font-semibold text-gray-600">Subtotal</th>
                                                    <th className="p-3"></th>
                                                </tr>
                                            </thead>
                                            <tbody className="divide-y divide-gray-50">
                                                {cart.map((item) => (
                                                    <tr key={item.idProducto} className="hover:bg-gray-50/50">
                                                        <td className="p-3 font-medium text-gray-900">{item.nombre}</td>
                                                        <td className="p-3 text-center">{item.cantidad}</td>
                                                        <td className="p-3 text-right font-bold">${(item.cantidad * item.precioUnitario).toLocaleString('en-US', { minimumFractionDigits: 2 })}</td>
                                                        <td className="p-3 text-right">
                                                            <button onClick={() => removeFromCart(item.idProducto)} className="text-red-400 hover:text-red-600 p-1">
                                                                <X size={16} />
                                                            </button>
                                                        </td>
                                                    </tr>
                                                ))}
                                            </tbody>
                                            <tfoot className="bg-gray-50/50 font-bold border-t border-gray-100">
                                                <tr>
                                                    <td colSpan="2" className="p-3 text-right text-gray-600">TOTAL</td>
                                                    <td className="p-3 text-right text-primary text-lg">
                                                        ${cart.reduce((sum, item) => sum + (item.cantidad * item.precioUnitario), 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                                                    </td>
                                                    <td></td>
                                                </tr>
                                            </tfoot>
                                        </table>
                                    </div>
                                ) : (
                                    <div className="p-8 text-center bg-gray-50 rounded-2xl text-gray-400 border-2 border-dashed border-gray-100">
                                        No has añadido productos al carrito
                                    </div>
                                )}
                            </div>
                        </div>

                        <div className="p-6 border-t border-gray-100 bg-gray-50 flex gap-3">
                            <button 
                                onClick={() => setIsAddModalOpen(false)}
                                className="flex-1 py-3 text-gray-600 font-bold hover:bg-gray-200 rounded-xl transition"
                            >
                                Cancelar
                            </button>
                            <button 
                                onClick={handleSaveOrder}
                                className="flex-[2] py-3 bg-primary text-white font-bold rounded-xl hover:bg-primary-dark transition shadow-lg shadow-primary/20"
                            >
                                Guardar Pedido
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Modal - Detalles del Pedido */}
            {selectedOrderDetails && (
                <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4">
                    <div className="bg-white rounded-3xl w-full max-w-lg shadow-2xl overflow-hidden">
                        <div className="p-6 border-b border-gray-100 flex justify-between items-center bg-primary text-white">
                            <h2 className="text-xl font-bold">Detalles del Pedido</h2>
                            <button onClick={() => setSelectedOrderDetails(null)} className="p-1 hover:bg-white/10 rounded-full">
                                <X size={24} />
                            </button>
                        </div>
                        <div className="p-6 space-y-4">
                            <div className="flex justify-between items-start">
                                <div>
                                    <p className="text-sm text-gray-500">ID Pedido</p>
                                    <p className="font-mono text-xs">{selectedOrderDetails.id}</p>
                                </div>
                                <div className="text-right">
                                    <p className="text-sm text-gray-500">Fecha</p>
                                    <p className="font-medium">{new Date(selectedOrderDetails.fechaPedido).toLocaleString()}</p>
                                </div>
                            </div>
                            <div className="space-y-2">
                                <h4 className="font-bold text-gray-900 border-b pb-1">Productos</h4>
                                <div className="max-h-40 overflow-y-auto space-y-2 pr-2 custom-scrollbar">
                                    {selectedOrderDetails.detalles?.map((d, i) => (
                                        <div key={i} className="flex justify-between text-sm">
                                            <span>{d.nombreProducto || d.idProducto} x{d.cantidad}</span>
                                            <span className="font-medium">${(d.cantidad * d.precioUnitario).toLocaleString('en-US', { minimumFractionDigits: 2 })}</span>
                                        </div>
                                    ))}
                                </div>
                            </div>
                            <div className="pt-4 border-t flex justify-between items-center">
                                <span className="font-bold text-gray-900 text-lg">TOTAL</span>
                                <span className="font-bold text-primary text-2xl">
                                    ${parseFloat(selectedOrderDetails.total || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                                </span>
                            </div>
                        </div>
                        <div className="p-6 bg-gray-50 flex gap-2">
                            <button onClick={() => setSelectedOrderDetails(null)} className="w-full py-3 bg-gray-200 text-gray-700 font-bold rounded-xl hover:bg-gray-300 transition">
                                Cerrar
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Modal - Confirmar Eliminación */}
            {isConfirmDeleteOpen && (
                <div className="fixed inset-0 bg-black/60 backdrop-blur-sm z-[100] flex items-center justify-center p-4">
                    <div className="bg-white rounded-3xl p-8 max-w-sm w-full text-center shadow-2xl scale-in">
                        <div className="w-20 h-20 bg-red-50 text-red-500 rounded-full flex items-center justify-center mx-auto mb-6">
                            <Trash2 size={40} />
                        </div>
                        <h3 className="text-2xl font-bold text-gray-900 mb-2">¿Eliminar Pedido?</h3>
                        <p className="text-gray-500 mb-8">Esta acción no se puede deshacer y el stock no se restaurará automáticamente.</p>
                        <div className="flex gap-3">
                            <button 
                                onClick={() => setIsConfirmDeleteOpen(false)}
                                className="flex-1 py-3 text-gray-500 font-bold hover:bg-gray-100 rounded-xl transition"
                            >
                                Cancelar
                            </button>
                            <button 
                                onClick={confirmDelete}
                                className="flex-1 py-3 bg-red-500 text-white font-bold rounded-xl hover:bg-red-600 transition shadow-lg shadow-red-200"
                            >
                                Eliminar
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

