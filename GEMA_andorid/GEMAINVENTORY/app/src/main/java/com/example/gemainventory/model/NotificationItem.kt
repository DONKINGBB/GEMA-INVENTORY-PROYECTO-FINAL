package com.example.gemainventory.model

data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val description: String,
    val timestamp: String, // ISO date or formatted
    val iconResId: Int
)

enum class NotificationType {
    ORDER,
    PURCHASE,
    ALERT,
    INFO
}
