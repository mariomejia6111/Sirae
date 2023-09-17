package com.example.sirae
import DatabaseHandler
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sirae.R
import java.util.Calendar


class datos_asistencia_tecnica: AppCompatActivity() {

    private lateinit var txtFecha: EditText
    private lateinit var txtDistrito: EditText
    private lateinit var txtLugar: EditText
    private lateinit var txtActividad: EditText
    private lateinit var txtCodigoDistrito: EditText
    private lateinit var txtMunicipio: EditText
    private lateinit var txtDepartamento: EditText
    private lateinit var txtHora: EditText
    private lateinit var txtParticipantes: EditText
    private lateinit var txtParticipantesMujeres: EditText
    private lateinit var txtParticipantesHombres: EditText
    private lateinit var txtObjetivo: EditText
    private lateinit var txtHallazgos: EditText
    private lateinit var txtRecomendaciones: EditText
    private lateinit var txtAcuerdos: EditText
    private lateinit var btnEnviar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_asistencia_tecnica)

        // Enlazar elementos de entrada y botón
        txtFecha = findViewById(R.id.txt_fecha)
        txtDistrito = findViewById(R.id.txt_distrito)
        txtLugar = findViewById(R.id.txt_lugar)
        txtActividad = findViewById(R.id.txt_actividad)
        txtCodigoDistrito = findViewById(R.id.txt_codigodistrito)
        txtMunicipio = findViewById(R.id.txt_municipio)
        txtDepartamento = findViewById(R.id.txt_departamento)
        txtHora = findViewById(R.id.txt_hora)
        txtParticipantes = findViewById(R.id.txt_participantes)
        txtParticipantesMujeres = findViewById(R.id.Participantes_M)
        txtParticipantesHombres = findViewById(R.id.Participantes_H)
        txtObjetivo = findViewById(R.id.Objetivo)
        txtHallazgos = findViewById(R.id.txt_hallazgos)
        txtRecomendaciones = findViewById(R.id.txt_recomendaciones)
        txtAcuerdos = findViewById(R.id.txt_acuerdos)
        btnEnviar = findViewById(R.id.btn_enviarSQlite)

        // Configurar clic en el EditText de fecha
        txtFecha.setOnClickListener {
            mostrarDialogoFecha()
        }

        // Configurar clic en el EditText de hora
        txtHora.setOnClickListener {
            mostrarDialogoHora()
        }

        // Configurar clic en el botón "Enviar"
        btnEnviar.setOnClickListener {
            guardarDatos()
        }

    }

    private fun mostrarDialogoFecha() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"
            txtFecha.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun mostrarDialogoHora() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val selectedTime = "$hourOfDay:$minute"
            txtHora.setText(selectedTime)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun guardarDatos() {
        // Obtener valores de los EditText
        val fecha = txtFecha.text.toString()
        val distrito = txtDistrito.text.toString()
        val lugar = txtLugar.text.toString()
        val actividad = txtActividad.text.toString()
        val codigoDistrito = txtCodigoDistrito.text.toString()
        val municipio = txtMunicipio.text.toString()
        val departamento = txtDepartamento.text.toString()
        val hora = txtHora.text.toString()
        val participantes = txtParticipantes.text.toString()
        val participantesMujeres = txtParticipantesMujeres.text.toString()
        val participantesHombres = txtParticipantesHombres.text.toString()
        val objetivo = txtObjetivo.text.toString()
        val hallazgos = txtHallazgos.text.toString()
        val recomendaciones = txtRecomendaciones.text.toString()
        val acuerdos = txtAcuerdos.text.toString()

        // Inicializa la instancia de DatabaseHandler
        val dbHandler = DatabaseHandler(this)

        // Abre la base de datos para escritura
        dbHandler.open()

        // Inserta los datos en la base de datos
        val result = dbHandler.insertData(
            fecha, distrito, lugar, actividad, codigoDistrito, municipio, departamento,
            hora, participantes, participantesMujeres, participantesHombres,
            objetivo, hallazgos, recomendaciones, acuerdos
        )

        // Cierra la base de datos
        dbHandler.close()

        if (result != -1L) {
            // Datos guardados exitosamente
            val mensaje = "Datos guardados:\nFecha: $fecha\nDistrito: $distrito\nLugar: $lugar"
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        } else {
            // Error al guardar los datos
            Toast.makeText(
                this,
                "Error al guardar los datos en la base de datos.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
