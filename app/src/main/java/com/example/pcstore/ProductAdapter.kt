package com.example.pcstore

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView

class ProductAdapter(private val productList: List<Product>) :
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
        holder.tvPrice.text = "${product.price} ₴"
        holder.rbRating.rating = product.rating

        if (product.oldPrice != null) {
            holder.tvOldPrice.visibility = View.VISIBLE
            holder.tvOldPrice.text = "${product.oldPrice} ₴"
            holder.tvOldPrice.paintFlags = holder.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.tvOldPrice.visibility = View.GONE
        }

        if (product.imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(product.imageUrl)
                .into(holder.ivProduct)
        }

        holder.btnAddToCart.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}