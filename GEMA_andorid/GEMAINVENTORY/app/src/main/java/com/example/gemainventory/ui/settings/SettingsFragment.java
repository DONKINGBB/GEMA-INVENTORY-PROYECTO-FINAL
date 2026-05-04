package com.example.gemainventory.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView; // <-- ¡¡AÑADIDO ESTE IMPORT!!
import android.widget.EditText;
import android.widget.Toast;

// Imports de Retrofit y Negocio
import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.api.ApiService;
import com.example.gemainventory.model.NegocioDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.compose.ui.platform.ComposeView;

import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.example.gemainventory.R;
import com.example.gemainventory.LoginActivity;

import androidx.lifecycle.ViewTreeLifecycleOwner;
import androidx.lifecycle.ViewTreeViewModelStoreOwner;
import androidx.savedstate.ViewTreeSavedStateRegistryOwner;

public class SettingsFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "GemaPrefs";
    private static final String DARK_MODE_KEY = "DarkMode";
    private ComposeView composeView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = requireContext().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        
        composeView = new ComposeView(requireContext());
        return composeView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateSettingsUI();
    }

    private void updateSettingsUI() {
        if (composeView == null) return;

        String nombre = sharedPreferences.getString("user_nombre", "Sin Nombre");
        String correo = sharedPreferences.getString("user_correo", "Sin Correo");
        String rawPhotoUrl = sharedPreferences.getString("user_foto_url", null);
        String photoUrl = com.example.gemainventory.api.RetrofitClient.getFullImageUrl(rawPhotoUrl);
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
        boolean useBiometric = sharedPreferences.getBoolean("use_biometric", false);
        int userRol = sharedPreferences.getInt("user_rol", -1);
        boolean showManageUsers = (userRol == 1);

        SettingsComposeHelper.setSettingsContent(
            composeView,
            isDarkMode,
            nombre,
            correo,
            photoUrl,
            useBiometric,
            showManageUsers,
            () -> { navigateTo(R.id.action_navigation_edit_profile_self); return kotlin.Unit.INSTANCE; },
            isChecked -> { toggleDarkMode(isChecked); return kotlin.Unit.INSTANCE; },
            isChecked -> { toggleBiometric(isChecked); return kotlin.Unit.INSTANCE; },
            () -> { navigateTo(R.id.action_settings_to_manage); return kotlin.Unit.INSTANCE; },
            () -> { openBusinessActivity(); return kotlin.Unit.INSTANCE; },
            () -> { showBusinessChoiceBottomSheet(); return kotlin.Unit.INSTANCE; },
            () -> { navigateTo(R.id.action_settings_to_switch_business); return kotlin.Unit.INSTANCE; },
            () -> { navigateTo(R.id.action_settings_to_users); return kotlin.Unit.INSTANCE; },
            () -> { navigateTo(R.id.navigation_notification_settings); return kotlin.Unit.INSTANCE; },
            () -> { navigateTo(R.id.action_settings_to_manual); return kotlin.Unit.INSTANCE; },
            () -> { showLogoutDialog(); return kotlin.Unit.INSTANCE; }
        );
    }

    private void navigateTo(int actionId) {
        try {
            NavHostFragment.findNavController(this).navigate(actionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleDarkMode(boolean isChecked) {
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            saveThemePreference(true);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            saveThemePreference(false);
        }
        updateSettingsUI();
    }

    private void toggleBiometric(boolean isChecked) {
        if (isChecked) {
            if (com.example.gemainventory.util.BiometricHelper.isBiometricAvailable(requireContext())) {
                sharedPreferences.edit().putBoolean("use_biometric", true).apply();
                Toast.makeText(getContext(), "Seguridad Biométrica activada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Tu dispositivo no soporta biometría", Toast.LENGTH_LONG).show();
            }
        } else {
            sharedPreferences.edit().putBoolean("use_biometric", false).apply();
        }
        updateSettingsUI();
    }

    private void saveThemePreference(boolean isDarkMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DARK_MODE_KEY, isDarkMode);
        editor.apply();
    }


    // --- MÉTODO 'showLogoutDialog' ACTUALIZADO ---
    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que quieres cerrar tu sesión?")
                .setPositiveButton("Sí, Salir", (dialog, which) -> {

                    // --- BORRADO SELECTIVO DE SESIÓN (Preserva el Modo Oscuro) ---
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("user_id");
                    editor.remove("user_nombre");
                    editor.remove("user_correo");
                    editor.remove("user_direccion");
                    editor.remove("user_telefono");
                    editor.remove("user_rol");
                    editor.remove("jwt_token");
                    editor.remove("id_negocio");
                    editor.putBoolean("is_logged_in", false);
                    editor.apply();
                    // --- FIN DE LA LÓGICA DE BORRADO ---

                    // Tu código para volver al Login (esto estaba perfecto)
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    // --- LÓGICA DE NEGOCIO ---
    private void openBusinessActivity() {
        Intent intent = new Intent(requireActivity(), com.example.gemainventory.ui.settings.BusinessActivity.class);
        startActivity(intent);
    }

    // --- UI PREMIUM: BOTTOM SHEET ---
    private void showBusinessChoiceBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_business_choice, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        bottomSheetView.findViewById(R.id.btn_create_business).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showCreateBusinessDialog();
        });

        bottomSheetView.findViewById(R.id.btn_join_business).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showJoinBusinessDialog();
        });

        bottomSheetDialog.show();
    }

    private void showCreateBusinessDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        ComposeView composeView = new ComposeView(requireContext());
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);

        SettingsComposeHelper.setCreateBusinessContent(
            composeView,
            isDarkMode,
            name -> {
                createBusiness(name, bottomSheetDialog);
                return kotlin.Unit.INSTANCE;
            },
            () -> {
                bottomSheetDialog.dismiss();
                return kotlin.Unit.INSTANCE;
            }
        );

        composeView.setViewCompositionStrategy(androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed.INSTANCE);
        bottomSheetDialog.setContentView(composeView);
        bottomSheetDialog.show();

        // IMPORTANTE: Para Diálogos, los owners deben estar en la DecorView del diálogo
        View decorView = bottomSheetDialog.getWindow().getDecorView();
        ViewTreeLifecycleOwner.set(decorView, getViewLifecycleOwner());
        ViewTreeViewModelStoreOwner.set(decorView, this);
        ViewTreeSavedStateRegistryOwner.set(decorView, this);
    }

    private void createBusiness(String name, BottomSheetDialog dialog) {
        ApiService api = RetrofitClient.INSTANCE.getInstance();
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("nombre", name);

        Toast.makeText(getContext(), "Creando negocio...", Toast.LENGTH_SHORT).show();

        api.createNegocio(body).enqueue(new Callback<java.util.Map<String, Object>>() {
            @Override
            public void onResponse(Call<java.util.Map<String, Object>> call, Response<java.util.Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(getContext(), "¡Negocio '" + name + "' creado con éxito!", Toast.LENGTH_LONG).show();
                    reiniciarSesionPorCambio();
                } else if (response.code() == 409) {
                    Toast.makeText(getContext(), "⚠️ Este nombre ya existe. Prueba con uno único.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Error al crear negocio: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<java.util.Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showJoinBusinessDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        ComposeView composeView = new ComposeView(requireContext());
        
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);

        SettingsComposeHelper.setJoinBusinessContent(
            composeView,
            isDarkMode,
            code -> {
                bottomSheetDialog.dismiss();
                joinBusiness(code, null, null);
                return kotlin.Unit.INSTANCE;
            },
            () -> {
                bottomSheetDialog.dismiss();
                scanQrCode();
                return kotlin.Unit.INSTANCE;
            },
            () -> {
                bottomSheetDialog.dismiss();
                return kotlin.Unit.INSTANCE;
            }
        );

        composeView.setViewCompositionStrategy(androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed.INSTANCE);
        bottomSheetDialog.setContentView(composeView);
        bottomSheetDialog.show();

        // IMPORTANTE: Para Diálogos, los owners deben estar en la DecorView del diálogo
        View decorView = bottomSheetDialog.getWindow().getDecorView();
        ViewTreeLifecycleOwner.set(decorView, getViewLifecycleOwner());
        ViewTreeViewModelStoreOwner.set(decorView, this);
        ViewTreeSavedStateRegistryOwner.set(decorView, this);
    }

    private void scanQrCode() {
        com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions options = 
            new com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(com.google.mlkit.vision.barcode.common.Barcode.FORMAT_QR_CODE)
                .build();
        com.google.mlkit.vision.codescanner.GmsBarcodeScanner scanner = 
            com.google.mlkit.vision.codescanner.GmsBarcodeScanning.getClient(requireContext(), options);

        scanner.startScan()
            .addOnSuccessListener(barcode -> {
                String rawValue = barcode.getRawValue();
                if (rawValue != null) {
                    joinBusiness(rawValue, null, null);
                }
            })
            .addOnCanceledListener(() -> {
                Toast.makeText(getContext(), "Escaneo cancelado", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Fallo al escanear: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void joinBusiness(String code, @Nullable AlertDialog dialog, @Nullable com.google.android.material.textfield.TextInputLayout inputLayout) {
        ApiService api = RetrofitClient.INSTANCE.getInstance();
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("codigoInvitacion", code);
        
        long userIdLocal = sharedPreferences.getLong("user_id", -1);
        body.put("userId", String.valueOf(userIdLocal));

        // Mostrar un pequeño brindis de carga si no hay dialog (porque venimos de Compose/QR)
        if (dialog == null) {
            Toast.makeText(getContext(), "Uniéndote al negocio...", Toast.LENGTH_SHORT).show();
        }

        api.joinNegocio(body).enqueue(new Callback<java.util.Map<String, Object>>() {
            @Override
            public void onResponse(Call<java.util.Map<String, Object>> call, Response<java.util.Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    if (dialog != null) dialog.dismiss();
                    Toast.makeText(getContext(), "¡Te has unido al negocio con éxito!", Toast.LENGTH_LONG).show();
                    reiniciarSesionPorCambio();
                } else {
                    String errorMsg = "Código inválido o error al unirse: " + response.code();
                    if (inputLayout != null) {
                        inputLayout.setError(errorMsg);
                    } else {
                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<java.util.Map<String, Object>> call, Throwable t) {
                if (inputLayout != null) {
                    inputLayout.setError("Error de conexión");
                } else {
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void reiniciarSesionPorCambio() {
        if (!isAdded() || getActivity() == null) return;
        
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("user_id");
        editor.remove("user_nombre");
        editor.remove("user_correo");
        editor.remove("jwt_token");
        editor.remove("id_negocio");
        editor.putBoolean("is_logged_in", false);
        editor.apply();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
