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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

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

        etProfileEmail.setText(currentUserEmail)

        val sharedPref = getSharedPreferences("PCStorePrefs", MODE_PRIVATE)
        etLastName.setText(sharedPref.getString("${currentUserEmail}_lastname", ""))
        etFirstName.setText(sharedPref.getString("${currentUserEmail}_firstname", ""))
        etMiddleName.setText(sharedPref.getString("${currentUserEmail}_middlename", ""))
        etPhone.setText(sharedPref.getString("${currentUserEmail}_phone", ""))
        tvDob.text = sharedPref.getString("${currentUserEmail}_dob", "Дата народження")
        tvGender.text = sharedPref.getString("${currentUserEmail}_gender", "Стать:")

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
            val editor = sharedPref.edit()
            editor.putString("${currentUserEmail}_lastname", etLastName.text.toString())
            editor.putString("${currentUserEmail}_firstname", etFirstName.text.toString())
            editor.putString("${currentUserEmail}_middlename", etMiddleName.text.toString())
            editor.putString("${currentUserEmail}_phone", etPhone.text.toString())
            editor.putString("${currentUserEmail}_dob", tvDob.text.toString())
            editor.putString("${currentUserEmail}_gender", tvGender.text.toString())
            editor.apply()

            Toast.makeText(this, "Дані успішно збережено!", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Ви вийшли з акаунта", Toast.LENGTH_SHORT).show()
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
                R.id.nav_profile -> {
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
                else -> false
            }
        }
    }
}