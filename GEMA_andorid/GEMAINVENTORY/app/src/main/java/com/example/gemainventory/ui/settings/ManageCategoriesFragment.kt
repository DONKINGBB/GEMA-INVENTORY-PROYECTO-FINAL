package com.example.gemainventory.ui.settings

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gemainventory.R
import com.example.gemainventory.api.RetrofitClient
import com.example.gemainventory.model.CategoriaDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageCategoriesFragment : Fragment() {

    private val categories = mutableStateListOf<CategoriaDto>()
    private val isLoading = mutableStateOf(true)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("DarkMode", false)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ManageCategoriesScreen(
                    darkTheme = isDarkMode,
                    categories = categories,
                    isLoading = isLoading.value,
                    onBackClick = { findNavController().popBackStack() },
                    onAddClick = {
                        findNavController().navigate(R.id.action_categories_to_addCategory)
                    },
                    onEditClick = { categoria ->
                        val args = Bundle().apply {
                            putInt("category_id", categoria.idCategoria)
                            putString("category_name", categoria.nombre)
                            putString("category_desc", categoria.descripcion)
                        }
                        findNavController().navigate(R.id.action_categories_to_addCategory, args)
                    },
                    onDeleteClick = { categoria ->
                        confirmarEliminar(categoria)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarCategorias()
    }

    private fun cargarCategorias() {
        val prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val userId = prefs.getString("user_id", null) ?: return
        
        isLoading.value = true
        RetrofitClient.instance.getCategorias(userId).enqueue(object : Callback<List<CategoriaDto>> {
            override fun onResponse(call: Call<List<CategoriaDto>>, response: Response<List<CategoriaDto>>) {
                isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    categories.clear()
                    categories.addAll(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<CategoriaDto>>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(context, "Error al cargar categorías", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun confirmarEliminar(categoria: CategoriaDto) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar ${categoria.nombre}")
            .setMessage("¿Estás seguro? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ -> eliminarCategoria(categoria.idCategoria) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarCategoria(id: Int) {
        RetrofitClient.instance.eliminarCategoria(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Categoría eliminada", Toast.LENGTH_SHORT).show()
                    cargarCategorias()
                } else {
                    Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
