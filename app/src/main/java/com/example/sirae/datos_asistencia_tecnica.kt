package com.example.sirae
import DatabaseHandler
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar


class datos_asistencia_tecnica: AppCompatActivity() {

    private lateinit var txtFecha: EditText
    private lateinit var txtDistrito: EditText
    private lateinit var txtLugar: EditText
    private lateinit var txtActividad: EditText
    private lateinit var txtCodigoDistrito: EditText
    private lateinit var txtHora: EditText
    private lateinit var txtParticipantes: EditText
    private lateinit var txtParticipantesMujeres: EditText
    private lateinit var txtParticipantesHombres: EditText
    private lateinit var txtObjetivo: EditText
    private lateinit var txtHallazgos: EditText
    private lateinit var txtRecomendaciones: EditText
    private lateinit var txtAcuerdos: EditText
    private lateinit var btnEnviar: Button
    private lateinit var spinnerDepartamento : Spinner
    private lateinit var spinnerMunicipio : Spinner

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_asistencia_tecnica)

        // Enlazar elementos de entrada y botón
        txtFecha = findViewById(R.id.txt_fecha)
        txtDistrito = findViewById(R.id.txt_distrito)
        txtLugar = findViewById(R.id.txt_lugar)
        txtActividad = findViewById(R.id.txt_actividad)
        txtCodigoDistrito = findViewById(R.id.txt_codigodistrito)
        spinnerMunicipio = findViewById(R.id.spinner_municipio)
        spinnerDepartamento = findViewById(R.id.spinner_departamento)
        txtHora = findViewById(R.id.txt_hora)
        txtParticipantes = findViewById(R.id.txt_participantes)
        txtParticipantesMujeres = findViewById(R.id.Participantes_M)
        txtParticipantesHombres = findViewById(R.id.Participantes_H)
        txtObjetivo = findViewById(R.id.Objetivo)
        txtHallazgos = findViewById(R.id.txt_hallazgos)
        txtRecomendaciones = findViewById(R.id.txt_recomendaciones)
        txtAcuerdos = findViewById(R.id.txt_acuerdos)
        btnEnviar = findViewById(R.id.btn_enviarSQlite)

        val departamentosElSalvador = arrayOf(
            "Ahuachapán",
            "Cabañas",
            "Chalatenango",
            "Cuscatlán",
            "La Libertad",
            "La Paz",
            "La Unión",
            "Morazán",
            "San Miguel",
            "San Salvador",
            "San Vicente",
            "Santa Ana",
            "Sonsonate",
            "Usulután"
        )
        // Crear un adaptador para el Spinner de departamentos
        val adapterDepartamento = ArrayAdapter(this, android.R.layout.simple_spinner_item, departamentosElSalvador)
        adapterDepartamento.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDepartamento.adapter = adapterDepartamento

// Establecer el Listener para el Spinner de departamentos
        spinnerDepartamento.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                // Obtener el departamento seleccionado
                val departamentoSeleccionado = departamentosElSalvador[position]

                // Obtener los municipios correspondientes al departamento seleccionado
                val municipiosCorrespondientes = MunicipioData.getMunicipios(departamentoSeleccionado)

                // Crear un adaptador para el Spinner de municipios
                val adapterMunicipio = ArrayAdapter(this@datos_asistencia_tecnica, android.R.layout.simple_spinner_item, municipiosCorrespondientes)
                adapterMunicipio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerMunicipio.adapter = adapterMunicipio
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Manejar el caso en que no se haya seleccionado nada
            }
        }




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
            // Verificar si todas las entradas están llenas antes de guardar los datos
            if (verificarEntradasLlenas()) {
                guardarDatos()
            } else {
                // Mostrar un mensaje de error si alguna entrada está vacía
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun verificarEntradasLlenas(): Boolean {
        // Verificar cada entrada de texto para asegurarse de que no esté vacía
        val todasLasEntradasLlenas = !(
                txtFecha.text.isBlank() || txtDistrito.text.isBlank() || txtLugar.text.isBlank() ||
                        txtActividad.text.isBlank() || txtCodigoDistrito.text.isBlank() ||
                        txtHora.text.isBlank() || txtParticipantes.text.isBlank() ||
                        txtParticipantesMujeres.text.isBlank() || txtParticipantesHombres.text.isBlank() ||
                        txtObjetivo.text.isBlank() || txtHallazgos.text.isBlank() ||
                        txtRecomendaciones.text.isBlank() || txtAcuerdos.text.isBlank() ||
                        spinnerMunicipio.selectedItem.toString().isBlank() ||
                        spinnerDepartamento.selectedItem.toString().isBlank()
                )

        return todasLasEntradasLlenas
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
        val municipio = spinnerMunicipio.selectedItem.toString()
        val departamento = spinnerDepartamento.selectedItem.toString()
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
