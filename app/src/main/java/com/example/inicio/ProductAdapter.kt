package com.example.inicio

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.inicio.com.example.inicio.carritomenu.ApiResponse
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.Normalizer

class ProductAdapter(
    private val products: List<Product>,
    private val context: Context // Se pasa un Contexto aquí
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var filteredList: List<Product> = products // Copia la lista original
    private val apiService: ApiService = RetrofitClient.apiService // Se inicializa aquí

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val productDescription: TextView = itemView.findViewById(R.id.product_description)
        val productPublisher: TextView = itemView.findViewById(R.id.product_publisher)
        val productStock: TextView = itemView.findViewById(R.id.product_stock)
        val addToCartButton: Button = itemView.findViewById(R.id.add_to_cart_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = filteredList[position]
        holder.productName.text = product.nombre_producto
        holder.productPrice.text = "$${product.precio}"
        holder.productDescription.text = product.descripcion
        holder.productPublisher.text = product.nombre_usuario
        holder.productStock.text = "Stock: ${product.stock}"

        // Carga de la imagen con Picasso
        Picasso.get()
            .load(product.imagen)
            .resize(200, 200)
            .centerCrop()
            .error(R.drawable.error_image)
            .into(holder.productImage)

        // Configurar el OnClickListener para agregar al carrito
        holder.addToCartButton.setOnClickListener {
            agregarProductoAlCarrito(product.id_producto, 1) // Agregar con cantidad 1
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    private fun removeAccents(text: String): String {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }

    fun filter(query: String) {
        val cleanedQuery = removeAccents(query.replace(",", "").trim())
        filteredList = if (cleanedQuery.isEmpty()) {
            products
        } else {
            products.filter {
                removeAccents(it.nombre_producto).contains(cleanedQuery, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    fun updateProducts(newProducts: List<Product>) {
        filteredList = newProducts
        notifyDataSetChanged()
    }

    private fun obtenerSessionToken(): String {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("session_token", "") ?: ""
    }

    private fun agregarProductoAlCarrito(idProducto: Int, cantidad: Int) {
        val sessionToken = obtenerSessionToken()

        // Verifica que el token no esté vacío antes de hacer la llamada
        if (sessionToken.isEmpty()) {
            Toast.makeText(context, "No se ha iniciado sesión", Toast.LENGTH_SHORT).show()
            Log.e("ProductAdapter", "Token de sesión vacío")
            return
        }

        val call = apiService.agregarProductoAlCarrito(idProducto, cantidad, sessionToken)

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    // Código si la llamada fue exitosa
                } else {
                    Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
