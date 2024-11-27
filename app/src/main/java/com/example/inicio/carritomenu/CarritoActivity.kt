package com.example.inicio.com.example.inicio.carritomenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inicio.MainActivity
import com.example.inicio.R
import com.example.inicio.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

class CarritoActivity : AppCompatActivity(), CarritoAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var carritoAdapter: CarritoAdapter
    private lateinit var textSubtotal: TextView
    private lateinit var textIva: TextView
    private lateinit var textTotal: TextView
    private lateinit var buttonClearCart: Button
    private lateinit var buttonProceedToPayment: Button

    private val listaProductos = mutableListOf<ProductoCarrito>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        setupViews()
        setupRecyclerView()
        setupListeners()
        obtenerProductosCarrito()
    }

    // --- Funciones principales del carrito ---
    private fun obtenerProductosCarrito() {
        val sessionToken = obtenerSessionToken()
        if (sessionToken.isBlank()) {
            showToast("No hay sesión activa. Inicia sesión.")
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.listarProductosDelCarrito(sessionToken)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        listaProductos.apply {
                            clear()
                            response.body()?.data?.let(::addAll)
                        }
                        carritoAdapter.notifyDataSetChanged()
                        actualizarTotales()
                    } else {
                        showToast("Error al obtener el carrito")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { showToast("Error de red: ${e.message}") }
            }
        }
    }

    private fun vaciarCarrito() {
        if (listaProductos.isEmpty()) return // No realizar ninguna acción si el carrito ya está vacío

        val sessionToken = obtenerSessionToken()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.eliminarTodosLosProductosDelCarrito(sessionToken, "true").execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        listaProductos.clear()
                        carritoAdapter.notifyDataSetChanged()
                        actualizarTotales()
                    }
                }
            } catch (e: Exception) {
                // Error de red, ignorado según los nuevos requerimientos
            }
        }
    }

    private fun procederAlPago() {
        if (listaProductos.isEmpty()) return // No realizar ninguna acción si el carrito está vacío

        val sessionToken = obtenerSessionToken()
        if (sessionToken.isBlank()) {
            showToast("Debes iniciar sesión para proceder al pago")
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.procesarCompra(sessionToken)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        showToast("¡Compra realizada con éxito! Gracias por tu compra.")
                        val intent = Intent(this@CarritoActivity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                    } else {
                        showToast("Error al procesar la compra")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { showToast("Error de red: ${e.message}") }
            }
        }
    }

    // --- Implementación de interfaz ---
    override fun onUpdateQuantityClick(position: Int, cantidad: Int) {
        val producto = listaProductos.getOrNull(position) ?: return
        if (producto.cantidad == cantidad) return // No realizar ninguna acción si la cantidad es la misma

        val sessionToken = obtenerSessionToken()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.actualizarCantidadProducto(
                    ActualizarCantidadRequest(sessionToken, producto.id_producto, cantidad)
                ).execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        producto.cantidad = cantidad
                        carritoAdapter.notifyItemChanged(position)
                        actualizarTotales()
                    }
                }
            } catch (e: Exception) {
                // Error de red, ignorado
            }
        }
    }

    override fun onRemoveFromCartClick(position: Int) {
        val producto = listaProductos.getOrNull(position) ?: return
        val sessionToken = obtenerSessionToken()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.eliminarProductoDelCarrito(sessionToken, producto.id_producto).execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        listaProductos.removeAt(position)
                        carritoAdapter.notifyItemRemoved(position)
                        actualizarTotales()
                    } else {
                        showToast("Error al eliminar el producto")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { showToast("Error de red: ${e.message}") }
            }
        }
    }

    // --- Auxiliares ---
    private fun actualizarTotales() {
        val totalConIva = listaProductos.sumOf { it.precio * it.cantidad }
        val subtotal = totalConIva / 1.16
        val iva = subtotal * 0.16

        val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        textSubtotal.text = "Subtotal: ${formatoMoneda.format(subtotal)}"
        textIva.text = "IVA (16%): ${formatoMoneda.format(iva)}"
        textTotal.text = "Total a Pagar: ${formatoMoneda.format(totalConIva)}"
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        textSubtotal = findViewById(R.id.textSubtotal)
        textIva = findViewById(R.id.textIva)
        textTotal = findViewById(R.id.textTotal)
        buttonClearCart = findViewById(R.id.buttonClearCart)
        buttonProceedToPayment = findViewById(R.id.buttonCheckout)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        carritoAdapter = CarritoAdapter(listaProductos, this)
        recyclerView.adapter = carritoAdapter
    }

    private fun setupListeners() {
        buttonClearCart.setOnClickListener { vaciarCarrito() }
        buttonProceedToPayment.setOnClickListener { procederAlPago() }
    }

    private fun obtenerSessionToken(): String {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("session_token", "").orEmpty().also {
            Log.d("SessionToken", "Token de sesión: $it")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
