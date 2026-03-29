package com.example.pcstore

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val handler = Handler(Looper.getMainLooper())

    // Час оновлення акцій: 60 000 мс = 1 хвилина.
    private val updateInterval = 60000L

    private val updateDealsRunnable = object : Runnable {
        override fun run() {
            loadRandomDeals()
            handler.postDelayed(this, updateInterval)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ivCart = findViewById<ImageView>(R.id.ivCart)
        val ivThemeToggle = findViewById<ImageView>(R.id.ivThemeToggle)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        ivCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        ivThemeToggle.setOnClickListener {
            Toast.makeText(this, "Перемикач теми буде тут!", Toast.LENGTH_SHORT).show()
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { true }
                R.id.nav_system_units -> {
                    startActivity(Intent(this, SystemUnitsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_components -> {
                    startActivity(Intent(this, ComponentsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_peripherals -> {
                    startActivity(Intent(this, PeripheralsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handler.removeCallbacks(updateDealsRunnable)
        handler.post(updateDealsRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateDealsRunnable)
    }

    private fun loadRandomDeals() {
        db.collection("products").get().addOnSuccessListener { result ->
            val allProducts = result.documents.toMutableList()

            if (allProducts.size >= 2) {
                allProducts.shuffle()
                val deal1 = allProducts[0]
                val deal2 = allProducts[1]

                DealManager.currentDeals.clear()
                val discount1 = Random.nextInt(5, 16)
                val discount2 = Random.nextInt(5, 16)
                DealManager.currentDeals[deal1.id] = discount1
                DealManager.currentDeals[deal2.id] = discount2

                setupDealCard(
                    deal1, discount1,
                    findViewById(R.id.tvNameDeal1),
                    findViewById(R.id.tvOldPriceDeal1),
                    findViewById(R.id.tvNewPriceDeal1),
                    findViewById(R.id.ivDeal1),
                    findViewById(R.id.btnBuyDeal1)
                )

                setupDealCard(
                    deal2, discount2,
                    findViewById(R.id.tvNameDeal2),
                    findViewById(R.id.tvOldPriceDeal2),
                    findViewById(R.id.tvNewPriceDeal2),
                    findViewById(R.id.ivDeal2),
                    findViewById(R.id.btnBuyDeal2)
                )
            } else {
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Помилка завантаження акцій", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDealCard(
        doc: DocumentSnapshot,
        discountPercent: Int,
        tvName: TextView,
        tvOldPrice: TextView,
        tvNewPrice: TextView,
        ivCardImage: ImageView,
        btnBuy: Button
    ) {
        val name = doc.getString("name") ?: "Товар"

        val oldPrice = doc.get("price")?.toString()?.toLong() ?: 0L
        val imageUrl = doc.getString("imageUrl") ?: ""

        val newPrice = oldPrice - (oldPrice * discountPercent / 100)

        tvName.text = name
        tvOldPrice.text = "$oldPrice ₴"
        tvOldPrice.paintFlags = tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        tvNewPrice.text = "$newPrice ₴ (-$discountPercent%)"

        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_dialog_alert)
                .centerCrop()
                .into(ivCardImage)
        }

        ivCardImage.setOnClickListener {
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", doc.id)
            startActivity(intent)
        }

        btnBuy.setOnClickListener {
            addToCart(name, newPrice)
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
                Toast.makeText(this, " $name додано в кошик по акції!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Помилка додавання", Toast.LENGTH_SHORT).show()
            }
    }
}