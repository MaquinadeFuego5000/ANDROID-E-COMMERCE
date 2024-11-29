package com.example.inicio.com.example.inicio.misventas

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inicio.ApiService
import com.example.inicio.R
import com.example.inicio.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MisVentasActivity : AppCompatActivity() {
    private var rvVentas: RecyclerView? = null
    private var ventasAdapter: VentasAdapter? = null
    private var ventasList: List<VentaResponse> = emptyList()  // Lista de ventas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_ventas)

        // Configuración del RecyclerView
        rvVentas = findViewById(R.id.rv_ventas)
        rvVentas?.layoutManager = LinearLayoutManager(this)

        // Inicializa el adaptador con una lista vacía de VentaResponse
        ventasAdapter = VentasAdapter(ventasList)
        rvVentas?.adapter = ventasAdapter

        // Obtener el session_token desde SharedPreferences
        val sessionToken = obtenerSessionToken()

        if (sessionToken.isBlank()) {
            showError("No hay sesión activa. Inicia sesión.")
            return
        }

        loadVentas(sessionToken)
    }

    private fun loadVentas(sessionToken: String) {
        val ventasRequest = VentasRequest(session_token = sessionToken)

        val apiService: ApiService = RetrofitClient.apiService
        val call: Call<RespuestaVentas> = apiService.obtenerVentas(ventasRequest)

        call.enqueue(object : Callback<RespuestaVentas> {
            override fun onResponse(call: Call<RespuestaVentas>, response: Response<RespuestaVentas>) {
                if (response.isSuccessful) {
                    // Actualiza la lista de ventas con la respuesta de la API
                    ventasList = response.body()?.ventas ?: emptyList()

                    // Actualiza el adaptador con la nueva lista de ventas
                    ventasAdapter?.updateVentas(ventasList)
                } else {
                    showError("Error al cargar las ventas")
                }
            }

            override fun onFailure(call: Call<RespuestaVentas>, t: Throwable) {
                Log.e("API_ERROR", "Error de conexión", t)
                showError("Error de conexión")
            }
        })
    }

    private fun obtenerSessionToken(): String {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("session_token", "").orEmpty()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
