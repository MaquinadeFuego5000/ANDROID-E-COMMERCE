package com.example.inicio.com.example.inicio.registromenu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inicio.com.example.inicio.loginmenu.LoginActivity
import com.example.inicio.R
import com.example.inicio.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicialización de vistas
        val etName: EditText = findViewById(R.id.etName)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val etAddress: EditText = findViewById(R.id.etAddress)
        val etPhoneNumber: EditText = findViewById(R.id.etPhoneNumber)
        val btnRegister: Button = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val address = etAddress.text.toString()
            val phone = etPhoneNumber.text.toString()

            if (validateInputs(name, email, password, address, phone)) {
                val newUser = RegistrarUsuario(
                    nombre = name,
                    correo_electronico = email,
                    direccion = address,
                    telefono = phone,
                    contrasena = password // Usamos la contraseña directamente para el registro
                )
                registerUser(newUser)
            }
        }
    }

    private fun validateInputs(name: String, email: String, password: String, address: String, phone: String): Boolean {
        return when {
            name.isEmpty() -> {
                showToast("Por favor, ingrese su nombre.")
                false
            }
            email.isEmpty() || !email.contains("@") -> {
                showToast("Por favor, ingrese un correo electrónico válido.")
                false
            }
            password.isEmpty() -> {
                showToast("Por favor, ingrese una contraseña.")
                false
            }
            address.isEmpty() -> {
                showToast("Por favor, ingrese una dirección.")
                false
            }
            phone.isEmpty() || phone.length != 10 -> {
                showToast("Por favor, ingrese un número de teléfono válido.")
                false
            }
            else -> true
        }
    }

    private fun registerUser(registrarUsuario: RegistrarUsuario) {
        RetrofitClient.apiService.registrarUsuario(registrarUsuario)
            .enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful) {
                        showToast("Registro exitoso. Bienvenido!")
                        redirectToLogin()
                    } else {
                        val errorMessage = response.errorBody()?.string()  // Obtener el cuerpo del error
                        if (response.code() == 409) {
                            showToast("El correo electrónico ya está registrado.")
                        } else {
                            // Si no hay mensaje, mostrar un mensaje genérico de error
                            showToast("Error al registrar el usuario. Intente de nuevo.")
                        }
                    }
                }


                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    showToast("Error de conexión: ${t.message}")
                }
            })
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun redirectToLogin() {
        // Redirige a la pantalla de inicio de sesión después del registro
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
