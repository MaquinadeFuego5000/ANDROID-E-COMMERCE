package com.example.inicio

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder

object RetrofitClient {
    // URL base del servidor (asegúrate de que termina con '/')
    private const val BASE_URL = "https://myluxor.shop/"

    // Configuración del cliente HTTP
    private val httpClient: OkHttpClient by lazy {
        // Interceptor para logs de red (solo para depuración)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Nivel de detalle de logs
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // Agregar el interceptor
            .build()
    }

    // Configuración personalizada para Gson (manejo de JSON)
    private val gson = GsonBuilder()
        .setLenient() // Aceptar JSON malformados si es necesario
        .create()


    // Instancia de Retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // URL base de tu API
            .addConverterFactory(GsonConverterFactory.create(gson)) // Convertidor de JSON con configuración personalizada
            .client(httpClient) // Cliente configurado con interceptor
            .build()
    }

    // Servicio de API
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
