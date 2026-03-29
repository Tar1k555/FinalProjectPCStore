package com.example.pcstore

object DealManager {
    // Зберігаємо ID товарів та їхній відсоток знижки
    val currentDeals = mutableMapOf<String, Int>()

    fun getDiscount(productId: String): Int? {
        return currentDeals[productId]
    }

    fun calculateNewPrice(oldPrice: Long, discountPercent: Int): Long {
        return oldPrice - (oldPrice * discountPercent / 100)
    }
}