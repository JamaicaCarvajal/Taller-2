package com.example.taller2.data

import com.example.taller2.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.Serializable

object UsuarioRepository {
    @Serializable
    data class UsuarioData(
        val id: String,
        val nombres: String,
        val apellidos: String,
        val correo:String?=null,
        val rol: String = "usuario",
        val foto_url :  String? = null
    )


    suspend fun existeUsuario(userId: String): Boolean {
        return try {
            val resultado = SupabaseClient.client
                .postgrest["usuarios"]
                .select(Columns.raw(value = "id")) {
                    filter { eq(column = "id", value = userId) }
                }
                .decodeList<Map<String, String>>()

            resultado.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun insertarUsuario(
        id: String,
        nombres: String,
        apellidos: String,
        correo: String
    ) {
        SupabaseClient.client.postgrest["usuarios"].insert(
            value = UsuarioData(
                id = id,
                nombres = nombres,
                apellidos = apellidos,
                correo = correo
            )
        )
    }


    suspend fun obtenerUsuario(userId: String): UsuarioData? {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return null

        return try {
            SupabaseClient.client
                .postgrest["usuarios"]
                .select {
                    filter { eq(column = "id", value = userId) }
                }
                .decodeSingle<UsuarioData>()
        } catch (e: Exception) {
            null
        }
    }




    suspend fun obtenerRolActual(): String {
        return try {
            val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return "usuario"

            val resultado =
                SupabaseClient.client
                    .postgrest["usuarios"]
                    .select(Columns.raw(value = "rol")) {
                        filter { eq(column = "id", value = userId) }
                    }
                    .decodeList<Map<String, String>>()

            resultado.firstOrNull()?.get("rol") ?: "usuario"
        } catch (e: Exception) {
            "usuario"
        }
    }






}