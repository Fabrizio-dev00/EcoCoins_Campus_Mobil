package com.ecocoins.campus.data.model

data class RankingUsuario(
    val id: String,
    val nombre: String,
    val carrera: String,
    val ecoCoins: Int,
    val totalReciclajes: Int,
    val totalKgReciclados: Double,
    val nivel: Int,
    val posicion: Int,
    val avatarUrl: String? = null,
    val badges: List<String> = emptyList()
)

data class RankingResponse(
    val miPosicion: Int,
    val miPuntos: Int,
    val topUsuarios: List<RankingUsuario>,
    val totalUsuarios: Int
)

data class PeriodoRanking(
    val id: String,
    val nombre: String,
    val descripcion: String
) {
    companion object {
        val SEMANAL = PeriodoRanking("semanal", "Esta Semana", "Últimos 7 días")
        val MENSUAL = PeriodoRanking("mensual", "Este Mes", "Últimos 30 días")
        val HISTORICO = PeriodoRanking("historico", "Histórico", "Todo el tiempo")

        fun valores() = listOf(SEMANAL, MENSUAL, HISTORICO)
    }
}