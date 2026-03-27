package com.example.pcstore // Заміни на свій пакет, якщо відрізняється

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
            finish() // Закриває цей екран і повертає на попередній
        }

        // Налаштування списку (RecyclerView) у 2 колонки
        val rvPeripherals = findViewById<RecyclerView>(R.id.rvPeripherals)
        rvPeripherals.layoutManager = GridLayoutManager(this, 2)

        // ТИМЧАСОВІ ДАНІ (Потім заміниш на завантаження з Firebase)
        // Використовуй свій клас даних (наприклад, Product) та адаптер.
        // val peripheralsList = listOf(
        //     Product("Samsung 27\" Odyssey G5...", 7999, R.drawable.monitor_samsung),
        //     Product("Razer Cobra 8500 dpi", 2299, R.drawable.mouse_razer),
        //     Product("HATOR Rockfall 2...", 3199, R.drawable.keyboard_hator)
        // )
        // rvPeripherals.adapter = ProductAdapter(peripheralsList)

        // Нижня навігація
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
                    true // Ми вже тут
                }
                else -> false
            }
        }
    }
}