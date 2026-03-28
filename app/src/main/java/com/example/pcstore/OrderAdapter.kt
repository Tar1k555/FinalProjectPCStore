package com.example.pcstore

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter(private var orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvProducts: TextView = view.findViewById(R.id.tvOrderProducts)
        val tvPrice: TextView = view.findViewById(R.id.tvOrderPrice)
        val tvStatus: TextView = view.findViewById(R.id.tvOrderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        // Показуємо тільки останні 6 символів ID, щоб було красиво
        holder.tvOrderId.text = "Замовлення #${order.orderId.takeLast(6).uppercase()}"
        holder.tvProducts.text = order.productNames
        holder.tvPrice.text = "Сума: ${order.totalPrice} ₴"

        // Перетворюємо мілісекунди в красиву дату
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        holder.tvDate.text = sdf.format(Date(order.timestamp))

        // МАГІЯ СТАТУСІВ: рахуємо різницю в часі
        val diffMinutes = (System.currentTimeMillis() - order.timestamp) / 60000

        when {
            diffMinutes < 1 -> {
                holder.tvStatus.text = "Комплектується 📦"
                holder.tvStatus.setTextColor(Color.parseColor("#FFA500")) // Оранжевий
            }
            diffMinutes in 1..5 -> {
                holder.tvStatus.text = "Доставляється 🚚"
                holder.tvStatus.setTextColor(Color.parseColor("#1E90FF")) // Синій
            }
            diffMinutes in 6..7 -> {
                holder.tvStatus.text = "У магазині 🏪"
                holder.tvStatus.setTextColor(Color.parseColor("#32CD32")) // Зелений
            }
            else -> {
                holder.tvStatus.text = "Отримано ✅"
                holder.tvStatus.setTextColor(Color.parseColor("#888888")) // Сірий
            }
        }
    }

    override fun getItemCount() = orders.size

    fun updateList(newList: List<Order>) {
        orders = newList
        notifyDataSetChanged()
    }
}