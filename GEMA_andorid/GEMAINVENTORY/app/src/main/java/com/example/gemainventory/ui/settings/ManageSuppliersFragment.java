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
import com.example.gemainventory.model.ProveedorDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageSuppliersFragment extends Fragment {

    private SupplierAdapter adapter;
    private List<ProveedorDto> supplierList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_suppliers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recycler = view.findViewById(R.id.recycler_suppliers);
        FloatingActionButton fab = view.findViewById(R.id.fab_add_supplier);
        View btnBack = view.findViewById(R.id.btn_back);

        adapter = new SupplierAdapter(supplierList, this::mostrarOpciones);
        recycler.setAdapter(adapter);

        // Ir a agregar proveedor
        fab.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(R.id.action_suppliers_to_addSupplier));

        // Regresar
        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        cargarProveedores();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarProveedores();
    }

    private void mostrarOpciones(ProveedorDto proveedor) {
        com.google.android.material.bottomsheet.BottomSheetDialog sheet = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_order_options, null);
        sheet.setContentView(view);

        ((android.widget.TextView) view.findViewById(R.id.tv_sheet_order_title)).setText(proveedor.getNombre());

        com.google.android.material.button.MaterialButton btn1 = view.findViewById(R.id.btn_sheet_view_details);
        btn1.setText("Editar Proveedor");
        btn1.setIcon(androidx.core.content.ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_edit));
        btn1.setOnClickListener(v -> {
            sheet.dismiss();
            mostrarDialogoEditar(proveedor);
        });

        view.findViewById(R.id.btn_sheet_mark_delivered).setVisibility(View.GONE);

        com.google.android.material.button.MaterialButton btnDelete = view.findViewById(R.id.btn_sheet_delete);
        btnDelete.setText("Eliminar Proveedor");
        btnDelete.setOnClickListener(v -> {
            sheet.dismiss();
            confirmarEliminar(proveedor);
        });

        sheet.show();
    }

    private void mostrarDialogoEditar(ProveedorDto proveedor) {
        Bundle args = new Bundle();
        args.putString("supplier_id", proveedor.getId());
        args.putString("supplier_name", proveedor.getNombre());
        args.putString("supplier_contact", proveedor.getContacto());
        args.putString("supplier_address", proveedor.getDireccion());

        NavHostFragment.findNavController(this).navigate(R.id.action_suppliers_to_addSupplier, args);
    }

    private void confirmarEliminar(ProveedorDto proveedor) {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Eliminar " + proveedor.getNombre())
                .setMessage("¿Estás seguro de que deseas eliminar este proveedor?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarProveedor(proveedor.getId()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarProveedor(String id) {
        Call<Void> call = RetrofitClient.INSTANCE.getInstance().eliminarProveedor(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Proveedor eliminado", Toast.LENGTH_SHORT).show();
                    cargarProveedores();
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

    private void cargarProveedores() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) {
            Toast.makeText(getContext(), "Error de sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<List<ProveedorDto>> call = RetrofitClient.INSTANCE.getInstance().getProveedores(userId);

        call.enqueue(new Callback<List<ProveedorDto>>() {
            @Override
            public void onResponse(Call<List<ProveedorDto>> call, Response<List<ProveedorDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    supplierList = response.body();
                    adapter.updateList(supplierList);
                }
            }
            @Override
            public void onFailure(Call<List<ProveedorDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar proveedores", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
