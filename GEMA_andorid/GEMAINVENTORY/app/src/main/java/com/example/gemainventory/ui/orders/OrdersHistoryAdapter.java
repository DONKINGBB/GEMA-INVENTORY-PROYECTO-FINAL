package com.example.gemainventory.ui.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gemainventory.R;
import com.example.gemainventory.model.PedidoDto;
import java.util.List;

public class OrdersHistoryAdapter extends RecyclerView.Adapter<OrdersHistoryAdapter.ViewHolder> {

    public interface OnOrderClickListener {
        void onOrderClick(PedidoDto pedido);
    }

    private List<PedidoDto> listaPedidos;
    private OnOrderClickListener listener;

    public OrdersHistoryAdapter(List<PedidoDto> listaPedidos, OnOrderClickListener listener) {
        this.listaPedidos = listaPedidos;
        this.listener = listener;
    }

    public void updateList(List<PedidoDto> nuevaLista) {
        this.listaPedidos = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PedidoDto pedido = listaPedidos.get(position);

        // 1. Nombre o ID
        String nombreVisual = (pedido.getNombre() != null && !pedido.getNombre().isEmpty())
                ? pedido.getNombre()
                : (pedido.getId() != null && pedido.getId().length() >= 8 ? "Pedido #" + pedido.getId().substring(0, 8) : "Pedido S/N");
        
        holder.tvId.setText(nombreVisual);

        // 2. Fecha (Límite o Creación)
        String fechaVisual = (pedido.getFechaLimite() != null && !pedido.getFechaLimite().isEmpty())
                ? "Vence: " + pedido.getFechaLimite()
                : (pedido.getFechaPedido() != null ? pedido.getFechaPedido().split("T")[0] : "---");
        
        holder.tvFecha.setText(fechaVisual);

        // 3. Total
        holder.tvTotal.setText(String.format("$%.2f", pedido.getTotal() != null ? pedido.getTotal() : 0.0));

        // 4. Estado (1=Pendiente)
        boolean esPendiente = (pedido.getIdEstado() != null && pedido.getIdEstado() == 1);
        holder.tvEstado.setText(esPendiente ? "Pendiente" : "Completado");
        holder.tvEstado.setTextColor(esPendiente ? android.graphics.Color.parseColor("#E65100") : android.graphics.Color.parseColor("#2E7D32")); // Orange vs Green

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOrderClick(pedido);
        });
    }

    @Override
    public int getItemCount() {
        return listaPedidos != null ? listaPedidos.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvFecha, tvTotal, tvEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_history_id);
            tvFecha = itemView.findViewById(R.id.tv_history_date);
            tvTotal = itemView.findViewById(R.id.tv_history_total);
            tvEstado = itemView.findViewById(R.id.tv_history_status);
        }
    }
}