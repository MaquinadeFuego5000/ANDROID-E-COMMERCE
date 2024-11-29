package com.example.inicio.com.example.inicio.misventas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inicio.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Callback

class VentasAdapter(private var ventas: List<VentaResponse>) : RecyclerView.Adapter<VentasAdapter.VentaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VentaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_venta, parent, false)
        return VentaViewHolder(view)
    }

    override fun onBindViewHolder(holder: VentaViewHolder, position: Int) {
        val venta = ventas[position]

        // Asigna los datos de la venta
        holder.tvIdVenta.text = "Venta #${venta.id_venta}"
        holder.tvFechaVenta.text = "Fecha: ${venta.fecha_venta}"
        holder.tvComprador.text = "Comprador: ${venta.nombre_comprador}"
        holder.tvSubtotalVenta.text = "Subtotal: $${venta.subtotal_venta}"
        holder.tvTotalVenta.text = "Total: $${venta.total_venta}"

        // Asigna el LayoutManager para el RecyclerView de productos
        holder.rvProductos.layoutManager = LinearLayoutManager(holder.itemView.context)

        // Adaptador para los productos de la venta actual
        val productosAdapter = ProductosAdapter(venta.productos)
        holder.rvProductos.adapter = productosAdapter
    }

    override fun getItemCount(): Int = ventas.size

    fun updateVentas(ventas: List<VentaResponse>) {
        this.ventas = ventas
        notifyDataSetChanged()
    }

    inner class VentaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIdVenta: TextView = itemView.findViewById(R.id.tv_id_venta)
        val tvFechaVenta: TextView = itemView.findViewById(R.id.tv_fecha_venta)
        val tvComprador: TextView = itemView.findViewById(R.id.tv_comprador)
        val tvSubtotalVenta: TextView = itemView.findViewById(R.id.tv_subtotal_venta)
        val tvTotalVenta: TextView = itemView.findViewById(R.id.tv_total_venta)
        val rvProductos: RecyclerView = itemView.findViewById(R.id.rv_productos)
    }

    class ProductosAdapter(private val productos: List<ProductoResponse>) : RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto_venta, parent, false)
            return ProductoViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
            val producto = productos[position]
            holder.tvNombreProducto.text = producto.nombre_producto
            holder.tvCantidadProducto.text = "Cantidad: ${producto.cantidad}"

            // Cargar la imagen con Picasso
            Picasso.get()
                .load(producto.imagen_url)
                .resize(200, 200) // Redimensiona la imagen para optimizarla
                .centerCrop() // Recorta la imagen para ajustarse al ImageView
                .error(R.drawable.error_image) // Imagen de error en caso de fallo
                .into(holder.ivImagenProducto, object : Callback {
                    override fun onSuccess() {
                        // Imagen cargada correctamente
                    }

                    override fun onError(e: Exception?) {
                        // Registra el error si no se puede cargar la imagen
                        e?.printStackTrace()
                    }
                })
        }

        override fun getItemCount(): Int = productos.size

        inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ivImagenProducto: ImageView = itemView.findViewById(R.id.iv_imagen_producto)
            val tvNombreProducto: TextView = itemView.findViewById(R.id.tv_nombre_producto)
            val tvCantidadProducto: TextView = itemView.findViewById(R.id.tv_cantidad_producto)
        }
    }
}
