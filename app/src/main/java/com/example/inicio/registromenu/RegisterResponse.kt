package com.example.inicio.com.example.inicio.registromenu

import com.example.inicio.com.example.inicio.usuariomenu.Usuario

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val data: Usuario?
)
