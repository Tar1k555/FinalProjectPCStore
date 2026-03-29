package com.example.pcstore

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProductDetailActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var productName = ""
    private var productPrice = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val ivImage = findViewById<ImageView>(R.id.ivProductImage)
        val tvName = findViewById<TextView>(R.id.tvProductName)
        val tvPrice = findViewById<TextView>(R.id.tvProductPrice)
        val tvOldPrice = findViewById<TextView>(R.id.tvOldPrice)
        val tvDescription = findViewById<TextView>(R.id.tvProductDescription)
        val tvSpecs = findViewById<TextView>(R.id.tvProductSpecs)
        val btnAddCart = findViewById<Button>(R.id.btnAddToCart)

        ivBack.setOnClickListener { finish() }

        val productId = intent.getStringExtra("PRODUCT_ID")
        if (productId != null) {
            loadProductDetails(productId, ivImage, tvName, tvPrice, tvOldPrice, tvDescription, tvSpecs)
        } else {
            finish()
        }

        btnAddCart.setOnClickListener { addToCart(productName, productPrice) }
    }

    private fun getCategoryPath(category: String?, subCategory: String?): String {
        val cat = when (category) {
            "peripherals" -> "Периферія"
            "components" -> "Комплектуючі"
            "gaming_pcs" -> "Системні блоки"
            "work_pcs" -> "Системні блоки"
            else -> "Каталог"
        }

        val sub = when (subCategory) {
            "monitors" -> "Монітори"
            "mouses" -> "Комп’ютерні мишки"
            "cpus" -> "Процесори"
            "gpus" -> "Відеокарти"
            "workstations" -> "Робочі станції"
            "gamingstations" -> "Ігрові ПК"
            else -> "Товари"
        }

        return "$cat ➔ $sub"
    }

    private fun loadProductDetails(
        id: String, ivImage: ImageView, tvName: TextView,
        tvPrice: TextView, tvOldPrice: TextView, tvDesc: TextView, tvSpecs: TextView
    ) {
        db.collection("products").document(id).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    productName = doc.getString("name") ?: "Товар"
                    val basePrice = doc.getLong("price") ?: 0L
                    val dbOldPrice = doc.get("oldPrice")?.toString()?.toLongOrNull()
                    val rating = doc.getDouble("rating") ?: 0.0

                    val category = doc.getString("category")
                    val subCategory = doc.getString("subCategory") // Нове поле
                    val displayId = doc.get("id")?.toString() ?: "0"

                    val imageUrl = doc.getString("imageUrl") ?: ""
                    tvDesc.text = doc.getString("description") ?: "Опис відсутній."
                    tvSpecs.text = doc.getString("specs") ?: "Характеристики відсутні."
                    tvName.text = productName

                    findViewById<TextView>(R.id.tvCategoryPath).text = getCategoryPath(category, subCategory)

                    findViewById<TextView>(R.id.tvProductId).text = "ID: $displayId"
                    findViewById<RatingBar>(R.id.rbProductRating).rating = rating.toFloat()
                    findViewById<TextView>(R.id.tvProductRatingValue).text = rating.toString()

                    val discountPercent = DealManager.getDiscount(id)

                    if (discountPercent != null) {
                        // Переконайся, що DealManager приймає Long
                        productPrice = DealManager.calculateNewPrice(basePrice, discountPercent)
                        tvPrice.text = "$productPrice ₴ (-$discountPercent%)"
                        tvPrice.setTextColor(Color.RED)

                        tvOldPrice.visibility = View.VISIBLE
                        tvOldPrice.text = "$basePrice ₴"
                        tvOldPrice.paintFlags = tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        productPrice = basePrice
                        tvPrice.text = "$productPrice ₴"
                        tvPrice.setTextColor(Color.BLACK)

                        if (dbOldPrice != null) {
                            tvOldPrice.visibility = View.VISIBLE
                            tvOldPrice.text = "$dbOldPrice ₴"
                            tvOldPrice.paintFlags = tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        } else {
                            tvOldPrice.visibility = View.GONE
                        }
                    }

                    if (imageUrl.isNotEmpty()) {
                        Glide.with(this).load(imageUrl).into(ivImage)
                    }
                }
            }
    }

    private fun addToCart(name: String, price: Long) {
        val uid = auth.currentUser?.uid ?: return
        val cartItem = hashMapOf("name" to name, "price" to price, "timestamp" to System.currentTimeMillis())
        db.collection("users").document(uid).collection("cart").add(cartItem)
            .addOnSuccessListener { Toast.makeText(this, "🎉 Додано!", Toast.LENGTH_SHORT).show() }
    }
}