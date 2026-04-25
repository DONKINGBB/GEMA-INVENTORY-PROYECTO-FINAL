package com.example.gemainventory.ui.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gemainventory.R;
import com.example.gemainventory.model.ClienteDto;
import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ClienteDto cliente);
    }

    private List<ClienteDto> clientes;
    private OnItemClickListener listener;

    public ClientAdapter(List<ClienteDto> clientes, OnItemClickListener listener) {
        this.clientes = clientes;
        this.listener = listener;
    }

    public void updateList(List<ClienteDto> newList) {
        this.clientes = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClienteDto cliente = clientes.get(position);
        holder.name.setText(cliente.getNombre());
        holder.contact.setText(cliente.getContacto());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(cliente);
        });
    }

    @Override
    public int getItemCount() { return clientes.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, contact;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_client_name);
            contact = itemView.findViewById(R.id.tv_client_contact);
        }
    }
}