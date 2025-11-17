package com.miempresa.ecocoinscampus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    @SerializedName("_id")
    val id: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("rol")
    val rol: String = "estudiante",

    @SerializedName("eco_coins")
    val ecoCoins: Double = 0.0,

    @SerializedName("carrera")
    val carrera: String? = null,

    @SerializedName("imagen_perfil")
    val imagenPerfil: String? = null
)

data class LoginRequest(
    @SerializedName("correo")
    val email: String,
    @SerializedName("contrasenia")
    val password: String
)

data class RegisterRequest(
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("correo")
    val email: String,
    @SerializedName("contrasenia")
    val password: String,
    @SerializedName("carrera")
    val carrera: String,
    @SerializedName("rol")
    val rol: String = "estudiante"
)

data class AuthResponse(
    @SerializedName("mensaje")
    val mensaje: String,

    @SerializedName("usuario")
    val usuario: User,

    @SerializedName("token")
    val token: String? = null
)