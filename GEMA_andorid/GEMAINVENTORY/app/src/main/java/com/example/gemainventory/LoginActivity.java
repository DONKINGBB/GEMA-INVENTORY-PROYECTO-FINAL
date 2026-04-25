package com.example.gemainventory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.model.GoogleLoginDto;
import com.google.android.material.button.MaterialButton;
// Importa las nuevas clases
import com.example.gemainventory.model.LoginResponse;
import com.example.gemainventory.model.Usuario;

// Importa SharedPreferences y Context para guardar la sesión
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Google
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText etCorreo, etContrasena;
    Button btnLogin, btnGoToRegister; // Asumiendo los IDs
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001; // Un código cualquiera

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // --- BÚSQUEDA CORREGIDA ---
        // ¡Ahora buscamos los IDs de los TextInputEditText!
        etCorreo = findViewById(R.id.et_login_email);
        etContrasena = findViewById(R.id.et_login_password);

        // (El código de findViewById para los botones ya estaba bien)
        btnLogin = findViewById(R.id.login_button);
        btnGoToRegister = findViewById(R.id.register_button);
        Button btnForgotPassword = findViewById(R.id.forgot_password_button);

        btnGoToRegister.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });
 
        btnForgotPassword.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(i);
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // Pedimos el email
                .requestProfile() // Pedimos el nombre
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 2. Listener del Botón Google
        findViewById(R.id.google_login_button).setOnClickListener(v -> {
            signInWithGoogle();
        });

        // --- LÓGICA DE LOGIN ---
        btnLogin.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();
            
            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            iniciarSesion(correo, contrasena);
        });
    }

    private void iniciarSesion(String correo, String contrasena) {
        Call<LoginResponse> call = RetrofitClient.INSTANCE.getInstance().loginUsuario(correo, contrasena);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.getSuccess()) {
                        // ¡ÉXITO! GUARDAMOS LA SESIÓN
                        Usuario usuario = loginResponse.getUsuario();

                        // --- ¡¡PROTECCIÓN AÑADIDA!! ---
                        // Verificamos que el usuario NO sea nulo
                        if (usuario != null && loginResponse.getToken() != null) {
                            guardarSesion(usuario, loginResponse.getToken());

                            // --- REGISTRAMOS EL TOKEN FCM ---
                            registrarTokenFCM(usuario.getIdUsuario());

                            // --- RUTEO ONBOARDING ---
                            Intent i;
                            if (usuario.getIdNegocio() == null) {
                                i = new Intent(LoginActivity.this, SelectBusinessActivity.class);
                            } else {
                                i = new Intent(LoginActivity.this, MainActivity.class);
                            }
                            startActivity(i);
                            finish();
                        } else {
                            // Si el 'usuario' es nulo, mostramos un error en vez de crashear
                            Toast.makeText(LoginActivity.this, "Error: Datos de usuario incompletos",
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // Error (ej. "Credenciales incorrectas")
                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error del servidor: " + response.code(), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ¡¡MÉTODO CRUCIAL PARA GUARDAR LA SESIÓN!!
    // Modificado para guardar el JWT Token y el Rol de Seguridad.
    private void guardarSesion(Usuario usuario, String token) {
        SharedPreferences prefs = getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Log.d("DEBUG_SESION", "Guardando ID: " + usuario.getIdUsuario() + " | Rol: " + usuario.getIdRol());

        editor.putString("user_id", usuario.getIdUsuario());
        editor.putString("user_nombre", usuario.getNombre());
        editor.putString("user_correo", usuario.getCorreo());
        editor.putString("user_direccion", usuario.getDireccion());
        editor.putString("user_telefono", usuario.getTelefono());
        editor.putInt("user_rol", usuario.getIdRol()); // ¡Nuevo! Guardamos el Rol
        editor.putString("jwt_token", token); // ¡Nuevo! Guardamos el Token Seguro
        if (usuario.getIdNegocio() != null) {
            editor.putString("id_negocio", usuario.getIdNegocio());
        } else {
            editor.remove("id_negocio"); // Limpiar por si acaso
        }
        editor.putBoolean("is_logged_in", true);

        editor.apply();
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // 3. Recibir el resultado de Google
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // ¡ÉXITO! Google dice que el usuario es válido.
            String email = account.getEmail();
            String nombre = account.getDisplayName();

            // AHORA: Enviamos estos datos a TU Spring Boot
            loginConGoogleEnBackend(email, nombre);

        } catch (ApiException e) {
            Toast.makeText(this, "Fallo Google: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loginConGoogleEnBackend(String email, String nombre) {
        GoogleLoginDto dto = new GoogleLoginDto(email, nombre);

        Call<LoginResponse> call = RetrofitClient.INSTANCE.getInstance().loginGoogle(dto);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.getSuccess()) {
                        // ¡ÉXITO! Guardamos sesión igual que en el login normal
                        if (loginResponse.getUsuario() != null && loginResponse.getToken() != null) {
                            guardarSesion(loginResponse.getUsuario(), loginResponse.getToken());

                            // --- REGISTRAMOS EL TOKEN FCM ---
                            registrarTokenFCM(loginResponse.getUsuario().getIdUsuario());

                            Intent i;
                            if (loginResponse.getUsuario().getIdNegocio() == null) {
                                i = new Intent(LoginActivity.this, SelectBusinessActivity.class);
                            } else {
                                i = new Intent(LoginActivity.this, MainActivity.class);
                            }
                            startActivity(i);
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error backend", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registrarTokenFCM(String userId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d("FCM", "Token recuperado: " + token);

                    // Enviar al servidor
                    Map<String, String> body = new HashMap<>();
                    body.put("token", token);

                    RetrofitClient.INSTANCE.getInstance().registerFcmToken(userId, body).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Log.d("FCM", "Token registrado en el servidor");
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e("FCM", "Error al enviar token al servidor", t);
                        }
                    });
                });
    }
}
