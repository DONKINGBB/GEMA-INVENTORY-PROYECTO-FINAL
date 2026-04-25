package com.example.gemainventory;
 
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
 
import androidx.appcompat.app.AppCompatActivity;
 
import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.databinding.ActivityForgotPasswordBinding;
 
import java.util.Map;
 
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
 
public class ForgotPasswordActivity extends AppCompatActivity {
 
    private ActivityForgotPasswordBinding binding;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
 
        binding.btnBack.setOnClickListener(v -> finish());
 
        binding.btnSendCode.setOnClickListener(v -> {
            String email = binding.etForgotEmail.getText().toString().trim();
            if (email.isEmpty()) {
                binding.emailLayout.setError("Ingresa tu correo");
                return;
            }
            binding.emailLayout.setError(null);
            solicitarCodigo(email);
        });
    }
 
    private void solicitarCodigo(String email) {
        setLoading(true);
        RetrofitClient.INSTANCE.getInstance().forgotPassword(email).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                setLoading(false);
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
                setLoading(false);
                Toast.makeText(ForgotPasswordActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
 
    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnSendCode.setEnabled(!loading);
    }
}
