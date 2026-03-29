package com.example.pcstore

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat // Додано новий імпорт!
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProductAdapter(private var productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProduct: ImageView = itemView.findViewById(R.id.ivProduct)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val rbRating: RatingBar = itemView.findViewById(R.id.rbRating)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvOldPrice: TextView = itemView.findViewById(R.id.tvOldPrice)
        val btnAddToCart: MaterialCardView = itemView.findViewById(R.id.btnAddToCart)
        val ivFavorite: ImageView = itemView.findViewById(R.id.ivFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser?.uid

        holder.tvProductName.text = product.name
        holder.rbRating.rating = product.rating

        // Логіка ціни та знижок
        val discountPercent = DealManager.getDiscount(product.id)
        val originalPrice = product.price.toLong()
        var finalPrice: Long = originalPrice

        if (discountPercent != null) {
            finalPrice = originalPrice - (originalPrice * discountPercent / 100)
            holder.tvPrice.text = "$finalPrice ₴ (-$discountPercent%)"
            holder.tvPrice.setTextColor(Color.RED) // Залишаємо червоний для акцій

            holder.tvOldPrice.visibility = View.VISIBLE
            holder.tvOldPrice.text = "$originalPrice ₴"
            holder.tvOldPrice.paintFlags = holder.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.tvPrice.text = "$originalPrice ₴"
            // ВИПРАВЛЕНО ТУТ: Беремо колір text_primary, який адаптується до світлої/темної теми
            holder.tvPrice.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.text_primary))

            if (product.oldPrice != null) {
                holder.tvOldPrice.visibility = View.VISIBLE
                holder.tvOldPrice.text = "${product.oldPrice} ₴"
                holder.tvOldPrice.paintFlags = holder.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                holder.tvOldPrice.visibility = View.GONE
            }
        }

        if (product.imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context).load(product.imageUrl).into(holder.ivProduct)
        }

        if (uid != null) {
            val favRef = db.collection("users").document(uid).collection("wishlist").document(product.id)

            favRef.get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    holder.ivFavorite.setImageResource(R.drawable.ic_heart_filled)
                    holder.ivFavorite.tag = "filled"
                } else {
                    holder.ivFavorite.setImageResource(R.drawable.ic_heart_empty)
                    holder.ivFavorite.tag = "empty"
                }
            }

            holder.ivFavorite.setOnClickListener {
                if (holder.ivFavorite.tag == "empty") {
                    favRef.set(product).addOnSuccessListener {
                        holder.ivFavorite.setImageResource(R.drawable.ic_heart_filled)
                        holder.ivFavorite.tag = "filled"
                    }
                } else {
                    favRef.delete().addOnSuccessListener {
                        holder.ivFavorite.setImageResource(R.drawable.ic_heart_empty)
                        holder.ivFavorite.tag = "empty"
                    }
                }
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", product.id)
            holder.itemView.context.startActivity(intent)
        }

        holder.btnAddToCart.setOnClickListener {
            if (uid != null) {
                val cartItem = hashMapOf("name" to product.name, "price" to finalPrice)
                db.collection("users").document(uid).collection("cart").add(cartItem)
                    .addOnSuccessListener { Toast.makeText(holder.itemView.context, "Додано в кошик!", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    override fun getItemCount() = productList.size

    fun updateList(newList: List<Product>) {
        productList = newList
        notifyDataSetChanged()
    }

    fun getCurrentList() = productList
}