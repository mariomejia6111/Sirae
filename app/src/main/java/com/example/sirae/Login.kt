package com.example.sirae

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import java.util.concurrent.TimeUnit


class Login : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var btnGoogle: Button



    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    handleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(data))
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        btnGoogle = findViewById<Button>(R.id.btn_google)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Verifica si el usuario ha iniciado sesión previamente y si la sesión ha expirado
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val lastLoginTime = sharedPreferences.getLong("lastLoginTime", 0)
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastLoginTime

        if (isLoggedIn && elapsedTime <= TimeUnit.MINUTES.toMillis(60)) {
            // El usuario ha iniciado sesión y la sesión aún es válida, dirigir a la actividad principal
            val email = sharedPreferences.getString("email", null)
            goToWelcomeActivity(email)
        }

        btnGoogle.setOnClickListener {
            signInWithGoogle()
        }


    }



    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(task: com.google.android.gms.tasks.Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            val email = account?.email

            // Verificar si el correo electrónico cumple con el patrón
            if (isEmailAllowed(email)) {
                // Almacena la información de inicio de sesión y marca de tiempo
                saveLoginInfo(email)

                // Redirigir a la nueva actividad con los datos
                goToWelcomeActivity(email)
            } else {
                // Correo electrónico no permitido, mostrar un mensaje o realizar alguna acción
                Toast.makeText(this, "Correo electrónico no permitido", Toast.LENGTH_SHORT).show()
                val editor = sharedPreferences.edit()
                editor.putBoolean("isLoggedIn", false)
                editor.putLong("lastLoginTime", 0)
                editor.putString("email", null)
                editor.apply()

                // También puedes desvincular la cuenta de Google (opcional)
                googleSignInClient.signOut()

                // Asegúrate de que el botón esté habilitado
                btnGoogle.isEnabled = true

            }
        } catch (e: ApiException) {
            Toast.makeText(this, "Error al obtener información del usuario", Toast.LENGTH_SHORT).show()
            // En caso de error, no deshabilitar el botón
        } finally {
            btnGoogle.isEnabled = true  // Asegurarse de que el botón se habilite o no después del manejo del resultado
        }
    }



    private fun isEmailAllowed(email: String?): Boolean {
        return email?.endsWith("@clases.edu.sv") == true ||
                email?.endsWith("@mined.gob.sv") == true ||
                email == "sistemaregistro001@gmail.com" ||
                email == "gonzalo.hernandez@catolica.edu.sv"
    }



    private fun saveLoginInfo(email: String?) {
        val currentTime = System.currentTimeMillis()
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putLong("lastLoginTime", currentTime)
        editor.putString("email", email)
        editor.apply()
    }

    private fun goToWelcomeActivity(email: String?) {
        val intent = Intent(this, Home::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }
}