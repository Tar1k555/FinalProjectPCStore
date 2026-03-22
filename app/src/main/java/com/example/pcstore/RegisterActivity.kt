package com.example.pcstore

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Проблемні рядки видалено, залишаємо тільки прив'язку дизайну:
        setContentView(R.layout.activity_register)

        // Знаходимо наші поля вводу та кнопку
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

            val userExists = MockDatabase.users.any { it.email == email }
            if (userExists) {
                Toast.makeText(this, "Користувач з таким Email вже існує!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newUser = User(email, password)
            MockDatabase.users.add(newUser)
            MockDatabase.currentUser = newUser

            Toast.makeText(this, "Реєстрація успішна!", Toast.LENGTH_SHORT).show()

            finish()
        }
    }
}