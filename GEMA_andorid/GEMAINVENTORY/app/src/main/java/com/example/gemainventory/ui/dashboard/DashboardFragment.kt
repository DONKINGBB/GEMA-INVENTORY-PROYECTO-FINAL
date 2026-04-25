package com.example.gemainventory.ui.dashboard

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gemainventory.R
import com.example.gemainventory.api.RetrofitClient
import com.example.gemainventory.databinding.FragmentDashboardBinding
import com.example.gemainventory.model.DashboardSummary
import com.example.gemainventory.model.InventarioDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchDashboardSummary()
        fetchStockAlerts()
        fetchRecentActivity()

        binding.btnIrInventario.setOnClickListener {
                findNavController().navigate(R.id.navigation_inventory)
        }
        

        binding.iconNotifications.setOnClickListener {
            findNavController().navigate(R.id.navigation_notifications)
        }
        if (shouldShowOnboarding()) {
            showOnboarding()
        }
    }

    private fun shouldShowOnboarding(): Boolean {
        val prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        return !prefs.getBoolean("has_seen_onboarding", false)
    }

    private fun showOnboarding() {
        val dialog = com.example.gemainventory.ui.onboarding.OnboardingDialog()
        dialog.onGoToSettings = {
            // Navigate to Settings -> Manage Categories (or just Settings)
            // Assuming R.id.navigation_settings exists and leads to SettingsFragment
            // If you want to deep link to Categories, we might need a specific action or bundle
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

        val call = RetrofitClient.instance.getDashboardSummary(userId)

        call.enqueue(object : Callback<DashboardSummary> {
            override fun onResponse(call: Call<DashboardSummary>, response: Response<DashboardSummary>) {
                if (response.isSuccessful) {
                    val summary = response.body()
                    if (summary != null && _binding != null) {
                        val formatMoneda = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
                        
                        binding.tvInventoryValue.text = formatMoneda.format(summary.valorInventario)
                        binding.tvPendingOrders.text = summary.pedidosPendientes.toString()
                        
                        binding.tvPendingInvoices.text = summary.productosBajoStock.toString()
                        
                        binding.tvMonthProfit.text = formatMoneda.format(summary.beneficioMes)
                        
                        // Lógica de color según el beneficio
                        if (summary.beneficioMes < 0) {
                            val colorRed = Color.parseColor("#EF4444")
                            binding.tvMonthProfit.setTextColor(colorRed)
                            binding.ivProfitIcon.setColorFilter(colorRed)
                        } else {
                            val colorGreen = Color.parseColor("#10B981")
                            binding.tvMonthProfit.setTextColor(colorGreen)
                            binding.ivProfitIcon.setColorFilter(colorGreen)
                        }
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
            override fun onResponse(
                call: Call<List<InventarioDto>>,
                response: Response<List<InventarioDto>>
            ) {
                if (response.isSuccessful && _binding != null) {
                    val productosInventario = response.body() ?: emptyList()

                    binding.containerAlertas.removeAllViews()

                    var hayAlertas = false

                    for (itemInventario in productosInventario) {
                        val stockMin = itemInventario.stockMinimo ?: 5
                        val cantidadActual = itemInventario.cantidadActual ?: 0

                        if (cantidadActual <= stockMin) {
                            hayAlertas = true

                            // Optimization: Limit to 5 alerts to prevent UI freeze
                            if (binding.containerAlertas.childCount < 5) {
                                val tvProducto = TextView(requireContext())
                                tvProducto.textSize = 14f
                                tvProducto.setPadding(0, 4, 0, 4)

                                if (cantidadActual == 0) {
                                    tvProducto.text = "• ${itemInventario.nombreProducto} (0) ¡AGOTADO!"
                                    tvProducto.setTextColor(Color.parseColor("#FF4444"))
                                    tvProducto.setTypeface(null, Typeface.BOLD)
                                } else {
                                    tvProducto.text = "• ${itemInventario.nombreProducto} ($cantidadActual) - Bajo Stock"
                                    tvProducto.setTextColor(Color.parseColor("#FFA000"))
                                }

                                binding.containerAlertas.addView(tvProducto)
                            } else if (binding.containerAlertas.childCount == 5) {
                                val tvMore = TextView(requireContext())
                                tvMore.text = "... y otros items con bajo stock"
                                tvMore.textSize = 12f
                                tvMore.setTypeface(null, Typeface.ITALIC)
                                binding.containerAlertas.addView(tvMore)
                            }
                        }
                    }

                    if (!hayAlertas) {
                        val tvOk = TextView(requireContext())
                        tvOk.text = "Todo en orden. Niveles de stock saludables."
                        tvOk.setTextColor(Color.GREEN)
                        binding.containerAlertas.addView(tvOk)
                    }
                }
            }

            override fun onFailure(call: Call<List<InventarioDto>>, t: Throwable) {
                Log.e("API_ALERTS", "Error cargando alertas de inventario: ${t.message}")
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
                        
                        displayRecentActivity(orders, purchases)
                    }

                    override fun onFailure(call2: Call<List<com.example.gemainventory.model.CompraDto>>, t: Throwable) {
                         displayRecentActivity(orders, emptyList())
                    }
                })
            }

            override fun onFailure(call: Call<List<com.example.gemainventory.model.PedidoDto>>, t: Throwable) {

            }
        })
    }

    private fun displayRecentActivity(orders: List<com.example.gemainventory.model.PedidoDto>, purchases: List<com.example.gemainventory.model.CompraDto>) {
        if (_binding == null) return

        val allItems = mutableListOf<com.example.gemainventory.model.NotificationItem>()

        val formatMoneda = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        for (o in orders) {
            allItems.add(com.example.gemainventory.model.NotificationItem(
                id = o.id ?: "",
                type = com.example.gemainventory.model.NotificationType.ORDER,
                title = o.nombre ?: "Venta #${o.id?.takeLast(5) ?: "?"}",
                description = "Pedido: ${formatMoneda.format(o.total ?: 0.0)}",
                timestamp = o.fechaPedido?.take(10) ?: "",
                iconResId = R.drawable.pedi2
            ))
        }

        for (c in purchases) {
             allItems.add(com.example.gemainventory.model.NotificationItem(
                id = c.id ?: "",
                type = com.example.gemainventory.model.NotificationType.PURCHASE,
                title = "Compra de Stock",
                description = "${c.nombreProveedor ?: "Proveedor"} - ${formatMoneda.format(c.total ?: 0.0)}",
                timestamp = c.fechaCompra?.toString()?.take(10) ?: "",
                iconResId = R.drawable.carro_azul
            ))
        }

        allItems.sortByDescending { it.timestamp }
        val recentItems = allItems.take(3)

        binding.containerRecentActivity.removeAllViews()

        if (recentItems.isEmpty()) {
            val tvEmpty = TextView(requireContext())
            tvEmpty.text = "No hay actividad reciente."
            tvEmpty.setPadding(16, 16, 16, 16)
            binding.containerRecentActivity.addView(tvEmpty)
            return
        }

        for (item in recentItems) {
            val itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_notification, binding.containerRecentActivity, false)
            
            val icon = itemView.findViewById<android.widget.ImageView>(R.id.iv_notification_icon)
            val iconBg = itemView.findViewById<android.view.View>(R.id.iv_notification_bg)
            val title = itemView.findViewById<android.widget.TextView>(R.id.tv_notification_title)
            val desc = itemView.findViewById<android.widget.TextView>(R.id.tv_notification_desc)
            val time = itemView.findViewById<android.widget.TextView>(R.id.tv_notification_time)

            icon.setImageResource(item.iconResId)
            title.text = item.title
            desc.text = item.description
            time.text = item.timestamp

            // Colores dinámicos según el tipo
            val (colorHex, bgHex) = if (item.type == com.example.gemainventory.model.NotificationType.ORDER) {
                "#22C55E" to "#1A22C55E" // Verde esmeralda (10% alfa para el fondo)
            } else {
                "#3B82F6" to "#1A3B82F6" // Azul brillante (10% alfa para el fondo)
            }

            icon.setColorFilter(android.graphics.Color.parseColor(colorHex))
            iconBg.background.setTint(android.graphics.Color.parseColor(bgHex))

            binding.containerRecentActivity.addView(itemView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
