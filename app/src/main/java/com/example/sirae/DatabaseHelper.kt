import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "mi_base_de_datos.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Define la estructura de tus tablas aquí
        val createTableSQL = """
            CREATE TABLE IF NOT EXISTS mi_tabla (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                fecha TEXT,
                distrito TEXT,
                lugar TEXT,
                actividad TEXT,
                codigoDistrito TEXT,
                municipio TEXT,
                departamento TEXT,
                hora TEXT,
                participantes TEXT,
                participantesMujeres TEXT,
                participantesHombres TEXT,
                objetivo TEXT,
                hallazgos TEXT,
                recomendaciones TEXT,
                acuerdos TEXT
            )
        """.trimIndent()

        db.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Aquí puedes realizar actualizaciones de la base de datos si es necesario
    }
}
