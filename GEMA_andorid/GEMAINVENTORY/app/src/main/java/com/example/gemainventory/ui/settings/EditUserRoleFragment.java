package com.example.gemainventory.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gemainventory.R;
import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.model.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserRoleFragment extends Fragment {

    private String targetUserId;
    private Usuario targetUser;
    private int currentUserRol;

    private android.widget.TextView tvName, tvEmail;
    private AutoCompleteTextView spinnerRole;
    private MaterialButton btnSave;

    private final String[] roles = { "ADMINISTRADOR", "SUPERVISOR", "VENDEDOR", "REPARTIDOR", "ALMACENISTA" };
    private final Map<String, Integer> roleMap = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            targetUserId = getArguments().getString("userId");
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        currentUserRol = prefs.getInt("user_rol", -1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_user_role, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        roleMap.put("ADMINISTRADOR", 2);
        roleMap.put("SUPERVISOR", 3);
        roleMap.put("VENDEDOR", 4);
        roleMap.put("REPARTIDOR", 5);
        roleMap.put("ALMACENISTA", 6);

        view.findViewById(R.id.btn_back_edit_role).setOnClickListener(v -> requireActivity().onBackPressed());

        tvName = view.findViewById(R.id.tv_edit_user_name);
        tvEmail = view.findViewById(R.id.tv_edit_user_email);
        spinnerRole = view.findViewById(R.id.spinner_edit_user_role);
        btnSave = view.findViewById(R.id.btn_update_user_role);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line,
                roles);
        spinnerRole.setAdapter(adapter);

        fetchTargetUser();

        btnSave.setOnClickListener(v -> updateRole());
    }

    private void fetchTargetUser() {
        RetrofitClient.INSTANCE.getInstance().getUsuarioById(targetUserId).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    targetUser = response.body();
                    displayUserInfo();
                    
                    // Si el usuario objetivo es el Propietario, deshabilitar edición
                    if (targetUser.getIdRol() != null && targetUser.getIdRol() == 1) {
                        btnSave.setEnabled(false);
                        spinnerRole.setEnabled(false);
                        Toast.makeText(getContext(), "El rol de Propietario es inmutable", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red al cargar usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayUserInfo() {
        if (targetUser != null) {
            tvName.setText(targetUser.getNombre());
            tvEmail.setText(targetUser.getCorreo());

            String currentRoleName = "Desconocido";
            if (targetUser.getIdRol() != null) {
                for (Map.Entry<String, Integer> entry : roleMap.entrySet()) {
                    if (entry.getValue().equals(targetUser.getIdRol())) {
                        currentRoleName = entry.getKey();
                        break;
                    }
                }
            }
            spinnerRole.setText(currentRoleName, false);
        }
    }

    private void updateRole() {
        String selectedRoleName = spinnerRole.getText().toString();
        Integer newRoleId = roleMap.get(selectedRoleName);

        if (targetUser == null || newRoleId == null)
            return;

        // Validación de UI (ya protegida en backend también)
        if (currentUserRol == 2 && targetUser.getIdRol() != 3) {
            Toast.makeText(getContext(), "No tienes rango para editar a este usuario", Toast.LENGTH_LONG).show();
            return;
        }

        targetUser.setIdRol(newRoleId);

        RetrofitClient.INSTANCE.getInstance().actualizarUsuario(targetUserId, targetUser)
                .enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Rol actualizado correctamente", Toast.LENGTH_SHORT).show();
                            requireActivity().onBackPressed();
                        } else if (response.code() == 403) {
                            // Ahora el backend manda mensajes específicos en el cuerpo o podemos usar el general
                            Toast.makeText(getContext(), "Error de Jerarquía: No tienes permisos para este cambio", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Error del Servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
