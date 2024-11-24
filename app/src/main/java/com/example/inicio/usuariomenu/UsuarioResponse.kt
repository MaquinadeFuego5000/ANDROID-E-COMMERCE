package com.example.inicio.com.example.inicio.usuariomenu

data class UsuarioResponse(
    val success: Boolean,
    val message: String,
    val data: Usuario
)

data class Usuario(
    val nombre: String,
    val correo_electronico: String,
    val direccion: String,
    val telefono: String
)

