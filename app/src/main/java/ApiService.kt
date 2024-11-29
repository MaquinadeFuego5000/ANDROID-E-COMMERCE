package com.example.inicio // Asegúrate de que este paquete coincide con tu proyecto

import com.example.inicio.com.example.inicio.misventas.RespuestaVentas
import com.example.inicio.com.example.inicio.misventas.VentasRequest
import com.example.inicio.com.example.inicio.carritomenu.ActualizarCantidadRequest
import com.example.inicio.com.example.inicio.carritomenu.ApiResponse
import com.example.inicio.com.example.inicio.carritomenu.CarritoResponse
import com.example.inicio.com.example.inicio.carritomenu.CompraExitosaResponse

import com.example.inicio.com.example.inicio.loginmenu.LoginRequest
import com.example.inicio.com.example.inicio.loginmenu.LoginResponse
import com.example.inicio.com.example.inicio.miscompras.PedidosRequest
import com.example.inicio.com.example.inicio.miscompras.PedidosResponse
import com.example.inicio.com.example.inicio.registromenu.RegisterResponse
import com.example.inicio.com.example.inicio.registromenu.RegistrarUsuario
import com.example.inicio.com.example.inicio.usuariomenu.UsuarioActualizacionResponse
import com.example.inicio.com.example.inicio.usuariomenu.UsuarioActualizar
import com.example.inicio.com.example.inicio.usuariomenu.UsuarioResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {

    //todas las api o servicios estan en la carpeta Android

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

    //-----------------------funciones del carrito---------------------------------------------//

    //AGREGAR UN PRODUCTO AL CARRITO DE COMPRAS
    @POST("Android/carrito.php")
    @FormUrlEncoded
    fun agregarProductoAlCarrito(
        @Field("id_producto") idProducto: Int,
        @Field("cantidad") cantidad: Int,
        @Field("session_token") sessionToken: String
    ): Call<ApiResponse> // Metodo para saber si fue exitoso

    //MOSTRAR U OBTENER PRODUCTOS DEL CARRITO
    // **Respuesta esperada**: Un objeto `CarritoResponse` con detalles del carrito.
    //Metodo de respuesta CarritoResponse similar a ApiResponse: espera un mensaje de respuesta sin embargo utiliza otra clase llamada ProductoCarrito que tien detalles del carrito
    @GET("Android/obtener-carrito.php")
    suspend fun listarProductosDelCarrito(@Query("session_token") sessionToken: String): Response<CarritoResponse>

    //ACTUALIZAR CANTIDAD DEL CARRITO
    //AcutalizarCantidadResques: es la solicitud esperada
    //CarritoResponse es el mismo de metodo de respuesta que se usa en OBTENER PRODUCTOS
    @PUT("Android/actualizar-carrito.php")
    fun actualizarCantidadProducto(
        @Body request: ActualizarCantidadRequest
    ): Call<CarritoResponse>

    // ELIMINAR UN PRODUCTO DEL CARRITO
    //ApiResponse: utilizado para acutalizar el estado y obtener respuesta
    @DELETE("Android/eliminar-carrito.php")
    fun eliminarProductoDelCarrito(
        @Query("session_token") sessionToken: String,
        @Query("id_producto") idProducto: Int
    ): Call<ApiResponse>

    //VACIAR EL CARRITO
    //ApiResponse: utilizado para acutalizar el estado y obtener respuesta
    @DELETE("Android/eliminar-carrito.php")
    fun eliminarTodosLosProductosDelCarrito(
        @Query("session_token") sessionToken: String,
        @Query("eliminar_todos") eliminarTodos: String = "true" // Parámetro para eliminar todos
    ): Call<ApiResponse>

    // PROCESA LA COMPRA DEL CARRITO
    //CompraExitosaResponse: Es la respuesta esperada para confirmar la compra
        @FormUrlEncoded
        @POST("Android/compra_exitosa.php")
        suspend fun procesarCompra(
            @Field("session_token") sessionToken: String
        ): Response<CompraExitosaResponse>

    //-----------Fin de las operaciones del carrito---------------------------------------------//

    @POST("Android/mis-compras.php")
    fun obtenerPedidos(@Body request: PedidosRequest): Call<PedidosResponse>

    @POST("Android/mis-ventas.php")
    fun obtenerVentas(@Body ventasRequest: VentasRequest): Call<RespuestaVentas>

}

