package com.example.gemainventory.ui.settings;

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
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUserFragment extends Fragment {

    private TextInputEditText etName, etEmail, etPassword;
    private AutoCompleteTextView spinnerRole;
    private MaterialButton btnSave;

    private final String[] roles = { "Administrador", "Supervisor", "Operario" };
    private final Map<String, Integer> roleMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        roleMap.put("Administrador", 1);
        roleMap.put("Supervisor", 2);
        roleMap.put("Operario", 3);

        view.findViewById(R.id.btn_back_add_user).setOnClickListener(v -> requireActivity().onBackPressed());

        etName = view.findViewById(R.id.et_user_name);
        etEmail = view.findViewById(R.id.et_user_email);
        etPassword = view.findViewById(R.id.et_user_password);
        spinnerRole = view.findViewById(R.id.spinner_user_role);
        btnSave = view.findViewById(R.id.btn_save_user);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line,
                roles);
        spinnerRole.setAdapter(adapter);

        btnSave.setOnClickListener(v -> saveUser());
    }

    private void saveUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String selectedRole = spinnerRole.getText().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || selectedRole.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Usamos el ID de rol correspondiente
        int idRol = roleMap.get(selectedRole);

        // El backend espera un Usuario (o DTO)
        Usuario user = new Usuario();
        user.setNombre(name);
        user.setCorreo(email);
        user.setPassword(password);
        user.setIdRol(idRol);

        // Llamar al API de usuarios (POST /api/usuarios/usuario)
        RetrofitClient.INSTANCE.getInstance().guardarUsuario(user).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Usuario añadido al equipo", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                } else {
                    Toast.makeText(getContext(), "Error: Comprueba si el correo ya existe", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
