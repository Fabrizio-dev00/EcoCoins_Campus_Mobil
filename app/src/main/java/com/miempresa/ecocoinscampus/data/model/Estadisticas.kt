package com.miempresa.ecocoinscampus.data.model

import com.google.gson.annotations.SerializedName

data class Estadisticas(
    @SerializedName("totalUsuarios")
    val totalUsuarios: Int,

    @SerializedName("totalReciclajes")
    val totalReciclajes: Int,

    @SerializedName("totalKgReciclados")
    val totalKgReciclados: Double,

    @SerializedName("totalEcoCoinsGeneradas")
    val totalEcoCoinsGeneradas: Int,

    @SerializedName("materialesMasReciclados")
    val materialesMasReciclados: List<MaterialStat>? = null,

    @SerializedName("rankingUsuarios")
    val rankingUsuarios: List<UserRanking>? = null
)

data class EstadisticasUsuario(
    @SerializedName("usuarioId")
    val usuarioId: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("totalReciclajes")
    val totalReciclajes: Int,

    @SerializedName("totalKgReciclados")
    val totalKgReciclados: Double,

    @SerializedName("ecoCoinsGanadas")
    val ecoCoinsGanadas: Int,

    @SerializedName("nivel")
    val nivel: String,

    @SerializedName("rankingPosicion")
    val rankingPosicion: Int? = null,

    @SerializedName("reciclajesPorMaterial")
    val reciclajesPorMaterial: List<MaterialStat>? = null
)

data class MaterialStat(
    @SerializedName("_id")
    val tipo: String,

    @SerializedName("total")
    val total: Double,

    @SerializedName("cantidad")
    val cantidad: Int,

    @SerializedName("ecocoins")
    val ecocoins: Int
)

data class UserRanking(
    @SerializedName("usuario")
    val usuario: String,

    @SerializedName("usuarioId")
    val usuarioId: String? = null,

    @SerializedName("total_ecocoins")
    val totalEcocoins: Int,

    @SerializedName("ranking")
    val ranking: Int? = null
)