@file:Suppress("DEPRECATION")

package com.example.sirae
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
import android.app.Activity
import android.content.Intent
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import java.io.File
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
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
import java.io.FileOutputStream


class Datosasistenciatecnica: AppCompatActivity() {
    // Inicializa Firebase Storage
    val storage = FirebaseStorage.getInstance()
    private val REQUEST_IMAGE1 = 1
    private val REQUEST_IMAGE2 = 2
    private lateinit var imagen1: Button
    private lateinit var imagen2: Button
    private var email: String? = null
    private var imageUri1: Uri? = null
    private var imageUri2: Uri? = null
    private var image1Selected = false
    private var image2Selected = false


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
    private lateinit var spinnerDepartamento: Spinner
    private lateinit var spinnerMunicipio: Spinner

    private val FOLDERNAME = "visitas"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_asistencia_tecnica)
        imagen1 = findViewById(R.id.btn_select_image1)
        imagen2 = findViewById(R.id.btn_select_image2)
        email = intent.getStringExtra("email")




        imagen1.setOnClickListener {
            if (verificarCamposFechaHoraLlenos()) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, REQUEST_IMAGE1)
            } else {
                Toast.makeText(this, "Por favor, complete los campos de fecha y hora antes de seleccionar la imagen.", Toast.LENGTH_SHORT).show()
            }
        }

        imagen2.setOnClickListener {
            if (verificarCamposFechaHoraLlenos()) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, REQUEST_IMAGE2)
            } else {
                Toast.makeText(this, "Por favor, complete los campos de fecha y hora antes de seleccionar la imagen.", Toast.LENGTH_SHORT).show()
            }
        }

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
        val adapterDepartamento =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, departamentosElSalvador)
        adapterDepartamento.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDepartamento.adapter = adapterDepartamento

        // Establecer el Listener para el Spinner de departamentos
        spinnerDepartamento.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                // Obtener el departamento seleccionado
                val departamentoSeleccionado = departamentosElSalvador[position]

                // Obtener los municipios correspondientes al departamento seleccionado
                val municipiosCorrespondientes =
                    MunicipioData.getMunicipios(departamentoSeleccionado)

                // Crear un adaptador para el Spinner de municipios
                val adapterMunicipio = ArrayAdapter(
                    this@Datosasistenciatecnica,
                    android.R.layout.simple_spinner_item,
                    municipiosCorrespondientes
                )
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
                if (image1Selected && image2Selected) {
                    // Ambas imágenes se han seleccionado, puedes guardar los datos
                    guardarDatos()
                } else {
                    // Mostrar un mensaje de error si falta seleccionar una o ambas imágenes
                    Toast.makeText(this, "Por favor, seleccione ambas imágenes.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Mostrar un mensaje de error si alguna entrada está vacía
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }



    }

    // Luego, en onActivityResult, configura las variables de selección
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE1 || requestCode == REQUEST_IMAGE2) {
                val imageUri = data?.data

                if (imageUri != null) {
                    if (requestCode == REQUEST_IMAGE1) {
                        imageUri1 = imageUri
                        image1Selected = true
                    } else if (requestCode == REQUEST_IMAGE2) {
                        imageUri2 = imageUri
                        image2Selected = true
                    }
                }

                // Actualiza la UI u otra lógica si es necesario
            } else {
                Toast.makeText(this, "Error al seleccionar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun copySelectedImageToPhotosFolder(imageUri: Uri, fecha: String, hora: String, requestCode: Int) {
        val sourceInputStream = contentResolver.openInputStream(imageUri)
        val photosFolder = File(getExternalFilesDir(null), "fotos")

        if (!photosFolder.exists()) {
            photosFolder.mkdirs() // Crea la carpeta si no existe
        }

        val formattedFecha = fecha.replace("/", "_")
        val formattedHora = hora.replace(":", "_")
        val requestCodeString = requestCode.toString()

        val fileName = when (requestCode) {
            REQUEST_IMAGE1 -> "img1_${formattedFecha}_${formattedHora}.jpg"
            REQUEST_IMAGE2 -> "img2_${formattedFecha}_${formattedHora}.jpg"
            else -> "unknown_${formattedFecha}_${formattedHora}.jpg"
        }

        val destinationFile = File(photosFolder, fileName)
        val destinationOutputStream = FileOutputStream(destinationFile)

        sourceInputStream?.use { input ->
            destinationOutputStream.use { output ->
                input.copyTo(output)
            }
        }

        sourceInputStream?.close()
        destinationOutputStream.close()
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

        val localImageUri1 = imageUri1
        val localImageUri2 = imageUri2

        if (localImageUri1 != null && localImageUri2 != null) {
            // Copia la primera imagen
            copySelectedImageToPhotosFolder(localImageUri1, fecha, hora, REQUEST_IMAGE1)

            // Copia la segunda imagen
            copySelectedImageToPhotosFolder(localImageUri2, fecha, hora, REQUEST_IMAGE2)
        }


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
            Toast.makeText(this, "Datos Guardados\n El archivo PDF se guardó en Descargas", Toast.LENGTH_LONG).show()
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
    private fun verificarCamposFechaHoraLlenos(): Boolean {
        val fechaLlena = !txtFecha.text.isNullOrBlank()
        val horaLlena = !txtHora.text.isNullOrBlank()
        return fechaLlena && horaLlena
    }


    private fun generarPdf() {
        try {

            val rootFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FOLDERNAME)
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
                .add(Paragraph("Total: ${txtParticipantesMujeres.text.toString().toInt() + txtParticipantesHombres.text.toString().toInt()}"))
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
    }
}