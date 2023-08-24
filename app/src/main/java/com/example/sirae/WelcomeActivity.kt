package com.example.sirae

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val email = intent.getStringExtra("email")
        val welcomeMessage = "Hola, bienvenido!"
        val welcomeText = findViewById<TextView>(R.id.welcome_text)
        welcomeText.text = "$welcomeMessage\nCorreo: $email"
    }
}