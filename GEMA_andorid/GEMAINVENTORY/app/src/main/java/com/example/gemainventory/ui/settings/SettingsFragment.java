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

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.example.gemainventory.R;
import com.example.gemainventory.LoginActivity;

public class SettingsFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "GemaPrefs";
    private static final String DARK_MODE_KEY = "DarkMode";
    ImageView ivProfileAvatar;

    // --- ¡AÑADIDAS VARIABLES PARA LOS DATOS DE USUARIO! ---
    TextView tvNombreUsuario, tvCorreoUsuario;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // --- Encontrar componentes ---
        MaterialCardView profileCard = view.findViewById(R.id.card_profile);
        LinearLayout manageCatalogRow = view.findViewById(R.id.row_manage_catalog);
        LinearLayout notificationsRow = view.findViewById(R.id.row_notifications);
        MaterialButton logoutButton = view.findViewById(R.id.button_logout);
        SwitchMaterial darkModeSwitch = view.findViewById(R.id.switch_dark_mode);
        LinearLayout rowSwitchBusiness = view.findViewById(R.id.row_switch_business);
        LinearLayout rowJoinCreateBusiness = view.findViewById(R.id.row_join_create_business);
        // --- ¡AÑADIDA REFERENCIA AL BOTÓN DEL MANUAL! ---
        LinearLayout userManualRow = view.findViewById(R.id.user_manual);

        // --- ¡AÑADIDA BÚSQUEDA DE TEXTVIEWS DE USUARIO! ---
        // (Asegúrate de que estos IDs existan en tu fragment_settings.xml)
        tvNombreUsuario = view.findViewById(R.id.tv_ajustes_nombre);
        tvCorreoUsuario = view.findViewById(R.id.tv_ajustes_correo);
        ivProfileAvatar = view.findViewById(R.id.profile_avatar);

        // --- NUEVAS REFERENCIAS PARA GESTIÓN DE EQUIPO ---
        LinearLayout manageUsersRow = view.findViewById(R.id.row_manage_users);
        View dividerManageUsers = view.findViewById(R.id.divider_manage_users);
        
        // --- NUEVAS REFERENCIAS PARA MI NEGOCIO ---
        LinearLayout rowBusiness = view.findViewById(R.id.row_business);

        // --- ¡AÑADIDA LLAMADA PARA CARGAR DATOS! ---
        cargarDatosDeSesion();

        // --- LÓGICA DEL MODO OSCURO (Esto ya estaba bien) ---
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
        darkModeSwitch.setChecked(isDarkMode);

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                saveThemePreference(true);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                saveThemePreference(false);
            }
        });
        // --- LÓGICA DEL MODO OSCURO (FIN) ---

        // --- Listeners de los otros botones (Esto ya estaba bien) ---
        profileCard.setOnClickListener(v -> {
            // Navegar a Editar Perfil
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_navigation_edit_profile_self);
        });

        manageCatalogRow.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_settings_to_manage);
        });

        notificationsRow.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.navigation_notification_settings);
        });

        logoutButton.setOnClickListener(v -> {
            showLogoutDialog();
        });

        // --- LISTENER DEL MANUAL ---
        userManualRow.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_settings_to_manual);
        });

        if (rowSwitchBusiness != null) {
            rowSwitchBusiness.setOnClickListener(v -> {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_settings_to_switch_business);
            });
        }

        if (rowJoinCreateBusiness != null) {
            rowJoinCreateBusiness.setOnClickListener(v -> {
                showBusinessChoiceBottomSheet();
            });
        }

        // --- LISTENER DE NEGOCIO ---
        if (rowBusiness != null) {
            rowBusiness.setOnClickListener(v -> {
                openBusinessActivity();
            });
        }

        // --- LÓGICA DE VISIBILIDAD GESTIÓN DE EQUIPO (SOLO ADMIN) ---
        int userRol = sharedPreferences.getInt("user_rol", -1);
        if (userRol == 1) { // 1 = ROLE_ADMIN
            if (manageUsersRow != null)
                manageUsersRow.setVisibility(View.VISIBLE);
            if (dividerManageUsers != null)
                dividerManageUsers.setVisibility(View.VISIBLE);
        }

        if (manageUsersRow != null) {
            manageUsersRow.setOnClickListener(v -> {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_settings_to_users);
            });
        }

        // --- LÓGICA DEL SWITCH BIOMÉTRICO ---
        SwitchMaterial biometricSwitch = view.findViewById(R.id.switch_biometric);
        if (biometricSwitch != null) {
            boolean useBiometric = sharedPreferences.getBoolean("use_biometric", false);
            biometricSwitch.setChecked(useBiometric);

            biometricSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (com.example.gemainventory.util.BiometricHelper.isBiometricAvailable(requireContext())) {
                        sharedPreferences.edit().putBoolean("use_biometric", true).apply();
                        Toast.makeText(getContext(), "Seguridad Biométrica activada", Toast.LENGTH_SHORT).show();
                    } else {
                        biometricSwitch.setChecked(false);
                        Toast.makeText(getContext(), "Tu dispositivo no soporta biometría", Toast.LENGTH_LONG).show();
                    }
                } else {
                    sharedPreferences.edit().putBoolean("use_biometric", false).apply();
                }
            });
        }
    }

    private void saveThemePreference(boolean isDarkMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DARK_MODE_KEY, isDarkMode);
        editor.apply();
    }

    // --- ¡AÑADIDO ESTE MÉTODO COMPLETO! ---
    private void cargarDatosDeSesion() {
        // 1. Obtener las preferencias
        String nombre = sharedPreferences.getString("user_nombre", "Sin Nombre");
        String correo = sharedPreferences.getString("user_correo", "Sin Correo");
        String remotePhotoUrl = sharedPreferences.getString("user_foto_url", null);
        String legacyPhotoUri = sharedPreferences.getString("user_photo_uri", null);

        // 2. Mostrar datos de texto
        if (tvNombreUsuario != null) tvNombreUsuario.setText(nombre);
        if (tvCorreoUsuario != null) tvCorreoUsuario.setText(correo);

        // --- CARGAR LA FOTO (Prioridad: Remota > Local) ---
        if (ivProfileAvatar != null) {
            String photoPath = (remotePhotoUrl != null && !remotePhotoUrl.isEmpty()) 
                ? RetrofitClient.getFullImageUrl(remotePhotoUrl) 
                : legacyPhotoUri;

            if (photoPath != null) {
                com.bumptech.glide.Glide.with(requireContext())
                    .load(photoPath)
                    .centerCrop()
                    .placeholder(R.drawable.ic_account_circle)
                    .into(ivProfileAvatar);
                ivProfileAvatar.setColorFilter(null);
            }
        }
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
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_input_universal, null);
        TextView title = dialogView.findViewById(R.id.dialog_title);
        TextView subtitle = dialogView.findViewById(R.id.dialog_subtitle);
        EditText input = dialogView.findViewById(R.id.dialog_input);
        com.google.android.material.textfield.TextInputLayout inputLayout = dialogView.findViewById(R.id.dialog_input_layout);
        MaterialButton btnConfirm = dialogView.findViewById(R.id.btn_confirm);

        title.setText("✨ Nuevo Negocio");
        subtitle.setText("Asigna un nombre único y empieza a brillar.");
        input.setHint("Nombre del negocio...");

        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
                .setView(dialogView)
                .create();

        // Limpiar error al escribir
        input.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputLayout.setError(null);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        btnConfirm.setOnClickListener(v -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) {
                inputLayout.setError("El nombre es obligatorio");
                return;
            }
            createBusiness(name, dialog, inputLayout);
        });

        dialog.show();
    }

    private void createBusiness(String name, AlertDialog dialog, com.google.android.material.textfield.TextInputLayout inputLayout) {
        ApiService api = RetrofitClient.INSTANCE.getInstance();
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("nombre", name);

        api.createNegocio(body).enqueue(new Callback<java.util.Map<String, Object>>() {
            @Override
            public void onResponse(Call<java.util.Map<String, Object>> call, Response<java.util.Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(getContext(), "¡Negocio '" + name + "' creado con éxito!", Toast.LENGTH_LONG).show();
                    reiniciarSesionPorCambio();
                } else if (response.code() == 409) {
                    inputLayout.setError("⚠️ Este nombre ya brilla en otro negocio. Prueba con uno único.");
                } else {
                    inputLayout.setError("Error al crear negocio: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<java.util.Map<String, Object>> call, Throwable t) {
                inputLayout.setError("Error de conexión");
            }
        });
    }

    private void showJoinBusinessDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_input_universal, null);
        TextView title = dialogView.findViewById(R.id.dialog_title);
        TextView subtitle = dialogView.findViewById(R.id.dialog_subtitle);
        EditText input = dialogView.findViewById(R.id.dialog_input);
        com.google.android.material.textfield.TextInputLayout inputLayout = dialogView.findViewById(R.id.dialog_input_layout);
        MaterialButton btnConfirm = dialogView.findViewById(R.id.btn_confirm);

        title.setText("🤝 Unirse a Negocio");
        subtitle.setText("Ingresa el código de invitación compartido contigo.");
        input.setHint("Código de 8 caracteres...");

        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
                .setView(dialogView)
                .create();

        // Limpiar error al escribir
        input.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputLayout.setError(null);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        btnConfirm.setOnClickListener(v -> {
            String code = input.getText().toString().trim();
            if (code.isEmpty()) {
                inputLayout.setError("El código es obligatorio");
                return;
            }
            joinBusiness(code, dialog, inputLayout);
        });

        dialog.show();
    }

    private void joinBusiness(String code, AlertDialog dialog, com.google.android.material.textfield.TextInputLayout inputLayout) {
        ApiService api = RetrofitClient.INSTANCE.getInstance();
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("codigoInvitacion", code); // <-- Corregido a 'codigoInvitacion' para coincidir con el backend
        
        long userIdLocal = sharedPreferences.getLong("user_id", -1);
        body.put("userId", String.valueOf(userIdLocal));

        api.joinNegocio(body).enqueue(new Callback<java.util.Map<String, Object>>() {
            @Override
            public void onResponse(Call<java.util.Map<String, Object>> call, Response<java.util.Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(getContext(), "¡Te has unido al negocio con éxito!", Toast.LENGTH_LONG).show();
                    reiniciarSesionPorCambio();
                } else {
                    inputLayout.setError("Código inválido o error al unirse: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<java.util.Map<String, Object>> call, Throwable t) {
                inputLayout.setError("Error de conexión");
            }
        });
    }

    private void reiniciarSesionPorCambio() {
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
