package com.example.gemainventory.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gemainventory.R
import com.example.gemainventory.api.RetrofitClient
import com.example.gemainventory.model.DashboardSummary
import com.example.gemainventory.model.InventarioDto
import com.example.gemainventory.model.NotificationItem
import com.example.gemainventory.model.NotificationType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardFragment : Fragment() {

    private var composeHelper: DashboardComposeHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val composeView = ComposeView(requireContext())
        composeHelper = DashboardComposeHelper(composeView)
        return composeView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("user_nombre", "Usuario") ?: "Usuario"
        val userRol = prefs.getInt("user_rol", 1)
        
        composeHelper?.apply {
            updateUserName(userName)
            setUserRol(userRol)
        }

        setupListeners()
        fetchDashboardSummary()
        fetchStockAlerts()
        fetchRecentActivity()

        if (shouldShowOnboarding()) {
            showOnboarding()
        }
    }

    private fun setupListeners() {
        composeHelper?.apply {
            onNotificationsClick = {
                findNavController().navigate(R.id.navigation_notifications)
            }
            onInventoryClick = {
                findNavController().navigate(R.id.navigation_inventory)
            }
            onActivityItemClick = { item ->
                // Opcional: Navegar al detalle del pedido/compra si existe
                Log.d("Dashboard", "Item clicked: ${item.title}")
            }
        }
    }

    private fun shouldShowOnboarding(): Boolean {
        val prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        return !prefs.getBoolean("has_seen_onboarding", false)
    }

    private fun showOnboarding() {
        val dialog = com.example.gemainventory.ui.onboarding.OnboardingDialog()
        dialog.onGoToSettings = {
            try {
                findNavController().navigate(R.id.navigation_settings) 
            } catch (e: Exception) {
                Log.e("Nav", "Error navigating to settings", e)
            }
        }
        dialog.show(parentFragmentManager, com.example.gemainventory.ui.onboarding.OnboardingDialog.TAG)
    }

    private fun fetchDashboardSummary() {
        val prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val userId = prefs.getString("user_id", null) ?: return

        RetrofitClient.instance.getDashboardSummary(userId).enqueue(object : Callback<DashboardSummary> {
            override fun onResponse(call: Call<DashboardSummary>, response: Response<DashboardSummary>) {
                if (response.isSuccessful) {
                    response.body()?.let { summary ->
                        composeHelper?.updateSummary(
                            summary.valorInventario,
                            summary.pedidosPendientes,
                            summary.productosBajoStock,
                            summary.beneficioMes
                        )
                    }
                }
            }
            override fun onFailure(call: Call<DashboardSummary>, t: Throwable) {
                Log.e("API_FAILURE", "Fallo dashboard: ${t.message}")
            }
        })
    }

    private fun fetchStockAlerts() {
        val prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val userId = prefs.getString("user_id", null) ?: return

        RetrofitClient.instance.getInventario(userId).enqueue(object : Callback<List<InventarioDto>> {
            override fun onResponse(call: Call<List<InventarioDto>>, response: Response<List<InventarioDto>>) {
                if (response.isSuccessful) {
                    val alerts = mutableListOf<StockAlert>()
                    response.body()?.forEach { item ->
                        val stockMin = item.stockMinimo ?: 5
                        val cantidadActual = item.cantidadActual ?: 0
                        if (cantidadActual <= stockMin) {
                            alerts.add(StockAlert(
                                productName = item.nombreProducto ?: "Producto",
                                quantity = cantidadActual,
                                isOutOfStock = cantidadActual == 0
                            ))
                        }
                    }
                    composeHelper?.updateAlerts(alerts)
                }
            }
            override fun onFailure(call: Call<List<InventarioDto>>, t: Throwable) {
                Log.e("API_ALERTS", "Error cargando alertas: ${t.message}")
            }
        })
    }

    private fun fetchRecentActivity() {
        val prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val userId = prefs.getString("user_id", null) ?: return

        RetrofitClient.instance.getPedidos(userId).enqueue(object : Callback<List<com.example.gemainventory.model.PedidoDto>> {
            override fun onResponse(call: Call<List<com.example.gemainventory.model.PedidoDto>>, response: Response<List<com.example.gemainventory.model.PedidoDto>>) {
                val orders = response.body() ?: emptyList()
                RetrofitClient.instance.getCompras(userId).enqueue(object : Callback<List<com.example.gemainventory.model.CompraDto>> {
                    override fun onResponse(call2: Call<List<com.example.gemainventory.model.CompraDto>>, response2: Response<List<com.example.gemainventory.model.CompraDto>>) {
                        val purchases = response2.body() ?: emptyList()
                        processActivity(orders, purchases)
                    }
                    override fun onFailure(call2: Call<List<com.example.gemainventory.model.CompraDto>>, t: Throwable) {
                        processActivity(orders, emptyList())
                    }
                })
            }
            override fun onFailure(call: Call<List<com.example.gemainventory.model.PedidoDto>>, t: Throwable) {}
        })
    }

    private fun processActivity(
        orders: List<com.example.gemainventory.model.PedidoDto>, 
        purchases: List<com.example.gemainventory.model.CompraDto>
    ) {
        val allItems = mutableListOf<NotificationItem>()
        val format = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es", "MX"))

        orders.forEach { o ->
            allItems.add(NotificationItem(
                id = o.id ?: "",
                type = NotificationType.ORDER,
                title = o.nombre ?: "Venta #${o.id?.takeLast(5) ?: "?"}",
                description = "Pedido: ${format.format(o.total ?: 0.0)}",
                timestamp = o.fechaPedido?.take(10) ?: "",
                iconResId = R.drawable.pedi2
            ))
        }

        purchases.forEach { c ->
            allItems.add(NotificationItem(
                id = c.id ?: "",
                type = NotificationType.PURCHASE,
                title = "Compra de Stock",
                description = "${c.nombreProveedor ?: "Proveedor"} - ${format.format(c.total ?: 0.0)}",
                timestamp = c.fechaCompra?.toString()?.take(10) ?: "",
                iconResId = R.drawable.carro_azul
            ))
        }

        allItems.sortByDescending { it.timestamp }
        composeHelper?.updateActivity(allItems.take(5))
    }
}
