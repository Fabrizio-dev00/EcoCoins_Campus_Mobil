package com.ecocoins.campus.data.model

data class EstadisticasDetalladas(
    val resumenGeneral: ResumenGeneral,
    val porTipoMaterial: List<EstadisticaMaterial>,
    val tendenciaSemanal: List<DatoTendencia>,
    val impactoAmbiental: ImpactoAmbiental,
    val comparativas: Comparativas,
    val rachas: RachasInfo
)

data class ResumenGeneral(
    val totalReciclajes: Int,
    val totalKgReciclados: Double,
    val totalEcoCoinsGanados: Int,
    val totalEcoCoinsGastados: Int,
    val saldoActual: Int,
    val promedioKgPorReciclaje: Double,
    val promedioEcoCoinsPorReciclaje: Int
)

data class EstadisticaMaterial(
    val tipoMaterial: String,
    val cantidad: Int,
    val pesoTotal: Double,
    val ecoCoinsGanados: Int,
    val porcentaje: Float,
    val color: String
)

data class DatoTendencia(
    val fecha: String,
    val cantidad: Int,
    val pesoKg: Double,
    val ecoCoins: Int
)

data class ImpactoAmbiental(
    val co2Ahorrado: Double, // en kg
    val arbolesEquivalentes: Int,
    val energiaAhorrada: Double, // en kWh
    val aguaAhorrada: Double, // en litros
    val residuosEvitados: Double // en kg
)

data class Comparativas(
    val promedioUniversidad: Double,
    val tuRendimiento: Double,
    val mejorQuePorc: Int,
    val posicionGeneral: Int
)

data class RachasInfo(
    val rachaActual: Int,
    val mejorRacha: Int,
    val diasTotales: Int,
    val ultimoReciclaje: String?
)