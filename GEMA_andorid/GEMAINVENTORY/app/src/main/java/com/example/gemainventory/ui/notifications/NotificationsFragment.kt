package com.example.gemainventory.ui.notifications

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gemainventory.R
import com.example.gemainventory.api.RetrofitClient
import com.example.gemainventory.databinding.FragmentNotificationsBinding
import com.example.gemainventory.model.NotificationItem
import com.example.gemainventory.model.NotificationType
import java.text.NumberFormat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: NotificationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NotificationsAdapter(emptyList())
        binding.rvNotifications.layoutManager = LinearLayoutManager(context)
        binding.rvNotifications.adapter = adapter

        fetchNotifications()
    }

    private fun fetchNotifications() {
        val prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val userId = prefs.getString("user_id", null) ?: return

        RetrofitClient.instance.getPedidos(userId).enqueue(object : Callback<List<com.example.gemainventory.model.PedidoDto>> {
            override fun onResponse(call: Call<List<com.example.gemainventory.model.PedidoDto>>, response: Response<List<com.example.gemainventory.model.PedidoDto>>) {
                val orders = response.body() ?: emptyList()
                
                RetrofitClient.instance.getCompras(userId).enqueue(object : Callback<List<com.example.gemainventory.model.CompraDto>> {
                    override fun onResponse(call2: Call<List<com.example.gemainventory.model.CompraDto>>, response2: Response<List<com.example.gemainventory.model.CompraDto>>) {
                        val purchases = response2.body() ?: emptyList()
                        
                        val allItems = mutableListOf<NotificationItem>()

                        val formatMoneda = NumberFormat.getCurrencyInstance(java.util.Locale("es", "MX"))
                        for (o in orders) {
                            allItems.add(NotificationItem(
                                id = o.id ?: "",
                                type = NotificationType.ORDER,
                                title = o.nombre ?: "Venta #${o.id?.takeLast(5) ?: "?"}",
                                description = "Total: ${formatMoneda.format(o.total ?: 0.0)}",
                                timestamp = o.fechaPedido?.take(10) ?: "N/A",
                                iconResId = R.drawable.pedi2
                            ))
                        }

                        for (c in purchases) {
                            allItems.add(NotificationItem(
                                id = c.id ?: "",
                                type = NotificationType.PURCHASE,
                                title = "Compra de Stock",
                                description = "${c.nombreProveedor ?: "Proveedor"} - ${formatMoneda.format(c.total ?: 0.0)}",
                                timestamp = c.fechaCompra?.toString()?.take(10) ?: "N/A",
                                iconResId = R.drawable.carro_azul
                            ))
                        }

                        allItems.sortByDescending { it.timestamp }
                        
                        adapter.updateList(allItems)
                    }

                    override fun onFailure(call2: Call<List<com.example.gemainventory.model.CompraDto>>, t: Throwable) {
                         Log.e("Notif", "Error fetching purchases")
                    }
                })
            }

            override fun onFailure(call: Call<List<com.example.gemainventory.model.PedidoDto>>, t: Throwable) {
                Log.e("Notif", "Error fetching orders")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
