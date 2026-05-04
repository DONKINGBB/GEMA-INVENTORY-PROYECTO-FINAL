package com.example.gemainventory.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gemainventory.api.RetrofitClient
import com.example.gemainventory.model.ClienteDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddClientFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val prefs = requireContext().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("DarkMode", false)
        val userId = prefs.getString("user_id", null)

        val args = arguments
        val clientId = args?.getString("client_id")
        val initialName = args?.getString("client_name") ?: ""
        val initialAddress = args?.getString("client_address") ?: ""
        val initialContact = args?.getString("client_contact") ?: ""

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                var name by remember { mutableStateOf(initialName) }
                var address by remember { mutableStateOf(initialAddress) }
                var contact by remember { mutableStateOf(initialContact) }
                var isLoading by remember { mutableStateOf(false) }

                val fields = listOf(
                    FormFieldData(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nombre del Cliente",
                        icon = Icons.Default.Badge
                    ),
                    FormFieldData(
                        value = address,
                        onValueChange = { address = it },
                        label = "Dirección",
                        icon = Icons.Default.Home
                    ),
                    FormFieldData(
                        value = contact,
                        onValueChange = { contact = it },
                        label = "Contacto (Email o Teléfono)",
                        icon = Icons.Default.ContactPage
                    )
                )

                GenericAddFormScreen(
                    title = if (clientId == null) "Nuevo Cliente" else "Editar Cliente",
                    darkTheme = isDarkMode,
                    fields = fields,
                    buttonText = if (clientId == null) "Guardar Cliente" else "Actualizar Cliente",
                    isLoading = isLoading,
                    onBackClick = { findNavController().popBackStack() },
                    onSaveClick = {
                        if (name.isBlank()) {
                            Toast.makeText(context, "El nombre es requerido", Toast.LENGTH_SHORT).show()
                        } else {
                            isLoading = true
                            saveClient(clientId, name, contact, address, userId) { success ->
                                isLoading = false
                                if (success) findNavController().popBackStack()
                            }
                        }
                    },
                    extraContent = {
                        Spacer(modifier = Modifier.height(16.dp))
                        MapSelectionButton(darkTheme = isDarkMode) {
                            Toast.makeText(context, "Módulo de mapa próximamente", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }

    private fun saveClient(id: String?, name: String, contact: String, address: String, userId: String?, onComplete: (Boolean) -> Unit) {
        val dto = ClienteDto(id, name, contact, address)
        val call = if (id == null) {
            RetrofitClient.instance.crearCliente(dto, userId ?: "")
        } else {
            RetrofitClient.instance.actualizarCliente(id, dto)
        }

        call.enqueue(object : Callback<ClienteDto> {
            override fun onResponse(call: Call<ClienteDto>, response: Response<ClienteDto>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Cliente guardado", Toast.LENGTH_SHORT).show()
                    onComplete(true)
                } else {
                    Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    onComplete(false)
                }
            }

            override fun onFailure(call: Call<ClienteDto>, t: Throwable) {
                Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
                onComplete(false)
            }
        })
    }
}
