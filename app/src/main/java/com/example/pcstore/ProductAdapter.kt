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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.tvProductName.text = product.name
        holder.rbRating.rating = product.rating

        val discountPercent = DealManager.getDiscount(product.id)

        val originalPrice = product.price.toString().toLong()
        var finalPrice: Long = originalPrice

        if (discountPercent != null) {
            finalPrice = originalPrice - (originalPrice * discountPercent / 100)

            holder.tvPrice.text = "$finalPrice ₴ (-$discountPercent%)"
            holder.tvPrice.setTextColor(Color.RED)

            holder.tvOldPrice.visibility = View.VISIBLE
            holder.tvOldPrice.text = "$originalPrice ₴"
            holder.tvOldPrice.paintFlags = holder.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.tvPrice.text = "$originalPrice ₴"
            holder.tvPrice.setTextColor(Color.BLACK)

            if (product.oldPrice != null) {
                holder.tvOldPrice.visibility = View.VISIBLE
                holder.tvOldPrice.text = "${product.oldPrice} ₴"
                holder.tvOldPrice.paintFlags = holder.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                holder.tvOldPrice.visibility = View.GONE
            }
        }

        if (product.imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(product.imageUrl)
                .into(holder.ivProduct)
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", product.id)
            context.startActivity(intent)
        }

        holder.btnAddToCart.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()
            val uid = auth.currentUser?.uid
            val context = holder.itemView.context

            if (uid != null) {
                val cartItem = hashMapOf(
                    "name" to product.name,
                    "price" to finalPrice // Додаємо акційну ціну, якщо є знижка!
                )

                db.collection("users").document(uid).collection("cart")
                    .add(cartItem)
                    .addOnSuccessListener {
                        Toast.makeText(context, "${product.name} додано в кошик!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Помилка: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Будь ласка, авторизуйтесь", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun updateList(newList: List<Product>) {
        productList = newList
        notifyDataSetChanged()
    }

    fun getCurrentList(): List<Product> {
        return productList
    }
}