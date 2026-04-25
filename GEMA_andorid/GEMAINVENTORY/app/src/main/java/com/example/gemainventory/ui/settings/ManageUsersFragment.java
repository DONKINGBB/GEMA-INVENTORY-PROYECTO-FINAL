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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gemainventory.R;
import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.model.Usuario;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageUsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<Usuario> userList = new ArrayList<>();
    
    private String currentUserId;
    private int currentUserRol;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Recuperar sesión del usuario actual
        SharedPreferences prefs = requireContext().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getString("user_id", "");
        currentUserRol = prefs.getInt("user_rol", -1);

        view.findViewById(R.id.btn_back_users).setOnClickListener(v -> requireActivity().onBackPressed());

        recyclerView = view.findViewById(R.id.recycler_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar adaptador con lógica de navegación y eliminación
        adapter = new UserAdapter(
                userList, 
                currentUserId, 
                currentUserRol,
                user -> {
                    // Navegar a edición de rol
                    Bundle args = new Bundle();
                    args.putString("userId", user.getIdUsuario());
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_users_to_editRole, args);
                },
                user -> {
                    // Acción de eliminar
                    mostrarConfirmacionEliminar(user);
                }
        );
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add_user);
        fabAdd.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_users_to_addUser);
        });

        fetchUsers();
    }

    private void mostrarConfirmacionEliminar(Usuario user) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Eliminar miembro")
                .setMessage("¿Estás seguro de que quieres eliminar a " + user.getNombre() + " del equipo?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    eliminarUsuario(user);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarUsuario(Usuario user) {
        RetrofitClient.INSTANCE.getInstance().eliminarUsuario(user.getIdUsuario()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
                    fetchUsers(); // Recargar lista
                } else {
                    // En vez de un error genérico, intentamos leer el mensaje que envía el servidor
                    String errorMsg = "Error: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String body = response.errorBody().string();
                            // Intentar extraer el mensaje del JSON de Spring si existe
                            if (body.contains("\"message\":\"")) {
                                errorMsg = body.split("\"message\":\"")[1].split("\"")[0];
                            } else {
                                errorMsg = body;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUsers() {
        RetrofitClient.INSTANCE.getInstance().obtenerUsuarios("").enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar equipo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
