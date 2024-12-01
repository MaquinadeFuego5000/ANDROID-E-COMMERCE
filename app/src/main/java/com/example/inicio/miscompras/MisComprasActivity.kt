package com.example.inicio.miscompras

import MisComprasAdapter
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inicio.R
import com.example.inicio.RetrofitClient
import com.example.inicio.ApiService
import com.example.inicio.com.example.inicio.miscompras.PedidosRequest
import com.example.inicio.com.example.inicio.miscompras.PedidosResponse
import com.example.inicio.loginmenu.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MisComprasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_compras)

        // Obtener el session_token desde SharedPreferences
        val sessionToken = obtenerSessionToken()

        // Verificar si hay sesión activa
        if (sessionToken.isBlank()) {
            // Redirigir a LoginActivity si no hay sesión activa
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Cerrar esta actividad
            return
        }

        // Configuración del RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCompras)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Crear la solicitud para obtener los pedidos
        val pedidosRequest = PedidosRequest(session_token = sessionToken)

        // Llamada a la API usando Retrofit (usa el servicio configurado en RetrofitClient)
        RetrofitClient.apiService.obtenerPedidos(pedidosRequest).enqueue(object : Callback<PedidosResponse> {
            override fun onResponse(
                call: Call<PedidosResponse>,
                response: Response<PedidosResponse>
            ) {
                if (response.isSuccessful) {
                    // Aquí se obtiene la lista de pedidos a partir del Map<String, Pedido> y lo convierte a lista
                    val pedidos = response.body()?.pedidos?.values?.toList() ?: emptyList()
                    recyclerView.adapter = MisComprasAdapter(pedidos)
                } else {
                    showError("Error al obtener los pedidos")
                }
            }

            override fun onFailure(call: Call<PedidosResponse>, t: Throwable) {
                showError("Error de conexión: ${t.message}")
            }
        })
    }

    private fun obtenerSessionToken(): String {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("session_token", "").orEmpty()
    }

    private fun showError(message: String) {
        // Mostrar un mensaje de error al usuario (puedes usar Toast o Snackbar)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
