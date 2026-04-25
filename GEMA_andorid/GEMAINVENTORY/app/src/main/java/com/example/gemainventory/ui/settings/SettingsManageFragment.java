package com.example.gemainventory.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.gemainventory.R;

public class SettingsManageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_manage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Botón Atrás (Fundamental)
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        // 2. Gestionar Productos
        view.findViewById(R.id.btn_manage_products).setOnClickListener(v -> {
            // Opción A: Redirigir al Inventario existente (Recomendado)
            NavHostFragment.findNavController(this).navigate(R.id.navigation_inventory);
        });

        // 3. Gestionar Categorías (Placeholder)
        view.findViewById(R.id.btn_manage_categories).setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_manage_to_categories); // <--- ID que creamos arriba
        });

        // 4. Gestionar Clientes (Placeholder)
        view.findViewById(R.id.btn_manage_clients).setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_manage_to_clients); // <--- Usa el ID que pusiste en el nav graph
        });

        // 5. Gestionar Almacenes (Placeholder)
        view.findViewById(R.id.btn_manage_warehouses).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_manage_to_warehouses);
        });

        // 6. Gestionar Proveedores
        view.findViewById(R.id.btn_manage_suppliers).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_manage_to_suppliers);
        });
    }
}