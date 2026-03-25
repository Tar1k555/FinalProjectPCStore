package com.example.pcstore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val ivBackCart = findViewById<ImageView>(R.id.ivBackCart)

        val btnContinueShopping = findViewById<Button>(R.id.btnContinueShopping)
        val btnViewOrders = findViewById<Button>(R.id.btnViewOrders)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        ivBackCart.setOnClickListener {
            finish()
        }

        btnContinueShopping.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Очищаємо стек екранів
            startActivity(intent)
            finish()
        }

        btnViewOrders.setOnClickListener {
            Toast.makeText(this, "Історія замовлень в розробці", Toast.LENGTH_SHORT).show()
        }


        bottomNav.selectedItemId = -1

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> {
                    Toast.makeText(this, "Цей розділ в розробці", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }
    }
}