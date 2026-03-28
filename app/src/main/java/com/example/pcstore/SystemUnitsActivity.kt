package com.example.pcstore

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore

class SystemUnitsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val productList = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter
    private lateinit var tvCategoryTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_units)

        tvCategoryTitle = findViewById(R.id.tvCategoryTitle)

        val rvPcs = findViewById<RecyclerView>(R.id.rvPcs)
        rvPcs.layoutManager = GridLayoutManager(this, 2)

        adapter = ProductAdapter(productList)
        rvPcs.adapter = adapter

        fetchPcsFromFirebase("gaming_pcs")

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    tvCategoryTitle.text = "Ігрові системні блоки"
                    fetchPcsFromFirebase("gaming_pcs")
                } else {
                    tvCategoryTitle.text = "Робочі системні блоки"
                    fetchPcsFromFirebase("work_pcs")
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_system_units
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_system_units -> true
                R.id.nav_components -> {
                    startActivity(Intent(this, ComponentsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_peripherals -> {
                    startActivity(Intent(this, PeripheralsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }
    }

    private fun fetchPcsFromFirebase(category: String) {
        db.collection("products")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { documents ->
                productList.clear()
                for (document in documents) {
                    val product = document.toObject(Product::class.java)
                    productList.add(product)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
            }
    }
}