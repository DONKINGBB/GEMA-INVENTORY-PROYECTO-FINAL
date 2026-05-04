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
import com.example.gemainventory.model.ProveedorDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageSuppliersFragment : Fragment() {

    private val suppliers = mutableStateListOf<ProveedorDto>()
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
                ManageSuppliersScreen(
                    darkTheme = isDarkMode,
                    suppliers = suppliers,
                    isLoading = isLoading.value,
                    onBackClick = { findNavController().popBackStack() },
                    onAddClick = {
                        findNavController().navigate(R.id.action_suppliers_to_addSupplier)
                    },
                    onEditClick = { supplier ->
                        val args = Bundle().apply {
                            putString("supplier_id", supplier.id)
                            putString("supplier_name", supplier.nombre)
                            putString("supplier_contact", supplier.contacto)
                            putString("supplier_address", supplier.direccion)
                        }
                        findNavController().navigate(R.id.action_suppliers_to_addSupplier, args)
                    },
                    onDeleteClick = { supplier ->
                        confirmarEliminar(supplier)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarProveedores()
    }

    private fun cargarProveedores() {
        val prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val userId = prefs.getString("user_id", null) ?: return
        
        isLoading.value = true
        RetrofitClient.instance.getProveedores(userId).enqueue(object : Callback<List<ProveedorDto>> {
            override fun onResponse(call: Call<List<ProveedorDto>>, response: Response<List<ProveedorDto>>) {
                isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    suppliers.clear()
                    suppliers.addAll(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<ProveedorDto>>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(context, "Error al cargar proveedores", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun confirmarEliminar(supplier: ProveedorDto) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar ${supplier.nombre}")
            .setMessage("¿Estás seguro de que deseas eliminar este proveedor?")
            .setPositiveButton("Eliminar") { _, _ -> eliminarProveedor(supplier.id) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarProveedor(id: String?) {
        if (id == null) return
        RetrofitClient.instance.eliminarProveedor(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Proveedor eliminado", Toast.LENGTH_SHORT).show()
                    cargarProveedores()
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
