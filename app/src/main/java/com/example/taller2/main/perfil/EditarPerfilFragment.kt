package com.example.taller2.main.perfil


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import java.io.File
import android.Manifest

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.taller2.R
import com.example.taller2.SupabaseClient
import com.example.taller2.data.UsuarioRepository

class EditarPerfilFragment : Fragment() {

    private var uriFotoSeleccionada: Uri? = null
    private lateinit var ivEditarFoto: ImageView
    private lateinit var archivoFotoTemp: File

    // Lanzador para solicitar el permiso de cámara.
// Se ejecuta cuando el usuario acepta o rechaza el permiso.
    private val lanzadorPermisoCamara =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { concedido ->
            if (concedido) {
                // El usuario aceptó → abrir la cámara
                abrirCamara()
            } else {
                // El usuario rechazó → mostrar mensaje
                Toast.makeText(requireContext(),
                    "Se necesita permiso de cámara para tomar fotos",
                    Toast.LENGTH_SHORT).show()
            }
        }

    // Lanzador para la cámara
    private val lanzadorCamara =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
            if (exito) {
                uriFotoSeleccionada = Uri.fromFile(archivoFotoTemp)
                ivEditarFoto.load(uriFotoSeleccionada) {
                    transformations(CircleCropTransformation())
                }
            }
        }

    // Lanzador para la galería
    private val lanzadorGaleria =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                uriFotoSeleccionada = uri
                ivEditarFoto.load(uri) {
                    transformations(CircleCropTransformation())
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_edit_peril, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        ivEditarFoto = view.findViewById(R.id.iv_foto_perfil)
        val etNombres      = view.findViewById<EditText>(R.id.InputNomrbeEditPerfl)
        val etApellidos    = view.findViewById<EditText>(R.id.InputApellidoEditPerfl)
        val etCorreo       = view.findViewById<EditText>(R.id.InputCorreoEditPerfl)
        val etContrasena   = view.findViewById<EditText>(R.id.InputContrasenaEditPerfl)
        val etReContrasena = view.findViewById<EditText>(R.id.InputContrasenaRepetirEditPerfl)
        val btnGuardar     = view.findViewById<Button>(R.id.botonEditPerfl)

        // Cargar datos actuales en los campos
        lifecycleScope.launch {
            val usuario = UsuarioRepository.obtenerUsuarioActual()
            if (usuario != null) {
                etNombres.setText(usuario.nombres)
                etApellidos.setText(usuario.apellidos)
                etCorreo.setText(usuario.correo ?: "")

                if (!usuario.foto_url.isNullOrEmpty()) {
                    ivEditarFoto.load(usuario.foto_url) {
                        transformations(CircleCropTransformation())
                        placeholder(R.mipmap.ic_logo_round)
                        error(R.mipmap.ic_logo_round)
                    }
                }
            }
        }

        // Click en el ícono de cámara → mostrar opciones
        ivEditarFoto.setOnClickListener {
            mostrarOpcionesFoto()
        }

        // Guardar cambios
        btnGuardar.setOnClickListener {
            guardarCambios(
                etNombres, etApellidos, etCorreo,
                etContrasena, etReContrasena
            )
        }
    }

    // Muestra un diálogo para elegir entre cámara y galería
    private fun mostrarOpcionesFoto() {
        val opciones = arrayOf("Tomar foto", "Elegir de galería")
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Foto de perfil")
            .setItems(opciones) { _, cual ->
                when (cual) {
                    0 -> verificarPermisoCamara() // ← cambio aquí
                    1 -> lanzadorGaleria.launch("image/*")
                }
            }
            .show()
    }

    private fun verificarPermisoCamara() {
        when {
            // El permiso ya fue concedido anteriormente → abrir cámara
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                abrirCamara()
            }

            // El permiso fue rechazado antes → explicar por qué
            // se necesita antes de volver a pedirlo
            shouldShowRequestPermissionRationale(
                Manifest.permission.CAMERA
            ) -> {
                android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Permiso de cámara")
                    .setMessage("Necesitamos acceso a la cámara para que puedas tomar tu foto de perfil.")
                    .setPositiveButton("Entendido") { _, _ ->
                        lanzadorPermisoCamara.launch(Manifest.permission.CAMERA)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }

            // Primera vez → solicitar el permiso directamente
            else -> {
                lanzadorPermisoCamara.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun abrirCamara() {
        // Crear archivo temporal para guardar la foto
        val carpeta = File(requireContext().cacheDir, "images")
        carpeta.mkdirs()
        archivoFotoTemp = File(carpeta, "foto_perfil_temp.jpg")

        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            archivoFotoTemp
        )
        lanzadorCamara.launch(uri)
    }

    private fun guardarCambios(
        etNombres: EditText,
        etApellidos: EditText,
        etCorreo: EditText,
        etContrasena: EditText,
        etReContrasena: EditText
    ) {
        val nombres    = etNombres.text.toString().trim()
        val apellidos  = etApellidos.text.toString().trim()
        val correo     = etCorreo.text.toString().trim()
        val contrasena = etContrasena.text.toString()
        val reContrasena = etReContrasena.text.toString()



        lifecycleScope.launch {
            try {
                // Subir foto si se seleccionó una nueva
                var fotoUrl: String? = null
                if (uriFotoSeleccionada != null) {
                    fotoUrl = UsuarioRepository.subirFotoPerfil(
                        requireContext(),
                        uriFotoSeleccionada!!
                    )
                    android.util.Log.d("DEBUG_FOTO", "fotoUrl retornada: $fotoUrl")
                }

                // Actualizar datos en la tabla usuarios
                UsuarioRepository.actualizarPerfil(
                    nombres   = nombres,
                    apellidos = apellidos,
                    correo    = correo,
                    fotoUrl   = fotoUrl
                )

                // Actualizar contraseña en Supabase Auth si se ingresó una nueva
                if (contrasena.isNotEmpty()) {
                    SupabaseClient.client.auth.updateUser {
                        password = contrasena
                    }
                }

                runOnUiThread {
                    Toast.makeText(requireContext(),
                        "Perfil actualizado correctamente",
                        Toast.LENGTH_SHORT).show()
                    // Volver al PerfilFragment
                    parentFragmentManager.popBackStack()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(requireContext(),
                        "Error al guardar: ${e.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun runOnUiThread(action: () -> Unit) {
        activity?.runOnUiThread(action)
    }
}