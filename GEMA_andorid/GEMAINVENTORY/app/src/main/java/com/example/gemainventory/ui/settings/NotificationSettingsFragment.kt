package com.example.gemainventory.ui.settings

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
import com.example.gemainventory.model.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationSettingsFragment : Fragment() {

    private var userId: String? = null
    
    // State for Compose
    private var lowStockEnabled by mutableStateOf(true)
    private var newOrdersEnabled by mutableStateOf(true)
    private var inventoryChangesEnabled by mutableStateOf(true)
    private var isLoading by mutableStateOf(false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sharedPreferences = requireContext().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("DarkMode", false)
        userId = sharedPreferences.getString("user_id", null)

        if (userId != null) {
            loadPreferences()
        }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                NotificationSettingsScreen(
                    darkTheme = isDarkMode,
                    lowStockEnabled = lowStockEnabled,
                    onLowStockChange = { lowStockEnabled = it },
                    newOrdersEnabled = newOrdersEnabled,
                    onNewOrdersChange = { newOrdersEnabled = it },
                    inventoryChangesEnabled = inventoryChangesEnabled,
                    onInventoryChangesChange = { inventoryChangesEnabled = it },
                    isLoading = isLoading,
                    onSaveClick = { savePreferences() },
                    onBackClick = { findNavController().popBackStack() }
                )
            }
        }
    }

    private fun loadPreferences() {
        isLoading = true
        userId?.let { id ->
            RetrofitClient.instance.getUserPreferences(id).enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    isLoading = false
                    if (response.isSuccessful && response.body() != null) {
                        val u = response.body()!!
                        lowStockEnabled = u.notifyLowStock ?: true
                        newOrdersEnabled = u.notifyNewOrders ?: true
                        inventoryChangesEnabled = u.notifyInventoryChanges ?: true
                    }
                }

                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    isLoading = false
                    Toast.makeText(context, "Error al cargar preferencias", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun savePreferences() {
        isLoading = true
        val settings = Usuario().apply {
            notifyLowStock = lowStockEnabled
            notifyNewOrders = newOrdersEnabled
            notifyInventoryChanges = inventoryChangesEnabled
        }

        userId?.let { id ->
            RetrofitClient.instance.updateNotifications(id, settings).enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Preferencias guardadas", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    isLoading = false
                    Toast.makeText(context, "Fallo de red", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
