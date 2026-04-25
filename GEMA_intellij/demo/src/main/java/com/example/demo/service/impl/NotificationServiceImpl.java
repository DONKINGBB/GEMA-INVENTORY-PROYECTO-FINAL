package com.example.demo.service.impl;

import com.example.demo.model.Usuario;
import com.example.demo.service.NotificationService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendNotification(Usuario usuario, String title, String body, String type) {
        if (usuario.getFcmToken() == null || usuario.getFcmToken().isEmpty()) {
            return;
        }

        // Verificar preferencias del usuario
        boolean shouldSend = false;
        if ("LOW_STOCK".equals(type) && Boolean.TRUE.equals(usuario.getNotifyLowStock())) {
            shouldSend = true;
        } else if ("NEW_ORDER".equals(type) && Boolean.TRUE.equals(usuario.getNotifyNewOrders())) {
            shouldSend = true;
        } else if ("INVENTORY_CHANGE".equals(type) && Boolean.TRUE.equals(usuario.getNotifyInventoryChanges())) {
            shouldSend = true;
        }

        if (!shouldSend)
            return;

        try {
            Message message = Message.builder()
                    .setToken(usuario.getFcmToken())
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putData("type", type)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            System.err.println("Error sending FCM message: " + e.getMessage());
        }
    }
}
