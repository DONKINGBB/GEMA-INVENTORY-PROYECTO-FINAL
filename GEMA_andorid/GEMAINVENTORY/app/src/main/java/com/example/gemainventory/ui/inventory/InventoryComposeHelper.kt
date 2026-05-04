package com.example.gemainventory.ui.inventory

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.example.gemainventory.ui.theme.GemaTheme

class InventoryComposeHelper(private val composeView: ComposeView) {

    private val products = mutableStateListOf<Product>()
    private val isSearchActive = mutableStateOf(false)
    private val searchQuery = mutableStateOf("")

    private var onProductClick: ((Product) -> Unit)? = null
    private var onAddProductClick: (() -> Unit)? = null
    private var onFilterClick: (() -> Unit)? = null

    fun setListeners(
        onProductClick: (Product) -> Unit,
        onAddProductClick: () -> Unit,
        onFilterClick: () -> Unit
    ) {
        this.onProductClick = onProductClick
        this.onAddProductClick = onAddProductClick
        this.onFilterClick = onFilterClick
    }

    init {
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                GemaTheme {
                    InventoryView(
                        products = if (searchQuery.value.isEmpty()) products else products.filter {
                            it.name.contains(searchQuery.value, ignoreCase = true) ||
                            it.sku.contains(searchQuery.value, ignoreCase = true)
                        },
                        isSearchActive = isSearchActive.value,
                        searchQuery = searchQuery.value,
                        onSearchQueryChange = { searchQuery.value = it },
                        onSearchToggle = { isSearchActive.value = it },
                        onFilterClick = { onFilterClick?.invoke() },
                        onProductClick = { onProductClick?.invoke(it) },
                        onAddProductClick = { onAddProductClick?.invoke() }
                    )
                }
            }
        }
    }

    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
    }

    fun sortProducts(comparator: Comparator<Product>) {
        val sortedList = products.sortedWith(comparator)
        products.clear()
        products.addAll(sortedList)
    }
}
