package com.example.gemainventory.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gemainventory.R

class SettingsManageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val prefs = requireContext().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("DarkMode", false)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SettingsManageScreen(
                    darkTheme = isDarkMode,
                    onBackClick = {
                        findNavController().popBackStack()
                    },
                    onManageProducts = {
                        findNavController().navigate(R.id.navigation_inventory)
                    },
                    onManageCategories = {
                        findNavController().navigate(R.id.action_manage_to_categories)
                    },
                    onManageClients = {
                        findNavController().navigate(R.id.action_manage_to_clients)
                    },
                    onManageWarehouses = {
                        findNavController().navigate(R.id.action_manage_to_warehouses)
                    },
                    onManageSuppliers = {
                        findNavController().navigate(R.id.action_manage_to_suppliers)
                    }
                )
            }
        }
    }
}
