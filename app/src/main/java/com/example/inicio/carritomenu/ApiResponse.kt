package com.example.inicio.com.example.inicio.carritomenu



data class ApiResponse(
    val success: Boolean,
    val message: String,
    val data: List<Any> // Cambia según el tipo de datos que esperas
)

