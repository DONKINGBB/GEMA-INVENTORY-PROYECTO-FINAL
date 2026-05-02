package com.example.demo.service;
 
import com.example.demo.model.PasswordResetToken;
import com.example.demo.model.Usuario;
import com.example.demo.repository.PasswordResetTokenRepository;
import com.example.demo.repository.UsuarioRepository;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
 
import java.time.LocalDateTime;
import java.util.Random;
 
@Service
@RequiredArgsConstructor
public class PasswordResetService {
 
    private final PasswordResetTokenRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final Resend resend;
 
    @Transactional
    public void requestPasswordReset(String email) {
        // 1. Verificar si el usuario existe
        usuarioRepository.findByCorreoAndActivoTrue(email).orElseThrow(() -> 
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Correo no registrado"));
 
        // 2. Limpiar tokens anteriores
        tokenRepository.deleteByEmail(email);
 
        // 3. Generar código de 6 dígitos
        String code = String.format("%06d", new Random().nextInt(1000000));
 
        // 4. Guardar token (expira en 15 minutos)
        PasswordResetToken token = PasswordResetToken.builder()
                .email(email)
                .code(code)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();
        tokenRepository.save(token);
 
        // 5. Enviar correo via Resend
        enviarCorreoResend(email, code);
    }
 
    private void enviarCorreoResend(String email, String code) {
        String htmlContent = "<!DOCTYPE html>" +
                "<html><head><style>" +
                "body { font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f4f7fa; margin: 0; padding: 0; }" +
                ".container { max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 30px rgba(0,0,0,0.1); }" +
                ".header { background-color: #0d2558; padding: 30px; text-align: center; color: #ffffff; }" +
                ".content { padding: 40px; text-align: center; color: #333333; }" +
                ".code-box { background-color: #f0f4ff; border: 2px dashed #0d2558; border-radius: 12px; padding: 20px; display: inline-block; margin: 25px 0; }" +
                ".code { font-size: 42px; font-weight: 800; letter-spacing: 8px; color: #0d2558; }" +
                ".footer { padding: 25px; text-align: center; font-size: 13px; color: #888888; background-color: #fdfdfd; border-top: 1px solid #eeeeee; }" +
                ".button { background-color: #0d2558; color: #ffffff; padding: 15px 30px; border-radius: 8px; text-decoration: none; display: inline-block; font-weight: bold; margin-top: 20px; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "  <div class='header'>" +
                "    <img src='https://i.ibb.co/TxCNQD2b/GEMA-INVENTORY-CUADRADO-BB-2.png' alt='GEMA Logo' style='height: 80px; margin-bottom: 10px; border-radius: 12px;'>" +
                "    <div style='letter-spacing: 4px; color: #ffffff; font-weight: 900; font-size: 22px;'>GEMA INVENTORY</div>" +
                "  </div>" +
                "  <div class='content'>" +
                "    <h2 style='color: #0d2558; margin-bottom: 20px;'>Recuperación de Acceso</h2>" +
                "    <p style='font-size: 16px; line-height: 1.6;'>Has solicitado restablecer tu contraseña. Para continuar con el proceso, introduce el siguiente <b>código de verificación</b> de 6 dígitos en la aplicación:</p>" +
                "    <div class='code-box'><span class='code'>" + code + "</span></div>" +
                "    <p style='font-size: 14px; color: #666666;'>Este código único es válido por <b>15 minutos</b>. Nunca compartas este código con nadie por tu seguridad.</p>" +
                "  </div>" +
                "  <div class='footer'>" +
                "    <p style='margin: 0;'>© 2026 JEDD AI S.A de C.V. Todos los derechos reservados.</p>" +
                "    <p style='margin: 5px 0 0 0;'>Si no has solicitado este cambio, puedes ignorar este mensaje de forma segura.</p>" +
                "  </div>" +
                "</div>" +
                "</body></html>";
 
        CreateEmailOptions options = CreateEmailOptions.builder()
                .from("GEMA Inventory <onboarding@resend.dev>") // Usando el remitente de prueba de Resend
                .to(email)
                .subject("Código de Recuperación: " + code)
                .html(htmlContent)
                .build();
 
        try {
            CreateEmailResponse response = resend.emails().send(options);
            System.out.println("Correo enviado con ID: " + response.getId());
        } catch (ResendException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al enviar correo: " + e.getMessage());
        }
    }
 
    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        // 1. Validar token
        PasswordResetToken token = tokenRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código o correo inválido"));
 
        if (token.isExpired()) {
            tokenRepository.delete(token);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código ha expirado");
        }
 
        // 2. Actualizar contraseña del usuario
        Usuario user = usuarioRepository.findByCorreoAndActivoTrue(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        
        user.setPasswordHash(newPassword); // Plain text como el resto del proyecto
        usuarioRepository.save(user);
 
        // 3. Eliminar token usado
        tokenRepository.delete(token);
    }
}
