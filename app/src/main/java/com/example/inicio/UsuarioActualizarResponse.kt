package com.example.inicio.com.example.inicio

data class UsuarioActualizacionResponse(
    val success: Boolean,
    val message: String?,  // Cambiado para permitir que sea nulo
    val data: Any?         // Ajusta según el tipo de 'data' que recibas
)
