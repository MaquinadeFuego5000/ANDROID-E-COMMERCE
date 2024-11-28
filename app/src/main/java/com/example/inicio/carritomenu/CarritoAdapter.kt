package com.example.inicio.com.example.inicio.carritomenu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inicio.R


class CarritoAdapter(private val listaProductos: List<ProductoCarrito>, private val listener: OnItemClickListener) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    interface OnItemClickListener {
        fun onUpdateQuantityClick(position: Int, cantidad: Int)
        fun onRemoveFromCartClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carrito, parent, false)
        return CarritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val producto = listaProductos[position]
        holder.bind(producto, position)
    }

    override fun getItemCount(): Int = listaProductos.size

    inner class CarritoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.product_name)
        private val productPrice: TextView = itemView.findViewById(R.id.product_price)
        private val productQuantity: EditText = itemView.findViewById(R.id.product_quantity)
        private val updateQuantityButton: Button = itemView.findViewById(R.id.update_quantity_button)
        private val removeFromCartButton: Button = itemView.findViewById(R.id.remove_from_cart_button)

        fun bind(producto: ProductoCarrito, position: Int) {
            productName.text = producto.nombre_producto
            productPrice.text = "$${producto.precio}"
            productQuantity.setText(producto.cantidad.toString())

            updateQuantityButton.setOnClickListener {
                val cantidad = productQuantity.text.toString().toIntOrNull() ?: return@setOnClickListener
                listener.onUpdateQuantityClick(position, cantidad)
            }

            removeFromCartButton.setOnClickListener {
                listener.onRemoveFromCartClick(position)
            }
        }
    }
}
