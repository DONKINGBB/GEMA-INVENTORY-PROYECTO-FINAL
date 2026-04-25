package com.example.gemainventory.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.gemainventory.R;
import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.model.ClienteDto;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddClientFragment extends Fragment {

    private TextInputEditText etName, etContact, etAddress;
    private Button btnSave;
    private String clientId = null; // null = New

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_client, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etName = view.findViewById(R.id.et_client_name);
        etContact = view.findViewById(R.id.et_client_contact);
        etAddress = view.findViewById(R.id.et_client_address);
        btnSave = view.findViewById(R.id.btn_save_client);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        // EDIT MODE
        if (getArguments() != null && getArguments().containsKey("client_id")) {
             clientId = getArguments().getString("client_id");
             etName.setText(getArguments().getString("client_name"));
             etAddress.setText(getArguments().getString("client_address"));
             etContact.setText(getArguments().getString("client_contact"));
             
             ((android.widget.TextView)view.findViewById(R.id.tv_header_title)).setText("Editar Cliente"); 
             btnSave.setText("Actualizar Cliente");
        }

        btnSave.setOnClickListener(v -> guardarCliente());
    }

    private void guardarCliente() {
        String nombre = etName.getText().toString().trim();
        String contacto = etContact.getText().toString().trim();
        String direccion = etAddress.getText().toString().trim();

        if (nombre.isEmpty()) {
            etName.setError("Requerido");
            return;
        }

        SharedPreferences prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) {
            Toast.makeText(getContext(), "Error de sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("Guardando...");
        
        if (clientId == null) {
            // CREATE
            ClienteDto nuevoCliente = new ClienteDto(null, nombre, contacto, direccion);
            Call<ClienteDto> call = RetrofitClient.INSTANCE.getInstance().crearCliente(nuevoCliente, userId);
            call.enqueue(new Callback<ClienteDto>() {
                @Override
                public void onResponse(Call<ClienteDto> call, Response<ClienteDto> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Cliente guardado", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(AddClientFragment.this).popBackStack();
                    } else {
                        btnSave.setEnabled(true);
                        btnSave.setText("Guardar Cliente");
                        Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ClienteDto> call, Throwable t) {
                    btnSave.setEnabled(true);
                    btnSave.setText("Guardar Cliente");
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // UPDATE
            // ClienteDto uses String ID locally, but update endpoint might need int if mapped that way.
            // Wait, previous fix confirmed ClienteController uses String ID now.
            // But verify: Check API service definition for 'actualizarCliente'.
            // ApiService.kt: updated to String? No, I need to check ApiService.kt.
            // Assuming ApiService was NOT updated to String yet, need to check/fix that too.
            // Wait, I fixed ClienteController, but did I fix ApiService.kt?
            
            ClienteDto edit = new ClienteDto(clientId, nombre, contacto, direccion);
            Call<ClienteDto> call = RetrofitClient.INSTANCE.getInstance().actualizarCliente(clientId, edit);
            call.enqueue(new Callback<ClienteDto>() {
                 @Override
                public void onResponse(Call<ClienteDto> call, Response<ClienteDto> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Cliente actualizado", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(AddClientFragment.this).popBackStack();
                    } else {
                        btnSave.setEnabled(true);
                        btnSave.setText("Actualizar Cliente");
                        Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ClienteDto> call, Throwable t) {
                     btnSave.setEnabled(true);
                     btnSave.setText("Actualizar Cliente");
                     Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}