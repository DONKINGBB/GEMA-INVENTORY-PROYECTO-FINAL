SELECT * FROM sql_gemma.usuarios;
USE sql_gemma;
INSERT IGNORE INTO almacenes (id_almacen, nombre, direccion, activo) 
VALUES (1, 'Almacén Principal', 'Dirección Central', 1);

select*from sql_gemma.productos;