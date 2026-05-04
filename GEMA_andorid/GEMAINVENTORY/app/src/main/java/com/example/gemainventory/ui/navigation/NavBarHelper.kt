package com.example.gemainventory.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.example.gemainventory.R
import com.example.gemainventory.ui.theme.GemaTheme

class NavBarHelper(private val composeView: ComposeView) {
    
    private var selectedIdState = mutableIntStateOf(R.id.navigation_dashboard)
    private val visibleItemsState = mutableStateListOf<NavItem>()
    
    var onItemClick: ((Int) -> Unit)? = null

    init {
        // Default items
        visibleItemsState.addAll(listOf(
            NavItem(R.id.navigation_dashboard, "Inicio", R.drawable.inicio),
            NavItem(R.id.navigation_inventory, "Inventario", R.drawable.inventario),
            NavItem(R.id.navigation_orders, "Ventas", R.drawable.pedidos),
            NavItem(R.id.navigation_finances, "Finanzas", R.drawable.finanzas),
            NavItem(R.id.navigation_settings, "Ajustes", R.drawable.ajustes)
        ))

        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                GemaTheme {
                    FloatingNavBar(
                        selectedId = selectedIdState.intValue,
                        visibleItems = visibleItemsState,
                        onItemClick = { id ->
                            selectedIdState.intValue = id
                            onItemClick?.invoke(id)
                        }
                    )
                }
            }
        }
    }

    fun setSelectedId(id: Int) {
        selectedIdState.intValue = id
    }

    fun setVisibleItems(ids: List<Int>) {
        val allItems = listOf(
            NavItem(R.id.navigation_dashboard, "Inicio", R.drawable.inicio),
            NavItem(R.id.navigation_inventory, "Inventario", R.drawable.inventario),
            NavItem(R.id.navigation_orders, "Ventas", R.drawable.pedidos),
            NavItem(R.id.navigation_finances, "Finanzas", R.drawable.finanzas),
            NavItem(R.id.navigation_settings, "Ajustes", R.drawable.ajustes)
        )
        
        visibleItemsState.clear()
        visibleItemsState.addAll(allItems.filter { ids.contains(it.id) })
    }
}
