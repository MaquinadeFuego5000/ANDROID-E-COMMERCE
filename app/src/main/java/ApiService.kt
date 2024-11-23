package com.example.inicio // Asegúrate de que este paquete coincide con tu proyecto

import com.example.inicio.Product // Importa la clase Product
import com.example.inicio.com.example.inicio.LoginRequest
import com.example.inicio.com.example.inicio.LoginResponse
import com.example.inicio.com.example.inicio.RegisterResponse
import com.example.inicio.com.example.inicio.RegistrarUsuario
import com.example.inicio.com.example.inicio.UsuarioActualizacionResponse
import com.example.inicio.com.example.inicio.UsuarioActualizar
import com.example.inicio.com.example.inicio.UsuarioResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Método para registrar un usuario
    @POST("control/control-registrar-usuario.php")
    fun registrarUsuario(@Body registrarUsuario: RegistrarUsuario): Call<RegisterResponse>

    //Metodo para logear usuario
    @POST("control/control-logear.php")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>

    //llamar productos al Main
    @GET("productos.php") // Cambia esto a la URL de tu endpoint
    fun getProducts(): Call<List<Product>>

    // funcion de obtener informacion del usuario
    @GET("control/editar-usuario.php")
    fun obtenerUsuario(@retrofit2.http.Query("id_usuario") idUsuario: Int): Call<UsuarioResponse>

    //funcion para actualizar la informacion del usuario
    @PUT("control/editar-usuario.php") // Usar PUT para la actualización
    suspend fun actualizarUsuario(@Query("id_usuario") idUsuario: Int, @Body usuarioActualizar: UsuarioActualizar): Response<UsuarioActualizacionResponse>





}

