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
import com.example.gemainventory.model.CategoriaDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageCategoriesFragment extends Fragment {

    private CategoryAdapter adapter;
    private List<CategoriaDto> categoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recycler = view.findViewById(R.id.recycler_categories);
        FloatingActionButton fab = view.findViewById(R.id.fab_add_category);
        View btnBack = view.findViewById(R.id.btn_back);

        adapter = new CategoryAdapter(categoryList, this::mostrarOpciones);
        recycler.setAdapter(adapter);

        // Navegación
        fab.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(R.id.action_categories_to_addCategory));
        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        cargarCategorias();
    }

    private void mostrarOpciones(CategoriaDto categoria) {
        com.google.android.material.bottomsheet.BottomSheetDialog sheet = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_order_options, null); // Reusing/Generic layout or create new one? 
        // Let's use a simple programmatic approach or reuse the order one but change text
        
        // Better: Programmatic or simple layout. Let's customize the order_options layout dynamically
        sheet.setContentView(view);
        
        ((android.widget.TextView) view.findViewById(R.id.tv_sheet_order_title)).setText(categoria.getNombre());
        
        com.google.android.material.button.MaterialButton btn1 = view.findViewById(R.id.btn_sheet_view_details);
        btn1.setText("Editar Categoría");
        btn1.setIcon(androidx.core.content.ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_edit));
        btn1.setOnClickListener(v -> {
            sheet.dismiss();
            mostrarDialogoEditar(categoria);
        });

        com.google.android.material.button.MaterialButton btn2 = view.findViewById(R.id.btn_sheet_mark_delivered);
        btn2.setVisibility(View.GONE); // Hide generic button

        com.google.android.material.button.MaterialButton btnDelete = view.findViewById(R.id.btn_sheet_delete);
        btnDelete.setText("Eliminar Categoría");
        btnDelete.setOnClickListener(v -> {
            sheet.dismiss();
            confirmarEliminar(categoria);
        });

        sheet.show();
    }

    private void mostrarDialogoEditar(CategoriaDto categoria) {
        Bundle args = new Bundle();
        args.putInt("category_id", categoria.getIdCategoria());
        args.putString("category_name", categoria.getNombre());
        args.putString("category_desc", categoria.getDescripcion());
        
        NavHostFragment.findNavController(this).navigate(R.id.action_categories_to_addCategory, args);
    }

    private void actualizarCategoria(CategoriaDto categoria) {
        Call<CategoriaDto> call = RetrofitClient.INSTANCE.getInstance().actualizarCategoria(categoria.getIdCategoria(), categoria);
        call.enqueue(new Callback<CategoriaDto>() {
            @Override
            public void onResponse(Call<CategoriaDto> call, Response<CategoriaDto> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getContext(), "Categoría actualizada", Toast.LENGTH_SHORT).show();
                    cargarCategorias();
                } else {
                    Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CategoriaDto> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmarEliminar(CategoriaDto categoria) {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Eliminar " + categoria.getNombre())
                .setMessage("¿Estás seguro? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarCategoria(categoria.getIdCategoria()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarCategoria(int id) {
        Call<Void> call = RetrofitClient.INSTANCE.getInstance().eliminarCategoria(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getContext(), "Categoría eliminada", Toast.LENGTH_SHORT).show();
                    cargarCategorias();
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

    private void cargarCategorias() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);
        if (userId == null) return;
        Call<List<CategoriaDto>> call = RetrofitClient.INSTANCE.getInstance().getCategorias(userId);
        call.enqueue(new Callback<List<CategoriaDto>>() {
            @Override
            public void onResponse(Call<List<CategoriaDto>> call, Response<List<CategoriaDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    adapter.updateList(categoryList);
                }
            }
            @Override
            public void onFailure(Call<List<CategoriaDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar categorías", Toast.LENGTH_SHORT).show();
            }
        });
    }
}