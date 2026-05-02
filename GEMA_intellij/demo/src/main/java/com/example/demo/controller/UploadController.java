package com.example.demo.controller;

import com.example.demo.service.SupabaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private SupabaseStorageService supabaseStorageService;

    @PostMapping
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam(value = "folder", defaultValue = "general") String folder,
            @RequestParam("file") MultipartFile file) {
        
        Map<String, String> response = new HashMap<>();
        
        if (file.isEmpty()) {
            response.put("error", "Error: Archivo vacío");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            // Determinar el bucket de Supabase (SIEMPRE EN MINÚSCULAS)
            String bucket = "general"; 
            if ("profiles".equalsIgnoreCase(folder)) {
                bucket = "profiles";
            } else if ("products".equalsIgnoreCase(folder)) {
                bucket = "products";
            }

            System.out.println("Intentando subir a Supabase. Bucket: " + bucket + " | Folder param: " + folder);

            // Subir a Supabase Storage
            String fileUrl = supabaseStorageService.uploadFile(bucket, file);

            System.out.println("Subida exitosa: " + fileUrl);

            // Devolver la URL pública devuelta por Supabase
            response.put("url", fileUrl);
            
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            System.err.println("ERROR CRÍTICO EN UPLOAD: " + ex.getMessage());
            ex.printStackTrace();
            response.put("error", "Error al subir el archivo a Supabase: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
