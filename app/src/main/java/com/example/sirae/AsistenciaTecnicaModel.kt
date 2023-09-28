package com.example.sirae

data class AsistenciaTecnicaModel(
    val id: Long,
    val fecha: String,
    val distrito: String, //4 numeros
    val lugar: String, //texto
    val actividad: String, //texto
    val codigoDistrito: String, //5 digitos
    val municipio: String,
    val departamento: String,
    val hora: String,
    val participantes: String,
    val participantesMujeres: Int,
    val participantesHombres: Int,
    val sumaParticipantes: Int,
    val objetivo: String,
    val hallazgos: String,
    val recomendaciones: String,
    val acuerdos: String
)