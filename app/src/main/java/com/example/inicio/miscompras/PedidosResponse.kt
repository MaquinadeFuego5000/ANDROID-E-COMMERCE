package com.example.inicio.com.example.inicio.miscompras

data class PedidosResponse(
    val success: Boolean,
    val pedidos: Map<String, Pedido> // Mapeamos las claves de los pedidos como String, porque en el JSON son claves numéricas
)

data class Pedido(
    val numero_pedido: Int,  // Renombrar a numero_pedido para que coincida con el JSON
    val fecha_pedido: String,
    val monto_total: Double,
    val estado: String,
    val productos: List<Producto>
)

data class Producto(
    val nombre_producto: String,
    val cantidad: Int,
    val precio: Double,
    val imagen_url: String
)

