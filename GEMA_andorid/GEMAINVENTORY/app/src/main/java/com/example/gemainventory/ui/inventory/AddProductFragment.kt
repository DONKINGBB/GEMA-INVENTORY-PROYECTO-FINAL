package com.example.gemainventory.ui.inventory

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gemainventory.R
import com.example.gemainventory.api.RetrofitClient
import com.example.gemainventory.data.local.AppDatabase
import com.example.gemainventory.data.local.entity.ProductoEntity
import com.example.gemainventory.data.local.entity.SyncState
import com.example.gemainventory.model.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException

class AddProductFragment : Fragment() {

    private var idProductoEditar: String? = null
    private var scanningField = 0 // 1 = Name, 2 = SKU

    // State for Compose
    private var categoriesState = mutableStateListOf<CategoriaDto>()
    private var warehousesState = mutableStateListOf<AlmacenDto>()
    private var imageUriState = mutableStateOf<Uri?>(null)
    private var currentImageUrlState = mutableStateOf<String?>(null)
    
    // Initial data for editing
    private var initialData = mutableStateOf<ProductoDto?>(null)

    private val getContentLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUriState.value = uri
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val categories = categoriesState
                val warehouses = warehousesState
                val imageUri by imageUriState
                val currentImageUrl by currentImageUrlState
                val initialProduct by initialData

                AddProductScreen(
                    isEditing = idProductoEditar != null,
                    initialName = initialProduct?.nombre ?: getArguments()?.getString("nombre") ?: "",
                    initialSku = initialProduct?.sku ?: getArguments()?.getString("sku") ?: "",
                    initialQuantity = (initialProduct?.cantidad ?: getArguments()?.getInt("cantidad") ?: 0).toString(),
                    initialCategory = initialProduct?.categoria ?: getArguments()?.getString("categoria") ?: "",
                    initialWarehouseId = (initialProduct?.idAlmacen ?: arguments?.let { if (it.containsKey("idAlmacen")) it.getInt("idAlmacen") else null })?.let { if (it == 0) null else it },
                    initialPriceBuy = (initialProduct?.precioCompra ?: getArguments()?.getDouble("precioCompra") ?: 0.0).toString(),
                    initialPriceSell = (initialProduct?.precioVenta ?: getArguments()?.getDouble("precioVenta") ?: 0.0).toString(),
                    initialDesc = initialProduct?.descripcion ?: getArguments()?.getString("descripcion") ?: "",
                    initialImageUrl = RetrofitClient.getFullImageUrl(currentImageUrl),
                    categories = categories,
                    warehouses = warehouses,
                    imageUri = imageUri,
                    onBack = { findNavController().popBackStack() },
                    onPickImage = { getContentLauncher.launch("image/*") },
                    onRemoveImage = {
                        imageUriState.value = null
                        currentImageUrlState.value = null
                    },
                    onScan = { field ->
                        scanningField = field
                        findNavController().navigate(R.id.navigation_scanner)
                    },
                    onAddCategory = {
                        val action = if (idProductoEditar == null) R.id.action_addProduct_to_addCategory else R.id.action_product_form_to_addCategory
                        findNavController().navigate(action)
                    },
                    onAddWarehouse = {
                        val action = if (idProductoEditar == null) R.id.action_addProduct_to_addWarehouse else R.id.action_product_form_to_addWarehouse
                        findNavController().navigate(action)
                    },
                    onSave = { dto ->
                        if (imageUriState.value != null) {
                            subirImagenYGuardar(dto)
                        } else {
                            if (idProductoEditar == null) {
                                crearProducto(dto)
                            } else {
                                actualizarProducto(dto)
                            }
                        }
                    },
                    onDelete = { mostrarConfirmacionBorrar() }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idProductoEditar = arguments?.getString("idProducto")
        
        setupFragmentListeners()
        fetchCategories()
        fetchWarehouses()

        if (idProductoEditar != null) {
            cargarDatosParaEditar()
        }
    }

    private fun setupFragmentListeners() {
        parentFragmentManager.setFragmentResultListener("request_scan", viewLifecycleOwner) { _, bundle ->
            val result = bundle.getString("scan_result")
            if (result != null) {
                // Since Compose uses internal state, we need a way to pass this back.
                // For now, I'll just update initialData or similar.
                // A better way would be using a ViewModel.
                val current = initialData.value ?: ProductoDto("", "", 0, "", 0.0, 0.0, "", 5, "", null)
                if (scanningField == 1) {
                    initialData.value = current.copy(nombre = result)
                } else if (scanningField == 2) {
                    initialData.value = current.copy(sku = result)
                }
            }
        }
    }

