package com.example.pcstore

// Модель користувача
data class User(
    val email: String,
    val password: String,
    // На майбутнє сюди додамо:
    // val cart: MutableList<Product> = mutableListOf(),
    // val orderHistory: MutableList<Order> = mutableListOf()
)

// Наша тимчасова локальна "База Даних"
object MockDatabase {
    // Список всіх зареєстрованих користувачів
    val users = mutableListOf<User>()

    // Користувач, який зараз залогінений (щоб знати, чий кошик показувати)
    var currentUser: User? = null

    init {
        // Додамо одного тестового користувача, щоб тобі не треба було
        // щоразу реєструватися при перезапуску апки для перевірки логіну
        users.add(User("test@gmail.com", "123456"))
    }
}