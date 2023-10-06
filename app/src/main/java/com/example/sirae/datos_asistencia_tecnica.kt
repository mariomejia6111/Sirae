@file:Suppress("DEPRECATION")

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
import android.Manifest
import android.content.Intent
import androidx.core.app.ActivityCompat
import android.os.Build
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import java.io.File
import android.content.pm.PackageManager
import android.net.Uri
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
import java.util.Locale
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class datos_asistencia_tecnica: AppCompatActivity() {
    // Inicializa Firebase Storage
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    private var selectedImageUri1: Uri? = null
    private var selectedImageUri2: Uri? = null

    private val IMAGE_PICK_CODE1 = 1001
    private val IMAGE_PICK_CODE2 = 1002
    private var email: String? = null


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
    private lateinit var imagen1 : Button
    private lateinit var imagen2 : Button

    private val FOLDERNAME = "visitas"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_asistencia_tecnica)
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        email = intent.getStringExtra("email")

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
        imagen1 = findViewById(R.id.btn_select_image1)
        imagen2 = findViewById(R.id.btn_select_image2)

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



        imagen1.setOnClickListener {
            seleccionarImagen(IMAGE_PICK_CODE1)
        }

        // Configurar clic en el botón para seleccionar la segunda imagen
        imagen2.setOnClickListener {
            seleccionarImagen(IMAGE_PICK_CODE2)
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
            guardarDatosimg(email.toString())
            if (verificarEntradasLlenas()) {

                guardarDatos()



            } else {
                // Mostrar un mensaje de error si alguna entrada está vacía
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == IMAGE_PICK_CODE1) {
                selectedImageUri1 = data.data
                Toast.makeText(this, "Imagen 1 seleccionada", Toast.LENGTH_SHORT).show()
            } else if (requestCode == IMAGE_PICK_CODE2) {
                selectedImageUri2 = data.data
                Toast.makeText(this, "Imagen 2 seleccionada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ... (otros métodos)

    private fun guardarDatosimg(correo: String) {

        // Obtener valores de los EditText
        val fecha = txtFecha.text.toString()
        val hora = txtHora.text.toString()
        // ... (otros campos)

        // Verificar si las imágenes han sido seleccionadas
        if (selectedImageUri1 != null && selectedImageUri2 != null) {
            // Subir las imágenes a Firebase Storage y pasar el correo, fecha y hora
            subirImagenAFirebaseStorage(selectedImageUri1!!, "imagen1", correo, fecha, hora)
            subirImagenAFirebaseStorage(selectedImageUri2!!, "imagen2", correo, fecha, hora)
        } else {
            // Si no se han seleccionado ambas imágenes, muestra un mensaje de error
            Toast.makeText(this, "Por favor, seleccione ambas imágenes.", Toast.LENGTH_SHORT).show()
            return
        }

        // Resto de tu código...
    }


    private fun subirImagenAFirebaseStorage(imageUri: Uri, imageName: String, correo: String, fecha: String, hora: String) {
        // Elimina caracteres no deseados del nombre de la imagen (en este caso, reemplazamos "/" con "_")
        val nombreArchivo = "$imageName-$fecha-$hora.jpg".replace("/", "_")

        // Obtén una referencia al archivo en Firebase Storage utilizando el correo como parte del nombre
        val imageRef = storageRef.child("carpeta_de_almacenamiento/$correo/$nombreArchivo")

        // Sube la imagen a Firebase Storage
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                // La imagen se subió exitosamente
                Toast.makeText(this, "Imagen $nombreArchivo subida exitosamente a Firebase Storage", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // Ocurrió un error al subir la imagen
                Toast.makeText(this, "Error al subir la imagen $nombreArchivo a Firebase Storage", Toast.LENGTH_SHORT).show()
            }
    }






    private fun seleccionarImagen(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, requestCode)
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
        val participantesMujeres = txtParticipantesMujeres.text.toString().toInt()
        val participantesHombres = txtParticipantesHombres.text.toString().toInt()
        val sumaParticipantes = participantesMujeres + participantesHombres
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
             sumaParticipantes,objetivo, hallazgos, recomendaciones, acuerdos
        )

        // Cierra la base de datos
        dbHandler.close()

        if (result != -1L) {
            // Datos guardados exitosamente
            Toast.makeText(this, "Se ha guardado la informacion exitosamente", Toast.LENGTH_LONG).show()
            generarPdf()
        } else {
            // Error al guardar los datos
            Toast.makeText(
                this,
                "Error al guardar los datos en la base de datos.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun generarPdf() {
        try {

            val rootFolder = File(Environment.getExternalStorageDirectory(), FOLDERNAME)
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
        val title = Paragraph("Informe de la Asistencia Tecnica Pedagogica")
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
                .add(Paragraph("Generalidades"))
        )

        table.addCell(
            Cell(2, 3)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .setBackgroundColor(ColorConstants.WHITE)
                .add(Paragraph("Actividad\n${txtActividad.text.toString()}"))
        )


        table.addCell(
            Cell(1, 2)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Lugar: ${txtLugar.text.toString()}"))
        )

        table.addCell(
            Cell(1, 1)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Codigo de Infraestructura: ${txtCodigoDistrito.text.toString()}"))
        )

        table.addCell(
            Cell(1, 1)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Distrito: ${txtDistrito.text.toString()}"))
        )

        table.addCell(
            Cell(1, 1)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Municipio: ${spinnerMunicipio.selectedItem.toString()}"))
        )

        table.addCell(
            Cell(1, 1)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Departmento: ${spinnerDepartamento.selectedItem.toString()}"))
        )

        table.addCell(
            Cell(1, 2)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Fecha: ${txtFecha.text.toString()}"))
        )

        table.addCell(
            Cell(1, 1)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Hora: ${txtHora.text.toString()}"))
        )

        table.addCell(
            Cell(4, 3)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Participantes\n${txtParticipantes.text.toString()}"))
        )

        table.addCell(
            Cell(1, 3)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Cantidad de Participantes"))
        )

        table.addCell(
            Cell(1, 1)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Mujeres: ${txtParticipantesMujeres.text.toString()}"))
        )

        table.addCell(
            Cell(1, 1)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Hombres: ${txtParticipantesHombres.text.toString()}"))
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
                .add(Paragraph("Objetivo\n${txtObjetivo.text.toString()}"))
        )

        table.addCell(
            Cell(1, 3)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.BLACK)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .add(Paragraph("Hallazgos\n${txtHallazgos.text.toString()}"))
        )


        table.addCell(
            Cell(6, 2)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.BLACK)
                .add(Paragraph("Recomendaciones\n${txtRecomendaciones.text.toString()}"))
        )

        table.addCell(
            Cell(6, 1)
                .add(Paragraph("Acuerdos\n${txtAcuerdos.text.toString()}"))
        )

        document.add(table)
        // Close the document
        document.close()

        Toast.makeText(this, "El documento se creo como: $file en la carpeta raiz de su dispositivo", Toast.LENGTH_LONG)
            .show()
    }
}