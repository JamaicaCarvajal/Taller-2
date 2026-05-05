package com.example.taller2.main

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import com.example.taller2.main.perfil.EditarPerfilFragment
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.taller2.R
import com.example.taller2.SupabaseClient
import com.example.taller2.activities.PantallaLogin
import com.example.taller2.data.UsuarioRepository
import com.example.taller2.main.admin.AdminFragment
import com.example.taller2.main.admin.usuariosFragment
import com.example.taller2.main.perfil.PerfilFragment

import com.example.taller2.main.productos.CarritoFragment
import com.example.taller2.main.productos.CatalogoFragment
import com.example.taller2.main.productos.HomeFragment
import com.example.taller2.main.productos.facoritosFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbarMainPage)
        drawerLayout = findViewById(R.id.drawerLayoutMainPage)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val navView = findViewById<NavigationView>(R.id.navew)

        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.TextColo1)

        CargarFragment(HomeFragment())

        bottomNav.selectedItemId = R.id.navHome

        configurarMenuPorRol(navView.menu)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> CargarFragment(HomeFragment())
                R.id.navCatalogo -> CargarFragment(CatalogoFragment())
                R.id.navCall -> CargarFragment(PerfilFragment())
                R.id.navAyuda -> CargarFragment(facoritosFragment())

            }
            true
        }

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> CargarFragment(HomeFragment())
                R.id.Admin -> CargarFragment(PerfilFragment())
                R.id.UsuarioNav -> CargarFragment(usuariosFragment())
                R.id.EditPerfilNav -> CargarFragment(EditarPerfilFragment())
                R.id.Nav_logOut -> cerrarSesion()

            }
            drawerLayout.closeDrawers()
            true
        }


    }



    private fun configurarMenuPorRol(menu: Menu){
        lifecycleScope.launch {
            val rol = UsuarioRepository.obtenerRolActual()
            runOnUiThread {
                when (rol) {
                    "admin" -> {
                        menu.findItem( R.id.Admin).isVisible = true
                        menu.findItem( R.id.UsuarioNav).isVisible = true
                    }
                    "Usuario" -> {
                        menu.findItem( R.id.Admin).isVisible = false
                        menu.findItem( R.id.UsuarioNav).isVisible = false
                    }
                    else -> {
                        menu.findItem( R.id.Admin).isVisible = false
                        menu.findItem(R.id.UsuarioNav).isVisible = false
                    }
                }
            }
        }
    }



    private fun CargarFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContenedor, fragment)
            .commit()
    }
    private fun cerrarSesion(){
        lifecycleScope.launch {
            try {
                SupabaseClient.client.auth.signOut()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Sesión Cerrada", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity, PantallaLogin::class.java))
                    finish()
                }

            }catch (e: Exception){
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "No se pudo cerrar secion", Toast.LENGTH_SHORT).show()

                }
            }


        }


    }

}