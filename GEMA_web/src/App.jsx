
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import PropTypes from 'prop-types';
import Login from './pages/Login';
import Register from './pages/Register';
import ForgotPassword from './pages/ForgotPassword';
import LandingPage from './pages/LandingPage';
import Layout from './components/Layout';
import Home from './pages/Home';
import Inventory from './pages/Inventory';
import Orders from './pages/Orders';
import Clients from './pages/Clients';
import Settings from './pages/Settings';
import CategorySettings from './pages/CategorySettings';
import WarehouseSettings from './pages/WarehouseSettings';
import Finances from './pages/Finances';
import Notifications from './pages/Notifications';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ThemeProvider } from './context/ThemeContext';
import { Toaster } from 'react-hot-toast';
import ProfileSettings from './pages/ProfileSettings';
import SecuritySettings from './pages/SecuritySettings';
import NotificationSettings from './pages/NotificationSettings';
import AppearanceSettings from './pages/AppearanceSettings';
import SupplierSettings from './pages/SupplierSettings';
import BusinessSettings from './pages/BusinessSettings';
import SwitchBusiness from './pages/SwitchBusiness';
import ManageTeam from './pages/ManageTeam';
import PremiumUpgrade from './pages/PremiumUpgrade';


const ProtectedRoute = ({ children }) => {
  const { user } = useAuth();
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

ProtectedRoute.propTypes = {
  children: PropTypes.node.isRequired,
};

function App() {
  return (
    <Router>
      <AuthProvider>
        <ThemeProvider>
          <Toaster position="top-right" />
          <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />

            {/* Main Layout containing nested routes */}
            <Route
              path="/app"
              element={
                <ProtectedRoute>
                  <Layout />
                </ProtectedRoute>
              }
            >
              <Route index element={<Home />} />
              <Route path="inventory" element={<Inventory />} />
              <Route path="orders" element={<Orders />} />
              <Route path="clients" element={<Clients />} />
              <Route path="finances" element={<Finances />} />
              <Route path="notifications" element={<Notifications />} />
              <Route path="settings" element={<Settings />} />
              <Route path="settings/categories" element={<CategorySettings />} />
              <Route path="settings/warehouses" element={<WarehouseSettings />} />
              <Route path="settings/suppliers" element={<SupplierSettings />} />
              <Route path="settings/clients" element={<Clients />} />
              <Route path="settings/profile" element={<ProfileSettings />} />
              <Route path="settings/security" element={<SecuritySettings />} />
              <Route path="settings/notifications" element={<NotificationSettings />} />
              <Route path="settings/appearance" element={<AppearanceSettings />} />
              <Route path="settings/business" element={<BusinessSettings />} />
              <Route path="settings/switch-business" element={<SwitchBusiness />} />
              <Route path="settings/team" element={<ManageTeam />} />
              <Route path="premium" element={<PremiumUpgrade />} />

            </Route>

            {/* Catch-all redirect to home */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </ThemeProvider>
      </AuthProvider>
    </Router>
  );
}

export default App;
