package com.example.sirae

data class AsistenciaTecnicaModel(
    val id: Long,
    val fecha: String,
    val distrito: String,
    val lugar: String,
    val actividad: String,
    val codigoDistrito: String,
    val municipio: String,
    val departamento: String,
    val hora: String,
    val participantes: String,
    val participantesMujeres: String,
    val participantesHombres: String,
    val objetivo: String,
    val hallazgos: String,
    val recomendaciones: String,
    val acuerdos: String
)