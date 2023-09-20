package com.example.sirae
import DatabaseHandler
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Secure
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class home : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var dbHandler: DatabaseHandler

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().reference
        dbHandler = DatabaseHandler(this)
        setContentView(R.layout.activity_home)
        val btn_datos = findViewById<ImageView>(R.id.ingresarDatosBt)
        val btn_firebase = findViewById<ImageView>(R.id.btn_firebase)

        val email = intent.getStringExtra("email")
        val installationId = obtenerIDUnico(this)

        val welcomeMessage = "Hola, bienvenido!"
        val welcomeText = findViewById<TextView>(R.id.welcome_text)
        welcomeText.text = "$welcomeMessage\nCorreo: $email"

        btn_datos.setOnClickListener {
            val intent = Intent(this, datos_asistencia_tecnica::class.java)
            startActivity(intent)
            finish()
        }

        btn_firebase.setOnClickListener {
            obtenerYSubirDatosAFirebase(email, installationId)
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

            // Crea un mapa para realizar un seguimiento de los IDs que se han subido
            val idsSubidos = mutableMapOf<Long, Boolean>()

            // Obtén los registros existentes en Firebase para evitar duplicados
            referenciaFirebase.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (registro in snapshot.children) {
                        val id = registro.child("id").value as Long
                        idsSubidos[id] = true
                    }

                    // Itera sobre los datos y súbelos a Firebase si el ID no existe
                    for (dato in datosSQLite) {
                        if (!idsSubidos.containsKey(dato.id)) {
                            // Crea una clave única en Firebase (puedes usar push() o un ID específico)
                            val nuevaClave = referenciaFirebase.push().key ?: "0"

                            // Sube el dato a Firebase usando la clave única
                            referenciaFirebase.child(nuevaClave).setValue(dato)
                        }
                    }

                    // Notifica al usuario que los datos se subieron con éxito
                    Toast.makeText(this@home, "Datos subidos a Firebase con éxito", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar el error si es necesario
                }
            })
        } else {
            // Manejar el caso en el que el correo o el identificador no sean válidos
            Toast.makeText(this, "Correo o identificador inválido", Toast.LENGTH_SHORT).show()
        }
    }


    private fun obtenerIDUnico(context: Context): String {
        return Secure.getString(context.contentResolver, Secure.ANDROID_ID)
    }

}
