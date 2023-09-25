package com.example.sirae

class MunicipioData {
    companion object {
        fun getMunicipios(departamento: String): Array<String> {
            // Obtener la lista de municipios para el departamento seleccionado
            return municipiosPorDepartamento[departamento] ?: emptyArray()
        }

        val municipiosPorDepartamento = mapOf(
            "Ahuachapán" to arrayOf(
                "Ahuachapán",
                "Apaneca",
                "Atiquizaya",
                "Concepción de Ataco",
                "El Refugio",
                "Guaymango",
                "Jujutla",
                "San Francisco Menéndez",
                "San Lorenzo",
                "San Pedro Puxtla",
                "Tacuba",
                "Turín"
            ),
            "Cabañas" to arrayOf(
                "Cinquera",
                "Dolores",
                "Guacotecti",
                "Ilobasco",
                "Jutiapa",
                "San Isidro",
                "Sensuntepeque",
                "Tejutepeque",
                "Victoria"
            ),
            "Chalatenango" to arrayOf(
                "Agua Caliente",
                "Arcatao",
                "Azacualpa",
                "Chalatenango",
                "Citalá",
                "Comalapa",
                "Concepción Quezaltepeque",
                "Dulce Nombre de María",
                "El Carrizal",
                "El Paraíso",
                "La Laguna",
                "La Palma",
                "La Reina",
                "Las Vueltas",
                "Nueva Concepción",
                "Nueva Trinidad",
                "Ojos de Agua",
                "Potonico",
                "San Antonio de la Cruz",
                "San Antonio Los Ranchos",
                "San Fernando",
                "San Francisco Lempa",
                "San Francisco Morazán",
                "San Ignacio",
                "San Isidro Labrador",
                "San Luis del Carmen",
                "San Miguel de Mercedes",
                "San Rafael",
                "Santa Rita",
                "Tejutla"
            ),
            "Cuscatlán" to arrayOf(
                "Candelaria",
                "Cojutepeque",
                "El Carmen",
                "El Rosario",
                "Monte San Juan",
                "Oratorio de Concepción",
                "San Bartolomé Perulapía",
                "San Cristóbal",
                "San José Guayabal",
                "San Pedro Perulapán",
                "San Rafael Cedros",
                "San Ramón",
                "Santa Cruz Analquito",
                "Santa Cruz Michapa",
                "Suchitoto",
                "Tenancingo"
            ),
            "La Libertad" to arrayOf(
                "Antiguo Cuscatlán",
                "Chiltiupán",
                "Ciudad Arce",
                "Colón",
                "Comasagua",
                "Huizúcar",
                "Jayaque",
                "Jicalapa",
                "La Libertad",
                "Santa Tecla",
                "Nuevo Cuscatlán",
                "Quezaltepeque",
                "Sacacoyo",
                "San Juan Opico",
                "San Matías",
                "San Pablo Tacachico",
                "Talnique",
                "Tamanique",
                "Teotepeque",
                "Zaragoza"
            ),
            "La Paz" to arrayOf(
                "Cuyultitán",
                "El Rosario",
                "Jerusalén",
                "Mercedes La Ceiba",
                "Olocuilta",
                "Paraíso de Osorio",
                "San Antonio Masahuat",
                "San Emigdio",
                "San Francisco Chinameca",
                "San Juan Nonualco",
                "San Juan Talpa",
                "San Juan Tepezontes",
                "San Luis La Herradura",
                "San Luis Talpa",
                "San Miguel Tepezontes",
                "San Pedro Masahuat",
                "San Pedro Nonualco",
                "San Rafael Obrajuelo",
                "Santa María Ostuma",
                "Santiago Nonualco",
                "Tapalhuaca",
                "Zacatecoluca"
            ),
            "La Unión" to arrayOf(
                "Anamorós",
                "Bolívar",
                "Concepción de Oriente",
                "Conchagua",
                "El Carmen",
                "El Sauce",
                "Intipucá",
                "La Unión",
                "Lislique",
                "Meanguera del Golfo",
                "Nueva Esparta",
                "Pasaquina",
                "Polorós",
                "San Alejo",
                "San José",
                "Santa Rosa de Lima",
                "Yayantique"
            ),
            "Morazán" to arrayOf(
                "Cacaopera",
                "Corinto",
                "Delicias de Concepción",
                "El Divisadero",
                "El Rosario",
                "Gualococti",
                "Guatajiagua",
                "Joateca",
                "Jocoaitique",
                "Jocoro",
                "Lolotique",
                "Meanguera",
                "Osicala",
                "Perquín",
                "San Carlos",
                "San Fernando",
                "San Francisco Gotera",
                "San Isidro",
                "San Simón",
                "Sensembra",
                "Sociedad",
                "Torola",
                "Yamabal",
                "Yoloaiquín"
            ),
            "San Miguel" to arrayOf(
                "Carolina",
                "Chapeltique",
                "Chinameca",
                "Chirilagua",
                "Ciudad Barrios",
                "Comacarán",
                "El Tránsito",
                "Lolotique",
                "Moncagua",
                "Nueva Guadalupe",
                "Nuevo Edén de San Juan",
                "Quelepa",
                "San Antonio",
                "San Gerardo",
                "San Jorge",
                "San Luis de la Reina",
                "San Miguel",
                "San Rafael Oriente",
                "Sesori",
                "Uluazapa"
            ),
            "San Salvador" to arrayOf(
                "Aguilares",
                "Apopa",
                "Ayutuxtepeque",
                "Cuscatancingo",
                "Delgado",
                "El Paisnal",
                "Guazapa",
                "Ilopango",
                "Mejicanos",
                "Nejapa",
                "Panchimalco",
                "Rosario de Mora",
                "San Marcos",
                "San Martín",
                "San Salvador",
                "Santiago Texacuangos",
                "Santo Tomás",
                "Soyapango",
                "Tonacatepeque"
            ),
            "San Vicente" to arrayOf(
                "Apastepeque",
                "Guadalupe",
                "San Cayetano Istepeque",
                "San Esteban Catarina",
                "San Ildefonso",
                "San Lorenzo",
                "San Sebastián",
                "San Vicente",
                "Santa Clara",
                "Santo Domingo",
                "Tecoluca",
                "Tepetitán",
                "Verapaz"
            ),
            "Santa Ana" to arrayOf(
                "Candelaria de la Frontera",
                "Chalchuapa",
                "Coatepeque",
                "El Congo",
                "El Porvenir",
                "Masahuat",
                "Metapán",
                "San Antonio Pajonal",
                "San Sebastián Salitrillo",
                "Santa Ana",
                "Santa Rosa Guachipilín",
                "Santiago de la Frontera",
                "Texistepeque"
            ),
            "Sonsonate" to arrayOf(
                "Acajutla",
                "Armenia",
                "Caluco",
                "Cuisnahuat",
                "Izalco",
                "Juayúa",
                "Nahuizalco",
                "Nahulingo",
                "Salcoatitán",
                "San Antonio del Monte",
                "San Julián",
                "Santa Catarina Masahuat",
                "Santa Isabel Ishuatán",
                "Santo Domingo de Guzmán",
                "Sonsonate",
                "Sonzacate"
            ),
            "Usulután" to arrayOf(
                "Alegría",
                "Berlín",
                "California",
                "Concepción Batres",
                "El Triunfo",
                "Ereguayquín",
                "Estanzuelas",
                "Jiquilisco",
                "Jucuapa",
                "Jucuarán",
                "Mercedes Umaña",
                "Nueva Granada",
                "Ozatlán",
                "Puerto El Triunfo",
                "San Agustín",
                "San Buenaventura",
                "San Dionisio",
                "San Francisco Javier",
                "Santa Elena",
                "Santa María",
                "Santiago de María",
                "Tecapán",
                "Usulután"
            )
            // Agrega los municipios para los demás departamentos...
        )

    }

}
