package com.example.demo.service.impl;

import com.example.demo.dto.UsuarioUpdateDto;
import com.example.demo.model.Usuario;
import com.example.demo.model.Negocio;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.NegocioRepository;
import com.example.demo.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@AllArgsConstructor
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final NegocioRepository negocioRepository;

    @Override
    public List<Usuario> getAll() {
        // Obtenemos el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return java.util.Collections.emptyList();
        }

        Object principal = auth.getPrincipal();
        Usuario currentUser = null;

        if (principal instanceof Usuario) {
            currentUser = (Usuario) principal;
        } else {
            // Si el principal es un String o UserDetails, buscamos el objeto completo (SOLO ACTIVOS)
            currentUser = usuarioRepository.findByUserAndActivoTrue(auth.getName()).orElse(null);
        }

        if (currentUser != null && currentUser.getIdNegocio() != null) {
            // Solo devolvemos a los miembros de la misma empresa (Y que estén ACTIVOS)
            return usuarioRepository.findByIdNegocioAndActivoTrue(currentUser.getIdNegocio());
        }

        return java.util.Collections.emptyList();
    }

    @Override
    public Usuario getById(String id) { // <-- Corregido a String
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public Usuario save(Usuario usuario) {
        // Asegúrate de asignar el idRol
        // (Probablemente quieras recibirlo desde el DTO)
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void delete(String id) {
        // 1. Obtener quién está intentando eliminar (usuario autenticado)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesion no valida");
        }

        // 1. Obtener el usuario que realiza la acción DESDE LA BASE DE DATOS
        // Extraemos el identificador (email) del principal de seguridad
        String username = null;
        Object principal = auth.getPrincipal();
        if (principal instanceof Usuario) {
            username = ((Usuario) principal).getUser();
        } else {
            username = auth.getName();
        }

        Usuario modificador = usuarioRepository.findByUserAndActivoTrue(username).orElse(null);

        if (modificador == null) {
            System.err.println(">>> ERROR: No se encontró al modificador (o está inactivo) en DB: " + username);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Modificador no encontrado en base de datos");
        }

        // 2. Buscar el usuario objetivo
        Usuario objetivo = usuarioRepository.findById(id).orElse(null);
        if (objetivo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario objetivo no encontrado");
        }

        // 3. REGLAS DE JERARQUÍA Y PERTENENCIA
        System.err.println("DEBUG ELIMINACION: ModificadorID=" + modificador.getId() + " [Role:" + modificador.getIdRol() + "] -> ObjetivoID=" + objetivo.getId() + " [Role:" + objetivo.getIdRol() + "]");

        // A. No puedes eliminarte a ti mismo
        if (modificador.getId().equals(objetivo.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Accion denegada: No puedes eliminarte a ti mismo.");
        }

        // B. Validar Negocio del Modificador
        if (modificador.getIdNegocio() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Error: Tu usuario no tiene un negocio asociado.");
        }

        // C. Obtener el Propietario Principal (Master Owner) del negocio
        Negocio negocio = negocioRepository.findById(modificador.getIdNegocio()).orElse(null);
        String masterOwnerId = (negocio != null) ? negocio.getPropietarioId() : null;

        // D. PROTECCIÓN DEL MASTER OWNER
        if (masterOwnerId != null && objetivo.getId().equals(masterOwnerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Accion denegada: El Propietario Principal del negocio no puede ser eliminado.");
        }

        // E. VALIDACIÓN DE PERTENENCIA
        // Si NO eres Dueño (Rol 1), validamos estrictamente que pertenezcan al mismo negocio
        if (modificador.getIdRol() == null || modificador.getIdRol() != 1) {
            if (objetivo.getIdNegocio() == null || !modificador.getIdNegocio().equals(objetivo.getIdNegocio())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                        "Error de Negocio: Solo puedes eliminar miembros de tu propio equipo.");
            }
        }

        // F. COMPROBACIÓN DE JERARQUÍA
        Integer rolMod = modificador.getIdRol();
        Integer rolObj = objetivo.getIdRol();

        System.err.println(">>> JERARQUIA: ModRol=" + rolMod + " ObjRol=" + rolObj);

        // 1. Si el modificador es el DUEÑO (ID ROL 1), tiene permiso TOTAL
        if (rolMod != null && rolMod == 1) {
            System.err.println(">>> PERMITIDO: El Modificador es DUEÑO (Rol 1)");
        } 
        // 2. Jerarquía numérica para otros roles (Admin=2 > Supervisor=3 > ...)
        else if (rolMod != null && rolObj != null && rolMod < rolObj) {
            System.err.println(">>> PERMITIDO: Jerarquia superior (" + rolMod + " < " + rolObj + ")");
        }
        else {
            System.err.println(">>> DENEGADO: Jerarquia insuficiente o nula.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Jerarquía insuficiente: Tu rol (" + rolMod + ") no permite eliminar a este usuario (" + rolObj + ").");
        }

        // 4. ELIMINACIÓN LÓGICA (Soft Delete)
        // En lugar de borrar de la DB, desactivamos para mantener historial
        objetivo.setActivo(false);
        usuarioRepository.save(objetivo);
        
        System.err.println("SUCESO: Usuario " + id + " desactivado con éxito (Soft Delete).");
    }


    @Override
    public Usuario update(String id, Usuario usuario) {
        // 1. Obtener quién está intentando modificar (usuario autenticado)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesion no valida");
        }

        Object principal = auth.getPrincipal();
        Usuario modificador = (principal instanceof Usuario) ? (Usuario) principal : usuarioRepository.findByUserAndActivoTrue(auth.getName()).orElse(null);

        if (modificador == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Modificador no encontrado");
        }

        // 2. Buscar el usuario objetivo
        Usuario objetivo = usuarioRepository.findById(id).orElse(null);
        if (objetivo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario objetivo no encontrado");
        }

        // 3. REGLAS DE JERARQUÍA Y NEGOCIO
        // Deben pertenecer al mismo negocio
        if (modificador.getIdNegocio() == null || !modificador.getIdNegocio().equals(objetivo.getIdNegocio())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Error de Negocio: No puedes modificar usuarios de otra empresa.");
        }

        // --- REGLAS DE PROPIETARIO ÚNICO ---
        // 1. No se puede cambiar el rol de alguien que ya es Propietario (es inmutable)
        if (objetivo.getIdRol() == 1 && usuario.getIdRol() != 1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Accion denegada: El rol de Propietario no puede ser cambiado.");
        }

        // 2. No se puede asignar el rol de Propietario a nadie más
        if (usuario.getIdRol() == 1 && objetivo.getIdRol() != 1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Accion denegada: Solo puede haber un Propietario por negocio (el fundador).");
        }

        // Admin (2) -> Puede modificar a todos excepto al Propietario (1)
        if (modificador.getIdRol() == 2 && objetivo.getIdRol() == 1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Jerarquia insuficiente: Un Administrador no puede modificar al Propietario.");
        }

        // Supervisor (3) -> Solo puede modificar niveles inferiores (4, 5, 6)
        if (modificador.getIdRol() == 3 && objetivo.getIdRol() <= 3) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Jerarquia insuficiente: Un Supervisor solo puede modificar Vendedores, Repartidores u Almacenistas.");
        }

        // Niveles 4, 5 y 6 (Vendedor, Repartidor, Almacenista) -> No pueden modificar nada
        if (modificador.getIdRol() >= 4) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Acceso denegado: Tu rol no tiene permisos para gestionar el equipo.");
        }

        // 4. Actualizar campos
        objetivo.setUser(usuario.getUser());
        objetivo.setNombre(usuario.getNombre());
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            objetivo.setPassword(usuario.getPassword());
        }
        objetivo.setIdRol(usuario.getIdRol());

        return usuarioRepository.save(objetivo);
    }

    @Override
    public Usuario actualizarPerfil(String id, UsuarioUpdateDto dto) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Solo actualizamos los campos permitidos
        u.setNombre(dto.getNombre());
        u.setDireccion(dto.getDireccion());
        u.setTelefono(dto.getTelefono());
        if (dto.getImagenUrl() != null) {
            u.setImagenUrl(dto.getImagenUrl());
        }

        return usuarioRepository.save(u);
    }

    @Override
    public void registersTokenFCM(String id, String token) {
        Usuario u = usuarioRepository.findById(id).orElse(null);
        if (u != null) {
            u.setFcmToken(token);
            usuarioRepository.save(u);
        }
    }

    @Override
    public Usuario updateNotificationSettings(String id, Usuario settings) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        u.setNotifyLowStock(settings.getNotifyLowStock());
        u.setNotifyNewOrders(settings.getNotifyNewOrders());
        u.setNotifyInventoryChanges(settings.getNotifyInventoryChanges());
        return usuarioRepository.save(u);
    }

    @Override
    @Transactional
    public void changePassword(String id, String oldPassword, String newPassword) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
 
        // Validación de contraseña actual (Plain text como el resto del proyecto)
        if (!u.getPassword().equals(oldPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual es incorrecta");
        }
 
        u.setPassword(newPassword);
        usuarioRepository.save(u);
    }
}