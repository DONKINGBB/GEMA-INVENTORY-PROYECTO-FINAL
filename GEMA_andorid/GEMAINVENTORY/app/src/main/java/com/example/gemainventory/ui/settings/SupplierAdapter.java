package com.example.gemainventory.ui.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gemainventory.R;
import com.example.gemainventory.model.ProveedorDto;
import java.util.List;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ProveedorDto proveedor);
    }

    private List<ProveedorDto> proveedores;
    private OnItemClickListener listener;

    public SupplierAdapter(List<ProveedorDto> proveedores, OnItemClickListener listener) {
        this.proveedores = proveedores;
        this.listener = listener;
    }

    public void updateList(List<ProveedorDto> newList) {
        this.proveedores = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supplier_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProveedorDto proveedor = proveedores.get(position);
        holder.name.setText(proveedor.getNombre());
        holder.contact.setText(proveedor.getContacto() != null ? proveedor.getContacto() : "Sin contacto");
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(proveedor);
        });
    }

    @Override
    public int getItemCount() { return proveedores.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, contact;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_supplier_name);
            contact = itemView.findViewById(R.id.tv_supplier_contact);
        }
    }
}
