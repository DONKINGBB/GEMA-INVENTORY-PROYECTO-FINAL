package com.example.gemainventory.ui.orders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gemainventory.R;
import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.model.PedidoDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersFragment extends Fragment {

    private RecyclerView ordersRecycler;
    private View emptyView;
    private OrdersHistoryAdapter adapter;
    private List<PedidoDto> orderList = new ArrayList<>();
    private String userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", null);

        // Configurar UI
        ordersRecycler = view.findViewById(R.id.orders_recyclerView);
        emptyView = view.findViewById(R.id.empty_view_orders);
        ordersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Configurar Adaptador
        adapter = new OrdersHistoryAdapter(orderList, this::mostrarOpcionesPedido);
        ordersRecycler.setAdapter(adapter);

        // Lógica de navegación compartida
        View.OnClickListener addAction = v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_orders_to_add_form);
        };

        // FAB
        FloatingActionButton fab = view.findViewById(R.id.fab_add_order);
        fab.setOnClickListener(addAction);

        // Botón en el estado vacío
        View btnEmpty = view.findViewById(R.id.btn_empty_create_order);
        if (btnEmpty != null) {
            btnEmpty.setOnClickListener(addAction);
        }

        cargarPedidosReales();
    }

    private void mostrarOpcionesPedido(PedidoDto pedido) {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = 
            new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_order_options, null);
        bottomSheetDialog.setContentView(sheetView);

        android.widget.TextView tvTitle = sheetView.findViewById(R.id.tv_sheet_order_title);
        String nombre = (pedido.getNombre() != null && !pedido.getNombre().isEmpty()) 
                        ? pedido.getNombre() 
                        : "Pedido #" + (pedido.getId() != null ? pedido.getId().substring(0, 8) : "S/N");
        tvTitle.setText(nombre);

        sheetView.findViewById(R.id.btn_sheet_view_details).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            mostrarDetallesProductos(pedido);
        });

        View btnDeliver = sheetView.findViewById(R.id.btn_sheet_mark_delivered);
        View btnDelete = sheetView.findViewById(R.id.btn_sheet_delete);

        // SI YA ESTÁ COMPLETADO (ID 2), OCULTAR BOTONES DE ACCIÓN PARA PROTEGER DATOS
        if (pedido.getIdEstado() != null && pedido.getIdEstado() == 2) {
            btnDeliver.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }

        btnDeliver.setOnClickListener(v -> {
             bottomSheetDialog.dismiss();
             marcarComoEntregado(pedido.getId());
        });

        btnDelete.setOnClickListener(v -> {
             bottomSheetDialog.dismiss();
             eliminarPedido(pedido.getId());
        });

        bottomSheetDialog.show();
    }

    private void mostrarDetallesProductos(PedidoDto pedido) {
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            Toast.makeText(getContext(), "Sin productos", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (com.example.gemainventory.model.DetallePedidoDto det : pedido.getDetalles()) {
            String prodName = det.getNombreProducto() != null ? det.getNombreProducto() : ("ID:" + det.getIdProducto().substring(0,5));
            sb.append("• ").append(prodName).append(" x").append(det.getCantidad()).append("\n");
        }

        new android.app.AlertDialog.Builder(requireContext())
            .setTitle("Productos del Pedido")
            .setMessage(sb.toString())
            .setPositiveButton("Cerrar", null)
            .show();
    }

    private void marcarComoEntregado(String idPedido) {
        RetrofitClient.INSTANCE.getInstance().marcarPedidoEntregado(idPedido).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "¡Pedido Entregado!", Toast.LENGTH_SHORT).show();
                    cargarPedidosReales();
                } else {
                    Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarPedido(String idPedido) {
        if (userId == null) return;
        RetrofitClient.INSTANCE.getInstance().eliminarPedido(idPedido, userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Pedido eliminado", Toast.LENGTH_SHORT).show();
                    cargarPedidosReales();
                } else {
                    String error = "No se puede eliminar";
                    if (response.code() == 403) {
                        error = "No se pueden eliminar pedidos completados (protección de finanzas)";
                    }
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarPedidosReales() {
        if (userId == null) {
            Toast.makeText(getContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.INSTANCE.getInstance().getPedidos(userId).enqueue(new Callback<List<PedidoDto>>() {
            @Override
            public void onResponse(Call<List<PedidoDto>> call, Response<List<PedidoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    updateEmptyView();
                } else {
                    Toast.makeText(getContext(), "No se pudieron cargar pedidos", Toast.LENGTH_SHORT).show();
                    updateEmptyView();
                }
            }

            @Override
            public void onFailure(Call<List<PedidoDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                updateEmptyView();
            }
        });
    }

    private void updateEmptyView() {
        if (orderList.isEmpty()) {
            ordersRecycler.setVisibility(View.GONE);
            if (emptyView != null) emptyView.setVisibility(View.VISIBLE);
        } else {
            ordersRecycler.setVisibility(View.VISIBLE);
            if (emptyView != null) emptyView.setVisibility(View.GONE);
        }
    }
}