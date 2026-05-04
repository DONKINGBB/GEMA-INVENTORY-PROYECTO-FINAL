package com.example.gemainventory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.model.LoginResponse;
import com.example.gemainventory.model.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.compose.ui.platform.ComposeView;
import com.example.gemainventory.ui.auth.RegisterComposeHelper;

public class RegisterActivity extends AppCompatActivity {

    private RegisterComposeHelper composeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ComposeView composeView = new ComposeView(this);
        setContentView(composeView);

        composeHelper = new RegisterComposeHelper(composeView);
        
        composeHelper.setOnRegisterListener((nombre, correo, contrasena, direccion, telefono) -> {
            if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Nombre, correo y contraseña son requeridos", Toast.LENGTH_SHORT).show();
            } else {
                registrarUsuario(nombre, correo, contrasena, direccion, telefono);
            }
        });

        composeHelper.setOnBackToLogin(() -> {
            finish();
        });
    }

    private void registrarUsuario(String nombre, String correo, String contrasena, String direccion, String telefono) {
        composeHelper.setLoading(true);
        Call<LoginResponse> call = RetrofitClient.INSTANCE.getInstance().registrarUsuario(nombre, correo, contrasena, direccion, telefono);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                composeHelper.setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    
                    if (loginResponse.getSuccess()) {
                        Toast.makeText(RegisterActivity.this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show();
                        
                        Usuario usuario = loginResponse.getUsuario();
                        String token = loginResponse.getToken();

                        if (usuario != null && token != null) {
                            guardarSesion(usuario, token);
                            
                            // Navegación directa al Onboarding (SelectBusinessActivity)
                            Intent i = new Intent(RegisterActivity.this, SelectBusinessActivity.class);
                            startActivity(i);
                            finish();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Error del servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                composeHelper.setLoading(false);
                Log.e("API_FAILURE", "Fallo en el registro: " + t.getMessage());
                Toast.makeText(RegisterActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarSesion(Usuario usuario, String token) {
        SharedPreferences prefs = getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("user_id", usuario.getIdUsuario());
        editor.putString("user_nombre", usuario.getNombre());
        editor.putString("user_correo", usuario.getCorreo());
        editor.putString("user_direccion", usuario.getDireccion());
        editor.putString("user_telefono", usuario.getTelefono());
        editor.putInt("user_rol", usuario.getIdRol());
        editor.putString("jwt_token", token);
        editor.putBoolean("is_logged_in", true);
        
        // Al registrarse, el id_negocio siempre es null/vacío
        editor.remove("id_negocio");

        editor.apply();
    }
}