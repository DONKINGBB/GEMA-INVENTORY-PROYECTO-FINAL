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
import com.example.gemainventory.model.ClienteDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageClientsFragment : Fragment() {

    private val clients = mutableStateListOf<ClienteDto>()
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
                ManageClientsScreen(
                    darkTheme = isDarkMode,
                    clients = clients,
                    isLoading = isLoading.value,
                    onBackClick = { findNavController().popBackStack() },
                    onAddClick = {
                        findNavController().navigate(R.id.action_clients_to_addClient)
                    },
                    onEditClick = { cliente ->
                        val args = Bundle().apply {
                            putString("client_id", cliente.idCliente)
                            putString("client_name", cliente.nombre)
                            putString("client_contact", cliente.contacto)
                            putString("client_address", cliente.direccion)
                        }
                        findNavController().navigate(R.id.action_clients_to_addClient, args)
                    },
                    onDeleteClick = { cliente ->
                        confirmarEliminar(cliente)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarClientes()
    }

    private fun cargarClientes() {
        val prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val userId = prefs.getString("user_id", null) ?: return
        
        isLoading.value = true
        RetrofitClient.instance.getClientes(userId).enqueue(object : Callback<List<ClienteDto>> {
            override fun onResponse(call: Call<List<ClienteDto>>, response: Response<List<ClienteDto>>) {
                isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    clients.clear()
                    clients.addAll(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<ClienteDto>>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(context, "Error al cargar clientes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun confirmarEliminar(cliente: ClienteDto) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar ${cliente.nombre}")
            .setMessage("¿Estás seguro?")
            .setPositiveButton("Eliminar") { _, _ -> eliminarCliente(cliente.idCliente) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarCliente(id: String?) {
        if (id == null) return
        RetrofitClient.instance.eliminarCliente(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Cliente eliminado", Toast.LENGTH_SHORT).show()
                    cargarClientes()
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
