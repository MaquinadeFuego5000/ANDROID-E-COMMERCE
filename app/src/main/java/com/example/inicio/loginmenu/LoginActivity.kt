package com.example.inicio.loginmenu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inicio.MainActivity
import com.example.inicio.R
import com.example.inicio.RetrofitClient
import com.example.inicio.com.example.inicio.loginmenu.LoginRequest
import com.example.inicio.com.example.inicio.loginmenu.LoginResponse
import com.example.inicio.registromenu.RegisterActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.luxor_loggin)

        val emailEditText: TextInputEditText = findViewById(R.id.etEmail)
        val passwordEditText: TextInputEditText = findViewById(R.id.etPassword)
        val loginButton: Button = findViewById(R.id.btnLogin)
        val registerTextView: TextView = findViewById(R.id.tvRegister)

        // Botón de inicio de sesión
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty()) {
                emailEditText.error = "Por favor, ingresa tu correo electrónico"
                return@setOnClickListener
            } else {
                emailEditText.error = null
            }

            if (password.isEmpty()) {
                passwordEditText.error = "Por favor, ingresa tu contraseña"
                return@setOnClickListener
            } else {
                passwordEditText.error = null
            }

            val loginRequest = LoginRequest(email, password)
            loginUser(loginRequest)
        }

        // Ir al registro
        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(loginRequest: LoginRequest) {
        RetrofitClient.apiService.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.success == true) {
                        saveSessionData(loginResponse)

                        // Redirigir al MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val message = loginResponse?.message ?: "Credenciales incorrectas"
                        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Error del servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveSessionData(loginResponse: LoginResponse) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Guardar el token de sesión y otros datos del usuario
        editor.putString("session_token", loginResponse.session_token ?: "")
        editor.putInt("user_id", loginResponse.user?.id ?: 0)
        editor.putString("user_name", loginResponse.user?.nombre ?: "")
        editor.putString("user_email", loginResponse.user?.correo_electronico ?: "")

        // Guardar estado de sesión
        editor.putBoolean("is_logged_in", true)

        // Aplicar cambios
        editor.apply()
    }
}
