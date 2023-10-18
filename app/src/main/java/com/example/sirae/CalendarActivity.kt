package com.example.sirae
import android.app.AlertDialog
import android.os.Bundle
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarActivity : AppCompatActivity() {
    private var selectedDate: String? = null
    private val events = mutableListOf<Event>() // Lista para almacenar eventos
    private var email: String? = null
    private lateinit var databaseReference: DatabaseReference
    private val datesWithEvents = mutableListOf<String>()
    private var eventCounter: Long = 0 // Contador para generar IDs únicos


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        email = intent.getStringExtra("email")

        // Inicializa la referencia de la base de datos de Firebase
        val userEmail = email?.substringBefore("@")?.replace(".", "_")
        databaseReference = FirebaseDatabase.getInstance().getReference(userEmail + "_Calendario")

        // Carga eventos existentes desde Firebase
        loadEventsFromFirebase()

        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = formatDate(year, month, dayOfMonth)
            // Al seleccionar una fecha, muestra los eventos para esa fecha
            showEventsForSelectedDate(selectedDate)
        }

        val fabAddEvent = findViewById<FloatingActionButton>(R.id.fabAddEvent)

        fabAddEvent.setOnClickListener {
            if (selectedDate != null) {
                showAddEventDialog()
            } else {
                Toast.makeText(this, "Selecciona una fecha primero", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun loadEventsFromFirebase() {
        // Agrega un listener para cargar eventos desde Firebase
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (eventSnapshot in dataSnapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)
                    if (event != null) {
                        events.add(event)
                        // Agrega la fecha con evento a la lista
                        datesWithEvents.add(event.date)
                    }
                }

                // Ordena los eventos por su ID
                events.sortBy { it.id }

                // Actualiza visualmente el calendario con las fechas con eventos
                updateCalendarWithEventDates()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Maneja el error en caso de que la lectura falle
                Toast.makeText(
                    this@CalendarActivity,
                    "Error al cargar eventos desde Firebase",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun updateCalendarWithEventDates() {
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val calendar = Calendar.getInstance()

        // Obtiene la fecha actual
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Establece la fecha actual en el calendario
        calendarView.setDate(calendar.timeInMillis, true, true)

        // Establece la fecha actual como seleccionada
        selectedDate = formatDate(year, month, day)
    }


    private fun showAddEventDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Agregar Evento")

        val eventDescriptionEditText = EditText(this)
        eventDescriptionEditText.hint = "Descripción del evento"
        builder.setView(eventDescriptionEditText)

        builder.setPositiveButton("Agregar") { dialog, _ ->
            val eventDescription = eventDescriptionEditText.text.toString()
            val userEmail = email?.substringBefore("@")?.replace(".", "_")

            if (userEmail != null) {
                // Genera un ID único para el evento
                val eventId = generateEventId()

                // Crea un objeto Evento con ID único
                val evento = Event(eventId, selectedDate ?: "", eventDescription)

                // Sube el evento a Firebase usando el ID como clave
                databaseReference.child(eventId.toString()).setValue(evento)

                // Agrega el evento a la lista local
                events.add(evento)

                // Ordena la lista de eventos por ID
                events.sortBy { it.id }

                dialog.dismiss()
            }


    }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun generateEventId(): Long {
        val maxId = events.maxByOrNull { it.id }?.id ?: 0
        return maxId + 1
    }


    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    // En el método showEventsForSelectedDate, ordena los eventos por su ID antes de mostrarlos
    private fun showEventsForSelectedDate(selectedDate: String?) {
        if (selectedDate.isNullOrEmpty()) {
            return
        }

        val eventsForSelectedDate = events.filter { it.date == selectedDate }

        if (eventsForSelectedDate.isNotEmpty()) {
            val eventDescriptions = eventsForSelectedDate.joinToString("\n") { "${it.id}. Evento: ${it.description}" }

            // Mostrar eventos en una ventana emergente
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Eventos para $selectedDate")
            alertDialogBuilder.setMessage(eventDescriptions)
            alertDialogBuilder.setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialogBuilder.show()
        } else {
            // No hay eventos para esta fecha
            Toast.makeText(this, "No hay eventos para $selectedDate", Toast.LENGTH_SHORT).show()
        }
    }



    data class Event(val id: Long = 0, val date: String = "", val description: String = "")

}

