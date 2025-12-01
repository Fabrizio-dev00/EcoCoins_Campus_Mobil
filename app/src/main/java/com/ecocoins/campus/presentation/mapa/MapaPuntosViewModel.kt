package com.ecocoins.campus.presentation.mapa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.PuntoReciclaje
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.repository.MapaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapaPuntosViewModel @Inject constructor(
    private val mapaRepository: MapaRepository
) : ViewModel() {

    // ✅ CORREGIDO: Usar StateFlow en lugar de LiveData
    private val _uiState = MutableStateFlow(MapaPuntosUiState())
    val uiState: StateFlow<MapaPuntosUiState> = _uiState.asStateFlow()

    init {
        loadPuntos()
    }

    // ✅ CORREGIDO: Renombrado de cargarPuntos() a loadPuntos()
    fun loadPuntos(tipo: String? = null, estado: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = mapaRepository.obtenerPuntos(tipo, estado)) {
                is Resource.Success -> {
                    val puntos = result.data ?: emptyList()
                    _uiState.update {
                        it.copy(
                            puntos = puntos,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Error al cargar puntos"
                        )
                    }
                }
                is Resource.Loading -> {
                    // Ya manejado con isLoading
                }
            }
        }
    }

    fun buscarPuntosCercanos(latitud: Double, longitud: Double, radioKm: Double = 5.0) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = mapaRepository.obtenerPuntosCercanos(latitud, longitud, radioKm)) {
                is Resource.Success -> {
                    val puntos = result.data ?: emptyList()
                    _uiState.update {
                        it.copy(
                            puntos = puntos,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Error al buscar puntos cercanos"
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun filtrarPorMaterial(material: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    filtroMaterial = material
                )
            }

            when (val result = mapaRepository.filtrarPorMaterial(material)) {
                is Resource.Success -> {
                    val puntos = result.data ?: emptyList()
                    _uiState.update {
                        it.copy(
                            puntos = puntos,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Error al filtrar puntos"
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun seleccionarPunto(punto: PuntoReciclaje) {
        _uiState.update { it.copy(puntoSeleccionado = punto) }
    }

    fun limpiarSeleccion() {
        _uiState.update { it.copy(puntoSeleccionado = null) }
    }

    fun aplicarFiltroTipo(tipo: String?) {
        _uiState.update { it.copy(filtroTipo = tipo) }
        loadPuntos(tipo = tipo)
    }

    fun limpiarFiltros() {
        _uiState.update {
            it.copy(
                filtroTipo = null,
                filtroMaterial = null
            )
        }
        loadPuntos()
    }

    fun refresh() {
        loadPuntos(_uiState.value.filtroTipo)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

// ✅ AGREGADO: Clase UiState que faltaba
data class MapaPuntosUiState(
    val puntos: List<PuntoReciclaje> = emptyList(),
    val puntoSeleccionado: PuntoReciclaje? = null,
    val filtroTipo: String? = null,
    val filtroMaterial: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)