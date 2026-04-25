package com.example.gemainventory.ui.inventory;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gemainventory.R;
import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.model.InventarioDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback; // <-- IMPORT CORRECTO
import retrofit2.Response;

public class InventoryFragment extends Fragment {

    private RecyclerView inventoryRecycler;
    private View cardReorder;
    private android.widget.LinearLayout reorderListContainer;
    private InventoryAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private SearchView searchView;
    private View emptyView;
    private View headerContainer;
    private View btnSearchIcon;
    private View btnFilterIcon;
    private View searchContainer;
    private View btnCloseSearch;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fab = view.findViewById(R.id.fab_add_product);
        inventoryRecycler = view.findViewById(R.id.inventory_recyclerView);
        searchView = view.findViewById(R.id.search_view);
        emptyView = view.findViewById(R.id.empty_view);
        headerContainer = view.findViewById(R.id.header_container);
        btnSearchIcon = view.findViewById(R.id.btn_search_icon);
        btnFilterIcon = view.findViewById(R.id.btn_filter_icon);
        cardReorder = view.findViewById(R.id.reorder_assistant_card);
        reorderListContainer = view.findViewById(R.id.reorder_list_container);
        searchView = view.findViewById(R.id.search_view);
        searchContainer = view.findViewById(R.id.search_container);
        btnCloseSearch = view.findViewById(R.id.btn_close_search);

