package com.miempresa.ecocoinscampus.data.model

import com.google.gson.annotations.SerializedName

data class EstadisticasGenerales(
    @SerializedName("total_usuarios")
    val totalUsuarios: Int,

    @SerializedName("total_reciclajes")
    val totalReciclajes: Int,

    @SerializedName("total_ecocoins")
    val totalEcocoins: Double,

    @SerializedName("materiales_mas_reciclados")
    val materialesMasReciclados: List<MaterialStat>
)

data class MaterialStat(
    @SerializedName("_id")
    val tipo: String,

    @SerializedName("total")
    val total: Double,

    @SerializedName("ecocoins")
    val ecocoins: Double
)

data class UserRanking(
    @SerializedName("usuario")
    val usuario: String,

    @SerializedName("total_ecocoins")
    val totalEcocoins: Double,

    @SerializedName("ranking")
    val ranking: Int? = null
)