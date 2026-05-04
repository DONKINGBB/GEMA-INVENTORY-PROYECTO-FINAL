package com.example.gemainventory.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.example.gemainventory.api.RetrofitClient
import com.example.gemainventory.model.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditUserRoleFragment : Fragment() {

    private var targetUserId: String? = null
    private var currentUserRol: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        targetUserId = arguments?.getString("userId")
        
        val prefs = requireContext().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        currentUserRol = prefs.getInt("user_rol", -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val prefs = requireContext().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("DarkMode", false)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                var targetUser by remember { mutableStateOf<Usuario?>(null) }
                var isLoading by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    fetchTargetUser { user ->
                        targetUser = user
                        isLoading = false
                    }
                }

                EditUserRoleScreen(
                    darkTheme = isDarkMode,
                    user = targetUser,
                    currentUserRole = currentUserRol,
                    isLoading = isLoading,
                    onBackClick = { requireActivity().onBackPressed() },
                    onUpdateClick = { newRoleId ->
                        updateRole(targetUser, newRoleId)
                    }
                )
            }
        }
    }

    private fun fetchTargetUser(onResult: (Usuario?) -> Unit) {
        val id = targetUserId ?: return
        RetrofitClient.instance.getUsuarioById(id).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    Toast.makeText(context, "Error al obtener datos", Toast.LENGTH_SHORT).show()
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
                onResult(null)
            }
        })
    }

    private fun updateRole(user: Usuario?, newRoleId: Int) {
        val id = targetUserId ?: return
        val currentUser = user ?: return

        // Validación de jerarquía (espejo de la lógica anterior)
        if (currentUserRol == 2 && currentUser.idRol != 3) {
            Toast.makeText(context, "No tienes rango para editar a este usuario", Toast.LENGTH_LONG).show()
            return
        }

        currentUser.idRol = newRoleId

        // Sincronizar correo y email para evitar errores 500 por nulos en el backend
        val tempCorreo = currentUser.correo
        val tempEmail = currentUser.email

        if (tempCorreo.isNullOrEmpty()) {
            if (!tempEmail.isNullOrEmpty()) {
                currentUser.correo = tempEmail
            }
        } else if (tempEmail.isNullOrEmpty()) {
            currentUser.email = tempCorreo
        }

        RetrofitClient.instance.actualizarUsuario(id, currentUser).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Rol actualizado correctamente", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                } else if (response.code() == 403) {
                    Toast.makeText(context, "Error de Jerarquía: Sin permisos", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Toast.makeText(context, "Fallo de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
