package com.example.gemainventory.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.compose.ui.platform.ComposeView;

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

    private ComposeView composeView;
    private List<Map<String, String>> businesses = new ArrayList<>();
    private String activeBusinessId;
    private SharedPreferences sharedPreferences;
    private static final String DARK_MODE_KEY = "DarkMode";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        activeBusinessId = sharedPreferences.getString("user_id_negocio", "");
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);

        composeView = new ComposeView(requireContext());
        
        updateComposeContent();
        loadBusinesses();

        return composeView;
    }

    private void updateComposeContent() {
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
        SettingsComposeHelper.setSwitchBusinessContent(
            composeView,
            isDarkMode,
            activeBusinessId,
            businesses,
            id -> {
                switchBusiness(id);
                return kotlin.Unit.INSTANCE;
            },
            () -> {
                requireActivity().onBackPressed();
                return kotlin.Unit.INSTANCE;
            }
        );
    }

    private void loadBusinesses() {
        // Podríamos mostrar un estado de carga en Compose si quisiéramos, 
        // por ahora usamos los datos cargados.
        RetrofitClient.INSTANCE.getInstance().getMisNegocios().enqueue(new Callback<List<Map<String, String>>>() {
            @Override
            public void onResponse(Call<List<Map<String, String>>> call, Response<List<Map<String, String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    businesses.clear();
                    businesses.addAll(response.body());
                    updateComposeContent();
                } else {
                    Toast.makeText(getContext(), "Error al cargar negocios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, String>>> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void switchBusiness(String idNegocio) {
        if (idNegocio != null && idNegocio.equals(activeBusinessId)) {
            Toast.makeText(getContext(), "Ya estás en este negocio", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.INSTANCE.getInstance().switchNegocio(idNegocio).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    
                    Map<String, Object> body = response.body();
                    if (body != null && body.containsKey("usuario")) {
                        Map<String, Object> user = (Map<String, Object>) body.get("usuario");
                        editor.putString("user_id_negocio", (String) user.get("idNegocio"));
                        if (user.get("idRol") instanceof Double) {
                            editor.putInt("user_rol", ((Double) user.get("idRol")).intValue());
                        } else if (user.get("idRol") instanceof Integer) {
                            editor.putInt("user_rol", (Integer) user.get("idRol"));
                        }
                        if (user.get("imagenUrl") != null) {
                            editor.putString("user_foto_url", (String) user.get("imagenUrl"));
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
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
