package com.example.pcstore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ivCart = findViewById<ImageView>(R.id.ivCart)
        val ivThemeToggle = findViewById<ImageView>(R.id.ivThemeToggle)

        val btnBuy1 = findViewById<Button>(R.id.btnBuy1)
        val btnBuy2 = findViewById<Button>(R.id.btnBuy2)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        ivCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        ivThemeToggle.setOnClickListener {
            Toast.makeText(this, "Перемикач теми буде тут!", Toast.LENGTH_SHORT).show()
        }

        btnBuy1.setOnClickListener {
            Toast.makeText(this, "ПК додано в кошик!", Toast.LENGTH_SHORT).show()
        }
        btnBuy2.setOnClickListener {
            Toast.makeText(this, "Мишку додано в кошик!", Toast.LENGTH_SHORT).show()
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Ви вже на Головній", Toast.LENGTH_SHORT).show()
                    true
                }
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
}