package com.example.inicio // Asegúrate de que este paquete coincide con tu proyecto

import com.example.inicio.Product // Importa la clase Product
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("productos.php") // Cambia esto a la URL de tu endpoint
    fun getProducts(): Call<List<Product>>
}

