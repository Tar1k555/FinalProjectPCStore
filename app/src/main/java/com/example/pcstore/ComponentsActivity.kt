package com.example.pcstore

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class ComponentsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val allProductsList = mutableListOf<Product>() // Зберігає всі завантажені товари
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_components)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        val rvComponents = findViewById<RecyclerView>(R.id.rvComponents)
        rvComponents.layoutManager = GridLayoutManager(this, 2)

        adapter = ProductAdapter(allProductsList)
        rvComponents.adapter = adapter

        fetchComponentsFromFirebase()
        setupSearchAndFilters()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_components

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
                R.id.nav_components -> true
                R.id.nav_peripherals -> {
                    startActivity(Intent(this, PeripheralsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchComponentsFromFirebase() {
        db.collection("products")
            .whereEqualTo("category", "components")
            .get()
            .addOnSuccessListener { documents ->
                allProductsList.clear()
                for (document in documents) {
                    val product = document.toObject(Product::class.java)
                    product.id = document.id
                    allProductsList.add(product)
                }
                adapter.updateList(allProductsList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Помилка завантаження: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun setupSearchAndFilters() {
        val etSearch = findViewById<EditText>(R.id.etSearch)
        val btnSort = findViewById<Button>(R.id.btnSort)
        val btnFilter = findViewById<Button>(R.id.btnFilter)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                val filteredList = allProductsList.filter { it.name.lowercase().contains(query) }
                adapter.updateList(filteredList)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnSort.setOnClickListener {
            val options = arrayOf("За ціною: від найдешевших", "За ціною: від найдорожчих", "За рейтингом: від найнижчого", "За рейтингом: від найвищого")
            AlertDialog.Builder(this)
                .setTitle("Сортувати за:")
                .setItems(options) { _, which ->
                    val currentList = adapter.getCurrentList()
                    val sortedList = when (which) {
                        0 -> currentList.sortedBy { it.price }
                        1 -> currentList.sortedByDescending { it.price }
                        2 -> currentList.sortedBy { it.rating }
                        3 -> currentList.sortedByDescending { it.rating }
                        else -> currentList
                    }
                    adapter.updateList(sortedList)
                }.show()
        }

        btnFilter.setOnClickListener {
            val options = arrayOf("Всі товари", "🔥 Тільки зі знижкою", "Intel", "AMD", "NVIDIA", "Gigabyte", "Asus")
            AlertDialog.Builder(this)
                .setTitle("Фільтри")
                .setItems(options) { _, which ->
                    val filteredList = when (which) {
                        1 -> allProductsList.filter { it.oldPrice != null || DealManager.getDiscount(it.id) != null }
                        2 -> allProductsList.filter { it.name.contains("Intel", true) }
                        3 -> allProductsList.filter { it.name.contains("AMD", true) }
                        4 -> allProductsList.filter { it.name.contains("NVIDIA", true) }
                        5 -> allProductsList.filter { it.name.contains("Gigabyte", true) }
                        6 -> allProductsList.filter { it.name.contains("Asus", true) }
                        else -> allProductsList
                    }
                    val currentSearchText = etSearch.text.toString().lowercase()
                    val finalFilteredList = if (currentSearchText.isNotEmpty()) {
                        filteredList.filter { it.name.lowercase().contains(currentSearchText) }
                    } else filteredList

                    adapter.updateList(finalFilteredList)
                }.show()
        }
    }
}