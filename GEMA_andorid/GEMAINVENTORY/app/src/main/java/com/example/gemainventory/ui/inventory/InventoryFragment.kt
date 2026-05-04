package com.example.gemainventory.ui.inventory

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gemainventory.R
import com.example.gemainventory.api.RetrofitClient
import com.example.gemainventory.model.InventarioDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InventoryFragment : Fragment() {

    private var composeHelper: InventoryComposeHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).also {
            composeHelper = InventoryComposeHelper(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeHelper?.setListeners(
            onProductClick = { product ->
                val bundle = Bundle().apply {
                    putString("productId", product.id)
                    // Pass other details to avoid immediate re-fetch
                    putString("nombre", product.name)
                    putString("sku", product.sku)
                    putInt("cantidad", product.quantity)
                    putString("categoria", product.category)
                    putDouble("precioVenta", product.salePrice)
                    putString("descripcion", product.description)
                    putString("imagenUrl", product.imageUrl)
                    putInt("stockMinimo", product.minStock)
                    putDouble("precioCompra", product.purchasePrice)
                    putString("warehouseName", product.warehouseName)
                    product.idAlmacen?.let { putInt("idAlmacen", it) }
                    putString("updatedAt", product.updatedAt)
                }
                findNavController().navigate(R.id.action_inventory_to_detail, bundle)
            },
            onAddProductClick = {
                findNavController().navigate(R.id.action_inventory_to_addProduct)
            },
            onFilterClick = {
                showFilterMenu(view)
            }
        )

        fetchProducts()
    }

    private fun fetchProducts() {
        val prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val userId = prefs.getString("user_id", null) ?: return

        RetrofitClient.instance.getInventario(userId).enqueue(object : Callback<List<InventarioDto>> {
            override fun onResponse(call: Call<List<InventarioDto>>, response: Response<List<InventarioDto>>) {
                if (response.isSuccessful && response.body() != null) {
                    val remoteList = response.body()!!
                    val mappedList = remoteList.map { dto ->
                        Product(
                            id = dto.idProducto,
                            name = dto.nombreProducto,
                            sku = dto.sku ?: "S/N",
                            quantity = dto.cantidadActual,
                            minStock = dto.stockMinimo ?: 5,
                            salePrice = dto.precioVenta ?: 0.0,
                            description = dto.descripcion,
                            category = dto.categoria ?: "Sin Categoría",
                            imageUrl = dto.imagenUrl,
                            warehouseName = dto.nombreAlmacen ?: "No especificado",
                            idAlmacen = dto.idAlmacen,
                            updatedAt = dto.fechaActualizacion ?: dto.fechaCreacion ?: "S/N"
                        ).apply {
                            purchasePrice = dto.precioCompra ?: 0.0
                        }
                    }
                    composeHelper?.updateProducts(mappedList)
                } else {
                    context?.let { Toast.makeText(it, "Error al cargar inventario", Toast.LENGTH_SHORT).show() }
                }
            }

            override fun onFailure(call: Call<List<InventarioDto>>, t: Throwable) {
                context?.let { Toast.makeText(it, "Error: ${t.message}", Toast.LENGTH_SHORT).show() }
            }
        })
    }

    private fun showFilterMenu(v: View) {
        val popup = PopupMenu(context, v)
        popup.menu.add("Nombre (A-Z)")
        popup.menu.add("Nombre (Z-A)")
        popup.menu.add("Precio (Menor a Mayor)")
        popup.menu.add("Precio (Mayor a Menor)")
        popup.menu.add("Stock (Críticos primero)")

        popup.setOnMenuItemClickListener { item ->
            val helper = composeHelper ?: return@setOnMenuItemClickListener false
            when (item.title.toString()) {
                "Nombre (A-Z)" -> helper.sortProducts { p1, p2 -> p1.name.compareTo(p2.name, ignoreCase = true) }
                "Nombre (Z-A)" -> helper.sortProducts { p1, p2 -> p2.name.compareTo(p1.name, ignoreCase = true) }
                "Precio (Menor a Mayor)" -> helper.sortProducts { p1, p2 -> p1.salePrice.compareTo(p2.salePrice) }
                "Precio (Mayor a Menor)" -> helper.sortProducts { p1, p2 -> p2.salePrice.compareTo(p1.salePrice) }
                "Stock (Críticos primero)" -> helper.sortProducts { p1, p2 -> p1.quantity.compareTo(p2.quantity) }
            }
            true
        }
        popup.show()
    }
}
