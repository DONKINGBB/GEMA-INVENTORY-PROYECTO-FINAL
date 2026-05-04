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
import com.example.gemainventory.model.AlmacenDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageWarehousesFragment : Fragment() {

    private val warehouses = mutableStateListOf<AlmacenDto>()
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
                ManageWarehousesScreen(
                    darkTheme = isDarkMode,
                    warehouses = warehouses,
                    isLoading = isLoading.value,
                    onBackClick = { findNavController().popBackStack() },
                    onAddClick = {
                        findNavController().navigate(R.id.action_warehouses_to_addWarehouse)
                    },
                    onEditClick = { almacen ->
                        val args = Bundle().apply {
                            putInt("warehouse_id", almacen.idAlmacen)
                            putString("warehouse_name", almacen.nombre)
                            putString("warehouse_address", almacen.direccion)
                            almacen.latitud?.let { putDouble("latitud", it) }
                            almacen.longitud?.let { putDouble("longitud", it) }
                        }
                        findNavController().navigate(R.id.action_warehouses_to_addWarehouse, args)
                    },
                    onDeleteClick = { almacen ->
                        confirmarEliminar(almacen)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarAlmacenes()
    }

    private fun cargarAlmacenes() {
        val prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val userId = prefs.getString("user_id", null) ?: return
        
        isLoading.value = true
        RetrofitClient.instance.getAlmacenes(userId).enqueue(object : Callback<List<AlmacenDto>> {
            override fun onResponse(call: Call<List<AlmacenDto>>, response: Response<List<AlmacenDto>>) {
                isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    warehouses.clear()
                    warehouses.addAll(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<AlmacenDto>>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(context, "Error al cargar almacenes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun confirmarEliminar(almacen: AlmacenDto) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar ${almacen.nombre}")
            .setMessage("¿Estás seguro?")
            .setPositiveButton("Eliminar") { _, _ -> eliminarAlmacen(almacen.idAlmacen) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarAlmacen(id: Int) {
        RetrofitClient.instance.eliminarAlmacen(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Almacén eliminado", Toast.LENGTH_SHORT).show()
                    cargarAlmacenes()
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
