package com.example.gemainventory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

@SuppressWarnings("CustomSplashScreen") // Usamos uno simple para el prototipo
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1500; // 1.5 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Handler para navegar después del retraso
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            android.content.SharedPreferences prefs = getSharedPreferences("GemaPrefs", android.content.Context.MODE_PRIVATE);
            boolean isLogged = prefs.getBoolean("is_logged_in", false);
            String idNegocio = prefs.getString("id_negocio", null);

            Intent intent;
            if (isLogged) {
                if (idNegocio != null && !idNegocio.trim().isEmpty()) {
                    // Estado 3: Todo listo -> Dashboard
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    // Estado 2: Logueado pero sin negocio -> Onboarding directo
                    // Esto evita que el usuario vea la pantalla de bienvenida si ya tiene sesión.
                    intent = new Intent(SplashActivity.this, SelectBusinessActivity.class);
                }
            } else {
                // Estado 1: Sin sesión -> Login/Bienvenida
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            finish(); // Cierra esta actividad
        }, SPLASH_DELAY);
    }
}
