package com.example.pcstore

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class CartActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var cartAdapter: CartAdapter

    private lateinit var layoutEmptyCart: LinearLayout
    private lateinit var layoutCartItems: RelativeLayout
    private lateinit var tvTotalPrice: TextView
    private lateinit var rvCartItems: RecyclerView

    private val currentCartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        layoutEmptyCart = findViewById(R.id.layoutEmptyCart)
        layoutCartItems = findViewById(R.id.layoutCartItems)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)
        rvCartItems = findViewById(R.id.rvCartItems)

        // Налаштовуємо список
        rvCartItems.layoutManager = LinearLayoutManager(this)

        // Ініціалізуємо адаптер і передаємо логіку видалення
        cartAdapter = CartAdapter(currentCartItems) { itemToDelete ->
            deleteItemFromCart(itemToDelete)
        }
        rvCartItems.adapter = cartAdapter

        // Кнопки toolbar
        findViewById<ImageView>(R.id.ivBackCart).setOnClickListener { finish() }

        // Кнопка в порожньому кошику
        findViewById<Button>(R.id.btnContinueShopping).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
            finish()
        }

        // Кнопка оформлення
        findViewById<Button>(R.id.btnCheckout).setOnClickListener {
            processCheckout()
        }

        setupBottomNav()
        loadCartItems()
    }

    private fun loadCartItems() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid).collection("cart")
            .get()
            .addOnSuccessListener { documents ->
                currentCartItems.clear()
                var total = 0L

                for (doc in documents) {
                    val item = doc.toObject(CartItem::class.java)
                    item.id = doc.id // Зберігаємо ID документа для видалення
                    currentCartItems.add(item)
                    total += item.price
                }

                if (currentCartItems.isEmpty()) {
                    layoutEmptyCart.visibility = View.VISIBLE
                    layoutCartItems.visibility = View.GONE
                } else {
                    layoutEmptyCart.visibility = View.GONE
                    layoutCartItems.visibility = View.VISIBLE
                    tvTotalPrice.text = "$total ₴"

                    cartAdapter.notifyDataSetChanged()
                }
            }
    }


    private fun deleteItemFromCart(item: CartItem) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid).collection("cart").document(item.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "${item.name} видалено", Toast.LENGTH_SHORT).show()
                loadCartItems()
            }
    }

    private fun processCheckout() {
        if (currentCartItems.isEmpty()) {
            Toast.makeText(this, "Кошик порожній", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = auth.currentUser?.uid ?: return

        val productNames = currentCartItems.joinToString(separator = ", ") { it.name }
        val totalPrice = currentCartItems.sumOf { it.price }
        val orderId = UUID.randomUUID().toString()

        val newOrder = hashMapOf(
            "orderId" to orderId,
            "productNames" to productNames,
            "totalPrice" to totalPrice,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users").document(uid).collection("orders").document(orderId)
            .set(newOrder)
            .addOnSuccessListener {

                val batch = db.batch()
                for (item in currentCartItems) {
                    val docRef = db.collection("users").document(uid).collection("cart").document(item.id)
                    batch.delete(docRef)
                }

                batch.commit().addOnSuccessListener {
                    Toast.makeText(this, "Замовлення оформлено! 🎉", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                }
            }
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = -1
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
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
    }
}