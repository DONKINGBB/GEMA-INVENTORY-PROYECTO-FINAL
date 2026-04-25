import { useState, useEffect } from 'react';
import { ArrowLeft, Plus, Edit, Trash2, Loader } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { categoryService } from '../services/inventoryService';
import { useAuth } from '../context/AuthContext';

export default function CategorySettings() {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingCategory, setEditingCategory] = useState(null);
    const [formData, setFormData] = useState({ nombre: '', descripcion: '' });
    const [saving, setSaving] = useState(false);
    const [itemToDelete, setItemToDelete] = useState(null);

    const fetchCategories = async () => {
        if (!user?.id) return;
        try {
            setLoading(true);
            const data = await categoryService.getAll(user.id);
            setCategories(data || []);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCategories();
    }, []);

    const handleCreate = () => {
        setEditingCategory(null);
        setFormData({ nombre: '' });
        setIsModalOpen(true);
    };

    const handleEdit = (category) => {
        setEditingCategory(category);
        setFormData({ nombre: category.nombre });
        setIsModalOpen(true);
    };

    const confirmDelete = (cat) => {
        setItemToDelete(cat);
    };

    const handleDelete = async () => {
        if (!itemToDelete) return;
        try {
            await categoryService.delete(itemToDelete.idCategoria || itemToDelete.id);
            setItemToDelete(null);
            await fetchCategories();
        } catch (error) {
            console.error("Error al eliminar categoría", error);
            alert("No se pudo eliminar la categoría.");
        }
    };

    const handleSave = async (e) => {
        e.preventDefault();
        setSaving(true);
        try {
            if (editingCategory) {
                await categoryService.update(editingCategory.idCategoria || editingCategory.id, formData);
            } else {
                await categoryService.create({ ...formData, idUsuario: user.id }, user.id);
            }
            setIsModalOpen(false);
            await fetchCategories();
        } catch (err) {
            alert("Error al guardar");
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="max-w-4xl mx-auto space-y-6">
            <div className="flex items-center gap-4 mb-6">
                <button 
                    onClick={() => navigate('/settings')}
                    className="p-2 hover:bg-gray-100 dark:hover:bg-slate-700 rounded-full transition"
                >
                    <ArrowLeft size={24} className="text-gray-600 dark:text-gray-400" />
                </button>
                <div>
                    <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Categorías</h1>
                    <p className="text-gray-500 dark:text-gray-400">Gestiona las categorías de tus productos</p>
                </div>
            </div>

            <div className="flex justify-end mb-4">
                <button
                    onClick={handleCreate}
                    className="bg-primary hover:bg-primary-dark text-white px-4 py-2 rounded-xl transition shadow flex items-center gap-2 font-medium"
                >
                    <Plus size={20} />
                    <span>Nueva Categoría</span>
                </button>
            </div>

            <div className="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-gray-100 dark:border-slate-700 overflow-hidden">
                {loading ? (
                    <div className="p-12 flex justify-center text-primary dark:text-blue-400">
                        <Loader className="animate-spin" size={32} />
                    </div>
                ) : (
                    <table className="w-full text-left text-sm text-gray-600 dark:text-gray-300">
                        <thead className="bg-gray-50 dark:bg-slate-900/50 text-gray-700 dark:text-gray-300 uppercase tracking-wider text-xs font-semibold border-b border-gray-100 dark:border-slate-700">
                            <tr>
                                <th className="p-5">Nombre de Categoría</th>
                                <th className="p-5 text-right">Acciones</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100 dark:divide-slate-700">
                            {categories.map((c) => (
                                <tr key={c.idCategoria || c.id} className="hover:bg-gray-50 dark:hover:bg-slate-700/50 transition-colors">
                                    <td className="p-5 font-medium text-gray-900 dark:text-white">{c.nombre}</td>
                                    <td className="p-5 text-right">
                                        <div className="flex justify-end gap-2">
                                            <button
                                                onClick={() => handleEdit(c)}
                                                className="text-blue-600 dark:text-blue-400 hover:text-blue-800 dark:hover:text-blue-300 p-2 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition"
                                            >
                                                <Edit size={18} />
                                            </button>
                                            <button onClick={() => confirmDelete(c)} className="text-gray-400 hover:text-red-500 transition-colors p-2 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20" title="Eliminar">
                                                <Trash2 size={18} />
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                            {categories.length === 0 && (
                                <tr>
                                    <td colSpan="2" className="p-10 text-center text-gray-500 dark:text-gray-400">No hay categorías registradas</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                )}
            </div>

            {isModalOpen && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4 animate-in fade-in duration-200">
                    <div className="bg-white dark:bg-slate-800 rounded-2xl shadow-2xl w-full max-w-md">
                        <div className="p-6 border-b border-gray-100 dark:border-slate-700">
                            <h2 className="text-xl font-bold dark:text-white">{editingCategory ? 'Editar Categoría' : 'Nueva Categoría'}</h2>
                        </div>
                        <form onSubmit={handleSave} className="p-6 space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Nombre</label>
                                <input
                                    type="text"
                                    required
                                    value={formData.nombre}
                                    onChange={(e) => setFormData({ nombre: e.target.value })}
                                    className="w-full px-4 py-2 bg-gray-50 dark:bg-slate-900 border border-gray-300 dark:border-slate-700 rounded-lg focus:ring-2 focus:ring-primary outline-none transition dark:text-white"
                                />
                            </div>
                            <div className="flex justify-end gap-3 pt-4 border-t border-gray-100 dark:border-slate-700 mt-4">
                                <button
                                    type="button"
                                    onClick={() => setIsModalOpen(false)}
                                    className="px-4 py-2 border border-gray-300 dark:border-slate-700 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-slate-700"
                                >
                                    Cancelar
                                </button>
                                <button
                                    type="submit"
                                    disabled={saving}
                                    className="px-4 py-2 bg-primary dark:bg-blue-600 text-white rounded-lg hover:bg-primary-dark dark:hover:bg-blue-700 flex items-center gap-2"
                                >
                                    {saving && <Loader size={16} className="animate-spin" />}
                                    Guardar
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {itemToDelete && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                    <div className="absolute inset-0 bg-black/40 backdrop-blur-sm" onClick={() => setItemToDelete(null)}></div>
                    <div className="bg-white dark:bg-slate-800 rounded-2xl shadow-2xl w-full max-w-sm relative z-10 overflow-hidden transform transition-all scale-100 p-6 text-center">
                        <div className="mx-auto flex items-center justify-center h-16 w-16 rounded-full bg-red-100 dark:bg-red-900/30 mb-6">
                            <Trash2 className="h-8 w-8 text-red-600 dark:text-red-400" />
                        </div>
                        <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2">¿Eliminar Categoría?</h3>
                        <p className="text-gray-500 dark:text-gray-400 mb-8">
                            Estás a punto de eliminar <span className="font-bold text-gray-700 dark:text-gray-200">{itemToDelete.nombre}</span>. Esta acción no se puede deshacer.
                        </p>
                        <div className="flex gap-3">
                            <button
                                type="button"
                                onClick={() => setItemToDelete(null)}
                                className="flex-1 shrink-0 py-2.5 bg-gray-100 dark:bg-slate-700 text-gray-700 dark:text-gray-300 font-bold rounded-xl hover:bg-gray-200 dark:hover:bg-slate-600 transition shadow-sm"
                            >
                                Cancelar
                            </button>
                            <button
                                type="button"
                                onClick={handleDelete}
                                className="flex-1 shrink-0 py-2.5 bg-red-600 text-white font-bold rounded-xl hover:bg-red-700 transition shadow-md"
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
