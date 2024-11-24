package com.example.inicio // Asegúrate de que este paquete coincide con tu proyecto

import com.example.inicio.com.example.inicio.loginmenu.LoginRequest
import com.example.inicio.com.example.inicio.loginmenu.LoginResponse
import com.example.inicio.com.example.inicio.registromenu.RegisterResponse
import com.example.inicio.com.example.inicio.registromenu.RegistrarUsuario
import com.example.inicio.com.example.inicio.usuariomenu.UsuarioActualizacionResponse
import com.example.inicio.com.example.inicio.usuariomenu.UsuarioActualizar
import com.example.inicio.com.example.inicio.usuariomenu.UsuarioResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {

    // Método para registrar un usuario
    @POST("Android/control-registrar-usuario.php")
    fun registrarUsuario(@Body registrarUsuario: RegistrarUsuario): Call<RegisterResponse>

    //Metodo para logear usuario
    @POST("Android/control-logear.php")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>

    //llamar productos al Main
    @GET("Android/productos.php")
    fun getProducts(): Call<List<Product>>

    // funcion de obtener informacion del usuario
    @GET("Android/editar-usuario.php")
    fun obtenerUsuario(@retrofit2.http.Query("id_usuario") idUsuario: Int): Call<UsuarioResponse>

    //funcion para actualizar la informacion del usuario
    @PUT("Android/editar-usuario.php") // Usar PUT para la actualización
    suspend fun actualizarUsuario(@Query("id_usuario") idUsuario: Int, @Body usuarioActualizar: UsuarioActualizar): Response<UsuarioActualizacionResponse>





}

