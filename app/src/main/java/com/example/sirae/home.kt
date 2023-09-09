package com.example.sirae

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class home : AppCompatActivity() {
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val btn_datos = findViewById<ImageView>(R.id.ingresarDatosBt)
        val email = intent.getStringExtra("email")
        val welcomeMessage = "Hola, bienvenido!"
        val welcomeText = findViewById<TextView>(R.id.welcome_text)
        welcomeText.text = "$welcomeMessage\nCorreo: $email"

        btn_datos.setOnClickListener {
            val intent = Intent(this, datos_asistencia_tecnica::class.java)
            startActivity(intent)
            finish()
        }



    }

}