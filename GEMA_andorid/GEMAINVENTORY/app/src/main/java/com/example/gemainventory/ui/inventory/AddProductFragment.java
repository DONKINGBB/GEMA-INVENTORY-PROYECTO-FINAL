package com.example.gemainventory.ui.inventory;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView; // Para cambiar el título
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.gemainventory.R;
import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.model.CategoriaDto;
import com.example.gemainventory.model.CategoriaDto;
import com.example.gemainventory.model.ProductoDto;
import com.example.gemainventory.model.AlmacenDto;
import com.example.gemainventory.model.CategoriaDto;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import com.example.gemainventory.model.UploadResponse;
import com.bumptech.glide.Glide;
import android.widget.ImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductFragment extends Fragment {

    private TextInputEditText etName, etSku, etQuantity, etPriceBuy, etPriceSell, etDesc;
    private AutoCompleteTextView etCategory, etWarehouse;
    private Button btnAction, btnDelete;
    private String idProductoEditar = null;
    private int scanningField = 0; // 1 = Name, 2 = SKU

    // Multimedia
    private ImageView ivPreview;
    private View layoutPlaceholder;
    private View btnRemoveImage;
    private Uri imageUri = null;
    private String currentImageUrl = null;
    private ActivityResultLauncher<String> mGetContent;

    private List<CategoriaDto> listaCategorias = new ArrayList<>();
    private List<AlmacenDto> listaAlmacenes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_form_full, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etName = view.findViewById(R.id.et_product_name);
        etSku = view.findViewById(R.id.et_product_sku);
        etQuantity = view.findViewById(R.id.et_product_quantity);
        etCategory = view.findViewById(R.id.et_product_category);
        etWarehouse = view.findViewById(R.id.et_product_warehouse);
        etPriceBuy = view.findViewById(R.id.et_price_buy);
        etPriceSell = view.findViewById(R.id.et_price_sell);
        etDesc = view.findViewById(R.id.et_product_desc);
        btnAction = view.findViewById(R.id.btn_create_product);
        btnDelete = view.findViewById(R.id.btn_delete_product);

        // Multimedia UI
        ivPreview = view.findViewById(R.id.iv_product_image_preview);
        layoutPlaceholder = view.findViewById(R.id.layout_image_placeholder);
        btnRemoveImage = view.findViewById(R.id.btn_remove_image);
        View cardImage = view.findViewById(R.id.card_product_image);



        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        Glide.with(requireContext()).load(uri).centerCrop().into(ivPreview);
                        ivPreview.setAlpha(1.0f);
                        layoutPlaceholder.setVisibility(View.GONE);
                        btnRemoveImage.setVisibility(View.VISIBLE);
                    }
                });

        cardImage.setOnClickListener(v -> mGetContent.launch("image/*"));
        btnRemoveImage.setOnClickListener(v -> {
            imageUri = null;
            currentImageUrl = null;
            ivPreview.setImageDrawable(null);  // placeholder default
            ivPreview.setAlpha(0.3f);
            layoutPlaceholder.setVisibility(View.VISIBLE);
            btnRemoveImage.setVisibility(View.GONE);
        });

        com.google.android.material.textfield.TextInputLayout layoutName = view.findViewById(R.id.layout_product_name);
        com.google.android.material.textfield.TextInputLayout layoutSku = view.findViewById(R.id.layout_product_sku);

        layoutName.setEndIconOnClickListener(v -> {
            scanningField = 1;
            NavHostFragment.findNavController(this).navigate(R.id.navigation_scanner);
        });

        layoutSku.setEndIconOnClickListener(v -> {
            scanningField = 2;
            NavHostFragment.findNavController(this).navigate(R.id.navigation_scanner);
        });

        getParentFragmentManager().setFragmentResultListener("request_scan", getViewLifecycleOwner(),
                (requestKey, bundle) -> {
                    String result = bundle.getString("scan_result");
                    if (result != null) {
                        if (scanningField == 1)
                            etName.setText(result);
                        else if (scanningField == 2)
                            etSku.setText(result);
                    }
                });

        View btnAddCategoryInline = view.findViewById(R.id.btn_add_category_inline);
        if (btnAddCategoryInline != null) {
            btnAddCategoryInline.setOnClickListener(v -> {
                if (idProductoEditar == null) {
                    NavHostFragment.findNavController(this).navigate(R.id.action_addProduct_to_addCategory);
                } else {
                    NavHostFragment.findNavController(this).navigate(R.id.action_product_form_to_addCategory);
                }
            });
        }

        View btnAddWarehouseInline = view.findViewById(R.id.btn_add_warehouse_inline);
        if (btnAddWarehouseInline != null) {
            btnAddWarehouseInline.setOnClickListener(v -> {
                if (idProductoEditar == null) {
                    NavHostFragment.findNavController(this).navigate(R.id.action_addProduct_to_addWarehouse);
                } else {
                    NavHostFragment.findNavController(this).navigate(R.id.action_product_form_to_addWarehouse);
                }
            });
        }

        setupCategoryDropdown();
        setupWarehouseDropdown();

        if (getArguments() != null) {
            idProductoEditar = getArguments().getString("idProducto");

            if (idProductoEditar != null) {
                btnAction.setText("Guardar");

                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(v -> mostrarConfirmacionBorrar());

                cargarDatosParaEditar();
            }
        }

        btnAction.setOnClickListener(v -> {
            if (!validarCampos())
                return;

            if (imageUri != null) {
                subirImagenYGuardar();
            } else {
                if (idProductoEditar == null) {
                    crearProducto();
                } else {
                    actualizarProducto();
                }
            }
        });

        View btnBack = view.findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        }
    }

    private void guardarProductoOffline() {
        ProductoDto dto = construirDto();
        String tempId = "TEMP_" + System.currentTimeMillis();

        com.example.gemainventory.data.local.entity.ProductoEntity entity = new com.example.gemainventory.data.local.entity.ProductoEntity(
                tempId,
                dto.getNombre(),
                dto.getDescripcion(),
                dto.getSku(),
                dto.getCantidad(),
                dto.getPrecioCompra(),
                dto.getPrecioVenta(),
                5, // Stock Mínimo predeterminado (5)
                null, // idCategoria (int) - Simplificando para MVP offline
                null, // idAlmacen (int)
                com.example.gemainventory.data.local.entity.SyncState.PENDING_CREATE,
                System.currentTimeMillis());

        new Thread(() -> {
            com.example.gemainventory.data.local.AppDatabase.Companion.getDatabase(requireContext())
                    .productoDao().insertProducto(entity);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Producto guardado localmente (Offline)", Toast.LENGTH_LONG).show();
                // Tío, aquí disparamos el trabajador de sincronización
                NavHostFragment.findNavController(this).popBackStack();
            });
        }).start();
    }

    private void cargarDatosParaEditar() {
        btnAction.setText("Guardar");
        etQuantity.setEnabled(true); // Ahora se permite editar para sincronizar con finanzas

        // 1. Cargar datos básicos del Bundle si existen (Fallback)
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey("nombre"))
                etName.setText(args.getString("nombre"));
            if (args.containsKey("sku"))
                etSku.setText(args.getString("sku"));
            if (args.containsKey("categoria"))
                etCategory.setText(args.getString("categoria"), false);
            if (args.containsKey("precioCompra"))
                etPriceBuy.setText(String.valueOf(args.getDouble("precioCompra")));
            if (args.containsKey("precioVenta"))
                etPriceSell.setText(String.valueOf(args.getDouble("precioVenta")));
            if (args.containsKey("descripcion"))
                etDesc.setText(args.getString("descripcion"));
            if (args.containsKey("imagenUrl") && args.getString("imagenUrl") != null) {
                currentImageUrl = args.getString("imagenUrl");
                Glide.with(requireContext())
                    .load(RetrofitClient.getFullImageUrl(currentImageUrl))
                    .into(ivPreview);
                ivPreview.setAlpha(1.0f);
                layoutPlaceholder.setVisibility(View.GONE);
                btnRemoveImage.setVisibility(View.VISIBLE);
            }
            if (args.containsKey("cantidad"))
                etQuantity.setText(String.valueOf(args.getInt("cantidad")));
            if (args.containsKey("cantidad"))
                etQuantity.setText(String.valueOf(args.getInt("cantidad")));
        }

        // 2. Cargar desde API para datos frescos y ALMACEN
        Call<ProductoDto> call = RetrofitClient.INSTANCE.getInstance().getProducto(idProductoEditar);
        call.enqueue(new Callback<ProductoDto>() {
            @Override
            public void onResponse(Call<ProductoDto> call, Response<ProductoDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductoDto p = response.body();
                    etName.setText(p.getNombre());
                    etSku.setText(p.getSku());

                    etCategory.setText(p.getCategoria(), false);
                    etPriceBuy.setText(String.valueOf(p.getPrecioCompra()));
                    etPriceSell.setText(String.valueOf(p.getPrecioVenta()));
                    etDesc.setText(p.getDescripcion());
                    etQuantity.setText(String.valueOf(p.getCantidad()));

                    // Cargar imagen si existe
                    if (p.getImagenUrl() != null && !p.getImagenUrl().isEmpty()) {
                        currentImageUrl = p.getImagenUrl();
                        Glide.with(requireContext())
                            .load(RetrofitClient.getFullImageUrl(currentImageUrl))
                            .into(ivPreview);
                        ivPreview.setAlpha(1.0f);
                        layoutPlaceholder.setVisibility(View.GONE);
                        btnRemoveImage.setVisibility(View.VISIBLE);
                    }

                    // Seleccionar Almacén
                    if (p.getIdAlmacen() != null) {
                        seleccionarAlmacenPorId(p.getIdAlmacen());
                    }
                } else {
                    // Si falla, al menos tenemos los datos del bundle (si venía de inventario)
                    Toast.makeText(getContext(), "Nota: No se pudieron cargar detalles adicionales (Almacén)",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductoDto> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red al cargar producto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void seleccionarAlmacenPorId(int idAlmacen) {
        if (listaAlmacenes.isEmpty())
            return;

        for (AlmacenDto a : listaAlmacenes) {
            if (a.getIdAlmacen() == idAlmacen) {
                etWarehouse.setText(a.getNombre(), false);
                break;
            }
        }
    }

    private void setupCategoryDropdown() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        Call<List<CategoriaDto>> call = RetrofitClient.INSTANCE.getInstance().getCategorias(userId);

        call.enqueue(new Callback<List<CategoriaDto>>() {
            @Override
            public void onResponse(Call<List<CategoriaDto>> call, Response<List<CategoriaDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaCategorias = response.body();

                    ArrayAdapter<CategoriaDto> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            listaCategorias);
                    etCategory.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<CategoriaDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar categorías", Toast.LENGTH_SHORT).show();
            }
        });

        etCategory.setOnItemClickListener((parent, view, position, id) -> {
            CategoriaDto seleccionada = (CategoriaDto) parent.getItemAtPosition(position);
        });
    }

    private void setupWarehouseDropdown() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        Call<List<AlmacenDto>> call = RetrofitClient.INSTANCE.getInstance().getAlmacenes(userId);

        call.enqueue(new Callback<List<AlmacenDto>>() {
            @Override
            public void onResponse(Call<List<AlmacenDto>> call, Response<List<AlmacenDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaAlmacenes = response.body();

                    ArrayAdapter<AlmacenDto> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            listaAlmacenes);

                    etWarehouse.setAdapter(adapter);

                    // Si estamos editando y ya tenemos lista, intentar seleccionar (si ya se cargó
                    // el producto)
                    // SOLO para edición, no para nuevos.
                    if (idProductoEditar != null) {
                        // Ya se manejó en cargarDatosParaEditar, pero dejamos el comentario
                    } // Pero si cargarDatos se ejecutó antes, aquí refrescamos el texto si hace falta
                }
            }

            @Override
            public void onFailure(Call<List<AlmacenDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar almacenes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validarCampos() {
        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError("Requerido");
            return false;
        }
        // Validar Almacén seleccionado
        String warehouseName = etWarehouse.getText().toString().trim();
        if (warehouseName.isEmpty() || warehouseName.equals("Selecciona un almacén")) {
            etWarehouse.setError("Selecciona un almacén");
            Toast.makeText(getContext(), "Debes seleccionar un almacén", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar que el almacén exista en la lista (evitar texto libre invalido)
        boolean exists = false;
        for (AlmacenDto a : listaAlmacenes) {
            if (a.getNombre().equals(warehouseName)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            etWarehouse.setError("Almacén inválido");
            return false;
        }
        return true;
    }

    private ProductoDto construirDto() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        int selectedWarehouseId = 0;
        String warehouseNombre = etWarehouse.getText().toString();

        for (AlmacenDto a : listaAlmacenes) {
            if (a.getNombre() != null && a.getNombre().equals(warehouseNombre)) {
                selectedWarehouseId = a.getIdAlmacen();
                break;
            }
        }

        ProductoDto p = new ProductoDto(
                etName.getText().toString().trim(),
                etSku.getText().toString().trim(),
                parseIntSafe(etQuantity.getText().toString()),
                etCategory.getText().toString().trim(),
                parseDoubleSafe(etPriceBuy.getText().toString()),
                etPriceSell.getText().toString().trim().isEmpty() ? 0.0 : Double.parseDouble(etPriceSell.getText().toString().trim()),
                etDesc.getText().toString().trim(),
                5, // Stock Mínimo predeterminado
                userId,
                selectedWarehouseId);
        
        // Asignar URLs de multimedia
        p.setImagenUrl(currentImageUrl);
        return p;
    }

    private byte[] getBytesFromUri(Uri uri) throws IOException {
        InputStream iStream = requireContext().getContentResolver().openInputStream(uri);
        if (iStream == null) return null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = iStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void subirImagenYGuardar() {
        btnAction.setEnabled(false);
        Toast.makeText(getContext(), "Subiendo imagen...", Toast.LENGTH_SHORT).show();

        try {
            byte[] imageBytes = getBytesFromUri(imageUri);
            if (imageBytes == null) {
                Toast.makeText(getContext(), "Error al leer imagen", Toast.LENGTH_SHORT).show();
                btnAction.setEnabled(true);
                return;
            }
            // Add a proper content type if possible, or default to image/jpeg
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile);

            Call<UploadResponse> call = RetrofitClient.INSTANCE.getInstance().uploadImage(body);
            call.enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getUrl() != null) {
                        String relativeUrl = response.body().getUrl(); // ej: /uploads/uuid.jpg
                        // Guardamos solo la ruta relativa en la base de datos
                        currentImageUrl = relativeUrl; 

                        if (idProductoEditar == null) {
                            crearProducto();
                        } else {
                            actualizarProducto();
                        }
                    } else {
                        btnAction.setEnabled(true);
                        Toast.makeText(getContext(), "Error del servidor subiendo imagen", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    btnAction.setEnabled(true);
                    Toast.makeText(getContext(), "Error de red subiendo imagen: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (IOException e) {
            btnAction.setEnabled(true);
            Toast.makeText(getContext(), "Error al cargar archivo local", Toast.LENGTH_LONG).show();
        }
    }

    private void crearProducto() {
        if (!validarCampos())
            return;

        ProductoDto dto = construirDto();

        Call<Void> call = RetrofitClient.INSTANCE.getInstance().crearProducto(dto);
        ejecutarLlamada(call, "Producto creado con éxito");
    }

    private void actualizarProducto() {
        if (!validarCampos())
            return;

        ProductoDto dto = construirDto();

        Call<Void> call = RetrofitClient.INSTANCE.getInstance().actualizarProducto(idProductoEditar, dto);
        ejecutarLlamada(call, "Producto actualizado correctamente");
    }

    private void ejecutarLlamada(Call<Void> call, String mensajeExito) {
        btnAction.setEnabled(false);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnAction.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), mensajeExito, Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(AddProductFragment.this).popBackStack();
                } else {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnAction.setEnabled(true);
                if (idProductoEditar == null) {
                    Toast.makeText(getContext(), "Sin conexión. Guardando localmente...", Toast.LENGTH_SHORT).show();
                    guardarProductoOffline();
                } else {
                    Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void mostrarConfirmacionBorrar() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Producto")
                .setMessage("¿Estás seguro? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    eliminarProductoApi();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarProductoApi() {
        btnDelete.setEnabled(false);

        Call<Void> call = RetrofitClient.INSTANCE.getInstance().eliminarProducto(idProductoEditar);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Producto eliminado", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(AddProductFragment.this).popBackStack();
                } else {
                    btnDelete.setEnabled(true);
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnDelete.setEnabled(true);
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoCrearCategoria() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_simple_input, null);
        TextInputEditText etName = dialogView.findViewById(R.id.et_input_name);
        TextInputEditText etDesc = dialogView.findViewById(R.id.et_input_desc);

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Nueva Categoría")
                .setView(dialogView)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = etName.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();
                    if (!nombre.isEmpty()) {
                        crearCategoriaApi(nombre, desc);
                    } else {
                        Toast.makeText(getContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoCrearAlmacen() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_simple_input, null);
        TextInputEditText etName = dialogView.findViewById(R.id.et_input_name);
        TextInputEditText etDesc = dialogView.findViewById(R.id.et_input_desc);

        if (etName != null) {
            etName.setHint("Nombre del Almacén");
        }
        if (etDesc != null) {
            etDesc.setHint("Ubicación / Dirección");
        }

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Nuevo Almacén")
                .setView(dialogView)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = etName.getText().toString().trim();
                    String ubicacion = etDesc.getText().toString().trim();
                    if (!nombre.isEmpty()) {
                        crearAlmacenApi(nombre, ubicacion);
                    } else {
                        Toast.makeText(getContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Interfaz funcional para lambda
    interface EntidadCreador {
        void crear(String nombre, String desc);
    }

    private void crearEntidadFallback(String tipo, EntidadCreador creador) {
        android.widget.LinearLayout layout = new android.widget.LinearLayout(getContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final android.widget.EditText inputNombre = new android.widget.EditText(getContext());
        inputNombre.setHint("Nombre del " + tipo);
        layout.addView(inputNombre);

        final android.widget.EditText inputDesc = new android.widget.EditText(getContext());
        inputDesc.setHint("Descripción (opcional)");
        layout.addView(inputDesc);

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Nuevo " + tipo)
                .setView(layout)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = inputNombre.getText().toString().trim();
                    String desc = inputDesc.getText().toString().trim();
                    if (!nombre.isEmpty()) {
                        creador.crear(nombre, desc);
                    } else {
                        Toast.makeText(getContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void crearCategoriaApi(String nombre, String desc) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", "");

        CategoriaDto dto = new CategoriaDto(0, nombre, desc);
        Call<CategoriaDto> call = RetrofitClient.INSTANCE.getInstance().crearCategoria(dto, userId);
        call.enqueue(new Callback<CategoriaDto>() {
            @Override
            public void onResponse(Call<CategoriaDto> call, Response<CategoriaDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Categoría creada In-Line", Toast.LENGTH_SHORT).show();
                    setupCategoryDropdown(); // Refrescar lista
                    etCategory.setText(nombre, false); // Autoseleccionar
                } else {
                    Toast.makeText(getContext(), "Error al crear: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoriaDto> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void crearAlmacenApi(String nombre, String ubicacion) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", "");

        AlmacenDto dto = new AlmacenDto(0, nombre, ubicacion);
        Call<AlmacenDto> call = RetrofitClient.INSTANCE.getInstance().crearAlmacen(dto, userId);
        call.enqueue(new Callback<AlmacenDto>() {
            @Override
            public void onResponse(Call<AlmacenDto> call, Response<AlmacenDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Almacén creado In-Line", Toast.LENGTH_SHORT).show();
                    setupWarehouseDropdown(); // Refrescar lista
                    etWarehouse.setText(nombre, false); // Autoseleccionar
                } else {
                    Toast.makeText(getContext(), "Error al crear: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AlmacenDto> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int parseIntSafe(String v) {
        try {
            return Integer.parseInt(v.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDoubleSafe(String v) {
        try {
            return Double.parseDouble(v.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
}