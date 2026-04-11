package com.example.taller2

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest


object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://tljsdhjkmyhrllfimsik.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRsanNkaGprbXlocmxsZmltc2lrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU4Nzk1NDUsImV4cCI6MjA5MTQ1NTU0NX0.4ly4wh3zlvj7tn6FfnfDysd3wSCq4tLWKES5P1Pa3K4"
    ){
        install(Auth)
        install(Postgrest)


    }



}