        fab.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_inventory_to_addProduct);
        });

        if (btnFilterIcon != null) {
            btnFilterIcon.setOnClickListener(this::showFilterMenu);
        }

        View btnEmptyAddProduct = view.findViewById(R.id.btn_empty_add_product);
        if (btnEmptyAddProduct != null) {
            btnEmptyAddProduct.setOnClickListener(v -> {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_inventory_to_addProduct);
            });
        }

        adapter = new InventoryAdapter(new ArrayList<>(productList), product -> {
            Bundle bundle = new Bundle();
            bundle.putString("idProducto", String.valueOf(product.getId()));
            bundle.putString("nombre", product.getName());
            bundle.putString("sku", product.getSku());
            bundle.putInt("cantidad", product.getQuantity());
            bundle.putString("categoria", product.getCategory());
            bundle.putDouble("precioCompra", product.getPurchasePrice());
            bundle.putDouble("precioVenta", product.getSalePrice());
            bundle.putString("descripcion", product.getDescription());
            bundle.putString("imagenUrl", product.getImageUrl());

            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_inventory_to_addProduct, bundle);
        });

        inventoryRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        inventoryRecycler.setAdapter(adapter);

        setupSearchLogic();
        fetchProducts();

        // Mostrar tutorial si es la primera vez
        if (fab != null) {
            showTutorial(fab);
        }
    }

    private void showTutorial(View target) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        boolean tutorialShown = prefs.getBoolean("tutorial_inventory_shown", false);

        if (!tutorialShown) {
            com.getkeepsafe.taptargetview.TapTargetView.showFor(requireActivity(),
                    com.getkeepsafe.taptargetview.TapTarget
                            .forView(target, "Agrega tus Productos",
                                    "Toca aquí para registrar nuevo inventario manualmente")
                            .outerCircleColor(R.color.primary)
                            .targetCircleColor(android.R.color.white)
                            .titleTextSize(20)
                            .titleTextColor(android.R.color.white)
                            .descriptionTextSize(14)
                            .textColor(android.R.color.white)
                            .drawShadow(true)
                            .cancelable(true)
                            .tintTarget(true)
                            .transparentTarget(true)
                            .targetRadius(60),
                    new com.getkeepsafe.taptargetview.TapTargetView.Listener() {
                        @Override
                        public void onTargetClick(com.getkeepsafe.taptargetview.TapTargetView view) {
                            super.onTargetClick(view);
                            prefs.edit().putBoolean("tutorial_inventory_shown", true).apply();
                            target.performClick();
                        }

                        @Override
                        public void onOuterCircleClick(com.getkeepsafe.taptargetview.TapTargetView view) {
                            super.onOuterCircleClick(view);
                            prefs.edit().putBoolean("tutorial_inventory_shown", true).apply();
                        }

                        @Override
                        public void onTargetDismissed(com.getkeepsafe.taptargetview.TapTargetView view,
                                boolean userInitiated) {
                            prefs.edit().putBoolean("tutorial_inventory_shown", true).apply();
                        }
                    });
        }
    }

    private void setupSearchLogic() {
        btnSearchIcon.setOnClickListener(v -> {
            headerContainer.setVisibility(View.GONE);
            searchContainer.setVisibility(View.VISIBLE);
            searchView.setIconified(false);
            searchView.requestFocus();
        });

        btnCloseSearch.setOnClickListener(v -> {
            cerrarBusqueda();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void fetchProducts() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null)
            return;

        Call<List<InventarioDto>> call = RetrofitClient.INSTANCE.getInstance().getInventario(userId);

        call.enqueue(new Callback<List<InventarioDto>>() {
            @Override
            public void onResponse(Call<List<InventarioDto>> call, Response<List<InventarioDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<InventarioDto> remoteList = response.body();

                    if (remoteList.isEmpty()) {
                        inventoryRecycler.setVisibility(View.GONE);
                        if (emptyView != null)
                            emptyView.setVisibility(View.VISIBLE);
                    } else {
                        inventoryRecycler.setVisibility(View.VISIBLE);
                        if (emptyView != null)
                            emptyView.setVisibility(View.GONE);

                        List<Product> mappedList = new ArrayList<>();
                        for (InventarioDto dto : remoteList) {
                            double pVenta = dto.getPrecioVenta() != null ? dto.getPrecioVenta() : 0.0;
                            double pCompra = dto.getPrecioCompra() != null ? dto.getPrecioCompra() : 0.0;
                            int minStock = dto.getStockMinimo() != null ? dto.getStockMinimo() : 5;

                            Product p = new Product(
                                    dto.getIdProducto(),
                                    dto.getNombreProducto(),
                                    dto.getSku() != null ? dto.getSku() : "S/N",
                                    dto.getCantidadActual(),
                                    minStock,
                                    pVenta,
                                    dto.getDescripcion(),
                                    dto.getCategoria() != null ? dto.getCategoria() : "Sin Categoría",
                                    dto.getImagenUrl());

                            p.setPurchasePrice(pCompra);

                            // Parsear fecha si existe para ordenamiento cronológico
                            if (dto.getFechaCreacion() != null) {
                                try {
                                    // El backend envía LocalDateTime.toString()
                                    java.time.LocalDateTime dt = java.time.LocalDateTime.parse(dto.getFechaCreacion());
                                    p.setDateAdded(dt.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
                                } catch (Exception e) {
                                    p.setDateAdded(System.currentTimeMillis());
                                }
                            }

                            mappedList.add(p);
                        }

                        productList.clear();
                        productList.addAll(mappedList);
                        adapter.filterList(productList);
                        actualizarAsistenteReorden(productList);
                    }
                } else {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Error al cargar inventario", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<InventarioDto>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
    }

    private void filter(String query) {
        List<Product> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            filteredList.addAll(productList);
        } else {
            String filterPattern = query.toLowerCase().trim();
            for (Product product : productList) {
                if (product.getName().toLowerCase().contains(filterPattern) ||
                        product.getSku().toLowerCase().contains(filterPattern)) {
                    filteredList.add(product);
                }
            }
        }
        adapter.filterList(filteredList);
    }

    private void actualizarAsistenteReorden(List<Product> productos) {
        if (productos == null || productos.isEmpty() || reorderListContainer == null) {
            cardReorder.setVisibility(View.GONE);
            return;
        }

        reorderListContainer.removeAllViews();
        int productosEnRiesgo = 0;
        int maxMostrar = 5;

        for (Product p : productos) {
            if (p.getQuantity() <= p.getMinStock()) {
                if (productosEnRiesgo < maxMostrar) {
                    TextView tvItem = new TextView(requireContext());
                    tvItem.setPadding(0, 4, 0, 4);
                    tvItem.setTextSize(14f);
                    tvItem.setTypeface(android.graphics.Typeface.create("poppins", android.graphics.Typeface.NORMAL));
                    
                    // Color de alta visibilidad (Ámbar brillante para tema oscuro)
                    tvItem.setTextColor(android.graphics.Color.parseColor("#FFB74D")); 
                    
                    // Formato de lista: • Producto (Cant)
                    java.lang.String text = "• " + p.getName() + " (" + p.getQuantity() + ")";
                    tvItem.setText(text);
                    
                    reorderListContainer.addView(tvItem);
                }
                productosEnRiesgo++;
            }
        }

        if (productosEnRiesgo > maxMostrar) {
            TextView tvMore = new TextView(requireContext());
            tvMore.setText("... y " + (productosEnRiesgo - maxMostrar) + " productos más.");
            tvMore.setTextSize(12f);
            tvMore.setAlpha(0.7f);
            tvMore.setTypeface(null, android.graphics.Typeface.ITALIC);
            reorderListContainer.addView(tvMore);
        }

        if (productosEnRiesgo > 0) {
            cardReorder.setVisibility(View.VISIBLE);
        } else {
            cardReorder.setVisibility(View.GONE);
        }
    }

    private void showFilterMenu(View v) {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(getContext(), v);
        popup.getMenu().add("Nombre (A-Z)");
        popup.getMenu().add("Nombre (Z-A)");
        popup.getMenu().add("Precio (Menor a Mayor)");
        popup.getMenu().add("Precio (Mayor a Menor)");
        popup.getMenu().add("Stock (Críticos primero)");
        popup.getMenu().add("Stock (Mayor a Menor)");
        popup.getMenu().add("Más Actual");
        popup.getMenu().add("Más Viejo");

        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            switch (title) {
                case "Nombre (A-Z)":
                    java.util.Collections.sort(productList, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                    break;
                case "Nombre (Z-A)":
                    java.util.Collections.sort(productList, (p2, p1) -> p1.getName().compareToIgnoreCase(p2.getName()));
                    break;
                case "Precio (Menor a Mayor)":
                    java.util.Collections.sort(productList, (p1, p2) -> Double.compare(p1.getSalePrice(), p2.getSalePrice()));
                    break;
                case "Precio (Mayor a Menor)":
                    java.util.Collections.sort(productList, (p2, p1) -> Double.compare(p1.getSalePrice(), p2.getSalePrice()));
                    break;
                case "Stock (Críticos primero)":
                    java.util.Collections.sort(productList, (p1, p2) -> Integer.compare(p1.getQuantity(), p2.getQuantity()));
                    break;
                case "Stock (Mayor a Menor)":
                    java.util.Collections.sort(productList, (p2, p1) -> Integer.compare(p1.getQuantity(), p2.getQuantity()));
                    break;
                case "Más Actual":
                    java.util.Collections.sort(productList, (p1, p2) -> Long.compare(p2.getDateAdded(), p1.getDateAdded()));
                    break;
                case "Más Viejo":
                    java.util.Collections.sort(productList, (p1, p2) -> Long.compare(p1.getDateAdded(), p2.getDateAdded()));
                    break;
            }
            filter(searchView.getQuery().toString());
            return true;
        });
        popup.show();
    }

    private void cerrarBusqueda() {
        searchView.setQuery("", false);
        searchContainer.setVisibility(View.GONE);
        headerContainer.setVisibility(View.VISIBLE);
        filter("");

        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }
}