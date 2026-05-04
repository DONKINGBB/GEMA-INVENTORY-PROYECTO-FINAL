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
import androidx.navigation.fragment.findNavController
import com.example.gemainventory.R
import com.example.gemainventory.api.RetrofitClient
import com.example.gemainventory.model.Usuario
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageUsersFragment : Fragment() {

    private var currentUserId: String = ""
    private var currentUserRol: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val prefs = requireContext().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        currentUserId = prefs.getString("user_id", "") ?: ""
        currentUserRol = prefs.getInt("user_rol", -1)
        
        val isDarkMode = prefs.getBoolean("DarkMode", false)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                var userList by remember { mutableStateOf<List<Usuario>>(emptyList()) }
                var isLoading by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    fetchUsers { users ->
                        userList = users
                        isLoading = false
                    }
                }

                ManageTeamScreen(
                    darkTheme = isDarkMode,
                    userList = userList,
                    currentUserId = currentUserId,
                    currentUserIdRol = currentUserRol,
                    isLoading = isLoading,
                    onBackClick = {
                        findNavController().popBackStack()
                    },
                    onAddUserClick = {
                        findNavController().navigate(R.id.action_users_to_addUser)
                    },
                    onEditRoleClick = { user ->
                        val args = Bundle().apply {
                            putString("userId", user.idUsuario)
                        }
                        findNavController().navigate(R.id.action_users_to_editRole, args)
                    },
                    onDeleteClick = { user ->
                        mostrarConfirmacionEliminar(user) {
                            isLoading = true
                            fetchUsers { users ->
                                userList = users
                                isLoading = false
                            }
                        }
                    }
                )
            }
        }
    }

    private fun fetchUsers(onResult: (List<Usuario>) -> Unit) {
        RetrofitClient.instance.obtenerUsuarios("").enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                val users = response.body()
                if (response.isSuccessful && users != null) {
                    val updatedUsers = users.toMutableList()
                    val prefs = requireContext().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
                    val currentUserEmail = prefs.getString("user_correo", "") ?: ""
                    val currentUserId = prefs.getString("user_id", "") ?: ""

                    // Primero, intentamos llenar lo que ya tenemos (ej. usuario actual)
                    users.forEach { user ->
                        if (user.idUsuario == currentUserId && user.correo.isNullOrBlank() && user.email.isNullOrBlank()) {
                            user.correo = currentUserEmail
                        }
                    }

                    // Ahora identificamos quiénes REALMENTE necesitan una llamada al servidor
                    val usersNeedingFetch = users.filter { 
                        it.correo.isNullOrBlank() && it.email.isNullOrBlank() && !it.idUsuario.isNullOrBlank()
                    }
                    
                    if (usersNeedingFetch.isEmpty()) {
                        onResult(updatedUsers)
                        return
                    }

                    var completedCount = 0
                    users.forEachIndexed { index, user ->
                        if (user.correo.isNullOrBlank() && user.email.isNullOrBlank() && !user.idUsuario.isNullOrBlank()) {
                            RetrofitClient.instance.getUsuarioById(user.idUsuario!!).enqueue(object : Callback<Usuario> {
                                override fun onResponse(call: Call<Usuario>, resp: Response<Usuario>) {
                                    val detailedUser = resp.body()
                                    if (resp.isSuccessful && detailedUser != null) {
                                        // MERGE: Solo actualizamos si el detalle trae información útil
                                        val mergedUser = user.copy(
                                            correo = detailedUser.correo ?: detailedUser.email ?: user.correo,
                                            email = detailedUser.email ?: detailedUser.correo ?: user.email,
                                            direccion = detailedUser.direccion ?: user.direccion,
                                            telefono = detailedUser.telefono ?: user.telefono,
                                            idRol = detailedUser.idRol ?: user.idRol,
                                            imagenUrl = detailedUser.imagenUrl ?: user.imagenUrl
                                        )
                                        updatedUsers[index] = mergedUser
                                    }
                                    completedCount++
                                    if (completedCount == usersNeedingFetch.size) onResult(updatedUsers)
                                }

                                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                                    completedCount++
                                    android.util.Log.e("ManageUsers", "Error al obtener detalles de ${user.idUsuario}: ${t.message}")
                                    if (completedCount == usersNeedingFetch.size) onResult(updatedUsers)
                                }
                            })
                        }
                    }
                } else {
                    onResult(emptyList())
                    Toast.makeText(context, "Error al cargar equipo", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                onResult(emptyList())
                Toast.makeText(context, "Fallo de red", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarConfirmacionEliminar(user: Usuario, onDeleted: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Eliminar miembro")
            .setMessage("¿Estás seguro de que quieres eliminar a ${user.nombre} del equipo?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarUsuario(user, onDeleted)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarUsuario(user: Usuario, onDeleted: () -> Unit) {
        val id = user.idUsuario ?: return
        RetrofitClient.instance.eliminarUsuario(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show()
                    onDeleted()
                } else {
                    var errorMsg = "Error: ${response.code()}"
                    try {
                        val body = response.errorBody()?.string()
                        if (body != null) {
                            if (body.contains("\"message\":\"")) {
                                errorMsg = body.split("\"message\":\"")[1].split("\"")[0]
                            } else {
                                errorMsg = body
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error de red al eliminar", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
