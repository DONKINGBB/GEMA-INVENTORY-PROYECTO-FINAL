package com.example.gemainventory.ui.inventory

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gemainventory.R
import com.example.gemainventory.api.RetrofitClient
import com.example.gemainventory.model.*
import com.example.gemainventory.ui.theme.GemaTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductDetailFragment : Fragment() {

    private val productState = mutableStateOf<Product?>(null)
    private val warehousesState = mutableStateListOf<AlmacenDto>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize from arguments
        arguments?.let { args ->
            val product = Product(
                id = args.getString("productId", ""),
                name = args.getString("nombre", "Producto"),
                sku = args.getString("sku", "N/A"),
                quantity = args.getInt("cantidad", 0),
                minStock = args.getInt("stockMinimo", 5),
                salePrice = args.getDouble("precioVenta", 0.0),
                description = args.getString("descripcion"),
                category = args.getString("categoria", "General"),
                imageUrl = args.getString("imagenUrl"),
                warehouseName = args.getString("warehouseName"),
                idAlmacen = if (args.containsKey("idAlmacen")) args.getInt("idAlmacen") else null,
                updatedAt = args.getString("updatedAt")
            ).apply {
                purchasePrice = args.getDouble("precioCompra", 0.0)
            }
            productState.value = product
        }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                GemaTheme {
                    val currentProduct = productState.value
                    if (currentProduct != null) {
                        
                        LaunchedEffect(Unit) {
                            loadWarehouses()
                        }
                        
                        val warehouses = warehousesState
                        
                        // Resolve warehouse name if missing but ID exists
                        val displayProduct = if (currentProduct.warehouseName == null || currentProduct.warehouseName == "No especificado") {
                            val foundName = warehouses.find { it.idAlmacen == currentProduct.idAlmacen }?.nombre
                            if (foundName != null) currentProduct.copy(warehouseName = foundName) else currentProduct
                        } else currentProduct

                        ProductDetailView(
                            product = displayProduct,
                            onBackClick = { findNavController().popBackStack() },
                            onEditClick = { navigateToEdit(displayProduct) },
                            onDeleteClick = { showDeleteConfirmDialog(displayProduct.id) }
                        )
                    } else {
                        // Loading state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            androidx.compose.material3.CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    private fun loadWarehouses() {
        val userId = getUserId() ?: return
        RetrofitClient.instance.getAlmacenes(userId).enqueue(object : Callback<List<AlmacenDto>> {
            override fun onResponse(call: Call<List<AlmacenDto>>, response: Response<List<AlmacenDto>>) {
                if (response.isSuccessful) {
                    warehousesState.clear()
                    response.body()?.let { warehousesState.addAll(it) }
                }
            }
            override fun onFailure(call: Call<List<AlmacenDto>>, t: Throwable) {}
        })
    }

    private fun getUserId(): String? {
        val sharedPref = requireContext().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("user_id", null)
    }

    private fun navigateToEdit(product: Product) {
        val bundle = Bundle().apply {
            putString("idProducto", product.id)
            putString("nombre", product.name)
            putString("sku", product.sku)
            putInt("cantidad", product.quantity)
            putString("categoria", product.category)
            putDouble("precioCompra", product.purchasePrice)
            putDouble("precioVenta", product.salePrice)
            putString("descripcion", product.description)
            putString("imagenUrl", product.imageUrl)
            product.idAlmacen?.let { putInt("idAlmacen", it) }
        }
        findNavController().navigate(R.id.action_detail_to_edit_form, bundle)
    }

    private fun showDeleteConfirmDialog(productId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Producto")
            .setMessage("¿Estás seguro de que deseas eliminar este producto? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteProduct(productId)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteProduct(productId: String) {
        RetrofitClient.instance.eliminarProducto(productId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Producto eliminado correctamente", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, "Error al eliminar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
