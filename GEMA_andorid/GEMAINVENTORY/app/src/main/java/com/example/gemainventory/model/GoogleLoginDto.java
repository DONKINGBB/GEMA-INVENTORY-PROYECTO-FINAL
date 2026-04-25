package com.example.gemainventory.model;
import com.google.gson.annotations.SerializedName;

public class GoogleLoginDto {
    @SerializedName("correo") private String correo;
    @SerializedName("nombre") private String nombre;

    public GoogleLoginDto(String correo, String nombre) {
        this.correo = correo;
        this.nombre = nombre;
    }
}