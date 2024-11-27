package com.example.inicio.com.example.inicio.carritomenu

data class ProductoCarrito(
    val id_producto: Int,
    val nombre_producto: String,
    val precio: Double,
    var cantidad: Int
)
