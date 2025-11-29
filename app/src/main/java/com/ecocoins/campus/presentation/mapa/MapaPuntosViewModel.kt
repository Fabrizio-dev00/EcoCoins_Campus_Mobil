package com.ecocoins.campus.presentation.mapa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.EstadoPunto
import com.ecocoins.campus.data.model.PuntoReciclaje
import com.ecocoins.campus.data.model.TipoPuntoReciclaje
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapaPuntosViewModel @Inject constructor(
    // TODO: Inyectar repositorio
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapaPuntosUiState())
    val uiState: StateFlow<MapaPuntosUiState> = _uiState.asStateFlow()

    fun loadPuntos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                delay(500)

                // MOCK DATA - Coordenadas de Lima, Perú
                val mockPuntos = listOf(
                    PuntoReciclaje(
                        id = "1",
                        nombre = "Campus Principal UTEC",
                        direccion = "Jr. Medrano Silva 165, Barranco",
                        latitud = -12.0986,
                        longitud = -77.0292,
                        tipo = TipoPuntoReciclaje.UNIVERSIDAD,
                        materialesAceptados = listOf("Plástico", "Papel", "Cartón", "Vidrio", "Metal"),
                        horario = "L-V 7:00-22:00, S 8:00-18:00",
                        distanciaKm = 0.0,
                        telefono = "+51 1 230 5000",
                        estado = EstadoPunto.ABIERTO,
                        capacidadActual = 45
                    ),
                    PuntoReciclaje(
                        id = "2",
                        nombre = "Centro de Acopio Barranco",
                        direccion = "Av. Grau 456, Barranco",
                        latitud = -12.1456,
                        longitud = -77.0201,
                        tipo = TipoPuntoReciclaje.CENTRO_ACOPIO,
                        materialesAceptados = listOf("Plástico", "Papel", "Cartón", "Vidrio"),
                        horario = "L-S 8:00-18:00",
                        distanciaKm = 0.8,
                        telefono = "+51 1 247 8899",
                        estado = EstadoPunto.ABIERTO,
                        capacidadActual = 65
                    ),
                    PuntoReciclaje(
                        id = "3",
                        nombre = "Punto Limpio Miraflores",
                        direccion = "Av. Larco 1301, Miraflores",
                        latitud = -12.1234,
                        longitud = -77.0345,
                        tipo = TipoPuntoReciclaje.PUNTO_LIMPIO,
                        materialesAceptados = listOf("Plástico", "Vidrio", "Metal", "Electrónicos"),
                        horario = "L-D 7:00-21:00",
                        distanciaKm = 1.2,
                        telefono = "+51 1 444 7777",
                        estado = EstadoPunto.ABIERTO,
                        capacidadActual = 30
                    ),
                    PuntoReciclaje(
                        id = "4",
                        nombre = "Contenedor Chorrillos",
                        direccion = "Av. Huaylas 234, Chorrillos",
                        latitud = -12.1678,
                        longitud = -77.0123,
                        tipo = TipoPuntoReciclaje.CONTENEDOR,
                        materialesAceptados = listOf("Plástico", "Papel"),
                        horario = "24/7",
                        distanciaKm = 2.1,
                        estado = EstadoPunto.LLENO,
                        capacidadActual = 95
                    ),
                    PuntoReciclaje(
                        id = "5",
                        nombre = "Centro Acopio San Isidro",
                        direccion = "Av. República de Panamá 3591, San Isidro",
                        latitud = -12.0987,
                        longitud = -77.0456,
                        tipo = TipoPuntoReciclaje.CENTRO_ACOPIO,
                        materialesAceptados = listOf("Papel", "Cartón", "Vidrio"),
                        horario = "L-V 9:00-17:00",
                        distanciaKm = 2.5,
                        telefono = "+51 1 421 5555",
                        estado = EstadoPunto.CERRADO,
                        capacidadActual = 20
                    ),
                    PuntoReciclaje(
                        id = "6",
                        nombre = "Punto Verde Surco",
                        direccion = "Av. Caminos del Inca 567, Surco",
                        latitud = -12.1345,
                        longitud = -76.9987,
                        tipo = TipoPuntoReciclaje.PUNTO_LIMPIO,
                        materialesAceptados = listOf("Plástico", "Papel", "Vidrio", "Metal", "Orgánicos"),
                        horario = "L-S 8:00-20:00",
                        distanciaKm = 3.2,
                        telefono = "+51 1 344 6666",
                        estado = EstadoPunto.ABIERTO,
                        capacidadActual = 50
                    )
                )

                _uiState.update {
                    it.copy(
                        puntos = mockPuntos.sortedBy { it.distanciaKm },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al cargar puntos: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun refresh() {
        loadPuntos()
    }
}

data class MapaPuntosUiState(
    val puntos: List<PuntoReciclaje> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)