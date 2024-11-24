package com.example.inicio.com.example.inicio.usuariomenu

// Definir el modelo de datos para la actualización
data class UsuarioActualizar(
    val id_usuario: Int,
    val nombre: String,
    val correo_electronico: String,
    val direccion: String,
    val telefono: String,
    val contrasena_actual: String? = null,
    val nueva_contrasena: String? = null
)