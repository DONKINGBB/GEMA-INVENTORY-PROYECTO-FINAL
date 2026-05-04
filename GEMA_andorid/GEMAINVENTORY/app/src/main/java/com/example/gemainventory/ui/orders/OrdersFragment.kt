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
import com.example.gemainventory.R
import com.example.gemainventory.api.RetrofitClient
import com.example.gemainventory.model.PedidoDto
import com.example.gemainventory.ui.theme.GemaTheme
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersFragment : Fragment() {

    private val orderList = mutableStateListOf<PedidoDto>()
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
                    OrdersView(
                        orders = orderList,
                        onAddOrderClick = { findNavController().navigate(R.id.action_orders_to_add_form) },
                        onDeliverOrder = { marcarComoEntregado(it) },
                        onDeleteOrder = { eliminarPedido(it) }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cargarPedidosReales()
    }

    private fun marcarComoEntregado(idPedido: String) {
        RetrofitClient.instance.marcarPedidoEntregado(idPedido).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "¡Pedido Entregado!", Toast.LENGTH_SHORT).show()
                    cargarPedidosReales()
                } else {
                    Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Fallo de red", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun eliminarPedido(idPedido: String) {
        val uid = userId ?: return
        RetrofitClient.instance.eliminarPedido(idPedido, uid).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Pedido eliminado", Toast.LENGTH_SHORT).show()
                    cargarPedidosReales()
                } else {
                    var error = "No se puede eliminar"
                    if (response.code() == 403) {
                        error = "No se pueden eliminar pedidos completados (protección de finanzas)"
                    }
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Fallo de red", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarPedidosReales() {
        val uid = userId ?: return

        RetrofitClient.instance.getPedidos(uid).enqueue(object : Callback<List<PedidoDto>> {
            override fun onResponse(call: Call<List<PedidoDto>>, response: Response<List<PedidoDto>>) {
                if (response.isSuccessful && response.body() != null) {
                    orderList.clear()
                    orderList.addAll(response.body()!!)
                } else {
                    Toast.makeText(context, "No se pudieron cargar pedidos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PedidoDto>>, t: Throwable) {
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
