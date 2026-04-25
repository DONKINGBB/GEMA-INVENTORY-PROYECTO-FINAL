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
import com.example.gemainventory.model.ClienteDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageClientsFragment extends Fragment {

    private ClientAdapter adapter;
    private List<ClienteDto> clientList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_clients, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recycler = view.findViewById(R.id.recycler_clients);
        FloatingActionButton fab = view.findViewById(R.id.fab_add_client);
        View btnBack = view.findViewById(R.id.btn_back);

        adapter = new ClientAdapter(clientList, this::mostrarOpciones);
        recycler.setAdapter(adapter);

        // Ir a agregar cliente
        fab.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(R.id.action_clients_to_addClient));

        // Regresar
        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        cargarClientes();
    }

    private void mostrarOpciones(ClienteDto cliente) {
        com.google.android.material.bottomsheet.BottomSheetDialog sheet = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_order_options, null);
        sheet.setContentView(view);
        
        ((android.widget.TextView) view.findViewById(R.id.tv_sheet_order_title)).setText(cliente.getNombre());
        
        com.google.android.material.button.MaterialButton btn1 = view.findViewById(R.id.btn_sheet_view_details);
        btn1.setText("Editar Cliente");
        btn1.setIcon(androidx.core.content.ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_edit));
        btn1.setOnClickListener(v -> {
            sheet.dismiss();
            mostrarDialogoEditar(cliente);
        });

        view.findViewById(R.id.btn_sheet_mark_delivered).setVisibility(View.GONE);

        com.google.android.material.button.MaterialButton btnDelete = view.findViewById(R.id.btn_sheet_delete);
        btnDelete.setText("Eliminar Cliente");
        btnDelete.setOnClickListener(v -> {
            sheet.dismiss();
            confirmarEliminar(cliente);
        });

        sheet.show();
    }

    private void mostrarDialogoEditar(ClienteDto cliente) {
        Bundle args = new Bundle();
        args.putString("client_id", cliente.getIdCliente());
        args.putString("client_name", cliente.getNombre());
        args.putString("client_contact", cliente.getContacto());
        args.putString("client_address", cliente.getDireccion());
        
        NavHostFragment.findNavController(this).navigate(R.id.action_clients_to_addClient, args);
    }

    private void confirmarEliminar(ClienteDto cliente) {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Eliminar " + cliente.getNombre())
                .setMessage("¿Estás seguro?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarCliente(cliente.getIdCliente()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarCliente(String id) {
        Call<Void> call = RetrofitClient.INSTANCE.getInstance().eliminarCliente(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getContext(), "Cliente eliminado", Toast.LENGTH_SHORT).show();
                    cargarClientes();
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

    private void cargarClientes() {
        // 1. OBTENER ID DEL USUARIO ACTUAL
        SharedPreferences prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) {
            Toast.makeText(getContext(), "Error de sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. ENVIAR ID A LA API
        Call<List<ClienteDto>> call = RetrofitClient.INSTANCE.getInstance().getClientes(userId);

        call.enqueue(new Callback<List<ClienteDto>>() {
            @Override
            public void onResponse(Call<List<ClienteDto>> call, Response<List<ClienteDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    clientList = response.body();
                    adapter.updateList(clientList);
                }
            }
            @Override
            public void onFailure(Call<List<ClienteDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar clientes", Toast.LENGTH_SHORT).show();
            }
        });
    }
}