package com.example.pcstore

// Firebase вимагає, щоб усі поля мали значення за замовчуванням
data class Product(
    var id: String = "",
    val name: String = "",
    val price: Int = 0,
    val oldPrice: Int? = null,
    val imageUrl: String = "",
    val rating: Float = 0f,
    val category: String = ""
)