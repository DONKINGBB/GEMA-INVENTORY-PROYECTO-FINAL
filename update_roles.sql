-- ==========================================
-- ACTUALIZACIÓN DE ROLES - GEMA INVENTORY
-- ==========================================

USE sql_gemma;

-- Limpiar roles antiguos y resetear auto-incremento (Precaución: esto asocia IDs nuevos)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE cat_roles;
SET FOREIGN_KEY_CHECKS = 1;

-- Insertar la nueva jerarquía de 6 niveles
INSERT INTO cat_roles (id_rol, nombre) VALUES 
(1, 'PROPIETARIO'),
(2, 'ADMINISTRADOR'),
(3, 'SUPERVISOR'),
(4, 'VENDEDOR'),
(5, 'REPARTIDOR'),
(6, 'ALMACENISTA');

-- Nota: Si tienes usuarios existentes, asegúrate de actualizar su id_rol 
-- para que coincida con la nueva lógica si es necesario.
-- Ejemplo: UPDATE usuarios SET id_rol = 2 WHERE id_rol = (antiguo administrador);
