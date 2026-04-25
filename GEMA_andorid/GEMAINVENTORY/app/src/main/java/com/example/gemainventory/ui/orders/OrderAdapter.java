package com.example.gemainventory.ui.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gemainventory.R;
import com.example.gemainventory.model.DetallePedidoDto;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final List<DetallePedidoDto> productos;
    private final Runnable onListUpdate; // Callback para recalcular el total al borrar

    public OrderAdapter(List<DetallePedidoDto> productos, Runnable onListUpdate) {
        this.productos = productos;
        this.onListUpdate = onListUpdate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_product_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetallePedidoDto item = productos.get(position);

        holder.tvNombre.setText(item.getNombreProducto() != null ? item.getNombreProducto() : "Producto");
        holder.tvCantidad.setText("x" + item.getCantidad());
        holder.tvPrecio.setText(String.format("$%.2f", item.getPrecioUnitario()));
        holder.tvSubtotal.setText(String.format("$%.2f", item.getSubtotal()));

        // Botón Eliminar
        holder.btnDelete.setOnClickListener(v -> {
            productos.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, productos.size());
            if (onListUpdate != null) onListUpdate.run();
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCantidad, tvPrecio, tvSubtotal;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_row_product_name);
            tvCantidad = itemView.findViewById(R.id.tv_row_quantity);
            tvPrecio = itemView.findViewById(R.id.tv_row_price);
            tvSubtotal = itemView.findViewById(R.id.tv_row_subtotal);
            btnDelete = itemView.findViewById(R.id.btn_row_delete);
        }
    }
}