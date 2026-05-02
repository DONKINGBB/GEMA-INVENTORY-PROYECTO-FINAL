package com.example.gemainventory.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gemainventory.R
import com.example.gemainventory.SplashActivity
import com.example.gemainventory.api.RetrofitClient
import com.example.gemainventory.model.PasswordUpdateDto
import com.example.gemainventory.model.UploadResponse
import com.example.gemainventory.model.Usuario
import com.example.gemainventory.model.UsuarioUpdateDto
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException

class EditProfileFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    
    // State for Compose
    private var name by mutableStateOf("")
    private var email by mutableStateOf("")
    private var address by mutableStateOf("")
    private var phone by mutableStateOf("")
    private var currentRemoteUrl by mutableStateOf<String?>(null)
    private var selectedLocalUri by mutableStateOf<Uri?>(null)
    private var isLoading by mutableStateOf(false)
    private var isDarkMode by mutableStateOf(false)

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val selectedImage = result.data?.data
            if (selectedImage != null) {
                try {
                    requireActivity().contentResolver.takePersistableUriPermission(
                        selectedImage,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
                selectedLocalUri = selectedImage
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        prefs = requireContext().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        isDarkMode = prefs.getBoolean("DarkMode", false)
        
        name = prefs.getString("user_nombre", "") ?: ""
        email = prefs.getString("user_correo", "") ?: ""
        address = prefs.getString("user_direccion", "") ?: ""
        phone = prefs.getString("user_telefono", "") ?: ""
        currentRemoteUrl = prefs.getString("user_foto_url", null)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val photoPreview = if (selectedLocalUri != null) {
                    selectedLocalUri.toString()
                } else if (currentRemoteUrl != null) {
                    RetrofitClient.getFullImageUrl(currentRemoteUrl)
                } else {
                    null
                }

                EditProfileScreen(
                    darkTheme = isDarkMode,
                    name = name,
                    onNameChange = { name = it },
                    email = email,
                    address = address,
                    onAddressChange = { address = it },
                    phone = phone,
                    onPhoneChange = { phone = it },
                    photoUrl = photoPreview,
                    onPhotoClick = { openGallery() },
                    onSaveClick = { handleSave() },
                    onPasswordChangeRequest = { oldP, newP -> executePasswordChange(oldP, newP) },
                    onDeleteAccountClick = { executeAccountDeletion() },
                    onBackClick = { findNavController().popBackStack() },
                    isLoading = isLoading
                )
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun handleSave() {
        if (selectedLocalUri != null) {
            uploadImageAndSave()
        } else {
            saveProfileData(currentRemoteUrl)
        }
    }

    private fun uploadImageAndSave() {
        isLoading = true
        Toast.makeText(context, "Subiendo imagen...", Toast.LENGTH_SHORT).show()

        try {
            val imageBytes = getBytesFromUri(selectedLocalUri!!)
            if (imageBytes == null) {
                isLoading = false
                Toast.makeText(context, "Error al leer imagen", Toast.LENGTH_SHORT).show()
                return
            }

            val mediaType = "image/jpeg".toMediaTypeOrNull()
            val requestFile = imageBytes.toRequestBody(mediaType)
            val body = MultipartBody.Part.createFormData("file", "profile.jpg", requestFile)

            RetrofitClient.instance.uploadImage(body).enqueue(object : Callback<UploadResponse> {
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        saveProfileData(response.body()!!.url)
                    } else {
                        isLoading = false
                        Toast.makeText(context, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    isLoading = false
                    Toast.makeText(context, "Fallo de red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: IOException) {
            isLoading = false
            Toast.makeText(context, "Error de archivo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfileData(photoUrl: String?) {
        if (name.isBlank()) {
            Toast.makeText(context, "Nombre requerido", Toast.LENGTH_SHORT).show()
            isLoading = false
            return
        }

        val userId = prefs.getString("user_id", null) ?: return
        val updateDto = UsuarioUpdateDto(name, address, phone, photoUrl)

        RetrofitClient.instance.actualizarPerfil(userId, updateDto).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                isLoading = false
                if (response.isSuccessful) {
                    prefs.edit().apply {
                        putString("user_nombre", name)
                        putString("user_direccion", address)
                        putString("user_telefono", phone)
                        photoUrl?.let { putString("user_foto_url", it) }
                        apply()
                    }
                    Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, "Error al guardar perfil: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                isLoading = false
                Toast.makeText(context, "Fallo de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun executePasswordChange(oldP: String, newP: String) {
        val userId = prefs.getString("user_id", null) ?: return
        val dto = PasswordUpdateDto(oldP, newP)

        RetrofitClient.instance.changePassword(userId, dto).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Toast.makeText(context, "Fallo de red", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun executeAccountDeletion() {
        val userId = prefs.getString("user_id", null) ?: return
        RetrofitClient.instance.eliminarCuentaPropia(userId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Cuenta eliminada", Toast.LENGTH_LONG).show()
                    logoutAndExit()
                } else {
                    Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Fallo de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun logoutAndExit() {
        prefs.edit().clear().apply()
        val intent = Intent(requireActivity(), SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun getBytesFromUri(uri: Uri): ByteArray? {
        return try {
            val iStream = requireContext().contentResolver.openInputStream(uri)
            val byteBuffer = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var len: Int
            while (iStream?.read(buffer).also { len = it ?: -1 } != -1) {
                byteBuffer.write(buffer, 0, len)
            }
            byteBuffer.toByteArray()
        } catch (e: Exception) {
            null
        }
    }
}
