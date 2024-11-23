package com.example.inicio

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val products: MutableList<Product> = mutableListOf()
    private lateinit var searchInput: EditText // Campo de búsqueda

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si el usuario está autenticado
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (!isLoggedIn) {
            // Redirigir a LoginActivity si no está autenticado
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Habilitar el modo edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializar el adaptador con la lista de productos
        productAdapter = ProductAdapter(products)
        recyclerView.adapter = productAdapter

        // Inicializar campo de búsqueda
        searchInput = findViewById(R.id.search_input)

        // Configurar el campo de búsqueda
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                productAdapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Configurar botones y filtros
        setupCategoryFilters()
        setupHomeButton()
        setupNavigationButton()
        setupCartButton()
        setupProfileButton() // Configurar el botón de perfil

        // Cargar los productos desde la API
        loadProducts()
    }

    private fun setupCartButton() {
        val carritoButton: ImageView = findViewById(R.id.icono3)
        carritoButton.setOnClickListener {
            val intent = Intent(this, CarritoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupCategoryFilters() {
        findViewById<TextView>(R.id.category_todo).setOnClickListener { showAllProducts() }
        findViewById<TextView>(R.id.category_electronica).setOnClickListener { filterByCategory(1) }
        findViewById<TextView>(R.id.category_ropa).setOnClickListener { filterByCategory(2) }
        findViewById<TextView>(R.id.category_hogar).setOnClickListener { filterByCategory(3) }
        findViewById<TextView>(R.id.category_deportes).setOnClickListener { filterByCategory(4) }
        findViewById<TextView>(R.id.category_juguetes).setOnClickListener { filterByCategory(5) }
    }

    private fun setupProfileButton() {
        val profileButton: ImageView = findViewById(R.id.icono2) // Cambia "icono_perfil" por el ID real de tu botón/ícono
        profileButton.setOnClickListener {
            val intent = Intent(this, UsuarioInfoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showAllProducts() {
        productAdapter.updateProducts(products)
        productAdapter.notifyDataSetChanged()
    }

    private fun filterByCategory(categoryId: Int) {
        val filteredProducts = products.filter { it.id_categoria == categoryId }
        productAdapter.updateProducts(filteredProducts)
        if (filteredProducts.isEmpty()) {
            Toast.makeText(this, "No hay productos en esta categoría", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupHomeButton() {
        val homeButton: ImageView = findViewById(R.id.icono1)
        homeButton.setOnClickListener {
            recyclerView.scrollToPosition(0)
            Toast.makeText(this, "Volviendo al inicio", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupNavigationButton() {
        val iconoNavegacion: ImageView = findViewById(R.id.icono_navegacion)
        iconoNavegacion.setOnClickListener {
            val popupMenu = PopupMenu(this, iconoNavegacion)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.opcion_vender_articulo -> {
                        Toast.makeText(this, "Opción: Vender artículo", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.opcion_mis_compras -> {
                        Toast.makeText(this, "Opción: Mis compras", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.opcion_mis_ventas -> {
                        Toast.makeText(this, "Opción: Mis ventas", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    private fun loadProducts() {
        RetrofitClient.apiService.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        products.clear()
                        products.addAll(it)
                        productAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
