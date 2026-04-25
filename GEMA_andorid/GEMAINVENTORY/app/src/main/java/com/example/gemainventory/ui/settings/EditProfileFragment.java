package com.example.gemainventory.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.gemainventory.R;
import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.model.UploadResponse;
import com.example.gemainventory.model.Usuario;
import com.example.gemainventory.model.UsuarioUpdateDto;
import com.example.gemainventory.model.PasswordUpdateDto;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.gemainventory.SplashActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {

    private TextInputEditText etName, etEmail, etAddress, etPhone;
    private ImageView ivProfile;
    private SharedPreferences prefs;

    private String selectedLocalUriString = null;
    private String currentRemoteUrl = null;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        try {
                            requireActivity().getContentResolver().takePersistableUriPermission(
                                    selectedImage,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }

                        Glide.with(requireContext()).load(selectedImage).centerCrop().into(ivProfile);
                        ivProfile.setColorFilter(null);
                        selectedLocalUriString = selectedImage.toString();
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etName = view.findViewById(R.id.et_edit_name);
        etEmail = view.findViewById(R.id.et_edit_email);
        etAddress = view.findViewById(R.id.et_edit_address);
        etPhone = view.findViewById(R.id.et_edit_phone);
        ivProfile = view.findViewById(R.id.iv_profile_image);

        Button btnSave = view.findViewById(R.id.btn_save_profile);
        FloatingActionButton btnPhoto = view.findViewById(R.id.btn_change_photo);
        View btnBack = view.findViewById(R.id.btn_back);
        Button btnChangePassword = view.findViewById(R.id.btn_change_password);
        Button btnDeleteAccount = view.findViewById(R.id.btn_delete_account);

        prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        etName.setText(prefs.getString("user_nombre", ""));
        etEmail.setText(prefs.getString("user_correo", ""));
        etAddress.setText(prefs.getString("user_direccion", ""));
        etPhone.setText(prefs.getString("user_telefono", ""));

        currentRemoteUrl = prefs.getString("user_foto_url", null);
        if (currentRemoteUrl != null && !currentRemoteUrl.isEmpty()) {
            Glide.with(requireContext())
                    .load(RetrofitClient.getFullImageUrl(currentRemoteUrl))
                    .centerCrop()
                    .placeholder(R.drawable.ic_account_circle)
                    .into(ivProfile);
            ivProfile.setColorFilter(null);
        }

        btnPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> {
            if (selectedLocalUriString != null) {
                subirImagenYGuardar();
            } else {
                guardarDatosPerfil(currentRemoteUrl);
            }
        });

        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnDeleteAccount.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void subirImagenYGuardar() {
        View btnSave = getView().findViewById(R.id.btn_save_profile);
        btnSave.setEnabled(false);
        Toast.makeText(getContext(), "Subiendo imagen...", Toast.LENGTH_SHORT).show();

        try {
            Uri uri = Uri.parse(selectedLocalUriString);
            byte[] imageBytes = getBytesFromUri(uri);
            if (imageBytes == null) {
                btnSave.setEnabled(true);
                Toast.makeText(getContext(), "Error al leer imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", "profile.jpg", requestFile);

            RetrofitClient.INSTANCE.getInstance().uploadImage(body).enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String newUrl = response.body().getUrl();
                        guardarDatosPerfil(newUrl);
                    } else {
                        btnSave.setEnabled(true);
                        Toast.makeText(getContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    btnSave.setEnabled(true);
                    Toast.makeText(getContext(), "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            btnSave.setEnabled(true);
            Toast.makeText(getContext(), "Error de archivo", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarDatosPerfil(String photoUrl) {
        String nombre = etName.getText().toString().trim();
        String direccion = etAddress.getText().toString().trim();
        String telefono = etPhone.getText().toString().trim();

        if (nombre.isEmpty()) {
            etName.setError("Nombre requerido");
            return;
        }

        String userId = prefs.getString("user_id", null);
        UsuarioUpdateDto updateDto = new UsuarioUpdateDto(nombre, direccion, telefono, photoUrl);

        RetrofitClient.INSTANCE.getInstance().actualizarPerfil(userId, updateDto).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("user_nombre", nombre);
                    editor.putString("user_direccion", direccion);
                    editor.putString("user_telefono", telefono);
                    if (photoUrl != null) {
                        editor.putString("user_foto_url", photoUrl);
                    }
                    editor.apply();

                    Toast.makeText(getContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(EditProfileFragment.this).popBackStack();
                } else {
                    Toast.makeText(getContext(), "Error al guardar perfil: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_password, null);
        TextInputEditText etOld = dialogView.findViewById(R.id.et_old_password);
        TextInputEditText etNew = dialogView.findViewById(R.id.et_new_password);
 
        new MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
                .setTitle("Cambiar Contraseña")
                .setView(dialogView)
                .setPositiveButton("Actualizar", (dialog, which) -> {
                    String oldP = etOld.getText().toString().trim();
                    String newP = etNew.getText().toString().trim();
                    if (oldP.isEmpty() || newP.isEmpty()) {
                        Toast.makeText(getContext(), "Campos requeridos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ejecutarCambioPassword(oldP, newP);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
 
    private void ejecutarCambioPassword(String oldP, String newP) {
        String userId = prefs.getString("user_id", null);
        PasswordUpdateDto dto = new PasswordUpdateDto(oldP, newP);
 
        RetrofitClient.INSTANCE.getInstance().changePassword(userId, dto).enqueue(new Callback<java.util.Map<String, String>>() {
            @Override
            public void onResponse(Call<java.util.Map<String, String>> call, Response<java.util.Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
 
            @Override
            public void onFailure(Call<java.util.Map<String, String>> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
 
    private void showDeleteConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
                .setTitle("¿Eliminar cuenta definitivamente?")
                .setMessage("Esta acción no se puede deshacer. Perderás todo el acceso a tus inventarios, reportes y configuraciones de negocio.")
                .setIcon(R.drawable.ic_delete_24)
                .setPositiveButton("SÍ, ELIMINAR TODO", (dialog, which) -> ejecutarEliminacionCuenta())
                .setNegativeButton("CANCELAR", null)
                .show();
    }
 
    private void ejecutarEliminacionCuenta() {
        String userId = prefs.getString("user_id", null);
        RetrofitClient.INSTANCE.getInstance().eliminarCuentaPropia(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Cuenta eliminada", Toast.LENGTH_LONG).show();
                    logoutAndExit();
                } else {
                    Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            }
 
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
 
    private void logoutAndExit() {
        prefs.edit().clear().apply();
        Intent intent = new Intent(requireActivity(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private byte[] getBytesFromUri(Uri uri) throws IOException {
        InputStream iStream = requireContext().getContentResolver().openInputStream(uri);
        if (iStream == null) return null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = iStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}