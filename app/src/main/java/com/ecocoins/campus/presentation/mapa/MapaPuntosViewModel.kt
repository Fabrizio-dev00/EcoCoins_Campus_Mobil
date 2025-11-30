package com.ecocoins.campus.presentation.mapa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.PuntoReciclaje
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.repository.MapaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapaPuntosViewModel @Inject constructor(
    private val mapaRepository: MapaRepository
) : ViewModel() {

    private val _puntos = MutableLiveData<Resource<List<PuntoReciclaje>>>()
    val puntos: LiveData<Resource<List<PuntoReciclaje>>> = _puntos

    private val _puntoSeleccionado = MutableLiveData<PuntoReciclaje?>()
    val puntoSeleccionado: LiveData<PuntoReciclaje?> = _puntoSeleccionado

    private val _filtroTipo = MutableLiveData<String?>()
    val filtroTipo: LiveData<String?> = _filtroTipo

    private val _filtroMaterial = MutableLiveData<String?>()
    val filtroMaterial: LiveData<String?> = _filtroMaterial

    init {
        cargarPuntos()
    }

    fun cargarPuntos(tipo: String? = null, estado: String? = null) {
        viewModelScope.launch {
            _puntos.value = Resource.Loading()

            when (val result = mapaRepository.obtenerPuntos(tipo, estado)) {
                is Resource.Success -> {
                    _puntos.value = Resource.Success(result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _puntos.value = Resource.Error(
                        result.message ?: "Error al cargar puntos"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun buscarPuntosCercanos(latitud: Double, longitud: Double, radioKm: Double = 5.0) {
        viewModelScope.launch {
            _puntos.value = Resource.Loading()

            when (val result = mapaRepository.obtenerPuntosCercanos(latitud, longitud, radioKm)) {
                is Resource.Success -> {
                    _puntos.value = Resource.Success(result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _puntos.value = Resource.Error(
                        result.message ?: "Error al buscar puntos cercanos"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun filtrarPorMaterial(material: String) {
        viewModelScope.launch {
            _puntos.value = Resource.Loading()
            _filtroMaterial.value = material

            when (val result = mapaRepository.filtrarPorMaterial(material)) {
                is Resource.Success -> {
                    _puntos.value = Resource.Success(result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _puntos.value = Resource.Error(
                        result.message ?: "Error al filtrar puntos"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun seleccionarPunto(punto: PuntoReciclaje) {
        _puntoSeleccionado.value = punto
    }

    fun limpiarSeleccion() {
        _puntoSeleccionado.value = null
    }

    fun aplicarFiltroTipo(tipo: String?) {
        _filtroTipo.value = tipo
        cargarPuntos(tipo = tipo)
    }

    fun limpiarFiltros() {
        _filtroTipo.value = null
        _filtroMaterial.value = null
        cargarPuntos()
    }

    fun refresh() {
        cargarPuntos(_filtroTipo.value)
    }
}