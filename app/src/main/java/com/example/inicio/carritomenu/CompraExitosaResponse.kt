package com.example.inicio.com.example.inicio.carritomenu

data class CompraExitosaResponse(
    val success: Boolean,
    val message: String,
    val precio_sin_iva: Double?,
    val iva_calculado: Double?,
    val total_a_pagar: Double?
)
