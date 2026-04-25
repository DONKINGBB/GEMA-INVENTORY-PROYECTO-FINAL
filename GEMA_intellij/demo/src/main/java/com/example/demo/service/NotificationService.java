package com.example.demo.service;

import com.example.demo.model.Usuario;

public interface NotificationService {
    void sendNotification(Usuario usuario, String title, String body, String type);
}
