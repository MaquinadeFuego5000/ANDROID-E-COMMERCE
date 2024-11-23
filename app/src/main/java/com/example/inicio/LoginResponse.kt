package com.example.inicio.com.example.inicio

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val session_token: String?, // Este campo puede ser nulo si la autenticación falla
    val user: User? // Puede ser nulo si el login falla
)

data class User(
    val id: Int,
    val nombre: String,
    val correo_electronico: String
)
