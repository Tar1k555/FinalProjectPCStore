package com.example.pcstore

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

data class Order(
    val orderId: String = "",
    val productNames: String = "",
    val totalPrice: Long = 0,
    val timestamp: Long = 0
)

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var rvOrders: RecyclerView
    private lateinit var orderAdapter: OrderAdapter

    private val activeOrders = mutableListOf<Order>()
    private val historyOrders = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val uid = currentUser.uid
        val currentUserEmail = currentUser.email ?: "unknown_user"

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val ivCartProfile = findViewById<ImageView>(R.id.ivCartProfile)

        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etMiddleName = findViewById<EditText>(R.id.etMiddleName)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etProfileEmail = findViewById<EditText>(R.id.etProfileEmail)
        val tvDob = findViewById<TextView>(R.id.tvDob)
        val tvGender = findViewById<TextView>(R.id.tvGender)
        val btnSaveProfile = findViewById<Button>(R.id.btnSaveProfile)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val btnMyOrders = findViewById<Button>(R.id.btnMyOrders)
        val btnFavorites = findViewById<Button>(R.id.btnFavorites)
        val btnOrderHistory = findViewById<Button>(R.id.btnOrderHistory)

        rvOrders = findViewById(R.id.rvOrders)
        rvOrders.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderAdapter(emptyList())
        rvOrders.adapter = orderAdapter

        etProfileEmail.setText(currentUserEmail)

        db.collection("users").document(uid).get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                etLastName.setText(document.getString("lastName") ?: "")
                etFirstName.setText(document.getString("firstName") ?: "")
                etMiddleName.setText(document.getString("middleName") ?: "")
                etPhone.setText(document.getString("phone") ?: "")
                tvDob.text = document.getString("dob") ?: "Дата народження"
                tvGender.text = document.getString("gender") ?: "Стать:"
            }
        }

        tvDob.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val dateStr = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                tvDob.text = dateStr
            }, year, month, day).show()
        }

        tvGender.setOnClickListener {
            val genders = arrayOf("Чоловіча", "Жіноча", "Не вказувати")
            AlertDialog.Builder(this)
                .setTitle("Оберіть стать")
                .setItems(genders) { _, which ->
                    tvGender.text = "Стать: ${genders[which]}"
                }
                .show()
        }

        btnSaveProfile.setOnClickListener {
            val userData = hashMapOf(
                "lastName" to etLastName.text.toString(),
                "firstName" to etFirstName.text.toString(),
                "middleName" to etMiddleName.text.toString(),
                "phone" to etPhone.text.toString(),
                "dob" to tvDob.text.toString(),
                "gender" to tvGender.text.toString(),
                "email" to currentUserEmail
            )

            db.collection("users").document(uid)
                .set(userData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Дані успішно збережено!", Toast.LENGTH_SHORT).show()
                }
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        ivBack.setOnClickListener { finish() }

        ivCartProfile.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        bottomNav.selectedItemId = R.id.nav_profile
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
                R.id.nav_profile -> true
                else -> false
            }
        }

        btnMyOrders.setOnClickListener { loadOrders("active") }
        btnOrderHistory.setOnClickListener { loadOrders("history") }
        btnFavorites.setOnClickListener {
            Toast.makeText(this, "Обране в розробці!", Toast.LENGTH_SHORT).show()
        }

        loadOrders("active")
    }

    private fun loadOrders(type: String) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid).collection("orders")
            .get()
            .addOnSuccessListener { documents ->
                activeOrders.clear()
                historyOrders.clear()

                val currentTime = System.currentTimeMillis()

                for (document in documents) {
                    val order = document.toObject(Order::class.java)
                    val diffMinutes = (currentTime - order.timestamp) / 60000

                    if (diffMinutes >= 8) {
                        historyOrders.add(order)
                    } else {
                        activeOrders.add(order)
                    }
                }

                if (type == "history") {
                    if (historyOrders.isEmpty()) {
                        Toast.makeText(this@ProfileActivity, "Історія замовлень порожня", Toast.LENGTH_SHORT).show()
                    }
                    orderAdapter.updateList(historyOrders)
                } else if (type == "active") {
                    if (activeOrders.isEmpty()) {
                        Toast.makeText(this@ProfileActivity, "Немає активних замовлень", Toast.LENGTH_SHORT).show()
                    }
                    orderAdapter.updateList(activeOrders)
                }
            }
    }
}