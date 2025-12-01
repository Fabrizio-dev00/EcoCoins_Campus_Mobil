package com.ecocoins.campus.data.model

/**
 * Tipos de materiales reciclables
 */
enum class TipoMaterial(val nombre: String, val descripcion: String) {
    PLASTICO("Plástico", "Botellas, envases, bolsas"),
    PAPEL("Papel", "Periódicos, revistas, cajas"),
    CARTON("Cartón", "Cajas de cartón"),
    VIDRIO("Vidrio", "Botellas, frascos"),
    METAL("Metal", "Latas de aluminio"),
    ALUMINIO("Aluminio", "Latas, envases"),
    ELECTRONICOS("Electrónicos", "Dispositivos electrónicos"),
    ORGANICOS("Orgánicos", "Residuos orgánicos");

    companion object {
        fun fromString(tipo: String): TipoMaterial? {
            return values().find {
                it.nombre.equals(tipo, ignoreCase = true)
            }
        }
    }
}