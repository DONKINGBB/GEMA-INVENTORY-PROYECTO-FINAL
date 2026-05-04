package com.example.gemainventory;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.ui.auth.ForgotPasswordComposeHelper;
import com.example.gemainventory.ui.auth.OnEmailSubmitListener;
import com.example.gemainventory.ui.auth.OnActionClickListener;

import java.util.Map;

import androidx.compose.ui.platform.ComposeView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ForgotPasswordComposeHelper composeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ComposeView composeView = new ComposeView(this);
        setContentView(composeView);

        composeHelper = new ForgotPasswordComposeHelper(composeView);

        composeHelper.setOnBackListener(() -> finish());

        composeHelper.setOnSubmitListener(email -> {
            if (email.isEmpty()) {
                Toast.makeText(this, "Ingresa tu correo", Toast.LENGTH_SHORT).show();
                return;
            }
            solicitarCodigo(email);
        });
    }

    private void solicitarCodigo(String email) {
        composeHelper.setLoading(true);
        RetrofitClient.INSTANCE.getInstance().forgotPassword(email).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                composeHelper.setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Código enviado", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                composeHelper.setLoading(false);
                Toast.makeText(ForgotPasswordActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
