package com.example.sirae

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent

import android.net.Uri
import android.os.Bundle
import android.provider.Settings.Secure
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class Home : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var dbHandler: DatabaseHandler


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().reference
        dbHandler = DatabaseHandler(this)
        setContentView(R.layout.activity_home)
        val btndatos = findViewById<ImageView>(R.id.ingresarDatosBt)
        val btnfirebase = findViewById<ImageView>(R.id.btn_firebase)
        val progVisitaBt = findViewById<ImageView>(R.id.progVisitaBt)

        val email = intent.getStringExtra("email")
        val installationId = obtenerIDUnico(this)
        val welcomeText = findViewById<TextView>(R.id.welcome_text)
        welcomeText.text = "Correo: $email"

        btndatos.setOnClickListener {
            val intent = Intent(this, Datosasistenciatecnica::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }
        progVisitaBt.setOnClickListener{
            val intent = Intent(this,CalendarActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        btnfirebase.setOnClickListener {
            obtenerYSubirDatosAFirebase(email, installationId)
            subirFotosAFirebaseStorage(email.toString())
        }
    }

    private fun obtenerYSubirDatosAFirebase(email: String?, installationId: String) {
        if (!email.isNullOrEmpty() && installationId.isNotEmpty()) {
            // Reemplaza los puntos en el correo electrónico con guiones bajos
            val emailSinPunto = email.replace(".", "_")

            // Usa la parte antes de "@" como parte de la ruta en Firebase
            val emailSinArroba = emailSinPunto.substringBefore("@")
            val nodoFirebase = "${emailSinArroba}/$installationId"

            // Obtén los datos de la base de datos SQLite
            val datosSQLite = dbHandler.obtenerTodosLosDatos()

            // Referencia al nodo en Firebase
            val referenciaFirebase = FirebaseDatabase.getInstance().getReference(nodoFirebase)

            // Verifica si hay datos nuevos para subir
            var hayDatosNuevos = false

            // Crea un mapa para realizar un seguimiento de los IDs que se han subido
            val idsSubidos = mutableMapOf<Long, Boolean>()

            // Obtén los registros existentes en Firebase para evitar duplicados
            referenciaFirebase.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (registro in snapshot.children) {
                        val id = registro.child("id").value as Long
                        idsSubidos[id] = true
                    }

                    // Verifica si hay datos nuevos para subir
                    for (dato in datosSQLite) {
                        if (!idsSubidos.containsKey(dato.id)) {
                            hayDatosNuevos = true
                            // No es necesario continuar verificando, ya sabemos que hay datos nuevos
                            break
                        }
                    }

                    if (hayDatosNuevos) {
                        // Si hay datos nuevos, sube los datos a Firebase
                        for (dato in datosSQLite) {
                            if (!idsSubidos.containsKey(dato.id)) {
                                // Crea una clave única en Firebase (puedes usar push() o un ID específico)
                                val nuevaClave = referenciaFirebase.push().key ?: "0"

                                // Sube el dato a Firebase usando la clave única
                                referenciaFirebase.child(nuevaClave).setValue(dato)
                            }
                        }

                        // Notifica al usuario que los datos se subieron con éxito
                        Toast.makeText(this@Home, "Datos subidos a Firebase con éxito", Toast.LENGTH_SHORT).show()
                    } else {
                        // No hay datos nuevos para subir, muestra un mensaje
                        Toast.makeText(this@Home, "No hay datos nuevos para subir", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar el error aquí
                    Log.e("FirebaseError", "Error en la lectura de Firebase: ${error.message}")
                    Toast.makeText(this@Home, "Error en la lectura de Firebase", Toast.LENGTH_SHORT).show()
                }

            })
        } else {
            // Manejar el caso en que el correo o el identificador no sean válidos
            Toast.makeText(this, "Correo o identificador inválido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun subirFotosAFirebaseStorage(email: String) {
        if (email.isNotEmpty()) {
            val emailSinPunto = email.replace(".", "_")
            val emailSinArroba = emailSinPunto.substringBefore("@")

            // Referencia a la raíz de Firebase Storage
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference

            // Ruta local donde se almacenan las imágenes
            val rutaLocal = File(getExternalFilesDir(null), "fotos")

            // Lista de archivos en la carpeta local
            val archivos = rutaLocal.listFiles()

            if (archivos != null && archivos.isNotEmpty()) {
                // Lista de nombres de archivos en Firebase Storage
                val nombresFirebase = mutableListOf<String>()

                // Obtener los nombres de los archivos en Firebase Storage
                storageRef.child(emailSinArroba).listAll()
                    .addOnSuccessListener { result ->
                        for (item in result.items) {
                            nombresFirebase.add(item.name)
                        }

                        // Verificar si hay imágenes nuevas para subir
                        var hayImagenesNuevas = false

                        archivos.forEach { archivo ->
                            val nombreArchivo = archivo.name
                            if (!nombresFirebase.contains(nombreArchivo)) {
                                // El archivo no existe en Firebase Storage
                                val imagenUri = Uri.fromFile(archivo)
                                val imagenRef = storageRef.child("$emailSinArroba/$nombreArchivo")

                                imagenRef.putFile(imagenUri)
                                    .addOnSuccessListener {
                                        // Imagen subida con éxito
                                        Toast.makeText(this@Home, "$nombreArchivo subido a Firebase Storage", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { exception ->
                                        // Error al subir la imagen
                                        Log.e("FirebaseStorageError", "Error al subir $nombreArchivo: ${exception.message}")
                                        Toast.makeText(this@Home, "Error al subir $nombreArchivo", Toast.LENGTH_SHORT).show()
                                    }

                                hayImagenesNuevas = true
                            }
                        }

                        if (hayImagenesNuevas) {
                            // Al menos una imagen nueva fue subida
                            Toast.makeText(this@Home, "Imágenes nuevas subidas a Firebase Storage", Toast.LENGTH_SHORT).show()
                        } else {
                            // No hay imágenes nuevas para subir
                            Toast.makeText(this@Home, "No hay imágenes nuevas para subir", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseStorageError", "Error al listar archivos en Firebase Storage: ${e.message}")
                    }
            } else {
                Toast.makeText(this@Home, "No hay imágenes locales para subir", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Manejar el caso en que el correo no sea válido
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
        }
    }






    @SuppressLint("HardwareIds")
    private fun obtenerIDUnico(context: Context): String {
        return Secure.getString(context.contentResolver, Secure.ANDROID_ID)
    }

}