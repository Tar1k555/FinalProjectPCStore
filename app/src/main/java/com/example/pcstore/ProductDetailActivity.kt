package com.example.pcstore
import android.os.Bundle
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
    private var productPrice = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val ivImage = findViewById<ImageView>(R.id.ivProductImage)
        val tvName = findViewById<TextView>(R.id.tvProductName)
        val tvPrice = findViewById<TextView>(R.id.tvProductPrice)
        val tvDescription = findViewById<TextView>(R.id.tvProductDescription)
        val tvSpecs = findViewById<TextView>(R.id.tvProductSpecs)
        val btnAddCart = findViewById<Button>(R.id.btnAddToCart)

        ivBack.setOnClickListener { finish() }

        val productId = intent.getStringExtra("PRODUCT_ID")

        if (productId != null) {
            loadProductDetails(productId, ivImage, tvName, tvPrice, tvDescription, tvSpecs)
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
        tvPrice: TextView, tvDesc: TextView, tvSpecs: TextView
    ) {
        db.collection("products").document(id).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    productName = doc.getString("name") ?: "Невідомий товар"
                    productPrice = doc.getLong("price") ?: 0L

                    val imageUrl = doc.getString("imageUrl") ?: ""
                    val description = doc.getString("description") ?: "Опис відсутній."
                    val specs = doc.getString("specs") ?: "Характеристики відсутні."

                    tvName.text = productName
                    tvPrice.text = "$productPrice ₴"
                    tvDesc.text = description
                    tvSpecs.text = specs

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
            "price" to price,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users").document(uid).collection("cart")
            .add(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "🎉 $name додано в кошик!", Toast.LENGTH_SHORT).show()
            }
    }
}