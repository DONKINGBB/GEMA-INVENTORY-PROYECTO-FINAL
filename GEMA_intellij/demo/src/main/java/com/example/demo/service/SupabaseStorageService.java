package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String uploadFile(String bucket, MultipartFile file) throws IOException {
        // Generar nombre único para el archivo
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.lastIndexOf('.') > 0) {
            extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        }
        String fileName = UUID.randomUUID().toString() + extension;

        // Construir la URL de subida
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + fileName;

        // Configurar cabeceras
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.set("apikey", supabaseKey);
        headers.setContentType(MediaType.parseMediaType(file.getContentType()));

        // Crear la petición
        HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);

        // Enviar a Supabase
        ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // Retornar la URL pública
            return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + fileName;
        } else {
            throw new IOException("Error al subir a Supabase: " + response.getBody());
        }
    }
}
