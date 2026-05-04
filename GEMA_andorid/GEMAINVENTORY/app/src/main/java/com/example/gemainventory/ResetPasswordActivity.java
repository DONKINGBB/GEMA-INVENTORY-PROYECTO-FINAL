package com.example.gemainventory;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.ui.auth.ResetPasswordComposeHelper;
import com.example.gemainventory.ui.auth.OnResetSubmitListener;
import com.example.gemainventory.ui.auth.OnActionClickListener;

import java.util.Map;

import androidx.compose.ui.platform.ComposeView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private ResetPasswordComposeHelper composeHelper;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        email = getIntent().getStringExtra("email");
        
        ComposeView composeView = new ComposeView(this);
        setContentView(composeView);

        composeHelper = new ResetPasswordComposeHelper(composeView, email != null ? email : "");

        composeHelper.setOnBackListener(() -> finish());

        composeHelper.setOnSubmitListener((code, newPassword) -> {
            if (code.length() < 6) {
                Toast.makeText(this, "Código incompleto", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newPassword.isEmpty()) {
                Toast.makeText(this, "Ingresa la nueva contraseña", Toast.LENGTH_SHORT).show();
                return;
            }
            restablecerContrasena(code, newPassword);
        });
    }

    private void restablecerContrasena(String code, String newPassword) {
        composeHelper.setLoading(true);
        RetrofitClient.INSTANCE.getInstance().resetPassword(email, code, newPassword).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                composeHelper.setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Contraseña actualizada", Toast.LENGTH_LONG).show();
                    finishAffinity();
                    startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Código incorrecto o expirado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                composeHelper.setLoading(false);
                Toast.makeText(ResetPasswordActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
