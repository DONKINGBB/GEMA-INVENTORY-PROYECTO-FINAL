package com.example.gemainventory;
 
import android.os.Bundle;
import android.widget.Toast;
 
import androidx.appcompat.app.AppCompatActivity;
 
import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.databinding.ActivityResetPasswordBinding;
 
import java.util.Map;
 
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
 
public class ResetPasswordActivity extends AppCompatActivity {
 
    private ActivityResetPasswordBinding binding;
    private String email;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
 
        email = getIntent().getStringExtra("email");
 
        binding.btnBack.setOnClickListener(v -> finish());
 
        binding.btnReset.setOnClickListener(v -> {
            String code = binding.etCode.getText().toString().trim();
            String newPassword = binding.etNewPassword.getText().toString().trim();
 
            if (code.length() < 6) {
                binding.codeLayout.setError("Código incompleto");
                return;
            }
            if (newPassword.isEmpty()) {
                binding.passwordLayout.setError("Ingresa la nueva contraseña");
                return;
            }
 
            binding.codeLayout.setError(null);
            binding.passwordLayout.setError(null);
            restablecerContrasena(code, newPassword);
        });
    }
 
    private void restablecerContrasena(String code, String newPassword) {
        binding.btnReset.setEnabled(false);
        RetrofitClient.INSTANCE.getInstance().resetPassword(email, code, newPassword).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                binding.btnReset.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Contraseña actualizada", Toast.LENGTH_LONG).show();
                    finishAffinity(); // Cierra todo y vuelve al login si está en el stack o redirige
                    startActivity(new android.content.Intent(ResetPasswordActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Código incorrecto o expirado", Toast.LENGTH_SHORT).show();
                }
            }
 
            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                binding.btnReset.setEnabled(true);
                Toast.makeText(ResetPasswordActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
