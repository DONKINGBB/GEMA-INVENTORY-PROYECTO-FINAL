package com.example.gemainventory.ui.orders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gemainventory.R;
import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.model.AlmacenDto;
import com.example.gemainventory.model.ClienteDto;
import com.example.gemainventory.model.DetallePedidoDto;
import com.example.gemainventory.model.PedidoDto;
// CAMBIO IMPORTANTE: Usamos el DTO de Lectura (Selección)
import com.example.gemainventory.model.ProductoLecturaDto;
import com.example.gemainventory.model.ProductoSeleccionDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddOrderFragment extends Fragment {

    private AutoCompleteTextView spinnerClientes, spinnerAlmacenes;
    private TextView tvTotal, tvEmptyMsg;
    private RecyclerView recyclerProductos;
    private OrderAdapter adapter;
    // ANTES: private List<ProductoDto> listaProductosDisponibles = ...
// AHORA:
    private List<ProductoSeleccionDto> listaProductosDisponibles = new ArrayList<>();
    private List<ClienteDto> listaClientes = new ArrayList<>();
    private List<AlmacenDto> listaAlmacenes = new ArrayList<>();

    // CAMBIO 1: La lista ahora es del tipo de lectura

    private List<DetallePedidoDto> carritoCompras = new ArrayList<>();

    private ClienteDto clienteSeleccionado;
    private AlmacenDto almacenSeleccionado;
    private String userId;

    private EditText etNombre, etFecha;
    private java.util.Calendar calendar = java.util.Calendar.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", null);

        spinnerClientes = view.findViewById(R.id.spinner_clientes);
        spinnerAlmacenes = view.findViewById(R.id.spinner_almacenes);
        tvTotal = view.findViewById(R.id.tv_total_amount);
        tvEmptyMsg = view.findViewById(R.id.tv_empty_cart_msg);
        recyclerProductos = view.findViewById(R.id.recycler_order_products);
        
        // Nuevos Campos
        etNombre = view.findViewById(R.id.et_order_name);
        etFecha = view.findViewById(R.id.et_deadline);

        etFecha.setOnClickListener(v -> mostrarDatePicker());

        recyclerProductos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderAdapter(carritoCompras, this::actualizarUI);
        recyclerProductos.setAdapter(adapter);

        cargarClientes();
        cargarAlmacenes();

        view.findViewById(R.id.btn_add_product_to_list).setOnClickListener(v -> mostrarDialogoProducto());
        view.findViewById(R.id.btn_save_order).setOnClickListener(v -> guardarPedido());
        view.findViewById(R.id.btn_back).setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
    }

    private void mostrarDatePicker() {
        new android.app.DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(java.util.Calendar.YEAR, year);
            calendar.set(java.util.Calendar.MONTH, month);
            calendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);
            
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
            etFecha.setText(sdf.format(calendar.getTime()));
        }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
    }

    private void actualizarUI() {
        double total = 0;
        for (DetallePedidoDto d : carritoCompras) {
            total += d.getSubtotal();
        }
        tvTotal.setText(String.format("$%.2f", total));

        if (carritoCompras.isEmpty()) {
            tvEmptyMsg.setVisibility(View.VISIBLE);
            recyclerProductos.setVisibility(View.GONE);
        } else {
            tvEmptyMsg.setVisibility(View.GONE);
            recyclerProductos.setVisibility(View.VISIBLE);
        }
    }

    private void cargarClientes() {
        if (userId == null) return;
        RetrofitClient.INSTANCE.getInstance().getClientes(userId).enqueue(new Callback<List<ClienteDto>>() {
            @Override
            public void onResponse(Call<List<ClienteDto>> call, Response<List<ClienteDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaClientes = response.body();
                    ArrayAdapter<ClienteDto> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, listaClientes);
                    spinnerClientes.setAdapter(adapter);
                    spinnerClientes.setOnItemClickListener((p, v, pos, id) -> clienteSeleccionado = (ClienteDto) p.getItemAtPosition(pos));
                }
            }
            @Override public void onFailure(Call<List<ClienteDto>> call, Throwable t) {}
        });
    }

    private void cargarAlmacenes() {
        if (userId == null) return;
        RetrofitClient.INSTANCE.getInstance().getAlmacenes(userId).enqueue(new Callback<List<AlmacenDto>>() {
            @Override
            public void onResponse(Call<List<AlmacenDto>> call, Response<List<AlmacenDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaAlmacenes = response.body();
                    ArrayAdapter<AlmacenDto> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, listaAlmacenes);
                    spinnerAlmacenes.setAdapter(adapter);
                    spinnerAlmacenes.setOnItemClickListener((p, v, pos, id) -> almacenSeleccionado = (AlmacenDto) p.getItemAtPosition(pos));
                }
            }
            @Override public void onFailure(Call<List<AlmacenDto>> call, Throwable t) {}
        });
    }

    private void mostrarDialogoProducto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_product_to_order, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Referencias a las vistas
        final AutoCompleteTextView spinProd = dialogView.findViewById(R.id.spinner_dialog_product);
        final EditText etCant = dialogView.findViewById(R.id.et_dialog_quantity);
        Button btnAdd = dialogView.findViewById(R.id.btn_dialog_add);

        final ProductoSeleccionDto[] prodSeleccionadoDialog = {null};

        // --- FIX 1: Configurar el AutoComplete para que actúe como Spinner ---
        // Esto fuerza a que la lista se despliegue en cuanto tocas la caja, sin tener que escribir.
        spinProd.setThreshold(1); // Muestra sugerencias con 1 letra (o 0 si lo forzamos)
        spinProd.setOnClickListener(v -> spinProd.showDropDown());
        spinProd.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !spinProd.getText().toString().isEmpty()) {
                spinProd.showDropDown();
            }
        });
        // --------------------------------------------------------------------

        if (userId != null) {
            // Verificar si hay almacén seleccionado
            if (almacenSeleccionado == null) {
                Toast.makeText(getContext(), "Primero selecciona un almacén", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            // Usar el nuevo endpoint filtrado por almacén
            RetrofitClient.INSTANCE.getInstance().getProductosPorAlmacen(userId, almacenSeleccionado.getIdAlmacen()).enqueue(new Callback<List<ProductoSeleccionDto>>() {
                @Override
                public void onResponse(Call<List<ProductoSeleccionDto>> call, Response<List<ProductoSeleccionDto>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        listaProductosDisponibles = response.body();

                        if (listaProductosDisponibles.isEmpty()) {
                            Toast.makeText(getContext(), "No hay stock en este almacén", Toast.LENGTH_SHORT).show();
                            // No retornamos para permitir (opcionalmente) que vea la lista vacía o cerrar
                        }
                        
                        // ... Resto del código del adaptador (igual que antes) ...
                        ArrayAdapter<ProductoSeleccionDto> prodAdapter = new ArrayAdapter<ProductoSeleccionDto>(
                                requireContext(),
                                android.R.layout.simple_dropdown_item_1line,
                                listaProductosDisponibles
                        ) {
                             // 1. Vista cuando está CERRADO
                             @Override
                             public View getView(int position, View convertView, ViewGroup parent) {
                                 // ... (Mismo código de vista) ...
                                 View view = super.getView(position, convertView, parent);
                                 TextView text = (TextView) view.findViewById(android.R.id.text1);
                                 int nightModeFlags = requireContext().getResources().getConfiguration().uiMode &
                                         android.content.res.Configuration.UI_MODE_NIGHT_MASK;
                                 if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                                     text.setTextColor(Color.WHITE);
                                 } else {
                                     text.setTextColor(Color.BLACK);
                                 }
                                 text.setText(getItem(position).getNombre());
                                 return view;
                             }
                             // 2. Vista cuando está ABIERTO
                             @Override
                             public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                  // ... (Mismo código de vista desplegable) ...
                                 View view = super.getDropDownView(position, convertView, parent);
                                 TextView text = (TextView) view.findViewById(android.R.id.text1);
                                 int nightModeFlags = requireContext().getResources().getConfiguration().uiMode &
                                         android.content.res.Configuration.UI_MODE_NIGHT_MASK;
                                 if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                                     text.setTextColor(Color.WHITE);
                                     text.setBackgroundColor(Color.parseColor("#333333"));
                                 } else {
                                     text.setTextColor(Color.BLACK);
                                     text.setBackgroundColor(Color.WHITE);
                                 }
                                 text.setText(getItem(position).getNombre());
                                 text.setPadding(30, 30, 30, 30);
                                 return view;
                             }
                        };
                        spinProd.setAdapter(prodAdapter);
                        if(spinProd.hasFocus()) {
                            spinProd.showDropDown();
                        }

                    } else {
                        Toast.makeText(getContext(), "Error al cargar: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<ProductoSeleccionDto>> call, Throwable t) {
                    Toast.makeText(getContext(), "Error red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        spinProd.setOnItemClickListener((parent, view, position, id) -> {
            prodSeleccionadoDialog[0] = (ProductoSeleccionDto) parent.getItemAtPosition(position);
            spinProd.setError(null); // Limpiar errores si los hubiera
        });

        btnAdd.setOnClickListener(v -> {
            if (prodSeleccionadoDialog[0] != null && !etCant.getText().toString().isEmpty()) {

                String cantidadTexto = etCant.getText().toString();
                int cantidad = 0;
                try {
                    cantidad = Integer.parseInt(cantidadTexto);
                } catch (NumberFormatException e) {
                    etCant.setError("Número inválido");
                    return;
                }

                // --- VALIDACIÓN DE STOCK REAL ---
                int stockDisponible = prodSeleccionadoDialog[0].getCantidad();
                if (cantidad > stockDisponible) {
                    etCant.setText(String.valueOf(stockDisponible));
                    
                    // Notificación con diseño de la app (Snackbar Premium)
                    com.google.android.material.snackbar.Snackbar snackbar = com.google.android.material.snackbar.Snackbar.make(
                            dialogView, "⚠️ Stock insuficiente (Disponible: " + stockDisponible + ")", 
                            com.google.android.material.snackbar.Snackbar.LENGTH_LONG);
                    
                    // Personalización Premium (Diseño GEMA)
                    View snackView = snackbar.getView();
                    snackView.setBackground(requireContext().getDrawable(R.drawable.bg_dashboard_card_premium));
                    snackView.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#EF4444"))); // Color Coral
                    
                    TextView tv = snackView.findViewById(com.google.android.material.R.id.snackbar_text);
                    if (tv != null) {
                        tv.setTypeface(android.graphics.Typeface.create("poppins_semibold", android.graphics.Typeface.NORMAL));
                        tv.setTextColor(android.graphics.Color.WHITE);
                    }
                    snackbar.show();
                    return;
                }
                // --------------------------------

                DetallePedidoDto detalle = new DetallePedidoDto(
                        prodSeleccionadoDialog[0].getIdProducto(),
                        prodSeleccionadoDialog[0].getNombre(),
                        cantidad,
                        prodSeleccionadoDialog[0].getPrecioVenta()
                );

                carritoCompras.add(detalle);
                adapter.notifyDataSetChanged();
                actualizarUI();
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Selecciona producto y cantidad válida", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void guardarPedido() {
        if (clienteSeleccionado == null || almacenSeleccionado == null || carritoCompras.isEmpty()) {
            Toast.makeText(getContext(), "Faltan datos o productos", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String nombre = etNombre.getText().toString();
        String fecha = etFecha.getText().toString();

        PedidoDto pedido = new PedidoDto(
                clienteSeleccionado.getIdCliente(),
                almacenSeleccionado.getIdAlmacen(),
                carritoCompras,
                null,
                null,
                null,
                null,
                nombre.isEmpty() ? null : nombre,
                fecha.isEmpty() ? null : fecha
        );

        RetrofitClient.INSTANCE.getInstance().crearPedido(pedido, userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Pedido Guardado", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(AddOrderFragment.this).popBackStack();
                } else {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}