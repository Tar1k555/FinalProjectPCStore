package com.example.pcstore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    // Ініціалізуємо Firebase Auth та Firestore
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

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

            // РЕЄСТРАЦІЯ У FIREBASE
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid

                        val userMap = hashMapOf(
                            "email" to email,
                            "firstName" to "",
                            "lastName" to "",
                            "phone" to ""
                        )

                        if (userId != null) {
                            db.collection("users").document(userId).set(userMap)
                        }

                        Toast.makeText(this, "Реєстрація успішна!", Toast.LENGTH_SHORT).show()
                        finish() // Повертаємось на екран входу
                    } else {
                        Toast.makeText(this, "Помилка: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}