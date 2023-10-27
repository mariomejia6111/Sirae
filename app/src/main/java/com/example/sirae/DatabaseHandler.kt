package com.example.sirae
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, getDatabasePath(context), null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "AsistenciaTecnicaDB"
        private const val DATABASE_PATH = "databases/"

        // Nombre de la tabla
        private const val TABLE_ASISTENCIA_TECNICA = "asistencia_tecnica"

        // Columnas de la tabla
        private const val KEY_ID = "_id"
        private const val KEY_FECHA = "fecha"
        private const val KEY_DISTRITO = "distrito"
        private const val KEY_LUGAR = "lugar"
        private const val KEY_ACTIVIDAD = "actividad"
        private const val KEY_CODIGO_DISTRITO = "codigo_distrito"
        private const val KEY_MUNICIPIO = "municipio"
        private const val KEY_DEPARTAMENTO = "departamento"
        private const val KEY_HORA = "hora"
        private const val KEY_PARTICIPANTES = "participantes"
        private const val KEY_PARTICIPANTES_MUJERES = "participantes_mujeres"
        private const val KEY_PARTICIPANTES_HOMBRES = "participantes_hombres"
        private const val KEY_SUMA_PARTICIPANTES = "suma_participantes"
        private const val KEY_OBJETIVO = "objetivo"
        private const val KEY_HALLAZGOS = "hallazgos"
        private const val KEY_RECOMENDACIONES = "recomendaciones"
        private const val KEY_ACUERDOS = "acuerdos"

        // Método para obtener la ruta completa de la base de datos
        private fun getDatabasePath(context: Context): String {
            val databaseFile = File(context.getExternalFilesDir(null), DATABASE_PATH)
            if (!databaseFile.exists()) {
                databaseFile.mkdirs() // Crea la carpeta si no existe
            }
            return File(databaseFile, DATABASE_NAME).absolutePath
        }
    }

    // Resto del código como antes
    override fun onCreate(db: SQLiteDatabase?) {
        // Crear la tabla cuando se crea la base de datos por primera vez
        val createasistenciatecnicatable = ("CREATE TABLE $TABLE_ASISTENCIA_TECNICA (" +
                "$KEY_ID INTEGER PRIMARY KEY," +
                "$KEY_FECHA TEXT," +
                "$KEY_DISTRITO INTEGER," +
                "$KEY_LUGAR TEXT," +
                "$KEY_ACTIVIDAD TEXT," +
                "$KEY_CODIGO_DISTRITO INTEGER," +
                "$KEY_MUNICIPIO TEXT," +
                "$KEY_DEPARTAMENTO TEXT," +
                "$KEY_HORA TEXT," +
                "$KEY_PARTICIPANTES TEXT," +
                "$KEY_PARTICIPANTES_MUJERES INTEGER," +
                "$KEY_PARTICIPANTES_HOMBRES INTEGER," +
                "$KEY_SUMA_PARTICIPANTES INTEGER," +
                "$KEY_OBJETIVO TEXT," +
                "$KEY_HALLAZGOS TEXT," +
                "$KEY_RECOMENDACIONES TEXT," +
                "$KEY_ACUERDOS TEXT)")
        db?.execSQL(createasistenciatecnicatable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Actualización de la base de datos (no se maneja en esta implementación)
    }

    // Método para insertar datos en la tabla asistencia_tecnica
    fun insertData(
        fecha: String, distrito: String, lugar: String, actividad: String,
        codigoDistrito: String, municipio: String, departamento: String,
        hora: String, participantes: String, participantesMujeres: Int,
        participantesHombres: Int, sumaParticipantes : Int, objetivo: String, hallazgos: String,
        recomendaciones: String, acuerdos: String
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_FECHA, fecha)
        values.put(KEY_DISTRITO, distrito)
        values.put(KEY_LUGAR, lugar)
        values.put(KEY_ACTIVIDAD, actividad)
        values.put(KEY_CODIGO_DISTRITO, codigoDistrito)
        values.put(KEY_MUNICIPIO, municipio)
        values.put(KEY_DEPARTAMENTO, departamento)
        values.put(KEY_HORA, hora)
        values.put(KEY_PARTICIPANTES, participantes)
        values.put(KEY_PARTICIPANTES_MUJERES, participantesMujeres)
        values.put(KEY_PARTICIPANTES_HOMBRES, participantesHombres)
        values.put(KEY_SUMA_PARTICIPANTES,sumaParticipantes)
        values.put(KEY_OBJETIVO, objetivo)
        values.put(KEY_HALLAZGOS, hallazgos)
        values.put(KEY_RECOMENDACIONES, recomendaciones)
        values.put(KEY_ACUERDOS, acuerdos)

        // Insertar fila
        val result = db.insert(TABLE_ASISTENCIA_TECNICA, null, values)

        // Cerrar la base de datos
        db.close()

        return result
    }


    // Método para abrir la base de datos
    fun open() {
        this.writableDatabase
    }
    // Método para obtener todos los datos de la tabla asistencia_tecnica
    @SuppressLint("Range")
    fun obtenerTodosLosDatos(): List<AsistenciaTecnicaModel> {
        val dataList = mutableListOf<AsistenciaTecnicaModel>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_ASISTENCIA_TECNICA"
        val cursor = db.rawQuery(query, null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(KEY_ID))
                val fecha = cursor.getString(cursor.getColumnIndex(KEY_FECHA))
                val distrito = cursor.getInt(cursor.getColumnIndex(KEY_DISTRITO))
                val lugar = cursor.getString(cursor.getColumnIndex(KEY_LUGAR))
                val actividad = cursor.getString(cursor.getColumnIndex(KEY_ACTIVIDAD))
                val codigoDistrito = cursor.getInt(cursor.getColumnIndex(KEY_CODIGO_DISTRITO))
                val municipio = cursor.getString(cursor.getColumnIndex(KEY_MUNICIPIO))
                val departamento = cursor.getString(cursor.getColumnIndex(KEY_DEPARTAMENTO))
                val hora = cursor.getString(cursor.getColumnIndex(KEY_HORA))
                val participantes = cursor.getString(cursor.getColumnIndex(KEY_PARTICIPANTES))
                val participantesMujeres = cursor.getInt(cursor.getColumnIndex(KEY_PARTICIPANTES_MUJERES))
                val participantesHombres = cursor.getInt(cursor.getColumnIndex(KEY_PARTICIPANTES_HOMBRES))
                val sumaParticipantes = cursor.getInt(cursor.getColumnIndex(KEY_SUMA_PARTICIPANTES))
                val objetivo = cursor.getString(cursor.getColumnIndex(KEY_OBJETIVO))
                val hallazgos = cursor.getString(cursor.getColumnIndex(KEY_HALLAZGOS))
                val recomendaciones = cursor.getString(cursor.getColumnIndex(KEY_RECOMENDACIONES))
                val acuerdos = cursor.getString(cursor.getColumnIndex(KEY_ACUERDOS))

                // Crea un objeto AsistenciaTecnicaModel con los datos
                val asistenciaTecnica = AsistenciaTecnicaModel(
                    id, fecha, distrito, lugar, actividad, codigoDistrito,
                    municipio, departamento, hora, participantes, participantesMujeres,
                    participantesHombres, sumaParticipantes,objetivo, hallazgos, recomendaciones, acuerdos
                )

                // Agrega el objeto a la lista
                dataList.add(asistenciaTecnica)
            } while (cursor.moveToNext())
        }

        cursor?.close()
        db.close()

        return dataList
    }


}