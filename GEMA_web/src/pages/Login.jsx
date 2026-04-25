import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LogIn, ShoppingBag, User } from 'lucide-react';
import api from '../services/api';
import { useTheme } from '../context/ThemeContext';

export default function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const { login } = useAuth();
    const { isDarkMode } = useTheme();
    const navigate = useNavigate();
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const params = new URLSearchParams();
            params.append('correo', email);
            params.append('contrasena', password);

            const response = await api.post('/auth/login', params, {
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            });

            const data = response.data;
            if (data.success) {
                // Ensure token is stored within the user object so `api.js` interceptor can pick it up
                const userDataToStore = {
                    ...data.usuario,
                    token: data.token
                };
                login(userDataToStore);
                navigate('/');
            } else {
                setError(data.message || 'Credenciales inválidas');
            }
        } catch (err) {
            console.error(err);
            setError('Error al iniciar sesión. Intente nuevamente.');
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-primary dark:bg-[#0f172a] p-4 transition-colors duration-200">
            <div className="bg-white dark:bg-slate-800 p-8 rounded-2xl shadow-xl w-full max-w-md border-t-4 border-accent dark:border-blue-600 transition-colors">
                <div className="text-center mb-8">
                    <div className="flex justify-center mb-4">
                        <img 
                            src={isDarkMode ? "/gema_white.svg" : "/src/assets/ic_logo_cuadrado_bb.png"} 
                            alt="GEMA Logo" 
                            className="w-24 h-24 object-contain" 
                        />
                    </div>
                    <h1 className="text-3xl font-bold text-primary dark:text-blue-400">GEMA Inventory</h1>
                    <p className="text-gray-500 dark:text-gray-400 mt-2">Bienvenido, inicia sesión para continuar</p>
                </div>

                {error && (
                    <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4" role="alert">
                        <span className="block sm:inline">{error}</span>
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-6">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Correo Electrónico / Usuario</label>
                        <div className="relative">
                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <User className="h-5 w-5 text-gray-400" />
                            </div>
                            <input
                                type="text"
                                required
                                className="pl-10 block w-full px-3 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary dark:focus:ring-blue-600 focus:border-transparent transition-colors bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
                                placeholder="Ingresa tu usuario"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                            />
                        </div>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Contraseña</label>
                        <div className="relative">
                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <LogIn className="h-5 w-5 text-gray-400" />
                            </div>
                            <input
                                type="password"
                                required
                                className="pl-10 block w-full px-3 py-2 border border-gray-300 dark:border-slate-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary dark:focus:ring-blue-600 focus:border-transparent transition-colors bg-white dark:bg-slate-700 text-gray-900 dark:text-white"
                                placeholder="••••••••"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                            />
                        </div>
                    </div>
                    <button
                        type="submit"
                        className="w-full flex justify-center py-3 px-4 border border-transparent rounded-lg shadow-sm text-sm font-bold text-white bg-primary dark:bg-blue-600 hover:bg-primary-dark dark:hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary transition-colors transform hover:scale-[1.02]"
                    >
                        Ingresar
                    </button>
                </form>
            </div>
        </div>
    );
}
