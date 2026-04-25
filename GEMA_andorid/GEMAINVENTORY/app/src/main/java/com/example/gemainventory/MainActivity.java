package com.example.gemainventory;

import android.content.Context; // Importa esto
import android.content.SharedPreferences; // Importa esto
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate; // Importa esto
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- APLICAR TEMA GUARDADO ---
        // ¡¡Debe ir ANTES de setContentView()!!
        SharedPreferences sharedPreferences = getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("DarkMode", false); // 'false' = por defecto

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setContentView(R.layout.activity_main);

        // --- PROTECCIÓN BIOMÉTRICA ---
        boolean useBiometric = sharedPreferences.getBoolean("use_biometric", false);
        if (useBiometric && com.example.gemainventory.util.BiometricHelper.isBiometricAvailable(this)) {
            // Ocultamos el contenido hasta que se autentique
            findViewById(R.id.container).setVisibility(android.view.View.GONE);

            com.example.gemainventory.util.BiometricHelper.showBiometricPrompt(
                    this,
                    "Acceso Protegido",
                    "Usa tu huella o PIN para entrar a GEMA Inventory",
                    new com.example.gemainventory.util.BiometricHelper.BiometricCallback() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(() -> {
                                findViewById(R.id.container).setVisibility(android.view.View.VISIBLE);
                            });
                        }

                        @Override
                        public void onError(String error) {
                            runOnUiThread(() -> {
                                android.widget.Toast.makeText(MainActivity.this, "Error: " + error, android.widget.Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }

                        @Override
                        public void onCancel() {
                            finish();
                        }
                    }
            );
        }
        // --- FIN PROTECCIÓN BIOMÉTRICA ---

        // Encontrar los componentes de navegación
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // --- PROTECCIÓN POR ROLES ---
        int rolUsuario = sharedPreferences.getInt("user_rol", 1); // Por defecto asume 1 (Admin) si falla
        aplicarRestriccionesDeRol(navView, rolUsuario);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // Conectar el BottomNavigationView con el NavController
            NavigationUI.setupWithNavController(navView, navController);

            // PERSONALIZACIÓN: Reiniciar la pestaña al cambiar (No restaurar estado)
            navView.setOnItemSelectedListener(item -> {
                androidx.navigation.NavOptions navOptions = new androidx.navigation.NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setRestoreState(false) // FALSE: Para que reinicie el fragmento
                        .setPopUpTo(navController.getGraph().getStartDestinationId(), false)
                        .build();

                try {
                    navController.navigate(item.getItemId(), null, navOptions);
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            });

            // FIX: Limpiar la pila al volver a pulsar el mismo ítem (Reselección)
            navView.setOnItemReselectedListener(item -> {
                navController.popBackStack(item.getItemId(), false);
            });
        }
    }

    private void aplicarRestriccionesDeRol(BottomNavigationView navView, int idRol) {
        android.view.Menu menu = navView.getMenu();

        if (idRol == 3) { // 3 = Operario
            // El operario NO tiene acceso a la pestaña de "Finanzas" (Costos de compra,
            // facturación)
            menu.findItem(R.id.navigation_finances).setVisible(false);

            // El operario NO tiene acceso a "Ajustes", que es donde se crean usuarios o
            // catalogos base
            menu.findItem(R.id.navigation_settings).setVisible(false);
        } else if (idRol == 2) { // 2 = Supervisor
            // El supervisor tiene acceso a casi todo, excepto la administración profunda de
            // usuarios
            // Podrías ocultar opciones específicas dentro del fragmento de Settings,
            // pero le dejamos ver el tab principal (donde quizás cree categorías).
        }
        // Rol 1 (Admin) = Ve todo por defecto.
    }
}