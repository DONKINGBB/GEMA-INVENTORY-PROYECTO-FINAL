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
import com.example.gemainventory.model.ProveedorDto;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSupplierFragment extends Fragment {

    private TextInputEditText etName, etContact, etAddress;
    private Button btnSave;
    private String supplierId = null; // null = New

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_supplier, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etName = view.findViewById(R.id.et_supplier_name);
        etContact = view.findViewById(R.id.et_supplier_contact);
        etAddress = view.findViewById(R.id.et_supplier_address);
        btnSave = view.findViewById(R.id.btn_save_supplier);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        // EDIT MODE
        if (getArguments() != null && getArguments().containsKey("supplier_id")) {
            supplierId = getArguments().getString("supplier_id");
            etName.setText(getArguments().getString("supplier_name"));
            etAddress.setText(getArguments().getString("supplier_address"));
            etContact.setText(getArguments().getString("supplier_contact"));

            ((android.widget.TextView) view.findViewById(R.id.tv_header_title)).setText("Editar Proveedor");
            btnSave.setText("Actualizar Proveedor");
        }

        btnSave.setOnClickListener(v -> guardarProveedor());
    }

    private void guardarProveedor() {
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

        if (supplierId == null) {
            // CREATE
            ProveedorDto nuevo = new ProveedorDto(null, nombre, contacto, direccion);
            Call<ProveedorDto> call = RetrofitClient.INSTANCE.getInstance().crearProveedor(nuevo, userId);
            call.enqueue(new Callback<ProveedorDto>() {
                @Override
                public void onResponse(Call<ProveedorDto> call, Response<ProveedorDto> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Proveedor guardado", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(AddSupplierFragment.this).popBackStack();
                    } else {
                        btnSave.setEnabled(true);
                        btnSave.setText("Guardar Proveedor");
                        Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ProveedorDto> call, Throwable t) {
                    btnSave.setEnabled(true);
                    btnSave.setText("Guardar Proveedor");
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // UPDATE
            ProveedorDto edit = new ProveedorDto(supplierId, nombre, contacto, direccion);
            Call<ProveedorDto> call = RetrofitClient.INSTANCE.getInstance().actualizarProveedor(supplierId, edit);
            call.enqueue(new Callback<ProveedorDto>() {
                @Override
                public void onResponse(Call<ProveedorDto> call, Response<ProveedorDto> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Proveedor actualizado", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(AddSupplierFragment.this).popBackStack();
                    } else {
                        btnSave.setEnabled(true);
                        btnSave.setText("Actualizar Proveedor");
                        Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ProveedorDto> call, Throwable t) {
                    btnSave.setEnabled(true);
                    btnSave.setText("Actualizar Proveedor");
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
