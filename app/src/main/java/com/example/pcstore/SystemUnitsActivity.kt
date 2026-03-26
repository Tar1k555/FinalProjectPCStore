package com.example.pcstore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

data class PcModel(val name: String, val specs: String, val price: String, val isGaming: Boolean)

class SystemUnitsActivity : AppCompatActivity() {

    private lateinit var rvPcs: RecyclerView
    private lateinit var adapter: PcAdapter
    private val allPcs = mutableListOf<PcModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_units)

        allPcs.add(PcModel("ULTIMATE Gaming PC", "64Gb/4Tb", "222 222 ₴", true))
        allPcs.add(PcModel("Panzer III v2.5", "64Gb/2Tb", "245 436 ₴", true))
        allPcs.add(PcModel("Gaming PC", "32Gb/1Tb", "55 656 ₴", true))

        allPcs.add(PcModel("Fujitsu Celsius W5012", "32Gb/1Tb", "59 999 ₴", false))
        allPcs.add(PcModel("HP OMEN 40L Desktop", "16/512Gb", "49 999 ₴", false))
        allPcs.add(PcModel("Business B48", "32Gb/1Tb", "33 000 ₴", false))

        rvPcs = findViewById(R.id.rvPcs)
        rvPcs.layoutManager = GridLayoutManager(this, 2)
        adapter = PcAdapter(allPcs.filter { it.isGaming }) // За замовчуванням показуємо ігрові
        rvPcs.adapter = adapter

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val tvCategoryTitle = findViewById<TextView>(R.id.tvCategoryTitle)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    tvCategoryTitle.text = "Ігрові системні блоки"
                    adapter.updateList(allPcs.filter { it.isGaming })
                } else {
                    tvCategoryTitle.text = "Робочі системні блоки"
                    adapter.updateList(allPcs.filter { !it.isGaming })
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
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_system_units -> true
                else -> false
            }
        }

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }
    }
}

class PcAdapter(private var pcList: List<PcModel>) : RecyclerView.Adapter<PcAdapter.PcViewHolder>() {

    class PcViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvPcName)
        val tvSpecs: TextView = view.findViewById(R.id.tvPcSpecs)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val ivCart: ImageView = view.findViewById(R.id.ivAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PcViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pc, parent, false)
        return PcViewHolder(view)
    }

    override fun onBindViewHolder(holder: PcViewHolder, position: Int) {
        val pc = pcList[position]
        holder.tvName.text = pc.name
        holder.tvSpecs.text = pc.specs
        holder.tvPrice.text = pc.price

        holder.ivCart.setOnClickListener {
            // Тут ми потім додамо відправку в Firebase!
            Toast.makeText(holder.itemView.context, "Додано в кошик: ${pc.name}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = pcList.size

    fun updateList(newList: List<PcModel>) {
        pcList = newList
        notifyDataSetChanged()
    }
}