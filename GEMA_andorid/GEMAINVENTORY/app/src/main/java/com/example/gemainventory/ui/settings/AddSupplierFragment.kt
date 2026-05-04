package com.example.gemainventory.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gemainventory.api.RetrofitClient
import com.example.gemainventory.model.ProveedorDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddSupplierFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val prefs = requireContext().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("DarkMode", false)
        val userId = prefs.getString("user_id", null)

        val args = arguments
        val supplierId = args?.getString("supplier_id")
        val initialName = args?.getString("supplier_name") ?: ""
        val initialAddress = args?.getString("supplier_address") ?: ""
        val initialContact = args?.getString("supplier_contact") ?: ""

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
                        label = "Nombre del Proveedor",
                        icon = Icons.Default.Business
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
                        icon = Icons.Default.ContactPhone
                    )
                )

                GenericAddFormScreen(
                    title = if (supplierId == null) "Nuevo Proveedor" else "Editar Proveedor",
                    darkTheme = isDarkMode,
                    fields = fields,
                    buttonText = if (supplierId == null) "Guardar Proveedor" else "Actualizar Proveedor",
                    isLoading = isLoading,
                    onBackClick = { findNavController().popBackStack() },
                    onSaveClick = {
                        if (name.isBlank()) {
                            Toast.makeText(context, "El nombre es requerido", Toast.LENGTH_SHORT).show()
                        } else {
                            isLoading = true
                            saveSupplier(supplierId, name, contact, address, userId) { success ->
                                isLoading = false
                                if (success) findNavController().popBackStack()
                            }
                        }
                    }
                )
            }
        }
    }

    private fun saveSupplier(id: String?, name: String, contact: String, address: String, userId: String?, onComplete: (Boolean) -> Unit) {
        val dto = ProveedorDto(id, name, contact, address)
        val call = if (id == null) {
            RetrofitClient.instance.crearProveedor(dto, userId ?: "")
        } else {
            RetrofitClient.instance.actualizarProveedor(id, dto)
        }

        call.enqueue(object : Callback<ProveedorDto> {
            override fun onResponse(call: Call<ProveedorDto>, response: Response<ProveedorDto>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Proveedor guardado", Toast.LENGTH_SHORT).show()
                    onComplete(true)
                } else {
                    Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    onComplete(false)
                }
            }

            override fun onFailure(call: Call<ProveedorDto>, t: Throwable) {
                Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
                onComplete(false)
            }
        })
    }
}
