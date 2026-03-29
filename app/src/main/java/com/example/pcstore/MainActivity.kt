package com.example.pcstore

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
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
    private val updateInterval = 60000L // 60 секунд

    companion object {
        var lastUpdateTime = 0L
    }

    private val updateDealsRunnable = object : Runnable {
        override fun run() {
            loadRandomDeals()
            handler.postDelayed(this, updateInterval)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPrefs = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        val isDarkTheme = sharedPrefs.getBoolean("isDark", false)

        if (isDarkTheme) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ivCart = findViewById<ImageView>(R.id.ivCart)
        val ivThemeToggle = findViewById<ImageView>(R.id.ivThemeToggle)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // --- Встановлюємо правильну іконку при старті ---
        if (isDarkTheme) {
            ivThemeToggle.setImageResource(R.drawable.ic_sun)
        } else {
            ivThemeToggle.setImageResource(R.drawable.ic_moon)
        }

        ivCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        // --- Логіка зміни теми ТА ІКОНКИ ---
        ivThemeToggle.setOnClickListener {
            val currentNightMode = sharedPrefs.getBoolean("isDark", false)
            val newNightMode = !currentNightMode

            sharedPrefs.edit().putBoolean("isDark", newNightMode).apply()

            if (newNightMode) {
                ivThemeToggle.setImageResource(R.drawable.ic_sun) // Змінюємо на сонце
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                ivThemeToggle.setImageResource(R.drawable.ic_moon) // Змінюємо на місяць
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
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
                val currentTime = System.currentTimeMillis()
                val deal1: DocumentSnapshot
                val deal2: DocumentSnapshot
                val discount1: Int
                val discount2: Int

                if (currentTime - lastUpdateTime >= updateInterval || DealManager.currentDeals.size < 2) {
                    allProducts.shuffle()
                    deal1 = allProducts[0]
                    deal2 = allProducts[1]

                    discount1 = Random.nextInt(5, 16)
                    discount2 = Random.nextInt(5, 16)

                    DealManager.currentDeals.clear()
                    DealManager.currentDeals[deal1.id] = discount1
                    DealManager.currentDeals[deal2.id] = discount2

                    lastUpdateTime = currentTime
                } else {
                    val dealIds = DealManager.currentDeals.keys.toList()
                    val savedDeal1 = allProducts.firstOrNull { it.id == dealIds[0] }
                    val savedDeal2 = allProducts.firstOrNull { it.id == dealIds[1] }

                    if (savedDeal1 != null && savedDeal2 != null) {
                        deal1 = savedDeal1
                        deal2 = savedDeal2
                        discount1 = DealManager.currentDeals[dealIds[0]] ?: 0
                        discount2 = DealManager.currentDeals[dealIds[1]] ?: 0
                    } else {
                        allProducts.shuffle()
                        deal1 = allProducts[0]
                        deal2 = allProducts[1]
                        discount1 = Random.nextInt(5, 16)
                        discount2 = Random.nextInt(5, 16)
                        DealManager.currentDeals.clear()
                        DealManager.currentDeals[deal1.id] = discount1
                        DealManager.currentDeals[deal2.id] = discount2
                        lastUpdateTime = currentTime
                    }
                }

                setupDealCard(deal1, discount1, findViewById(R.id.tvNameDeal1), findViewById(R.id.tvOldPriceDeal1), findViewById(R.id.tvNewPriceDeal1), findViewById(R.id.ivDeal1), findViewById(R.id.btnBuyDeal1))
                setupDealCard(deal2, discount2, findViewById(R.id.tvNameDeal2), findViewById(R.id.tvOldPriceDeal2), findViewById(R.id.tvNewPriceDeal2), findViewById(R.id.ivDeal2), findViewById(R.id.btnBuyDeal2))
            }
        }
    }

    private fun setupDealCard(doc: DocumentSnapshot, discountPercent: Int, tvName: TextView, tvOldPrice: TextView, tvNewPrice: TextView, ivCardImage: ImageView, btnBuy: Button) {
        val name = doc.getString("name") ?: "Товар"
        val oldPrice = doc.getLong("price") ?: 0L
        val imageUrl = doc.getString("imageUrl") ?: ""
        val rating = doc.getDouble("rating") ?: 0.0
        val productId = doc.id
        val uid = auth.currentUser?.uid

        val rb = if (tvName.id == R.id.tvNameDeal1) findViewById<RatingBar>(R.id.rbRatingDeal1) else findViewById<RatingBar>(R.id.rbRatingDeal2)
        val tvR = if (tvName.id == R.id.tvNameDeal1) findViewById<TextView>(R.id.tvRatingValueDeal1) else findViewById<TextView>(R.id.tvRatingValueDeal2)
        val ivFav = if (tvName.id == R.id.tvNameDeal1) findViewById<ImageView>(R.id.ivFavDeal1) else findViewById<ImageView>(R.id.ivFavDeal2)

        val newPrice = oldPrice - (oldPrice * discountPercent / 100)

        tvName.text = name
        tvOldPrice.text = "$oldPrice ₴"
        tvOldPrice.paintFlags = tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        tvNewPrice.text = "$newPrice ₴ (-$discountPercent%)"
        rb?.rating = rating.toFloat()
        tvR?.text = rating.toString()

        if (imageUrl.isNotEmpty()) {
            Glide.with(this).load(imageUrl).centerCrop().into(ivCardImage)
        }

        if (uid != null && ivFav != null) {
            val favRef = db.collection("users").document(uid).collection("wishlist").document(productId)
            favRef.get().addOnSuccessListener { d ->
                if (d.exists()) {
                    ivFav.setImageResource(R.drawable.ic_heart_filled)
                    ivFav.tag = "filled"
                } else {
                    ivFav.setImageResource(R.drawable.ic_heart_empty)
                    ivFav.tag = "empty"
                }
            }
            ivFav.setOnClickListener {
                if (ivFav.tag == "empty") {
                    val product = doc.toObject(Product::class.java)
                    product?.id = productId
                    if (product != null) {
                        favRef.set(product).addOnSuccessListener {
                            ivFav.setImageResource(R.drawable.ic_heart_filled)
                            ivFav.tag = "filled"
                        }
                    }
                } else {
                    favRef.delete().addOnSuccessListener {
                        ivFav.setImageResource(R.drawable.ic_heart_empty)
                        ivFav.tag = "empty"
                    }
                }
            }
        }

        ivCardImage.setOnClickListener {
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", productId)
            startActivity(intent)
        }

        btnBuy.setOnClickListener { addToCart(name, newPrice) }
    }

    private fun addToCart(name: String, price: Long) {
        val uid = auth.currentUser?.uid ?: return
        val cartItem = hashMapOf("name" to name, "price" to price, "timestamp" to System.currentTimeMillis())
        db.collection("users").document(uid).collection("cart").add(cartItem)
            .addOnSuccessListener { Toast.makeText(this, "🎉 $name додано!", Toast.LENGTH_SHORT).show() }
    }
}