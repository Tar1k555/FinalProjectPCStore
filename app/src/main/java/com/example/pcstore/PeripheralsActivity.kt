package com.example.pcstore

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
class PeripheralsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val productList = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peripherals)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        val rvPeripherals = findViewById<RecyclerView>(R.id.rvPeripherals)
        rvPeripherals.layoutManager = GridLayoutManager(this, 2)

        adapter = ProductAdapter(productList)
        rvPeripherals.adapter = adapter

        fetchPeripheralsFromFirebase()

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
                    // Тепер веде на реальну сторінку комплектуючих!
                    startActivity(Intent(this, ComponentsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_peripherals -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchPeripheralsFromFirebase() {
        db.collection("products")
            .whereEqualTo("category", "peripherals")
            .get()
            .addOnSuccessListener { documents ->
                productList.clear()
                for (document in documents) {
                    val product = document.toObject(Product::class.java)
                    productList.add(product)
                }
                adapter.notifyDataSetChanged() // Оновлюємо екран
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Помилка завантаження: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}