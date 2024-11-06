package com.example.inicio

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

        // Configura el campo de búsqueda
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                productAdapter.filter(s.toString()) // Filtra los productos según el texto ingresado
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Configurar filtros de categoría
        setupCategoryFilters()

        // Configurar botón de inicio
        setupHomeButton()

        // Configurar botón de navegación
        setupNavigationButton()

        // Cargar los productos desde la API
        loadProducts()
    }

    private fun setupCategoryFilters() {
        // Configurar OnClickListeners para cada categoría
        findViewById<TextView>(R.id.category_todo).setOnClickListener { showAllProducts() }
        findViewById<TextView>(R.id.category_electronica).setOnClickListener { filterByCategory(1) }
        findViewById<TextView>(R.id.category_ropa).setOnClickListener { filterByCategory(2) }
        findViewById<TextView>(R.id.category_hogar).setOnClickListener { filterByCategory(3) }
        findViewById<TextView>(R.id.category_deportes).setOnClickListener { filterByCategory(4) }
        findViewById<TextView>(R.id.category_juguetes).setOnClickListener { filterByCategory(5) }
    }

    private fun showAllProducts() {
        // Muestra todos los productos en el adaptador
        productAdapter.updateProducts(products)
        productAdapter.notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado
    }

    private fun filterByCategory(categoryId: Int) {
        val filteredProducts = products.filter { it.id_categoria == categoryId }
        productAdapter.updateProducts(filteredProducts)
        if (filteredProducts.isEmpty()) {
            Toast.makeText(this, "No hay productos en esta categoría", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupHomeButton() {
        // Configurar el botón de inicio
        val homeButton: ImageView = findViewById(R.id.icono1)
        homeButton.setOnClickListener {
            // Volver al inicio del RecyclerView
            recyclerView.scrollToPosition(0)
            Toast.makeText(this, "Volviendo al inicio", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupNavigationButton() {
        // Configurar el botón de navegación
        val iconoNavegacion: ImageView = findViewById(R.id.icono_navegacion)
        iconoNavegacion.setOnClickListener {
            // Crear el PopupMenu
            val popupMenu = PopupMenu(this, iconoNavegacion)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

            // Configurar acciones para cada opción del menú
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

            // Mostrar el menú
            popupMenu.show()
        }
    }

    private fun loadProducts() {
        // Llamada a la API para obtener los productos
        RetrofitClient.apiService.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val fetchedProducts = response.body()
                    fetchedProducts?.let {
                        // Actualizar la lista de productos y notificar al adaptador
                        products.clear() // Limpia la lista antes de agregar nuevos productos
                        products.addAll(it)
                        Log.d("MainActivity", "Fetched Products: $it")
                        productAdapter.notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado
                    } ?: run {
                        Toast.makeText(this@MainActivity, "No se encontraron productos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "Error fetching products: ${t.message}")
            }
        })
    }
}
