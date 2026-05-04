package com.example.gemainventory.ui.notifications

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.example.gemainventory.model.NotificationItem
import com.example.gemainventory.ui.theme.GemaTheme

class NotificationsComposeHelper(private val composeView: ComposeView) {
    
    private val notificationList = mutableStateListOf<NotificationItem>()
    
    var onBackClick: (() -> Unit)? = null
    var onItemClick: ((NotificationItem) -> Unit)? = null

    init {
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                GemaTheme {
                    NotificationsView(
                        notifications = notificationList,
                        onBackClick = { onBackClick?.invoke() },
                        onItemClick = { item -> onItemClick?.invoke(item) }
                    )
                }
            }
        }
    }

    fun updateNotifications(newList: List<NotificationItem>) {
        notificationList.clear()
        notificationList.addAll(newList)
    }
}
