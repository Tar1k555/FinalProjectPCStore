package com.example.pcstore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnLogout.setOnClickListener {
            // Записуємо в "блокнот", що юзер вийшов
            val sharedPref = getSharedPreferences("PCStorePrefs", MODE_PRIVATE)
            sharedPref.edit().putBoolean("is_logged_in", false).apply()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}