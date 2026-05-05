package com.example.taller2.activities

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.taller2.R
import com.example.taller2.SupabaseClient
import com.example.taller2.data.UsuarioRepository


import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import org.slf4j.MDC.put

import kotlin.jvm.java

class  registro : AppCompatActivity() {

    private lateinit var etNombres: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var etReContrasena: EditText
    private lateinit var checkTerminos: CheckBox
    private lateinit var btnRegistro: Button
    private  lateinit var tvCuenta: TextView



@Serializable
data class UsuarioData(
    val id: String,
    val nombres: String,
    val apellidos: String,


        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_registro)
        val scrollView = findViewById<ViewGroup>(R.id.ScrollRegistro)

        ViewCompat.setOnApplyWindowInsetsListener(scrollView){ v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeIntents = insets.getInsets(WindowInsetsCompat.Type.ime())

            val bottomPading = maxOf(systemBars.bottom, imeIntents.bottom)

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                bottomPading
            )
            insets


        }


        val volver = findViewById<TextView>(R.id.VolverLogIn)
        volver.setOnClickListener {
            val intent = Intent(this, PantallaLogin::class.java)
            startActivity(intent)
            finish() // Opcional
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainRegistro)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        etNombres= findViewById(R.id.InputNomrbe)
        etApellidos = findViewById(R.id.InputApellido)
        etCorreo = findViewById(R.id.InputCorreo)
        etContrasena = findViewById(R.id.InputContrasena)
        etReContrasena =findViewById(R.id.InputContrasenaRepetir)
        checkTerminos = findViewById(R.id.checkBoxRegistro)
        btnRegistro = findViewById(R.id.botonRegistrar)
        tvCuenta =  findViewById(R.id.Re_cuenta)



        btnRegistro.setOnClickListener {
            val nombres = etNombres.text.toString().trim()
            val apellidos = etApellidos.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()
            val reContrasena = etReContrasena.text.toString().trim()
            val terminosAceptados = checkTerminos.isChecked


            //Validaciones
            if(nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || reContrasena.isEmpty()){

                Toast.makeText(this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
            if(!checkTerminos.isChecked){
                Toast.makeText(this, "Debe aceptar los terminos y condiciones", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(contrasena!=reContrasena){
                Toast.makeText(this, "Las Contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (contrasena.length<8){
                Toast.makeText(this, "La contraseña debe tener almenoz 8 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {

                try {
                    SupabaseClient.client.auth.signUpWith(Email){
                        email = correo
                        password = contrasena
                        data= buildJsonObject {
                            put("nombres", nombres)
                            put("apellidos", apellidos)


                        }
                    }

                    val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: ""
                    UsuarioRepository.insertarUsuario(userId, nombres, apellidos, correo)

                    runOnUiThread {
                        Toast.makeText(this@registro, "Registro exitoso", Toast.LENGTH_SHORT).show()

                        startActivity(Intent(this@registro, PantallaLogin::class.java))
                        finish()
                    }

                }catch (e: Exception){
                    runOnUiThread {
                        Toast.makeText(this@registro, "Error en el registro: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                tvCuenta.setOnClickListener {
                    startActivity(Intent(this@registro, PantallaLogin::class.java))
                    finish()
                }

                }
            }



        }



    }




}