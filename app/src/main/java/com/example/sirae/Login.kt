package com.example.sirae

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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

import android.Manifest
import androidx.core.app.ActivityCompat
import android.os.Build
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import java.io.File
import android.content.pm.PackageManager
import android.os.Environment
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.layout.element.Cell
import java.text.SimpleDateFormat
import java.util.Date
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import java.util.Locale

class Login : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    private lateinit var sharedPreferences: SharedPreferences
    private val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1

    private val FOLDER_NAME = "visitas"


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

        val btnGoogle = findViewById<Button>(R.id.btn_google)
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
            val displayName = account?.displayName

            // Almacena la información de inicio de sesión y marca de tiempo
            saveLoginInfo(email)

            // Redirigir a la nueva actividad con los datos
            goToWelcomeActivity(email)
        } catch (e: ApiException) {
            Log.e("LoginActivity", "Error al obtener información del usuario: ${e.message}")
            Toast.makeText(this, "Error al obtener información del usuario", Toast.LENGTH_SHORT).show()
        }
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
        val intent = Intent(this, home::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }
}