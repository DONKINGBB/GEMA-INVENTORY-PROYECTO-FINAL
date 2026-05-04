package com.example.gemainventory.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gemainventory.api.RetrofitClient
import com.example.gemainventory.model.AlmacenDto
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddWarehouseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val prefs = requireContext().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("DarkMode", false)
        val userId = prefs.getString("user_id", null)

        val args = arguments
        val warehouseId = args?.getInt("warehouse_id") ?: 0
        val initialName = args?.getString("warehouse_name") ?: ""
        val initialAddress = args?.getString("warehouse_address") ?: ""
        val initialLat = if (args?.containsKey("latitud") == true) args.getDouble("latitud") else null
        val initialLng = if (args?.containsKey("longitud") == true) args.getDouble("longitud") else null

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                var name by remember { mutableStateOf(initialName) }
                var address by remember { mutableStateOf(initialAddress) }
                var lat by remember { mutableStateOf(initialLat) }
                var lng by remember { mutableStateOf(initialLng) }
                var isLoading by remember { mutableStateOf(false) }

                val fields = listOf(
                    FormFieldData(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nombre del Almacén",
                        icon = Icons.Default.Business
                    ),
                    FormFieldData(
                        value = address,
                        onValueChange = { address = it },
                        label = "Ubicación / Dirección",
                        icon = Icons.Default.LocationOn
                    )
                )

                GenericAddFormScreen(
                    title = if (warehouseId == 0) "Nuevo Almacén" else "Editar Almacén",
                    darkTheme = isDarkMode,
                    fields = fields,
                    buttonText = if (warehouseId == 0) "Guardar Almacén" else "Actualizar Almacén",
                    isLoading = isLoading,
                    onBackClick = { findNavController().popBackStack() },
                    onSaveClick = {
                        if (name.isBlank()) {
                            Toast.makeText(context, "El nombre es requerido", Toast.LENGTH_SHORT).show()
                        } else {
                            isLoading = true
                            saveWarehouse(warehouseId, name, address, lat, lng, userId) { success ->
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
                        
                        if (lat != null && lng != null) {
                            Text(
                                text = "Coordenadas: ${"%.4f".format(lat)}, ${"%.4f".format(lng)}",
                                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                                color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                )
            }
        }
    }

    private fun saveWarehouse(id: Int, name: String, address: String, lat: Double?, lng: Double?, userId: String?, onComplete: (Boolean) -> Unit) {
        val dto = AlmacenDto(id, name, address, lat, lng)
        val call = if (id == 0) {
            RetrofitClient.instance.crearAlmacen(dto, userId ?: "")
        } else {
            RetrofitClient.instance.actualizarAlmacen(id, dto)
        }

        call.enqueue(object : Callback<AlmacenDto> {
            override fun onResponse(call: Call<AlmacenDto>, response: Response<AlmacenDto>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Almacén guardado", Toast.LENGTH_SHORT).show()
                    onComplete(true)
                } else {
                    Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    onComplete(false)
                }
            }

            override fun onFailure(call: Call<AlmacenDto>, t: Throwable) {
                Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
                onComplete(false)
            }
        })
    }
}
