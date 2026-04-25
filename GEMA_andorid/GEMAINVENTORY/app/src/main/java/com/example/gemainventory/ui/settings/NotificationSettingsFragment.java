package com.example.gemainventory.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.gemainventory.R;
import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.model.Usuario;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationSettingsFragment extends Fragment {

    private SwitchMaterial switchLowStock, switchNewOrders, switchInventoryChanges;
    private MaterialButton btnSave;
    private ProgressBar progressBar;
    private SharedPreferences prefs;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View btnBack = view.findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        switchLowStock = view.findViewById(R.id.switch_low_stock);
        switchNewOrders = view.findViewById(R.id.switch_new_orders);
        switchInventoryChanges = view.findViewById(R.id.switch_inventory_changes);
        btnSave = view.findViewById(R.id.btn_save_settings);
        progressBar = view.findViewById(R.id.progress_bar);

        prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", null);

        if (userId != null) {
            loadPreferences();
        }

        btnSave.setOnClickListener(v -> savePreferences());
    }

    private void loadPreferences() {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.INSTANCE.getInstance().getUserPreferences(userId).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Usuario u = response.body();
                    switchLowStock.setChecked(Boolean.TRUE.equals(u.getNotifyLowStock()));
                    switchNewOrders.setChecked(Boolean.TRUE.equals(u.getNotifyNewOrders()));
                    switchInventoryChanges.setChecked(Boolean.TRUE.equals(u.getNotifyInventoryChanges()));
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar preferencias", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePreferences() {
        progressBar.setVisibility(View.VISIBLE);
        Usuario settings = new Usuario();
        settings.setNotifyLowStock(switchLowStock.isChecked());
        settings.setNotifyNewOrders(switchNewOrders.isChecked());
        settings.setNotifyInventoryChanges(switchInventoryChanges.isChecked());

        RetrofitClient.INSTANCE.getInstance().updateNotifications(userId, settings).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Preferencias guardadas", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Fallo de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
