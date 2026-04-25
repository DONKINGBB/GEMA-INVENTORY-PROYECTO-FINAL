import { useState, useEffect } from 'react';
import { Search, Plus, Filter, Edit2, Trash2, Download, Package, LayoutGrid, List } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { productService } from '../services/productService';
import ProductForm from '../components/ProductForm';

export default function Inventory() {
    const { user } = useAuth();
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');
    const [viewMode, setViewMode] = useState('grid'); // 'list' or 'grid'

    // Modal State
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingProduct, setEditingProduct] = useState(null);
    const [itemToDelete, setItemToDelete] = useState(null);

    const fetchProducts = async () => {
        if (!user?.id) return;
        try {
            setLoading(true);
            const data = await productService.getAll(user.id);
            if (Array.isArray(data)) {
                setProducts(data);
            } else {
                console.warn("API returned non-array", data);
                setProducts([]);
            }
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchProducts();
    }, [user]);

    const handleCreate = () => {
        setEditingProduct(null);
        setIsModalOpen(true);
    };

    const handleEdit = (product) => {
        setEditingProduct(product);
        setIsModalOpen(true);
    };

    const confirmDelete = (product) => {
        setItemToDelete(product);
    };

    const handleDelete = async () => {
        if (!itemToDelete) return;
        try {
            await productService.delete(itemToDelete.idProducto || itemToDelete.id);
            setItemToDelete(null);
            await fetchProducts(); // Refresh list
        } catch (err) {
            alert("Error al eliminar el producto");
        }
    };

    const handleSave = async (formData) => {
        const productData = {
            ...formData,
            idUsuario: user.id
        };

        try {
            if (editingProduct) {
                await productService.update(editingProduct.idProducto || editingProduct.id, productData);
            } else {
                await productService.create(productData);
            }
            await fetchProducts(); // Refresh list
            setIsModalOpen(false);
        } catch (err) {
            console.error("Error saving product:", err);
        }
    };

    const filteredProducts = products.filter(p =>
        p.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        p.sku?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="space-y-6">
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900 dark:text-white transition-colors">Inventario</h1>
                    <p className="text-gray-500 dark:text-gray-400">Gestiona tus productos y existencias</p>
                </div>
                <button
                    onClick={handleCreate}
                    className="bg-primary text-white px-6 py-3 rounded-xl hover:bg-primary-dark transition shadow-lg flex items-center gap-2 font-medium"
                >
                    <Plus size={20} />
                    <span>Nuevo Producto</span>
                </button>
            </div>

            {/* Filter Bar */}
            <div className="bg-white dark:bg-slate-800 p-4 rounded-xl shadow-sm border border-gray-100 dark:border-slate-700 flex flex-col sm:flex-row gap-4 justify-between items-center transition-colors">
                <div className="relative flex-1 w-full sm:w-auto">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <Search className="h-5 w-5 text-gray-400" />
                    </div>
                    <input
                        type="text"
                        className="block w-full pl-10 pr-3 py-2 border border-gray-200 dark:border-slate-600 rounded-lg leading-5 bg-gray-50 dark:bg-slate-700 placeholder-gray-400 dark:placeholder-gray-500 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent sm:text-sm transition duration-150 ease-in-out"
                        placeholder="Buscar por nombre o SKU..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>
                
                <div className="flex items-center gap-3">
                    <div className="flex bg-gray-100 dark:bg-slate-700 p-1 rounded-xl">
                        <button
                            onClick={() => setViewMode('grid')}
                            className={`p-2 rounded-lg transition ${viewMode === 'grid' ? 'bg-white dark:bg-slate-600 shadow-sm text-primary dark:text-white' : 'text-gray-500 dark:text-gray-400'}`}
                            title="Vista Cuadrícula"
                        >
                            <LayoutGrid size={20} />
                        </button>
                        <button
                            onClick={() => setViewMode('list')}
                            className={`p-2 rounded-lg transition ${viewMode === 'list' ? 'bg-white dark:bg-slate-600 shadow-sm text-primary dark:text-white' : 'text-gray-500 dark:text-gray-400'}`}
                            title="Vista Lista"
                        >
                            <List size={20} />
                        </button>
                    </div>

                    <div className="flex gap-2">
                        <button className="p-3 bg-white dark:bg-slate-800 border border-gray-200 dark:border-slate-700 rounded-xl text-gray-600 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-slate-700 transition">
                            <Filter size={20} />
                        </button>
                        <button className="p-3 bg-white dark:bg-slate-800 border border-gray-200 dark:border-slate-700 rounded-xl text-gray-600 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-slate-700 transition">
                            <Download size={20} />
                        </button>
                    </div>
                </div>
            </div>

            {/* Product Display */}
            {loading ? (
                <div className="p-20 flex justify-center text-primary">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
                </div>
            ) : filteredProducts.length === 0 ? (
                <div className="p-20 text-center bg-white dark:bg-slate-800 rounded-2xl border border-dashed border-gray-200 dark:border-slate-700 transition-colors">
                    <Package className="mx-auto h-12 w-12 text-gray-300 dark:text-gray-600 mb-4" />
                    <h3 className="text-lg font-medium text-gray-900 dark:text-white">No se encontraron productos</h3>
                    <p className="text-gray-500 dark:text-gray-400">Prueba con otra búsqueda o agrega un nuevo producto.</p>
                </div>
            ) : viewMode === 'list' ? (
                <div className="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-gray-100 dark:border-slate-700 overflow-hidden transition-colors">
                    <div className="overflow-x-auto">
                        <table className="w-full text-left text-sm text-gray-600 dark:text-gray-300">
                            <thead className="bg-gray-50 dark:bg-slate-700/50 text-gray-400 dark:text-gray-400 uppercase tracking-wider text-xs font-semibold border-b border-gray-100 dark:border-slate-700">
                                <tr>
                                    <th className="p-4">Producto</th>
                                    <th className="p-4">Categoría</th>
                                    <th className="p-4">Stock</th>
                                    <th className="p-4">Precio</th>
                                    <th className="p-4">Estado</th>
                                    <th className="p-4 text-right">Acciones</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-100 dark:divide-slate-700">
                                {filteredProducts.map((product) => (
                                    <tr key={product.idProducto || product.id} className="hover:bg-gray-50 dark:hover:bg-slate-700/30 transition-colors">
                                        <td className="p-4">
                                            <div className="flex items-center gap-3">
                                                <div className="w-10 h-10 bg-pink-100 dark:bg-pink-900/30 text-pink-600 dark:text-pink-400 rounded-lg flex items-center justify-center font-bold text-xs">
                                                    {product.sku?.substring(0, 3) || 'SKU'}
                                                </div>
                                                <div>
                                                    <p className="font-bold text-gray-900 dark:text-white uppercase truncate max-w-[200px]">{product.nombre}</p>
                                                    <p className="text-xs text-gray-400">{product.sku}</p>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="p-4">
                                            <span className="px-2 py-1 bg-gray-100 dark:bg-slate-700 text-gray-600 dark:text-gray-300 rounded-md text-xs font-medium uppercase">
                                                {product.categoria || 'Sin Categoría'}
                                            </span>
                                        </td>
                                        <td className="p-4">
                                            <p className="font-semibold text-gray-900 dark:text-white">{product.cantidad || 0}</p>
                                            <p className="text-xs text-gray-400">min. {product.stockMinimo || 0}</p>
                                        </td>
                                        <td className="p-4 font-medium text-gray-900 dark:text-white">
                                            ${(product.precioVenta || 0).toLocaleString()}
                                        </td>
                                        <td className="p-4">
                                            {(product.cantidad || 0) > (product.stockMinimo || 0) ? (
                                                <span className="px-2 py-1 bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400 rounded-full text-xs font-bold">En Stock</span>
                                            ) : (product.cantidad || 0) > 0 ? (
                                                <span className="px-2 py-1 bg-yellow-100 dark:bg-yellow-900/30 text-yellow-700 dark:text-yellow-400 rounded-full text-xs font-bold">Bajo Stock</span>
                                            ) : (
                                                <span className="px-2 py-1 bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400 rounded-full text-xs font-bold">Agotado</span>
                                            )}
                                        </td>
                                        <td className="p-4 text-right">
                                            <div className="flex justify-end gap-2">
                                                <button onClick={() => handleEdit(product)} className="p-2 text-blue-600 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition" title="Editar">
                                                    <Edit2 size={18} />
                                                </button>
                                                <button onClick={() => confirmDelete(product)} className="p-2 text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition" title="Eliminar">
                                                    <Trash2 size={18} />
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                    {filteredProducts.map((product) => (
                        <div key={product.idProducto || product.id} className="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-gray-100 dark:border-slate-700 p-4 flex items-center gap-4 hover:shadow-md transition-all group overflow-hidden relative">
                            {/* SKU BOX (Mobile Style) */}
                            <div className="w-16 h-16 bg-[#FCE7F3] dark:bg-pink-900/20 rounded-xl flex items-center justify-center flex-shrink-0">
                                <span className="text-[#DB2777] dark:text-pink-400 font-bold text-xs uppercase text-center px-1 leading-tight">
                                    {product.sku?.includes('-') ? product.sku.split('-')[0] : (product.sku?.substring(0, 3) || 'SKU')}
                                </span>
                            </div>

                            <div className="flex-1 min-w-0">
                                <h3 className="font-bold text-gray-900 dark:text-white truncate uppercase text-sm sm:text-base">{product.nombre}</h3>
                                <p className="text-xs text-gray-500 dark:text-gray-400 mb-2">SKU: {product.sku}</p>
                                
                                {(product.cantidad || 0) > (product.stockMinimo || 0) ? (
                                    <span className="px-2 py-0.5 bg-[#E8F5E9] dark:bg-green-900/30 text-[#388E3C] dark:text-green-400 rounded-md text-[10px] font-bold uppercase">En Stock</span>
                                ) : (product.cantidad || 0) > 0 ? (
                                    <span className="px-2 py-0.5 bg-[#FFF8E1] dark:bg-yellow-900/30 text-[#FFA000] dark:text-yellow-400 rounded-md text-[10px] font-bold uppercase">Bajo Stock</span>
                                ) : (
                                    <span className="px-2 py-0.5 bg-[#FFEBEE] dark:bg-red-900/30 text-[#D32F2F] dark:text-red-400 rounded-md text-[10px] font-bold uppercase">Agotado</span>
                                )}
                            </div>

                            <div className="text-right">
                                <p className="font-bold text-primary dark:text-blue-400 text-sm sm:text-base">${(product.precioVenta || 0).toLocaleString()}</p>
                                <p className="text-[10px] text-gray-400">Stock: {product.cantidad || 0}</p>
                            </div>

                            {/* Hover Actions */}
                            <div className="absolute right-2 top-2 opacity-0 group-hover:opacity-100 transition-opacity flex gap-1 bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm p-1 rounded-lg">
                                <button onClick={() => handleEdit(product)} className="p-1.5 text-blue-600 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition">
                                    <Edit2 size={14} />
                                </button>
                                <button onClick={() => confirmDelete(product)} className="p-1.5 text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition">
                                    <Trash2 size={14} />
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            <ProductForm
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onSubmit={handleSave}
                initialData={editingProduct}
                title={editingProduct ? 'Editar Producto' : 'Nuevo Producto'}
            />

            {itemToDelete && (
                <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
                    <div className="absolute inset-0 bg-black/40 backdrop-blur-sm" onClick={() => setItemToDelete(null)}></div>
                    <div className="bg-white dark:bg-slate-800 rounded-2xl shadow-2xl w-full max-w-sm relative z-10 overflow-hidden transform transition-all scale-100 p-6 text-center border border-gray-100 dark:border-slate-700">
                        <div className="mx-auto flex items-center justify-center h-16 w-16 rounded-full bg-red-100 dark:bg-red-900/30 mb-6">
                            <Trash2 className="h-8 w-8 text-red-600 dark:text-red-400" />
                        </div>
                        <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2">¿Eliminar Producto?</h3>
                        <p className="text-gray-500 dark:text-gray-400 mb-8">
                            Estás a punto de eliminar <span className="font-bold text-gray-700 dark:text-gray-300">{itemToDelete.nombre}</span>. Esta acción no se puede deshacer.
                        </p>
                        <div className="flex gap-3">
                            <button
                                type="button"
                                onClick={() => setItemToDelete(null)}
                                className="flex-1 py-2.5 bg-gray-100 dark:bg-slate-700 text-gray-700 dark:text-gray-300 font-bold rounded-xl hover:bg-gray-200 dark:hover:bg-slate-600 transition"
                            >
                                Cancelar
                            </button>
                            <button
                                type="button"
                                onClick={handleDelete}
                                className="flex-1 py-2.5 bg-red-600 text-white font-bold rounded-xl hover:bg-red-700 transition shadow-md"
                            >
                                Sí, eliminar
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
