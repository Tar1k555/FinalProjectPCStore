package com.example.pcstore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Одразу малюємо екран логіну, ніяких перевірок на автопропуск!
        setContentView(R.layout.activity_login)

        val sharedPref = getSharedPreferences("PCStorePrefs", MODE_PRIVATE)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заповніть всі поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val savedEmail = sharedPref.getString("saved_email", "")
            val savedPassword = sharedPref.getString("saved_password", "")

            if (email == savedEmail && password == savedPassword) {
                // Зберігаємо статус, що ми залогінені (хоча зараз ми його не використовуємо для автопропуску, хай буде)
                sharedPref.edit().putBoolean("is_logged_in", true).apply()

                Toast.makeText(this, "Вхід успішний!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Неправильний email або пароль (або ви не зареєстровані)", Toast.LENGTH_SHORT).show()
            }
        }

        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}