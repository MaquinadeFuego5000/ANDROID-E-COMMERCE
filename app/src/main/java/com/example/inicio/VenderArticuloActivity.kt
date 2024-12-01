package com.example.inicio

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.inicio.databinding.ActivityVenderArticuloBinding
import com.example.inicio.loginmenu.LoginActivity
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

class VenderArticuloActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVenderArticuloBinding
    private var selectedImageUri: Uri? = null

    companion object {
        const val PICK_IMAGE_REQUEST = 1
        const val PERMISSION_REQUEST_CODE = 123
        const val MAX_IMAGE_SIZE_MB = 5L // Max image size in MB
        val ALLOWED_IMAGE_EXTENSIONS = listOf("jpg", "jpeg", "png")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVenderArticuloBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Agregar la funcionalidad para el botón de regresar
        binding.btnBack.setOnClickListener {
            onBackPressed()  // Regresa a la actividad anterior
        }

        checkPermissions()

        // Verificar si hay sesión activa
        if (obtenerSessionToken().isBlank()) {
            // Redirigir a LoginActivity si no hay sesión activa
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Cerrar la actividad actual
            return
        }

        // Configuración del spinner
        val categorias = listOf("Electrónica", "Ropa", "Hogar", "Deportes", "Juguetes")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categoria.adapter = adapter

        // Manejo de eventos
        binding.imagen.setOnClickListener { abrirGaleria() }
        binding.btnAgregarProducto.setOnClickListener { publicarArticulo() }
    }

    // Verificar permisos para acceder a la galería
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }

    // Abrir la galería para seleccionar imagen
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Manejar el resultado de la selección de imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri?.let { binding.nombreImagenSeleccionada.text = "Imagen seleccionada: ${getFileNameFromUri(it)}" }
        }
    }

    // Obtener el nombre del archivo de la imagen seleccionada
    private fun getFileNameFromUri(uri: Uri?): String {
        uri?.let {
            contentResolver.query(it, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    return cursor.getString(nameIndex)
                }
            }
        }
        return "archivo_desconocido"
    }

    // Publicar el artículo después de la validación
    private fun publicarArticulo() {
        val nombreArticulo = binding.nombreProducto.text.toString().trim()
        val descripcion = binding.descripcion.text.toString().trim()
        val precio = binding.precio.text.toString().toDoubleOrNull()
        val stock = binding.stock.text.toString().toIntOrNull()
        val categoria = binding.categoria.selectedItemPosition + 1

        // Limpiar mensajes de error
        binding.msgError.visibility = android.view.View.GONE

        // Validación de los campos
        when {
            nombreArticulo.isEmpty() || descripcion.isEmpty() || precio == null || stock == null || selectedImageUri == null -> showError("Por favor completa todos los campos")
            nombreArticulo.length > 45 -> showError("El nombre del producto no debe exceder los 45 caracteres.")
            descripcion.length > 1000 -> showError("La descripción no debe exceder los 1000 caracteres.")
            precio < 0.01 || precio > 99999 -> showError("El precio debe estar entre 0.01 y 99,999.")
            stock <= 0 -> showError("El stock debe ser mayor que 0.")
            categoria == 0 -> showError("La categoría es obligatoria.")
            obtenerSessionToken().isBlank() -> showError("No hay sesión activa. Inicia sesión.")
            else -> publicarArticuloConImagen(nombreArticulo, descripcion, precio, stock, categoria)
        }
    }

    // Mostrar el mensaje de error
    private fun showError(message: String) {
        binding.msgError.text = message
        binding.msgError.visibility = android.view.View.VISIBLE
    }

    // Publicar el artículo con imagen
    private fun publicarArticuloConImagen(
        nombreArticulo: String, descripcion: String, precio: Double, stock: Int,
        categoria: Int
    ) {
        lifecycleScope.launch {
            try {
                val file = File(cacheDir, getFileNameFromUri(selectedImageUri))
                val fileSize = file.length() / 1024 / 1024 // Tamaño en MB

                // Validaciones de tamaño y formato de imagen
                if (fileSize > MAX_IMAGE_SIZE_MB) {
                    showError("La imagen debe ser menor de 5 MB.")
                    return@launch
                }
                if (file.extension.lowercase() !in ALLOWED_IMAGE_EXTENSIONS) {
                    showError("Solo se permiten imágenes en formatos JPG, JPEG o PNG.")
                    return@launch
                }

                // Guardar imagen en el dispositivo
                contentResolver.openInputStream(selectedImageUri!!)?.use { input ->
                    FileOutputStream(file).use { output -> input.copyTo(output) }
                }

                // Crear solicitud de Retrofit
                val imagenPart = MultipartBody.Part.createFormData("imagen", file.name, RequestBody.create("image/*".toMediaTypeOrNull(), file))
                val nombreProductoRequestBody = RequestBody.create(MultipartBody.FORM, nombreArticulo)
                val descripcionRequestBody = RequestBody.create(MultipartBody.FORM, descripcion)
                val precioRequestBody = RequestBody.create(MultipartBody.FORM, precio.toString())
                val stockRequestBody = RequestBody.create(MultipartBody.FORM, stock.toString())
                val categoriaRequestBody = RequestBody.create(MultipartBody.FORM, categoria.toString())
                val sessionTokenRequestBody = RequestBody.create(MultipartBody.FORM, obtenerSessionToken())

                // Enviar solicitud
                val response = RetrofitClient.apiService.subirProducto(
                    sessionTokenRequestBody, nombreProductoRequestBody, descripcionRequestBody,
                    precioRequestBody, stockRequestBody, categoriaRequestBody, imagenPart
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@VenderArticuloActivity, "Artículo publicado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    showError("Error desconocido. Intenta nuevamente.")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            }
        }
    }

    // Obtener el token de sesión desde SharedPreferences
    private fun obtenerSessionToken(): String {
        return getSharedPreferences("user_prefs", MODE_PRIVATE).getString("session_token", "") ?: ""
    }

    // Manejar resultados de la solicitud de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val message = if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                "Permiso concedido"
            } else {
                "Permiso denegado, no podrás seleccionar una imagen"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
