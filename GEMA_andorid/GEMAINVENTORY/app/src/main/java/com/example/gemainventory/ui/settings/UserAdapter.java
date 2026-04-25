package com.example.gemainventory.ui.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gemainventory.R;
import com.example.gemainventory.api.RetrofitClient;
import com.example.gemainventory.model.Usuario;
import com.bumptech.glide.Glide;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<Usuario> userList;
    private final String currentUserId;
    private final Integer currentUserIdRol;
    private final OnUserClickListener listener;
    private final OnDeleteClickListener deleteListener;

    public interface OnUserClickListener {
        void onUserClick(Usuario user);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Usuario user);
    }

    public UserAdapter(List<Usuario> userList, String currentUserId, Integer currentUserIdRol, 
                       OnUserClickListener listener, OnDeleteClickListener deleteListener) {
        this.userList = userList;
        this.currentUserId = currentUserId;
        this.currentUserIdRol = currentUserIdRol;
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Usuario user = userList.get(position);
        holder.tvName.setText(user.getNombre());
        holder.tvRole.setText(getRoleName(user.getIdRol()));
        holder.tvEmail.setText(user.getCorreo());
        
        // --- Aplicar estilo de color según el Rol ---
        applyRoleStyle(holder, user.getIdRol());

        // --- Mostrar corona solo si es PROPIETARIO (Rol 1) ---
        if (user.getIdRol() != null && user.getIdRol() == 1) {
            holder.ivCrown.setVisibility(View.VISIBLE);
        } else {
            holder.ivCrown.setVisibility(View.GONE);
        }

        // --- LOGICA DE ELIMINACIÓN (Jerarquía) ---
        // No puedes eliminarte a ti mismo.
        // Si eres Propietario (Rol 1), puedes eliminar a cualquiera (excepto a ti mismo).
        // Si no, solo puedes eliminar roles estrictamente menores al tuyo (ID de rol mayor).
        boolean canDelete = currentUserIdRol != null && user.getIdRol() != null &&
                           !user.getIdUsuario().equals(currentUserId) &&
                           (currentUserIdRol == 1 || currentUserIdRol < user.getIdRol());

        if (canDelete) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.ivChevron.setVisibility(View.GONE);
            holder.btnDelete.setOnClickListener(v -> deleteListener.onDeleteClick(user));
        } else {
            holder.btnDelete.setVisibility(View.GONE);
            holder.ivChevron.setVisibility(View.VISIBLE);
        }

        // --- Carga de foto de perfil ---
        String photoUrl = RetrofitClient.getFullImageUrl(user.getImagenUrl());
        
        Glide.with(holder.itemView.getContext())
                .load(photoUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .into(holder.ivAvatar);

        holder.itemView.setOnClickListener(v -> listener.onUserClick(user));
    }

    private void applyRoleStyle(UserViewHolder holder, Integer idRol) {
        if (idRol == null) return;

        int backgroundRes;
        int textColor;

        switch (idRol) {
            case 1: // PROPIETARIO
                backgroundRes = R.drawable.label_bg_role_1;
                textColor = holder.itemView.getContext().getColor(R.color.white);
                break;
            case 2: // ADMINISTRADOR
                backgroundRes = R.drawable.label_bg_role_2;
                textColor = holder.itemView.getContext().getColor(R.color.white);
                break;
            case 3: // SUPERVISOR
                backgroundRes = R.drawable.label_bg_role_3;
                textColor = holder.itemView.getContext().getColor(R.color.white);
                break;
            default: // OTROS (4, 5, 6)
                backgroundRes = R.drawable.label_bg_role_base;
                textColor = holder.itemView.getContext().getColor(R.color.black);
                break;
        }

        holder.tvRole.setBackgroundResource(backgroundRes);
        holder.tvRole.setTextColor(textColor);
    }

    private String getRoleName(Integer idRol) {
        if (idRol == null)
            return "Sin Rol";
        switch (idRol) {
            case 1:
                return "PROPIETARIO";
            case 2:
                return "ADMINISTRADOR";
            case 3:
                return "SUPERVISOR";
            case 4:
                return "VENDEDOR";
            case 5:
                return "REPARTIDOR";
            case 6:
                return "ALMACENISTA";
            default:
                return "Usuario";
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRole, tvEmail;
        ImageView ivAvatar, ivCrown, btnDelete, ivChevron;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_user_name);
            tvRole = itemView.findViewById(R.id.tv_user_role);
            tvEmail = itemView.findViewById(R.id.tv_user_email);
            ivAvatar = itemView.findViewById(R.id.iv_user_avatar);
            ivCrown = itemView.findViewById(R.id.iv_crown);
            btnDelete = itemView.findViewById(R.id.btn_delete_user);
            ivChevron = itemView.findViewById(R.id.iv_chevron);
        }
    }
}
