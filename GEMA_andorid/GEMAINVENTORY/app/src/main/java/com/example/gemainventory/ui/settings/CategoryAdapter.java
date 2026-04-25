package com.example.gemainventory.ui.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gemainventory.R;
import com.example.gemainventory.model.CategoriaDto;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(CategoriaDto categoria);
    }

    private List<CategoriaDto> categorias;
    private OnItemClickListener listener;

    public CategoryAdapter(List<CategoriaDto> categorias, OnItemClickListener listener) {
        this.categorias = categorias;
        this.listener = listener;
    }

    public void updateList(List<CategoriaDto> newList) {
        this.categorias = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoriaDto cat = categorias.get(position);
        holder.name.setText(cat.getNombre());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(cat);
        });
    }

    @Override
    public int getItemCount() { return categorias.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_category_name);
        }
    }
}