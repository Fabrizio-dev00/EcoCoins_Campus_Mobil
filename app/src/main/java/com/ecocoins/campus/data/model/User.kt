package com.ecocoins.campus.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("_id")
    val id: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("correo")
    val correo: String,

    @SerializedName("rol")
    val rol: String = "usuario",

    @SerializedName("ecoCoins")
    val ecoCoins: Int = 0,

    @SerializedName("carrera")
    val carrera: String? = null,

    @SerializedName("telefono")
    val telefono: String? = null,

    @SerializedName("nivel")
    val nivel: Int = 0,

    @SerializedName("totalReciclajes")
    val totalReciclajes: Int = 0,

    @SerializedName("totalKgReciclados")
    val totalKgReciclados: Double = 0.0,

    @SerializedName("estado")
    val estado: String = "activo",

    @SerializedName("firebaseUid")
    val firebaseUid: String? = null,
    val email: String
)

// REQUEST MODELS
data class LoginRequest(
    @SerializedName("correo")
    val correo: String,

    @SerializedName("contrasenia")
    val contrasenia: String
)

data class RegisterRequest(
    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("correo")
    val correo: String,

    @SerializedName("contrasenia")
    val contrasenia: String,

    @SerializedName("carrera")
    val carrera: String,

    @SerializedName("telefono")
    val telefono: String? = null
)

// RESPONSE MODELS
data class LoginResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("tipo")
    val tipo: String = "Bearer",

    @SerializedName("id")
    val id: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("correo")
    val correo: String,

    @SerializedName("rol")
    val rol: String,

    @SerializedName("ecoCoins")
    val ecoCoins: Int
)