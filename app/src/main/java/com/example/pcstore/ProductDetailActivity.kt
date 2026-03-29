package com.example.pcstore

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
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
    private var productPrice = 0L // Сюди запишеться або звичайна, або акційна ціна

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val ivImage = findViewById<ImageView>(R.id.ivProductImage)
        val tvName = findViewById<TextView>(R.id.tvProductName)
        val tvPrice = findViewById<TextView>(R.id.tvProductPrice)
        val tvOldPrice = findViewById<TextView>(R.id.tvOldPrice) // Наше нове поле
        val tvDescription = findViewById<TextView>(R.id.tvProductDescription)
        val tvSpecs = findViewById<TextView>(R.id.tvProductSpecs)
        val btnAddCart = findViewById<Button>(R.id.btnAddToCart)

        ivBack.setOnClickListener { finish() }

        val productId = intent.getStringExtra("PRODUCT_ID")

        if (productId != null) {
            loadProductDetails(productId, ivImage, tvName, tvPrice, tvOldPrice, tvDescription, tvSpecs)
        } else {
            Toast.makeText(this, "Помилка: Товар не знайдено", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnAddCart.setOnClickListener {
            addToCart(productName, productPrice)
        }
    }

    private fun loadProductDetails(
        id: String, ivImage: ImageView, tvName: TextView,
        tvPrice: TextView, tvOldPrice: TextView, tvDesc: TextView, tvSpecs: TextView
    ) {
        db.collection("products").document(id).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    productName = doc.getString("name") ?: "Невідомий товар"
                    val basePrice = doc.getLong("price") ?: 0L
                    val dbOldPrice = doc.getLong("oldPrice")

                    val imageUrl = doc.getString("imageUrl") ?: ""
                    val description = doc.getString("description") ?: "Опис відсутній."
                    val specs = doc.getString("specs") ?: "Характеристики відсутні."

                    tvName.text = productName
                    tvDesc.text = description
                    tvSpecs.text = specs

                    // 🔴 ПЕРЕВІРЯЄМО РАНДОМНУ ЗНИЖКУ 🔴
                    val discountPercent = DealManager.getDiscount(id)

                    if (discountPercent != null) {
                        // Товар в акції!
                        productPrice = DealManager.calculateNewPrice(basePrice, discountPercent)
                        tvPrice.text = "$productPrice ₴ (-$discountPercent%)"
                        tvPrice.setTextColor(Color.RED)

                        tvOldPrice.visibility = View.VISIBLE
                        tvOldPrice.text = "$basePrice ₴"
                        tvOldPrice.paintFlags = tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        // Звичайна ціна
                        productPrice = basePrice
                        tvPrice.text = "$productPrice ₴"
                        tvPrice.setTextColor(Color.BLACK)

                        // Якщо є стара ціна в базі (стаціонарна знижка)
                        if (dbOldPrice != null) {
                            tvOldPrice.visibility = View.VISIBLE
                            tvOldPrice.text = "$dbOldPrice ₴"
                            tvOldPrice.paintFlags = tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        } else {
                            tvOldPrice.visibility = View.GONE
                        }
                    }

                    // Завантажуємо фото
                    if (imageUrl.isNotEmpty()) {
                        Glide.with(this).load(imageUrl).into(ivImage)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Помилка завантаження", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addToCart(name: String, price: Long) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "Увійдіть в акаунт!", Toast.LENGTH_SHORT).show()
            return
        }

        val cartItem = hashMapOf(
            "name" to name,
            "price" to price, // Тут вже лежить правильна ціна (звичайна або зі знижкою)
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users").document(uid).collection("cart")
            .add(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "🎉 $name додано в кошик!", Toast.LENGTH_SHORT).show()
            }
    }
}