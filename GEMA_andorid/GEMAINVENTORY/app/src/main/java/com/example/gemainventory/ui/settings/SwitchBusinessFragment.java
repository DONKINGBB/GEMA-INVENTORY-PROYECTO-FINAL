package com.example.gemainventory.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gemainventory.MainActivity;
import com.example.gemainventory.R;
import com.example.gemainventory.api.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SwitchBusinessFragment extends Fragment {

    private RecyclerView rvBusinessList;
    private ProgressBar progressBar;
    private BusinessAdapter adapter;
    private List<Map<String, String>> businesses = new ArrayList<>();

    private String activeBusinessId;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_switch_business, container, false);

        rvBusinessList = root.findViewById(R.id.rv_business_list);
        progressBar = root.findViewById(R.id.progress_bar);
        View btnBack = root.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Obtener el negocio actual de SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        activeBusinessId = prefs.getString("user_id_negocio", "");

        rvBusinessList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BusinessAdapter(businesses);
        rvBusinessList.setAdapter(adapter);

        loadBusinesses();

        return root;
    }

    private void loadBusinesses() {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.INSTANCE.getInstance().getMisNegocios().enqueue(new Callback<List<Map<String, String>>>() {
            @Override
            public void onResponse(Call<List<Map<String, String>>> call, Response<List<Map<String, String>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    businesses.clear();
                    businesses.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar negocios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, String>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Fallo de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void switchBusiness(String idNegocio) {
        if (idNegocio != null && idNegocio.equals(activeBusinessId)) {
            Toast.makeText(getContext(), "Ya estás en este negocio", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.INSTANCE.getInstance().switchNegocio(idNegocio).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    SharedPreferences prefs = getActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    
                    Map<String, Object> body = response.body();
                    if (body != null && body.containsKey("usuario")) {
                        Map<String, Object> user = (Map<String, Object>) body.get("usuario");
                        editor.putString("user_id_negocio", (String) user.get("idNegocio"));
                        if (user.get("idRol") instanceof Double) {
                            editor.putInt("user_rol", ((Double) user.get("idRol")).intValue());
                        } else if (user.get("idRol") instanceof Integer) {
                            editor.putInt("user_rol", (Integer) user.get("idRol"));
                        }
                    } else {
                        editor.putString("user_id_negocio", idNegocio);
                    }
                    editor.apply();

                    Toast.makeText(getContext(), "Negocio cambiado con éxito", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Error al cambiar de negocio", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.ViewHolder> {
        private final List<Map<String, String>> items;

        public BusinessAdapter(List<Map<String, String>> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_business, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Map<String, String> item = items.get(position);
            String id = item.get("id");
            holder.tvName.setText(item.get("nombre"));
            
            String rolStr = "Miembro del equipo";
            String rawRol = String.valueOf(item.get("idRol"));
            if ("1".equals(rawRol) || "1.0".equals(rawRol)) rolStr = "Dueño / Propietario";
            else if ("2".equals(rawRol) || "2.0".equals(rawRol)) rolStr = "Administrador / Empleado";
            
            holder.tvRole.setText(rolStr);

            // Resaltar si es el activo
            boolean isActive = id != null && id.equals(activeBusinessId);
            holder.ivCheck.setVisibility(isActive ? View.VISIBLE : View.GONE);
            holder.itemView.setAlpha(isActive ? 1.0f : 0.9f);

            holder.itemView.setOnClickListener(v -> switchBusiness(id));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvRole;
            View ivCheck;

            ViewHolder(android.view.View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_business_name);
                tvRole = itemView.findViewById(R.id.tv_business_role);
                ivCheck = itemView.findViewById(R.id.iv_active_check);
            }
        }
    }
}
