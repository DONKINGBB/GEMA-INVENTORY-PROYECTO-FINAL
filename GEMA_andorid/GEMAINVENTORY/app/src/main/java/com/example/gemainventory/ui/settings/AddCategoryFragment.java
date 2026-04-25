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
import com.example.gemainventory.model.CategoriaDto;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCategoryFragment extends Fragment {

    private TextInputEditText etName, etDesc;
    private Button btnSave;
    private int categoryId = 0; // 0 = New
    private CategoriaDto categoriaEditable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etName = view.findViewById(R.id.et_category_name);
        etDesc = view.findViewById(R.id.et_category_desc);
        btnSave = view.findViewById(R.id.btn_save_category);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        
        // CHECK EDIT MODE
        if (getArguments() != null && getArguments().containsKey("category_id")) {
             categoryId = getArguments().getInt("category_id");
             String name = getArguments().getString("category_name");
             String desc = getArguments().getString("category_desc");
             
             etName.setText(name);
             if(desc != null) etDesc.setText(desc);
             
             ((android.widget.TextView)view.findViewById(R.id.tv_header_title)).setText("Editar Categoría");
             btnSave.setText("Actualizar Categoría");
        }

        btnSave.setOnClickListener(v -> guardarCategoria());
    }

    private void guardarCategoria() {
        String nombre = etName.getText().toString().trim();
        String descripcion = etDesc.getText().toString().trim();
        
        if (nombre.isEmpty()) {
            etName.setError("Requerido");
            return;
        }

        SharedPreferences prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        btnSave.setEnabled(false);
        
        if (categoryId == 0) {
            // CREATE
            CategoriaDto nueva = new CategoriaDto(0, nombre, descripcion);
            Call<CategoriaDto> call = RetrofitClient.INSTANCE.getInstance().crearCategoria(nueva, userId);
            call.enqueue(new Callback<CategoriaDto>() {
                @Override
                public void onResponse(Call<CategoriaDto> call, Response<CategoriaDto> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Categoría guardada", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(AddCategoryFragment.this).popBackStack();
                    } else {
                        btnSave.setEnabled(true);
                        Toast.makeText(getContext(), "Error del servidor", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<CategoriaDto> call, Throwable t) {
                    btnSave.setEnabled(true);
                    Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // UPDATE
            CategoriaDto edit = new CategoriaDto(categoryId, nombre, descripcion);
            Call<CategoriaDto> call = RetrofitClient.INSTANCE.getInstance().actualizarCategoria(categoryId, edit);
            call.enqueue(new Callback<CategoriaDto>() {
                @Override
                public void onResponse(Call<CategoriaDto> call, Response<CategoriaDto> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Categoría actualizada", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(AddCategoryFragment.this).popBackStack();
                    } else {
                        btnSave.setEnabled(true);
                        Toast.makeText(getContext(), "Error:" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<CategoriaDto> call, Throwable t) {
                    btnSave.setEnabled(true);
                    Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}