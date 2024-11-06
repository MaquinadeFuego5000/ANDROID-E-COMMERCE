package com.example.inicio

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ProductAdapter(private val products: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var filteredList: List<Product> = products // Copia la lista original

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val productDescription: TextView = itemView.findViewById(R.id.product_description)
        val productPublisher: TextView = itemView.findViewById(R.id.product_publisher)
        val productStock: TextView = itemView.findViewById(R.id.product_stock)
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
            .error(R.drawable.error_image) // Imagen de error si la carga falla
            .into(holder.productImage)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    // Método para filtrar productos
    fun filter(query: String) {
        // Eliminar comas y espacios en blanco de la cadena de búsqueda
        val cleanedQuery = query.replace(",", "").trim()

        filteredList = if (cleanedQuery.isEmpty()) {
            products // Si la búsqueda está vacía, muestra todos
        } else {
            products.filter {
                it.nombre_producto.contains(cleanedQuery, ignoreCase = true) // Filtra por nombre
            }
        }
        notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado
    }

    // Método para actualizar la lista de productos
    fun updateProducts(newProducts: List<Product>) {
        filteredList = newProducts // Actualiza la lista filtrada con los nuevos productos
        notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado
    }
}
