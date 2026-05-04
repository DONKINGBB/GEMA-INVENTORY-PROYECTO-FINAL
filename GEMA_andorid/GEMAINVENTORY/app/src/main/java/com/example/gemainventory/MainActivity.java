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

        // --- BARRA DE NAVEGACIÓN FLOTANTE (COMPOSE) ---
        androidx.compose.ui.platform.ComposeView composeNavView = findViewById(R.id.compose_nav_view);
        com.example.gemainventory.ui.navigation.NavBarHelper navHelper = new com.example.gemainventory.ui.navigation.NavBarHelper(composeNavView);

        // --- PROTECCIÓN POR ROLES ---
        int rolUsuario = sharedPreferences.getInt("user_rol", 1);
        aplicarRestriccionesDeRolCompose(navHelper, rolUsuario);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // Sincronizar selección de Compose con Navegación
            navHelper.setOnItemClick(itemId -> {
                androidx.navigation.NavOptions navOptions = new androidx.navigation.NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setRestoreState(false)
                        .setPopUpTo(navController.getGraph().getStartDestinationId(), false)
                        .build();

                try {
                    navController.navigate(itemId, null, navOptions);
                    return kotlin.Unit.INSTANCE;
                } catch (IllegalArgumentException e) {
                    return kotlin.Unit.INSTANCE;
                }
            });

            // Sincronizar cambios externos (ej. navegación programática)
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int destId = destination.getId();
                // Map sub-screens to their parent nav item
                int selectedId;
                if (destId == R.id.navigation_product_form || destId == R.id.navigation_add_product
                        || destId == R.id.navigation_add_category || destId == R.id.navigation_add_warehouse
                        || destId == R.id.navigation_product_detail) {
                    selectedId = R.id.navigation_inventory;
                } else if (destId == R.id.navigation_add_order) {
                    selectedId = R.id.navigation_orders;
                } else if (destId == R.id.navigation_edit_profile) {
                    selectedId = R.id.navigation_settings;
                } else {
                    selectedId = destId;
                }
                navHelper.setSelectedId(selectedId);
            });
        }
    }

    private void aplicarRestriccionesDeRolCompose(com.example.gemainventory.ui.navigation.NavBarHelper navHelper, int idRol) {
        java.util.List<Integer> idsVisibles = new java.util.ArrayList<>();
        idsVisibles.add(R.id.navigation_dashboard);
        idsVisibles.add(R.id.navigation_inventory);
        idsVisibles.add(R.id.navigation_orders);

        if (idRol == 3) { // Operario: NO ve Finanzas ni Ajustes
            // Solo los base ya añadidos
        } else if (idRol == 2) { // Supervisor: Ve Ajustes pero no Finanzas (según lógica previa)
            idsVisibles.add(R.id.navigation_settings);
        } else { // Admin: Ve todo
            idsVisibles.add(R.id.navigation_finances);
            idsVisibles.add(R.id.navigation_settings);
        }
        
        navHelper.setVisibleItems(idsVisibles);
    }
}