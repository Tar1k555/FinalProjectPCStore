package com.example.pcstore

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etPassword = findViewById<EditText>(R.id.etRegPassword)
        val etRepeatPassword = findViewById<EditText>(R.id.etRegRepeatPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val repeatPassword = etRepeatPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
                Toast.makeText(this, "Заповніть всі поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != repeatPassword) {
                Toast.makeText(this, "Паролі не співпадають!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Відкриваємо  SharedPreferences ("блокнот" з назвою PCStorePrefs)
            val sharedPref = getSharedPreferences("PCStorePrefs", MODE_PRIVATE)
            val editor = sharedPref.edit()

            editor.putString("saved_email", email)
            editor.putString("saved_password", password)
            editor.apply()

            Toast.makeText(this, "Реєстрація успішна! Дані збережено.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}