    private fun fetchCategories() {
        val userId = getUserId() ?: return
        RetrofitClient.instance.getCategorias(userId).enqueue(object : Callback<List<CategoriaDto>> {
            override fun onResponse(call: Call<List<CategoriaDto>>, response: Response<List<CategoriaDto>>) {
                if (response.isSuccessful && response.body() != null) {
                    categoriesState.clear()
                    categoriesState.addAll(response.body()!!)
                }
            }
            override fun onFailure(call: Call<List<CategoriaDto>>, t: Throwable) {
                Toast.makeText(context, "Error al cargar categorías", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchWarehouses() {
        val userId = getUserId() ?: return
        RetrofitClient.instance.getAlmacenes(userId).enqueue(object : Callback<List<AlmacenDto>> {
            override fun onResponse(call: Call<List<AlmacenDto>>, response: Response<List<AlmacenDto>>) {
                if (response.isSuccessful && response.body() != null) {
                    warehousesState.clear()
                    warehousesState.addAll(response.body()!!)
                }
            }
            override fun onFailure(call: Call<List<AlmacenDto>>, t: Throwable) {
                Toast.makeText(context, "Error al cargar almacenes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarDatosParaEditar() {
        val id = idProductoEditar ?: return
        RetrofitClient.instance.getProducto(id).enqueue(object : Callback<ProductoDto> {
            override fun onResponse(call: Call<ProductoDto>, response: Response<ProductoDto>) {
                if (response.isSuccessful && response.body() != null) {
                    val p = response.body()!!
                    initialData.value = p
                    currentImageUrlState.value = p.imagenUrl
                }
            }
            override fun onFailure(call: Call<ProductoDto>, t: Throwable) {
                Toast.makeText(context, "Error al cargar producto", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun subirImagenYGuardar(dto: ProductoDto) {
        val uri = imageUriState.value ?: return
        Toast.makeText(context, "Subiendo imagen...", Toast.LENGTH_SHORT).show()

        try {
            val bytes = getBytesFromUri(uri)
            if (bytes == null) {
                Toast.makeText(context, "Error al leer imagen", Toast.LENGTH_SHORT).show()
                return
            }
            val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), bytes)
            val body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)

            RetrofitClient.instance.uploadImage(body).enqueue(object : Callback<UploadResponse> {
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if (response.isSuccessful && response.body()?.url != null) {
                        val relativeUrl = response.body()!!.url
                        val updatedDto = dto.copy(imagenUrl = relativeUrl, usuarioId = getUserId() ?: "")
                        if (idProductoEditar == null) crearProducto(updatedDto) else actualizarProducto(updatedDto)
                    } else {
                        Toast.makeText(context, "Error subiendo imagen", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Toast.makeText(context, "Error de red subiendo imagen", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Toast.makeText(context, "Error al procesar imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun crearProducto(dto: ProductoDto) {
        val finalDto = dto.copy(usuarioId = getUserId() ?: "")
        val userId = getUserId() ?: ""
        val creationDto = finalDto.toCreacionDto(userId)
        
        RetrofitClient.instance.crearProducto(creationDto).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Producto creado", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                guardarProductoOffline(finalDto)
            }
        })
    }

    private fun actualizarProducto(dto: ProductoDto) {
        val id = idProductoEditar ?: return
        // Preserve existing image URL if no new image was selected
        val existingImageUrl = if (dto.imagenUrl.isNullOrEmpty()) currentImageUrlState.value else dto.imagenUrl
        val finalDto = dto.copy(usuarioId = getUserId() ?: "", imagenUrl = existingImageUrl)
        RetrofitClient.instance.actualizarProducto(id, finalDto).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Producto actualizado", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarConfirmacionBorrar() {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Producto")
            .setMessage("¿Estás seguro?")
            .setPositiveButton("Eliminar") { _, _ -> eliminarProductoApi() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarProductoApi() {
        val id = idProductoEditar ?: return
        RetrofitClient.instance.eliminarProducto(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun guardarProductoOffline(dto: ProductoDto) {
        val tempId = "TEMP_${System.currentTimeMillis()}"
        val entity = ProductoEntity(
            idProducto = tempId,
            nombre = dto.nombre,
            descripcion = dto.descripcion,
            sku = dto.sku,
            cantidad = dto.cantidad,
            precioCompra = dto.precioCompra,
            precioVenta = dto.precioVenta,
            stockMinimo = 5,
            idCategoria = null,
            idAlmacen = dto.idAlmacen,
            syncState = SyncState.PENDING_CREATE,
            updatedAt = System.currentTimeMillis()
        )
        Thread {
            AppDatabase.getDatabase(requireContext()).productoDao().insertProducto(entity)
            requireActivity().runOnUiThread {
                Toast.makeText(context, "Guardado localmente (Offline)", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
        }.start()
    }

    private fun getUserId(): String? {
        val prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        return prefs.getString("user_id", null)
    }

    private fun getBytesFromUri(uri: Uri): ByteArray? {
        return try {
            val iStream = requireContext().contentResolver.openInputStream(uri) ?: return null
            val byteBuffer = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var len: Int
            while (iStream.read(buffer).also { len = it } != -1) {
                byteBuffer.write(buffer, 0, len)
            }
            byteBuffer.toByteArray()
        } catch (e: IOException) {
            null
        }
    }
}
