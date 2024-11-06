package com.example.inicio

// Clase de datos que representa un producto
data class Product(
    val id_producto: Int,
    val nombre_producto: String,
    val descripcion: String,
    val precio: Double,
    val imagen: String, // Este campo ya contiene la URL completa de la imagen
    val nombre_usuario: String,
    val stock: Int,
    val id_categoria: Int // Añadir este campo
)
