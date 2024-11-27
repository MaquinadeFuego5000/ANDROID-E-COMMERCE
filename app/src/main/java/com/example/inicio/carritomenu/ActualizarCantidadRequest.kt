package com.example.inicio.com.example.inicio.carritomenu

data class ActualizarCantidadRequest(
    val session_token: String,
    val id_producto: Int,
    val cantidad: Int
)