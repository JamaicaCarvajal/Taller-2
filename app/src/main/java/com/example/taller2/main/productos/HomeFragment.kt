package com.example.taller2.main.productos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taller2.R


class HomeFragment : Fragment() {

    private val listaProductos = listOf(
        producto(nombre = "Camisa Casual", precio = 10.99, imageRes = R.drawable.camisauno),
        producto(nombre = "Camisa Polo", precio = 15.99, imageRes = R.drawable.camisados),
        producto(nombre = "Camisa Sport", precio = 12.99, imageRes = R.drawable.camisatres),
        producto(nombre = "Chaqueta de Cuero", precio = 72.99, imageRes = R.drawable.camisacuatro),
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.RecyclerProductos)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = ProductosAdaptador(listaProductos)

        return view
    }

}
