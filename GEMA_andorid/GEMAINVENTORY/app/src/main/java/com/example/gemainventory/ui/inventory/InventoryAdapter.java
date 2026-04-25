package com.example.gemainventory.ui.inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gemainventory.R;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ProductViewHolder> {

    private final List<Product> productList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public InventoryAdapter(List<Product> productList, OnItemClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void filterList(List<Product> filteredList) {
        this.productList.clear();
        this.productList.addAll(filteredList);
        notifyDataSetChanged();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView productName, productSku, productSkuBox;
        TextView productStatusLabel;
        ImageView productThumbnail;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productSku = itemView.findViewById(R.id.product_sku);
            productSkuBox = itemView.findViewById(R.id.product_sku_box);
            productStatusLabel = itemView.findViewById(R.id.product_status_label);
            productThumbnail = itemView.findViewById(R.id.iv_product_thumbnail);
        }

        public void bind(final Product product, final OnItemClickListener listener) {
            productName.setText(product.getName());
            productSku.setText("SKU: " + product.getSku());
            productSkuBox.setText(product.getSku());
            itemView.setOnClickListener(v -> listener.onItemClick(product));

            Product.StockStatus status = product.getStatus();
            Context context = itemView.getContext();

            if (status == Product.StockStatus.OUT_OF_STOCK) {
                productStatusLabel.setText("Sin Stock");
                productStatusLabel.setBackgroundResource(R.drawable.label_bg_red);
                productStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.red_text));

            } else if (status == Product.StockStatus.LOW_STOCK) {
                productStatusLabel.setText("Stock Bajo (" + product.getQuantity() + ")");
                productStatusLabel.setBackgroundResource(R.drawable.label_bg_amber);
                productStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.amber_text));

            } else { // IN_STOCK
                productStatusLabel.setText("En Stock (" + product.getQuantity() + ")");
                productStatusLabel.setBackgroundResource(R.drawable.label_bg_green);
                productStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.green_text));
            }

            // Load Image
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                productThumbnail.setVisibility(View.VISIBLE);
                productSkuBox.setVisibility(View.GONE);
                Glide.with(context)
                    .load(com.example.gemainventory.api.RetrofitClient.getFullImageUrl(product.getImageUrl()))
                    .centerCrop()
                    .into(productThumbnail);
            } else {
                productThumbnail.setVisibility(View.GONE);
                productSkuBox.setVisibility(View.VISIBLE);
            }
        }
    }
}