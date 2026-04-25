package com.example.gemainventory.ui.inventory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.gemainventory.R;

public class ProductDetailFragment extends Fragment {

    private int receivedProductId = -1;
    private TextView productTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView backButton = view.findViewById(R.id.back_button);
        Button editButton = view.findViewById(R.id.edit_button);
        Button deleteButton = view.findViewById(R.id.delete_button);
        productTitle = view.findViewById(R.id.product_detail_title);

        if (getArguments() != null) {
            receivedProductId = getArguments().getInt("productId", -1);
        }

        if (receivedProductId != -1) {
            productTitle.setText("Detalle Producto " + receivedProductId);
            Toast.makeText(getContext(), "Mostrando detalle de ID " + receivedProductId, Toast.LENGTH_SHORT).show();
        }

        backButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        editButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("productId", receivedProductId);

            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_detail_to_edit_form, bundle);
        });

        deleteButton.setOnClickListener(v -> {
            showDeleteDialog();
        });
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Producto")
                .setMessage("Esta acción es permanente. ¿Estás seguro?")
                .setPositiveButton("Sí, Eliminar", (dialog, which) -> {
                    Toast.makeText(getContext(), "Producto eliminado (prototipo)", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(this).popBackStack();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
}