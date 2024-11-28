import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inicio.R
import com.example.inicio.com.example.inicio.miscompras.Pedido
import com.example.inicio.com.example.inicio.miscompras.Producto
import com.squareup.picasso.Picasso

// Adapter principal para manejar los pedidos
class MisComprasAdapter(private val pedidos: List<Pedido>) :
    RecyclerView.Adapter<MisComprasAdapter.PedidoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]

        // Configurar los datos del pedido
        holder.bind(pedido)
    }

    override fun getItemCount(): Int = pedidos.size

    // ViewHolder para los pedidos
    inner class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvIdPedido: TextView = view.findViewById(R.id.tvIdPedido)
        private val tvFechaPedido: TextView = view.findViewById(R.id.tvFechaPedido)
        private val tvMontoTotal: TextView = view.findViewById(R.id.tvMontoTotal)
        private val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        private val rvProductos: RecyclerView = view.findViewById(R.id.rvProductos)

        fun bind(pedido: Pedido) {
            // Mostrar los detalles del pedido
            tvIdPedido.text = "Pedido #${pedido.numero_pedido}"
            tvFechaPedido.text = "Fecha: ${pedido.fecha_pedido}"
            tvMontoTotal.text = "Total: $${pedido.monto_total}"
            tvEstado.text = "Estado: ${pedido.estado}"

            // Configurar el RecyclerView de productos dentro del pedido
            val productosAdapter = ProductosAdapter(pedido.productos)
            rvProductos.adapter = productosAdapter
            rvProductos.layoutManager = LinearLayoutManager(rvProductos.context)
        }
    }

    // Adapter interno para manejar los productos dentro de cada pedido
    class ProductosAdapter(private val productos: List<Producto>) :
        RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_compra, parent, false)
            return ProductoViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
            val producto = productos[position]
            holder.bind(producto)
        }

        override fun getItemCount(): Int = productos.size

        // ViewHolder para los productos
        inner class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val ivProductoImagen: ImageView = view.findViewById(R.id.ivProductoImagen)
            private val tvProductoNombre: TextView = view.findViewById(R.id.tvProductoNombre)
            private val tvCantidadPrecio: TextView = view.findViewById(R.id.tvCantidadPrecio)

            fun bind(producto: Producto) {
                // Configurar los datos del producto
                tvProductoNombre.text = producto.nombre_producto
                tvCantidadPrecio.text = "Cantidad: ${producto.cantidad}\n$${producto.precio}"

                // Cargar la imagen del producto usando Picasso
                Picasso.get()
                    .load(producto.imagen_url)
                    .resize(200, 200)
                    .centerCrop()
                    .error(R.drawable.error_image)
                    .into(ivProductoImagen)
            }
        }
    }
}
