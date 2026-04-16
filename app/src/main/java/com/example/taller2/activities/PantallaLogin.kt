package com.example.taller2.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText

import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.example.taller2.R
import com.example.taller2.SupabaseClient
import com.example.taller2.main.MainActivity

import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.launch
import kotlin.jvm.java
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.taller2.data.CredencialesManager

class PantallaLogin : AppCompatActivity() {

    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var tvRegistrar: TextView
    private lateinit var btnGoogle: Button
    private lateinit var tvRecuperarContrasena: TextView

    private lateinit var tvHuella: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pantalla_login)


        val rootView = findViewById<LinearLayout>(R.id.main)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = maxOf(systemBars.bottom, imeInsets.bottom)

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                bottomPadding
            )
            insets
        }

        etCorreo = findViewById(R.id.InputUsuario)
        etContrasena = findViewById(R.id.InputPasword)
        btnIniciarSesion = findViewById(R.id.botonPantallaLogIn)
        tvRecuperarContrasena = findViewById(R.id.RecuperarLogin)
        btnGoogle = findViewById(R.id.BotonGoogle)


        val textoRegistro = findViewById<TextView>(R.id.TextoRegistroLogIn)
        textoRegistro.setOnClickListener {
            val intent = Intent(this, registro::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        tvHuella = findViewById(R.id.in_huella)

        configurarVisibilidadHuella()

        tvHuella.setOnClickListener {
            mostrarDialogoHuella()


        }






        btnIniciarSesion.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Todos los campos son requeridos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener

            }

            if (contrasena.length < 8) {
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 8 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }



            lifecycleScope.launch {
                try {
                    SupabaseClient.client.auth.signInWith(Email) {
                        email = correo
                        password = contrasena
                    }
                    CredencialesManager.guardarCredenciales(
                        this@PantallaLogin, correo, contrasena
                    )

                    runOnUiThread {
                        Toast.makeText(
                            this@PantallaLogin,
                            "Inicio de sesion exitoso",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@PantallaLogin, MainActivity::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@PantallaLogin,
                        "Error en el inicio de sesion",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        btnGoogle.setOnClickListener {
            iniciarSesionConGoogle()


        }


    }


    override fun onResume() {
        super.onResume()
        configurarVisibilidadHuella()
    }

    private fun configurarVisibilidadHuella() {

        val huellaActiva = CredencialesManager.huellaActiva(this)

        val biometricManager = BiometricManager.from(this)
        val biometriaDisponible = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        ) == BiometricManager.BIOMETRIC_SUCCESS

        tvHuella.visibility = if (huellaActiva && biometriaDisponible)
            android.view.View.VISIBLE
        else
            android.view.View.GONE
    }


    private fun mostrarDialogoHuella() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                // Huella reconocida correctamente
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    val correo = CredencialesManager.obtenerCorreo(this@PantallaLogin)
                    val contrasena =
                        CredencialesManager.obtenerContrasena(this@PantallaLogin)

                    if (correo != null && contrasena != null) {
                        // Hacer signIn real con las credenciales guardadas
                        lifecycleScope.launch {
                            try {
                                SupabaseClient.client.auth.signInWith(Email) {
                                    email = correo
                                    password = contrasena
                                }
                                irAPantallaPrincipal()
                            } catch (e: Exception) {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@PantallaLogin,
                                        "Error al iniciar sesion: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    } else {

                        Toast.makeText(
                            this@PantallaLogin,
                            "Sesion expirada. Inicia sesion con tu correo.",
                            Toast.LENGTH_LONG
                        ).show()
                        CredencialesManager.limpiarCredenciales(this@PantallaLogin)
                        configurarVisibilidadHuella()
                    }
                }

                // Error irrecuperable del sensor
                override fun onAuthenticationError(
                    errorCode: Int, errString: CharSequence
                ) {

                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
                    ) {
                        Toast.makeText(
                            this@PantallaLogin,
                            "Error biometrico: $errString",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


                override fun onAuthenticationFailed() {
                    Toast.makeText(

                        this@PantallaLogin,
                        "Huella no reconocida, intenta de nuevo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })


        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Acceso con huella")
            .setSubtitle("Usa tu huella dactilar para ingresar")
            .setNegativeButtonText("Cancelar")
            .build()
        biometricPrompt.authenticate(promptInfo)
    }


    private fun irAPantallaPrincipal() {
        runOnUiThread {
            startActivity(Intent(this@PantallaLogin, MainActivity::class.java))
            finishAffinity()
        }
    }

    private fun iniciarSesionConGoogle() {
        lifecycleScope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("404131668713-n1ate1rris5emnmv7ehmnnqn66lv6n82.apps.googleusercontent.com")
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                val credentialManager = CredentialManager.create(this@PantallaLogin)
                val result = credentialManager.getCredential(this@PantallaLogin, request)
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(result.credential.data)

                SupabaseClient.client.auth.signInWith(IDToken) {
                    idToken = googleIdTokenCredential.idToken
                    provider = Google


                }
                runOnUiThread {
                    Toast.makeText(
                        this@PantallaLogin,
                        "Inicio de sesion con google exitoso",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@PantallaLogin, MainActivity::class.java))
                    finish()

                }


            } catch (e: Exception) {
                Log.e("GOOGLE_LOGIN_ERROR", "Error real:", e)
                Toast.makeText(
                    this@PantallaLogin,
                    e.message ?: "Error desconocido",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }


}



