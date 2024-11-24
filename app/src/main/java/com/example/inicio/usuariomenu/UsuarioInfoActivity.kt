package com.example.inicio.com.example.inicio.usuariomenu

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.inicio.com.example.inicio.loginmenu.LoginActivity
import com.example.inicio.MainActivity
import com.example.inicio.R
import com.example.inicio.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsuarioInfoActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var message: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario_info)

        // Initializing views
        val ivCerrarSesion: ImageView = findViewById(R.id.ivCerrarSesion)
        val etName: EditText = findViewById(R.id.etName)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etAddress: EditText = findViewById(R.id.etAddress)
        val etPhone: EditText = findViewById(R.id.etPhone)
        val etCurrentPassword: EditText = findViewById(R.id.etCurrentPassword)
        val etNewPassword: EditText = findViewById(R.id.etNewPassword)
        val btnSave: Button = findViewById(R.id.btnSave)
        message = findViewById(R.id.message)

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId == -1) {
            redirectToLogin()
            return
        }

        loadUserData(userId, etName, etEmail, etAddress, etPhone)

        ivCerrarSesion.setOnClickListener { logout() }

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val address = etAddress.text.toString()
            val phone = etPhone.text.toString()
            val currentPassword = etCurrentPassword.text.toString()
            val newPassword = etNewPassword.text.toString()

            if (validateInputs(name, address, phone, currentPassword, newPassword)) {
                val userToUpdate = UsuarioActualizar(
                    id_usuario = userId,
                    nombre = name,
                    correo_electronico = etEmail.text.toString(),
                    direccion = address,
                    telefono = phone,
                    contrasena_actual = currentPassword,
                    nueva_contrasena = if (newPassword.isNotEmpty()) newPassword else null
                )
                updateUser(userId, userToUpdate)
            }
        }
    }

    private fun loadUserData(userId: Int, etName: EditText, etEmail: EditText, etAddress: EditText, etPhone: EditText) {
        RetrofitClient.apiService.obtenerUsuario(userId)
            .enqueue(object : Callback<UsuarioResponse> {
                override fun onResponse(call: Call<UsuarioResponse>, response: Response<UsuarioResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        response.body()?.data?.let {
                            etName.setText(it.nombre)
                            etEmail.setText(it.correo_electronico)
                            etAddress.setText(it.direccion)
                            etPhone.setText(it.telefono)
                        } ?: showError("No se pudo obtener la información del usuario.")
                    } else {
                        showError("Error al obtener la información del usuario.")
                    }
                }

                override fun onFailure(call: Call<UsuarioResponse>, t: Throwable) {
                    showError("Error de conexión: ${t.message}")
                }
            })
    }

    private fun updateUser(userId: Int, usuarioActualizar: UsuarioActualizar) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.actualizarUsuario(userId, usuarioActualizar)
                if (response.isSuccessful && response.body()?.success == true) {
                    showToast("Información actualizada exitosamente.")
                    redirectToMain()
                } else {
                    handleUpdateError(response.body())
                }
            } catch (e: Exception) {
                showError("Error de conexión: ${e.message}")
            }
        }
    }

    private fun validateInputs(name: String, address: String, phone: String, currentPassword: String, newPassword: String): Boolean {
        return when {
            name.isEmpty() || address.isEmpty() || phone.isEmpty() || currentPassword.isEmpty() -> {
                message.text = "Por favor, completa todos los campos obligatorios."
                false
            }
            newPassword.isNotEmpty() && currentPassword != newPassword -> true
            newPassword.isEmpty() && currentPassword != newPassword -> {
                message.text = "Si deseas mantener la misma contraseña, repítela en ambos campos."
                false
            }
            phone.length > 10 -> {
                message.text = "El número de teléfono no puede ser mayor a 10 dígitos."
                false
            }
            else -> true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleUpdateError(responseBody: UsuarioActualizacionResponse?) {
        // Verificar si la respuesta es válida y si la propiedad 'success' es falsa
        if (responseBody != null && !responseBody.success) {
            when (responseBody.message) {
                "La contraseña actual no es correcta." -> {
                    message.text = "La contraseña actual es incorrecta. Por favor, inténtalo de nuevo."
                    message.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                }
                else -> {
                    message.text = "Error al actualizar la información."
                    message.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                }
            }
        } else {
            showToast("Error al actualizar la información.")
        }
    }



    private fun redirectToLogin() {
        Toast.makeText(this, "ID de usuario no encontrado. Redirigiendo al inicio de sesión.", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun redirectToMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun logout() {
        sharedPreferences.edit().clear().apply()
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        redirectToLogin()
    }
}
