package com.example.pcstore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val ivCartProfile = findViewById<ImageView>(R.id.ivCartProfile)

        val etProfileEmail = findViewById<EditText>(R.id.etProfileEmail)

        val btnMyOrders = findViewById<Button>(R.id.btnMyOrders)
        val btnFavorites = findViewById<Button>(R.id.btnFavorites)
        val btnOrderHistory = findViewById<Button>(R.id.btnOrderHistory)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // --- ЛОГІКА ДАНИХ ---
        // Дістаємо збережений email і автоматично вставляємо в поле
        val sharedPref = getSharedPreferences("PCStorePrefs", MODE_PRIVATE)
        val savedEmail = sharedPref.getString("saved_email", "")
        etProfileEmail.setText(savedEmail)


        // --- ОБРОБКА КЛІКІВ ---
        ivBack.setOnClickListener {
            // Кнопка назад просто повертає на попередній екран
            finish()
        }

        ivCartProfile.setOnClickListener {
            Toast.makeText(this, "Кошик відкриється тут!", Toast.LENGTH_SHORT).show()
        }

        btnMyOrders.setOnClickListener {
            Toast.makeText(this, "Тут будуть мої замовлення", Toast.LENGTH_SHORT).show()
        }

        btnFavorites.setOnClickListener {
            Toast.makeText(this, "Улюблені товари!", Toast.LENGTH_SHORT).show()
        }

        btnOrderHistory.setOnClickListener {
            Toast.makeText(this, "Тут буде історія", Toast.LENGTH_SHORT).show()
        }

        // --- НИЖНЯ ПАНЕЛЬ ---
        bottomNav.selectedItemId = R.id.nav_profile

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    // Ми вже тут
                    true
                }
                else -> {
                    Toast.makeText(this, "В розробці", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }
    }
}