package com.example.gemainventory.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gemainventory.R
import com.example.gemainventory.model.NotificationItem

class NotificationsAdapter(private var list: List<NotificationItem>) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_notification_title)
        val desc: TextView = itemView.findViewById(R.id.tv_notification_desc)
        val time: TextView = itemView.findViewById(R.id.tv_notification_time)
        val icon: ImageView = itemView.findViewById(R.id.iv_notification_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.title.text = item.title
        holder.desc.text = item.description
        holder.time.text = item.timestamp
        holder.icon.setImageResource(item.iconResId)

        // Colores dinámicos según el tipo
        val (colorHex, bgHex) = if (item.type == com.example.gemainventory.model.NotificationType.ORDER) {
            "#22C55E" to "#1A22C55E" // Verde esmeralda (10% alfa para el fondo)
        } else {
            "#3B82F6" to "#1A3B82F6" // Azul brillante (10% alfa para el fondo)
        }

        holder.icon.setColorFilter(android.graphics.Color.parseColor(colorHex))
        
        val iconBg = holder.itemView.findViewById<android.view.View>(R.id.iv_notification_bg)
        iconBg?.background?.setTint(android.graphics.Color.parseColor(bgHex))
    }

    override fun getItemCount(): Int = list.size

    fun updateList(newList: List<NotificationItem>) {
        list = newList
        notifyDataSetChanged()
    }
}
