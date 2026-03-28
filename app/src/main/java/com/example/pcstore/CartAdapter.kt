package com.example.pcstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Модель даних (має збігатися з полями в Firebase: name, price)
data class CartItem(
    var id: String = "", // ID документа в Firebase
    val name: String = "",
    val price: Long = 0
)

class CartAdapter(
    private var cartItems: MutableList<CartItem>,
    private val onDeleteClick: (CartItem) -> Unit // Функція, яку ми викличемо в Activity
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCartItemName)
        val tvPrice: TextView = view.findViewById(R.id.tvCartItemPrice)
        val ivDelete: ImageView = view.findViewById(R.id.ivDeleteCartItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.tvName.text = item.name
        holder.tvPrice.text = "${item.price} ₴"

        // Обробка кліку на видалення
        holder.ivDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount() = cartItems.size

    fun updateList(newList: List<CartItem>) {
        cartItems.clear()
        cartItems.addAll(newList)
        notifyDataSetChanged()
    }
}