package com.example.taller2.main.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.example.taller2.R
import com.example.taller2.data.UsuarioRepository
import kotlinx.coroutines.launch

class PerfilFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivFoto    = view.findViewById<ImageView>(R.id.iv_foto_perfil)
        val tvNombre  = view.findViewById<TextView>(R.id.tv_perfil_nombre)
        val tvRol     = view.findViewById<TextView>(R.id.tv_perfil_rol)
        val tvCorreo  = view.findViewById<TextView>(R.id.tv_perfil_correo)
        val btnEditar = view.findViewById<Button>(R.id.btn_editar_perfil)

        // Cargar datos del usuario desde Supabase
        lifecycleScope.launch {
            val Usuarios = UsuarioRepository.obtenerUsuarioActual()
            android.util.Log.d("DEBUG_PERFIL", "foto_url: ${Usuarios?.foto_url}")
            if (Usuarios != null) {
                tvNombre.text  = "${Usuarios.nombres} ${Usuarios.apellidos}"
                tvCorreo.text  = Usuarios.correo ?: ""
                tvRol.text     = Usuarios.rol.replaceFirstChar { it.uppercase() }

                // Cargar foto con Coil si existe URL
                if (!Usuarios.foto_url.isNullOrEmpty()) {
                    // Agregar timestamp a la URL para forzar que Coil
                    // descargue la imagen nueva ignorando el caché.
                    // La URL real no cambia, el timestamp es solo
                    // para engañar al sistema de caché de Coil.
                    val urlConTimestamp = "${Usuarios.foto_url}?t=${System.currentTimeMillis()}"

                    ivFoto?.load(urlConTimestamp) {
                        transformations(CircleCropTransformation())
                        placeholder(R.mipmap.ic_logo_round)
                        error(R.mipmap.ic_logo_round)
                        memoryCachePolicy(CachePolicy.DISABLED)
                        diskCachePolicy(CachePolicy.DISABLED)
                    }
                }
            } else {
                Toast.makeText(requireContext(),
                    "Error al cargar perfil", Toast.LENGTH_SHORT).show()
            }
        }

        btnEditar.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContenedor, EditarPerfilFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}