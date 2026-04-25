package com.example.gemainventory.ui.settings;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.gemainventory.R;
import com.example.gemainventory.api.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Import NegocioDto y Negocio model
import com.example.gemainventory.model.NegocioDto;

public class BusinessActivity extends AppCompatActivity {

    private TextView tvBusinessName, tvInviteCode;
    private ImageView ivQrCode, btnEditName, btnCopyCode, btnBack;
    private NegocioDto currentBusiness = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);

        tvBusinessName = findViewById(R.id.tv_business_name);
        tvInviteCode   = findViewById(R.id.tv_invite_code);
        ivQrCode       = findViewById(R.id.iv_qr_code);
        btnEditName    = findViewById(R.id.btn_edit_business_name);
        btnCopyCode    = findViewById(R.id.btn_copy_code);
        btnBack        = findViewById(R.id.btn_back_business);

        // Volver
        btnBack.setOnClickListener(v -> onBackPressed());

        // Copiar Código
        btnCopyCode.setOnClickListener(v -> copyToClipboard());

        // UI basada en roles
        SharedPreferences prefs = getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        int roldId = prefs.getInt("user_rol", 1); // 1 = Admin

        if (roldId != 1) {
            btnEditName.setVisibility(View.GONE); // No pueden editar nombre
        } else {
            btnEditName.setOnClickListener(v -> showEditNameDialog());
        }

        findViewById(R.id.btn_join_another).setOnClickListener(v -> {
            com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions options = new com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions.Builder()
                    .setBarcodeFormats(com.google.mlkit.vision.barcode.common.Barcode.FORMAT_QR_CODE)
                    .build();
            com.google.mlkit.vision.codescanner.GmsBarcodeScanner scanner = com.google.mlkit.vision.codescanner.GmsBarcodeScanning.getClient(this, options);

            scanner.startScan()
                    .addOnSuccessListener(barcode -> {
                        String rawValue = barcode.getRawValue();
                        if (rawValue != null) {
                            joinAnotherBusiness(rawValue);
                        }
                    })
                    .addOnCanceledListener(() -> {
                        Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Fallo al escanear: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        fetchMyBusiness();
    }

    private void joinAnotherBusiness(String inviteCode) {
        Map<String, String> body = new HashMap<>();
        body.put("codigoInvitacion", inviteCode);

        RetrofitClient.INSTANCE.getInstance().joinNegocio(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> bodyResp = response.body();
                    if (Boolean.TRUE.equals(bodyResp.get("success"))) {
                        Toast.makeText(BusinessActivity.this, "¡Te has unido exitosamente! Eres operario.", Toast.LENGTH_LONG).show();
                        // Actualizamos rol local a 3
                        SharedPreferences prefs = getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
                        prefs.edit().putInt("user_rol", 3).apply();
                        fetchMyBusiness(); // Recargar los datos visuales
                    } else {
                        Toast.makeText(BusinessActivity.this, "Error: " + bodyResp.get("message"), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.code() == 404) {
                        Toast.makeText(BusinessActivity.this, "Código de invitación inválido", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BusinessActivity.this, "Error en el servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(BusinessActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMyBusiness() {
        RetrofitClient.INSTANCE.getInstance().getMiNegocio().enqueue(new Callback<NegocioDto>() {
            @Override
            public void onResponse(Call<NegocioDto> call, Response<NegocioDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentBusiness = response.body();
                    updateUI(currentBusiness);
                } else {
                    Toast.makeText(BusinessActivity.this, "Error al cargar negocio: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NegocioDto> call, Throwable t) {
                Toast.makeText(BusinessActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(NegocioDto b) {
        tvBusinessName.setText(b.getNombre());
        tvInviteCode.setText(b.getCodigoInvitacion());

        // Generar QR usando la API web de qrserver (ultra rápida y ligera)
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=" + b.getCodigoInvitacion();
        Glide.with(this)
             .load(qrUrl)
             .placeholder(R.drawable.ic_launcher_background) // Mientras carga
             .into(ivQrCode);
    }

    private void copyToClipboard() {
        if (currentBusiness != null) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Código GEMA", currentBusiness.getCodigoInvitacion());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Código copiado", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditNameDialog() {
        if (currentBusiness == null) return;

        EditText input = new EditText(this);
        input.setText(currentBusiness.getNombre());

        new AlertDialog.Builder(this)
                .setTitle("Editar Empresa")
                .setMessage("Ingresa el nuevo nombre comercial:")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevo = input.getText().toString().trim();
                    if (!nuevo.isEmpty()) updateBusinessName(nuevo);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void updateBusinessName(String nuevoNombre) {
        if (currentBusiness == null) return;

        Map<String, String> body = new HashMap<>();
        body.put("nombre", nuevoNombre);

        RetrofitClient.INSTANCE.getInstance().updateNegocio(currentBusiness.getIdNegocio(), body)
                .enqueue(new Callback<NegocioDto>() {
                    @Override
                    public void onResponse(Call<NegocioDto> call, Response<NegocioDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            currentBusiness.setNombre(response.body().getNombre());
                            tvBusinessName.setText(currentBusiness.getNombre());
                            Toast.makeText(BusinessActivity.this, "Actualizado exitosamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BusinessActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<NegocioDto> call, Throwable t) {
                        Toast.makeText(BusinessActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
