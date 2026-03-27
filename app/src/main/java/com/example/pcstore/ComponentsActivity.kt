package com.example.pcstore

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore // Імпортуємо Firestore

class ComponentsActivity : AppCompatActivity() {

    // Створюємо змінні для бази, списку та адаптера
    private val db = FirebaseFirestore.getInstance()
    private val productList = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_components)

        // Кнопка Назад
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        // Налаштування списку
        val rvComponents = findViewById<RecyclerView>(R.id.rvComponents)
        rvComponents.layoutManager = GridLayoutManager(this, 2)

        // Підключаємо наш адаптер до порожнього списку
        adapter = ProductAdapter(productList)
        rvComponents.adapter = adapter

        // Викликаємо функцію завантаження даних
        fetchComponentsFromFirebase()

        // ... ТУТ ЗАЛИШАЄТЬСЯ ТВІЙ КОД НИЖНЬОГО МЕНЮ (bottomNav.setOnItemSelectedListener) ...
    }

    // Функція, яка бере дані з бази
    private fun fetchComponentsFromFirebase() {
        db.collection("products")
            .whereEqualTo("category", "components") // Беремо ТІЛЬКИ комплектуючі!
            .get()
            .addOnSuccessListener { documents ->
                productList.clear()
                for (document in documents) {
                    // Перетворюємо документ з бази у наш клас Product
                    val product = document.toObject(Product::class.java)
                    productList.add(product)
                }
                adapter.notifyDataSetChanged() // Кажемо адаптеру оновити екран
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Помилка завантаження: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}