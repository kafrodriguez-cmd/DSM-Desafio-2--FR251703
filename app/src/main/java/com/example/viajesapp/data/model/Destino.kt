package com.example.viajesapp.data.model

data class Destino(
    var id: String = "",
    var nombre: String = "",
    var pais: String = "",
    var precio: Double = 0.0,
    var descripcion: String = "",
    var imagenUrl: String = "",
    var userId: String = ""
) {
    // Constructor vacío requerido por Firestore
    constructor() : this("", "", "", 0.0, "", "", "")
}
