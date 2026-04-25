package com.example.gemainventory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.model.LoginResponse; // Reusing or using a general map representation

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectBusinessActivity extends AppCompatActivity {

    private EditText etCrearNombre, etUnirCodigo;
    private Button btnCrear, btnUnir, btnQr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_business);

        etCrearNombre = findViewById(R.id.et_crear_nombre);
        etUnirCodigo  = findViewById(R.id.et_unir_codigo);
        btnCrear      = findViewById(R.id.btn_crear_negocio);
        btnUnir       = findViewById(R.id.btn_unir_negocio);
        btnQr         = findViewById(R.id.btn_unir_qr);

        btnCrear.setOnClickListener(v -> {
            String nombre = etCrearNombre.getText().toString().trim();
            if (nombre.isEmpty()) {
                etCrearNombre.setError("Requerido");
                return;
            }
            crearNegocio(nombre);
        });

        btnUnir.setOnClickListener(v -> {
            String codigo = etUnirCodigo.getText().toString().trim();
            if (codigo.isEmpty()) {
                etUnirCodigo.setError("Requerido");
                return;
            }
            unirNegocio(codigo);
        });

        btnQr.setOnClickListener(v -> {
            com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions options = new com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions.Builder()
                    .setBarcodeFormats(com.google.mlkit.vision.barcode.common.Barcode.FORMAT_QR_CODE)
                    .build();
            com.google.mlkit.vision.codescanner.GmsBarcodeScanner scanner = com.google.mlkit.vision.codescanner.GmsBarcodeScanning.getClient(this, options);
            
            scanner.startScan()
                    .addOnSuccessListener(barcode -> {
                        String rawValue = barcode.getRawValue();
                        if (rawValue != null) {
                            etUnirCodigo.setText(rawValue);
                            unirNegocio(rawValue);
                        }
                    })
                    .addOnCanceledListener(() -> {
                        Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Fallo al escanear: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void crearNegocio(String nombre) {
        btnCrear.setEnabled(false);
        Map<String, String> body = new HashMap<>();
        body.put("nombre", nombre);

        RetrofitClient.INSTANCE.getInstance().createNegocio(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                btnCrear.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> bodyResp = response.body();
                    if (Boolean.TRUE.equals(bodyResp.get("success"))) {
                        Map<String, Object> negocio = (Map<String, Object>) bodyResp.get("negocio");
                        String realId = (negocio != null) ? String.valueOf(negocio.get("idNegocio")) : "ASIGNADO";
                        guardarNegocioYContinuar(realId, 1);
                    } else {
                        Toast.makeText(SelectBusinessActivity.this, "Error: " + bodyResp.get("message"), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SelectBusinessActivity.this, "Error en el servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                btnCrear.setEnabled(true);
                Toast.makeText(SelectBusinessActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unirNegocio(String codigo) {
        btnUnir.setEnabled(false);
        Map<String, String> body = new HashMap<>();
        body.put("codigoInvitacion", codigo);

        RetrofitClient.INSTANCE.getInstance().joinNegocio(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                btnUnir.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> bodyResp = response.body();
                    if (Boolean.TRUE.equals(bodyResp.get("success"))) {
                        Map<String, Object> negocio = (Map<String, Object>) bodyResp.get("negocio");
                        String realId = (negocio != null) ? String.valueOf(negocio.get("idNegocio")) : "ASIGNADO";
                        guardarNegocioYContinuar(realId, 3); // 3 es Operario
                    } else {
                        Toast.makeText(SelectBusinessActivity.this, "Error: " + bodyResp.get("message"), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.code() == 404) {
                        Toast.makeText(SelectBusinessActivity.this, "Código de invitación inválido", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SelectBusinessActivity.this, "Error en el servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                btnUnir.setEnabled(true);
                Toast.makeText(SelectBusinessActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarNegocioYContinuar(String businessId, int nuevoRol) {
        SharedPreferences prefs = getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        editor.putString("id_negocio", businessId); 
        editor.putInt("user_rol", nuevoRol); 
        editor.apply();

        Toast.makeText(this, "¡Configuración Exitosa!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
