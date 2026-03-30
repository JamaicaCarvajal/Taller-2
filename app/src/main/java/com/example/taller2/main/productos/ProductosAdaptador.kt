package com.example.taller2.main.productos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taller2.R


class ProductosAdaptador(private val productos: List<producto>) :
    RecyclerView.Adapter<ProductosAdaptador.ProductoViewHolder>() {

    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagen: ImageView = itemView.findViewById(R.id.imgProduct)
        val nombre: TextView = itemView.findViewById(R.id.NombreProducto)
        val precio: TextView = itemView.findViewById(R.id.PrecioTexto)
        val btnAgregar: Button = itemView.findViewById(R.id.botonAgregar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun getItemCount(): Int = productos.size

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.imagen.setImageResource(producto.imageRes)
        holder.nombre.text = producto.nombre
        holder.precio.text  = "$${producto.precio}"
        holder.btnAgregar.setOnClickListener {
            //agregar al carrito :p
        }


    }




}