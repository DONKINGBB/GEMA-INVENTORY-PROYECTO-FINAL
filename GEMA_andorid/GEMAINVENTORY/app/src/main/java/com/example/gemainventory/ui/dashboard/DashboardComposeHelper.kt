package com.example.gemainventory.ui.dashboard

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.example.gemainventory.model.NotificationItem
import com.example.gemainventory.ui.theme.GemaTheme

class DashboardComposeHelper(private val composeView: ComposeView) {
    
    private val inventoryValue = mutableStateOf(0.0)
    private val pendingOrders = mutableStateOf(0)
    private val lowStockCount = mutableStateOf(0)
    private val monthProfit = mutableStateOf(0.0)
    private val userName = mutableStateOf("")
    
    private val stockAlerts = mutableStateListOf<StockAlert>()
    private val recentActivity = mutableStateListOf<NotificationItem>()
    
    var onNotificationsClick: (() -> Unit)? = null
    var onInventoryClick: (() -> Unit)? = null
    var onActivityItemClick: ((NotificationItem) -> Unit)? = null

    init {
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                GemaTheme {
                    DashboardView(
                        inventoryValue = inventoryValue.value,
                        pendingOrders = pendingOrders.value,
                        lowStockCount = lowStockCount.value,
                        monthProfit = monthProfit.value,
                        stockAlerts = stockAlerts,
                        recentActivity = recentActivity,
                        onNotificationsClick = { onNotificationsClick?.invoke() },
                        onInventoryClick = { onInventoryClick?.invoke() },
                        onActivityItemClick = { item -> onActivityItemClick?.invoke(item) },
                        userName = userName.value
                    )
                }
            }
        }
    }

    fun updateSummary(value: Double, pending: Int, lowStock: Int, profit: Double) {
        inventoryValue.value = value
        pendingOrders.value = pending
        lowStockCount.value = lowStock
        monthProfit.value = profit
    }

    fun updateAlerts(alerts: List<StockAlert>) {
        stockAlerts.clear()
        stockAlerts.addAll(alerts)
    }

    fun updateActivity(activity: List<NotificationItem>) {
        recentActivity.clear()
        recentActivity.addAll(activity)
    }

    fun updateUserName(name: String) {
        userName.value = name
    }
}
