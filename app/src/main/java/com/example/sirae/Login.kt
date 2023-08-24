package com.example.sirae
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
class Login : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        if (supportActionBar != null) supportActionBar?.hide()
        // Declarar el botón con el ID "btn_google"
        val btnGoogle = findViewById<Button>(R.id.btn_google)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Configurar el escucha de clics del botón
        btnGoogle.setOnClickListener {
            signInWithGoogle()
            //goToMainActivity()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }


    private fun handleSignInResult(completedTask: com.google.android.gms.tasks.Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // El inicio de sesión con Google fue exitoso, puedes obtener la información del usuario aquí

            val email = account?.email
            val displayName = account?.displayName
            // Puedes obtener más información del usuario según tus necesidades

            // Luego de obtener la información, puedes redirigir a la siguiente pantalla o realizar otras acciones
            goToMainActivity()
        } catch (e: ApiException) {
            Log.e("LoginActivity", "Error al obtener información del usuario: ${e.message}")
            Toast.makeText(this, "Error al obtener información del usuario", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun goToMainActivity() {
       val intent = Intent(this, home::class.java)

        // Puedes pasar datos adicionales a MainActivity si lo deseas
        //intent.putExtra("email", email)

        startActivity(intent)

        // Finalizar esta actividad para que el usuario no pueda regresar a la pantalla de inicio de sesión con el botón "Atrás"
        finish()
    }
}
