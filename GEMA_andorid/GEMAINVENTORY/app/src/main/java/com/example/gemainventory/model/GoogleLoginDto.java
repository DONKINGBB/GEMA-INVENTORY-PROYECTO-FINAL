package com.example.gemainventory.model;
import com.google.gson.annotations.SerializedName;

public class GoogleLoginDto {
    @SerializedName("id") private String id;
    @SerializedName("correo") private String correo;
    @SerializedName("nombre") private String nombre;

    public GoogleLoginDto(String id, String correo, String nombre) {
        this.id = id;
        this.correo = correo;
        this.nombre = nombre;
    }
}