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


    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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

        //Generacion de PDF
        val generatedPDF: Button = findViewById(R.id.generatePdf)
        generatedPDF.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1001)
                } else {
                    createPdf()
                }
            } else {
                createPdf()
            }
        }
    }

    private fun createPdf() {
        try {

            val rootFolder = File(Environment.getExternalStorageDirectory(), FOLDER_NAME)
            if (!rootFolder.exists()) {
                rootFolder.mkdirs()
            }

            val currentDate = Date()
            val dateFormat = SimpleDateFormat("ddMMyyyy_HHmmss")
            val formattedDate = dateFormat.format(currentDate)
            val pdfFileName = "asistencia_tecnica_$formattedDate.pdf"
            val pdfFilePath = File(rootFolder, pdfFileName)

            if (pdfFilePath.exists()) {
                AlertDialog.Builder(this)
                    .setTitle("File Exists")
                    .setMessage("Do you want to overwrite the existing file?")
                    .setPositiveButton("Yes") { _, _ ->
                        saveFile(pdfFilePath)
                    }
                    .setNegativeButton("No") { _, _ ->
                        // Handle the case when the user chooses not to overwrite the file
                        // You can generate a new file name based on the current time, for example:
                        val timeStamp = SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(Date())
                        val newFileName = "asistencia_tecnica_${timeStamp}.pdf"
                        val newFile = File(pdfFilePath.parentFile, newFileName)
                        saveFile(newFile)
                    }
                    .setCancelable(false)
                    .show()
            } else {
                saveFile(pdfFilePath)
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun saveFile(file: File) {
        val writer = PdfWriter(file)
        val pdf = PdfDocument(writer)
        val document = Document(pdf)

        // Add content to the PDF
        val title = Paragraph("Application Form")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(18f)
            .setBold()
            .setMarginBottom(20f)

        document.add(title)

        // Create a table with 3 columns and 13 rows
        val table = Table(floatArrayOf(1f, 1f, 1f))
            .setHorizontalAlignment(HorizontalAlignment.CENTER)

        table.addCell(
            Cell(1, 3)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.BLACK)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .add(Paragraph("Basic Information"))
        )

        table.addCell(
            Cell(2, 3)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .setBackgroundColor(ColorConstants.WHITE)
                .add(Paragraph("Activity\nLorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non risus."))
        )


        table.addCell(
            Cell(1, 2)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Place: Some random place"))
        )

        table.addCell(
            Cell(1, 1)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Code: 12345"))
        )

        table.addCell(
            Cell(1, 1)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("District: Hot District"))
        )

        table.addCell(
            Cell(1, 1)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("State: Hot State"))
        )

        table.addCell(
            Cell(1, 1)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Department: Hot Department"))
        )

        table.addCell(
            Cell(1, 2)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Date: 01/01/2001"))
        )

        table.addCell(
            Cell(1, 1)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Hour: 00:00"))
        )

        table.addCell(
            Cell(4, 3)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Participants\n\n\n"))
        )

        table.addCell(
            Cell(1, 3)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Participants Quantity"))
        )

        table.addCell(
            Cell(1, 1)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Women: 000"))
        )

        table.addCell(
            Cell(1, 1)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Men: 000"))
        )

        table.addCell(
            Cell(1, 1)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Total: 000"))
        )

        table.addCell(
            Cell(5, 3)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Objectives\n\n\n\n"))
        )

        table.addCell(
            Cell(1, 3)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.BLACK)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .add(Paragraph("Happenings"))
        )

        table.addCell(
            Cell(1, 3)
                .add(Paragraph("\n\n\n\n\n\n"))
        )

        table.addCell(
            Cell(6, 2)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Recommendations\n\n\n\n\n"))
        )

        table.addCell(
            Cell(6, 1)
                .add(Paragraph("Agreements\n\n\n\n\n"))
        )

        document.add(table)
        // Close the document
        document.close()

        Toast.makeText(this, "PDF created successfully: $file", Toast.LENGTH_LONG)
            .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, create the PDF
                createPdf()
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission denied, cannot create PDF", Toast.LENGTH_SHORT).show()
            }
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