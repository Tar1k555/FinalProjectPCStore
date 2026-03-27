package com.example.pcstore

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class PeripheralsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peripherals)

        // Кнопка Назад
        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        val rvPeripherals = findViewById<RecyclerView>(R.id.rvPeripherals)
        rvPeripherals.layoutManager = GridLayoutManager(this, 2)


        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_peripherals // Робить кнопку "Периферія" активною

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
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
                    Toast.makeText(this, "Комплектуючі в розробці", Toast.LENGTH_SHORT).show()
                    false
                }
                R.id.nav_peripherals -> {
                    true
                }
                else -> false
            }
        }
    }
}