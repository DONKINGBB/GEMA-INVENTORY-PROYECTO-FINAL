package com.example.gemainventory.ui.orders

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
import com.example.gemainventory.api.RetrofitClient
import com.example.gemainventory.model.*
import com.example.gemainventory.ui.theme.GemaTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddOrderFragment : Fragment() {

    private val clientsState = mutableStateListOf<ClienteDto>()
    private val warehousesState = mutableStateListOf<AlmacenDto>()
    private val availableProductsState = mutableStateListOf<ProductoSeleccionDto>()
    
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sharedPref = requireContext().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        userId = sharedPref.getString("user_id", null)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                GemaTheme {
                    AddOrderScreen(
                        clients = clientsState,
                        warehouses = warehousesState,
                        availableProducts = availableProductsState,
                        onBack = { findNavController().popBackStack() },
                        onWarehouseSelected = { warehouseId -> fetchProductsByWarehouse(warehouseId) },
                        onSave = { pedido -> guardarPedido(pedido) }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchClients()
        fetchWarehouses()
    }

    private fun fetchClients() {
        val uid = userId ?: return
        RetrofitClient.instance.getClientes(uid).enqueue(object : Callback<List<ClienteDto>> {
            override fun onResponse(call: Call<List<ClienteDto>>, response: Response<List<ClienteDto>>) {
                if (response.isSuccessful && response.body() != null) {
                    clientsState.clear()
                    clientsState.addAll(response.body()!!)
                }
            }
            override fun onFailure(call: Call<List<ClienteDto>>, t: Throwable) {}
        })
    }

    private fun fetchWarehouses() {
        val uid = userId ?: return
        RetrofitClient.instance.getAlmacenes(uid).enqueue(object : Callback<List<AlmacenDto>> {
            override fun onResponse(call: Call<List<AlmacenDto>>, response: Response<List<AlmacenDto>>) {
                if (response.isSuccessful && response.body() != null) {
                    warehousesState.clear()
                    warehousesState.addAll(response.body()!!)
                }
            }
            override fun onFailure(call: Call<List<AlmacenDto>>, t: Throwable) {}
        })
    }

    private fun fetchProductsByWarehouse(warehouseId: Int) {
        val uid = userId ?: return
        RetrofitClient.instance.getProductosPorAlmacen(uid, warehouseId).enqueue(object : Callback<List<ProductoSeleccionDto>> {
            override fun onResponse(call: Call<List<ProductoSeleccionDto>>, response: Response<List<ProductoSeleccionDto>>) {
                if (response.isSuccessful && response.body() != null) {
                    availableProductsState.clear()
                    availableProductsState.addAll(response.body()!!)
                    if (response.body()!!.isEmpty()) {
                        Toast.makeText(context, "No hay stock en este almacén", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<List<ProductoSeleccionDto>>, t: Throwable) {
                Toast.makeText(context, "Error al cargar productos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun guardarPedido(pedido: PedidoDto) {
        val uid = userId ?: return
        RetrofitClient.instance.crearPedido(pedido, uid).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Pedido Guardado", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
