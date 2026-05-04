package com.example.gemainventory.ui.inventory

data class Product(
    val id: String,
    val name: String,
    val sku: String,
    val quantity: Int,
    val minStock: Int,
    val salePrice: Double,
    val description: String?,
    val category: String,
    val imageUrl: String?,
    var purchasePrice: Double = 0.0,
    var dateAdded: Long = System.currentTimeMillis(),
    val warehouseName: String? = null,
    val idAlmacen: Int? = null,
    val updatedAt: String? = null
) {
    enum class StockStatus {
        IN_STOCK,
        LOW_STOCK,
        OUT_OF_STOCK
    }

    val status: StockStatus
        get() = when {
            quantity <= 0 -> StockStatus.OUT_OF_STOCK
            quantity <= minStock -> StockStatus.LOW_STOCK
            else -> StockStatus.IN_STOCK
        }

    fun getFullImageUrl(): String? {
        if (imageUrl == null || imageUrl.isEmpty()) return null
        return if (imageUrl.startsWith("http")) imageUrl 
        else "https://gema-inventory-backend.onrender.com/uploads/$imageUrl"
    }
}

fun getFullImageUrl(imageUrl: String?): String? {
    if (imageUrl == null) return null
    return if (imageUrl.startsWith("http")) imageUrl 
    else "https://gema-inventory-backend.onrender.com/uploads/$imageUrl"
}
