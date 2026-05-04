package com.example.gemainventory.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Label
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gemainventory.api.RetrofitClient
import com.example.gemainventory.model.CategoriaDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCategoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val prefs = requireContext().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("DarkMode", false)
        val userId = prefs.getString("user_id", null)

        val args = arguments
        val categoryId = args?.getInt("category_id") ?: 0
        val initialName = args?.getString("category_name") ?: ""
        val initialDesc = args?.getString("category_desc") ?: ""

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                var name by remember { mutableStateOf(initialName) }
                var desc by remember { mutableStateOf(initialDesc) }
                var isLoading by remember { mutableStateOf(false) }

                val fields = listOf(
                    FormFieldData(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nombre de Categoría",
                        icon = Icons.Default.Label
                    ),
                    FormFieldData(
                        value = desc,
                        onValueChange = { desc = it },
                        label = "Descripción",
                        icon = Icons.Default.Description,
                        singleLine = false
                    )
                )

                GenericAddFormScreen(
                    title = if (categoryId == 0) "Nueva Categoría" else "Editar Categoría",
                    darkTheme = isDarkMode,
                    fields = fields,
                    buttonText = if (categoryId == 0) "Guardar Categoría" else "Actualizar Categoría",
                    isLoading = isLoading,
                    onBackClick = { findNavController().popBackStack() },
                    onSaveClick = {
                        if (name.isBlank()) {
                            Toast.makeText(context, "El nombre es requerido", Toast.LENGTH_SHORT).show()
                        } else {
                            isLoading = true
                            saveCategory(categoryId, name, desc, userId) { success ->
                                isLoading = false
                                if (success) findNavController().popBackStack()
                            }
                        }
                    }
                )
            }
        }
    }

    private fun saveCategory(id: Int, name: String, desc: String, userId: String?, onComplete: (Boolean) -> Unit) {
        val dto = CategoriaDto(id, name, desc)
        val call = if (id == 0) {
            RetrofitClient.instance.crearCategoria(dto, userId ?: "")
        } else {
            RetrofitClient.instance.actualizarCategoria(id, dto)
        }

        call.enqueue(object : Callback<CategoriaDto> {
            override fun onResponse(call: Call<CategoriaDto>, response: Response<CategoriaDto>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Categoría guardada", Toast.LENGTH_SHORT).show()
                    onComplete(true)
                } else {
                    Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    onComplete(false)
                }
            }

            override fun onFailure(call: Call<CategoriaDto>, t: Throwable) {
                Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
                onComplete(false)
            }
        })
    }
}
