package com.example.gemainventory.ui.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gemainventory.R;
import com.example.gemainventory.model.AlmacenDto;
import java.util.List;

public class WarehouseAdapter extends RecyclerView.Adapter<WarehouseAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(AlmacenDto almacen);
    }

    private List<AlmacenDto> almacenes;
    private OnItemClickListener listener;

    public WarehouseAdapter(List<AlmacenDto> almacenes, OnItemClickListener listener) {
        this.almacenes = almacenes;
        this.listener = listener;
    }

    public void updateList(List<AlmacenDto> newList) {
        this.almacenes = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_warehouse_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlmacenDto almacen = almacenes.get(position);
        holder.name.setText(almacen.getNombre());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(almacen);
        });
    }

    @Override
    public int getItemCount() { return almacenes.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_warehouse_name);
        }
    }
}