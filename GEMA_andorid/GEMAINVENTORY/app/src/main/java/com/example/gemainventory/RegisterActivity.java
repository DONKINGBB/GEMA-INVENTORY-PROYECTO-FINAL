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

public class RegisterActivity extends AppCompatActivity {

    EditText etNombre, etCorreo, etContrasena, etDireccion, etTelefono;
    Button goToLoginButton, registerConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        goToLoginButton = findViewById(R.id.go_to_login_button);
        registerConfirmButton = findViewById(R.id.register_button_confirm);

        etNombre = findViewById(R.id.et_register_name);
        etCorreo = findViewById(R.id.et_register_email);
        etContrasena = findViewById(R.id.et_register_password);
        etDireccion = findViewById(R.id.et_register_direccion);
        etTelefono = findViewById(R.id.et_register_telefono);

        goToLoginButton.setOnClickListener(v -> {
            finish();
        });

        registerConfirmButton.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();
            String direccion = etDireccion.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();

            if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Nombre, correo y contraseña son requeridos", Toast.LENGTH_SHORT).show();
                return;
            }

            registrarUsuario(nombre, correo, contrasena, direccion, telefono);
        });
    }

    private void registrarUsuario(String nombre, String correo, String contrasena, String direccion, String telefono) {
        Call<LoginResponse> call = RetrofitClient.INSTANCE.getInstance().registrarUsuario(nombre, correo, contrasena, direccion, telefono);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
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