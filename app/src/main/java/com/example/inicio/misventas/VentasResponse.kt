package com.example.inicio.com.example.inicio.misventas

// Clase envolvente para la respuesta de la API
data class RespuestaVentas(
    val ventas: List<VentaResponse> // Lista de ventas que proviene de la API
)

// Respuesta de la venta
data class VentaResponse(
    val id_venta: Int,               // ID de la venta
    val fecha_venta: String,         // Fecha de la venta
    val nombre_comprador: String,    // Nombre del comprador
    val productos: List<ProductoResponse>, // Lista de productos asociados
    val total_venta: Double,        // Total de la venta con IVA
    val subtotal_venta: Double      // Subtotal de la venta sin IVA
)


// Respuesta de un producto dentro de una venta
data class ProductoResponse(
    val id_producto: Int,       // ID del producto
    val cantidad: Int,          // Cantidad vendida del producto
    val nombre_producto: String,// Nombre del producto
    val precio: Double,         // Precio del producto
    val imagen_url: String      // URL de la imagen del producto
)

data class Producto(
    val idProducto: Int,       // ID del producto
    val cantidad: Int,         // Cantidad vendida del producto
    val nombreProducto: String,// Nombre del producto
    val precio: Double,        // Precio del producto
    val imagenUrl: String      // URL de la imagen del producto
)
