package com.example.gemainventory.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gemainventory.R;
import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.model.AlmacenDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageWarehousesFragment extends Fragment {

    private WarehouseAdapter adapter;
    private List<AlmacenDto> warehouseList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_warehouses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recycler = view.findViewById(R.id.recycler_warehouses);
        FloatingActionButton fab = view.findViewById(R.id.fab_add_warehouse);
        View btnBack = view.findViewById(R.id.btn_back);

        adapter = new WarehouseAdapter(warehouseList, this::mostrarOpciones);
        recycler.setAdapter(adapter);

        fab.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(R.id.action_warehouses_to_addWarehouse));
        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        cargarAlmacenes();
    }

    private void mostrarOpciones(AlmacenDto almacen) {
        com.google.android.material.bottomsheet.BottomSheetDialog sheet = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_order_options, null);
        sheet.setContentView(view);
        
        ((android.widget.TextView) view.findViewById(R.id.tv_sheet_order_title)).setText(almacen.getNombre());
        
        com.google.android.material.button.MaterialButton btn1 = view.findViewById(R.id.btn_sheet_view_details);
        btn1.setText("Editar Almacén");
        btn1.setIcon(androidx.core.content.ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_edit));
        btn1.setOnClickListener(v -> {
            sheet.dismiss();
            mostrarDialogoEditar(almacen);
        });

        view.findViewById(R.id.btn_sheet_mark_delivered).setVisibility(View.GONE);

        com.google.android.material.button.MaterialButton btnDelete = view.findViewById(R.id.btn_sheet_delete);
        btnDelete.setText("Eliminar Almacén");
        btnDelete.setOnClickListener(v -> {
            sheet.dismiss();
            confirmarEliminar(almacen);
        });

        sheet.show();
    }

    private void mostrarDialogoEditar(AlmacenDto almacen) {
        Bundle args = new Bundle();
        args.putInt("warehouse_id", almacen.getIdAlmacen());
        args.putString("warehouse_name", almacen.getNombre());
        args.putString("warehouse_address", almacen.getDireccion());
        
        if (almacen.getLatitud() != null && almacen.getLongitud() != null) {
            args.putDouble("latitud", almacen.getLatitud());
            args.putDouble("longitud", almacen.getLongitud());
        }
        
        NavHostFragment.findNavController(this).navigate(R.id.action_warehouses_to_addWarehouse, args);
    }

    private void actualizarAlmacen(AlmacenDto almacen) {
        int id = almacen.getIdAlmacen();
        Call<AlmacenDto> call = RetrofitClient.INSTANCE.getInstance().actualizarAlmacen(id, almacen);
        call.enqueue(new Callback<AlmacenDto>() {
            @Override
            public void onResponse(Call<AlmacenDto> call, Response<AlmacenDto> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getContext(), "Almacén actualizado", Toast.LENGTH_SHORT).show();
                    cargarAlmacenes();
                } else {
                    Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<AlmacenDto> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmarEliminar(AlmacenDto almacen) {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Eliminar " + almacen.getNombre())
                .setMessage("¿Estás seguro?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarAlmacen(almacen.getIdAlmacen()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarAlmacen(int id) {
        Call<Void> call = RetrofitClient.INSTANCE.getInstance().eliminarAlmacen(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getContext(), "Almacén eliminado", Toast.LENGTH_SHORT).show();
                    cargarAlmacenes();
                } else {
                    Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarAlmacenes() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) {
            Toast.makeText(getContext(), "Error de sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<List<AlmacenDto>> call = RetrofitClient.INSTANCE.getInstance().getAlmacenes(userId);

        call.enqueue(new Callback<List<AlmacenDto>>() {
            @Override
            public void onResponse(Call<List<AlmacenDto>> call, Response<List<AlmacenDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    warehouseList = response.body();
                    adapter.updateList(warehouseList);
                }
            }
            @Override
            public void onFailure(Call<List<AlmacenDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar almacenes", Toast.LENGTH_SHORT).show();
            }
        });
    }
}