package com.ecocoins.campus.presentation.mapa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.PuntoReciclaje
import com.ecocoins.campus.data.repository.MapaRepository
import com.ecocoins.campus.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapaPuntosViewModel @Inject constructor(
    private val mapaRepository: MapaRepository
) : ViewModel() {

    private val _puntos = MutableLiveData<List<PuntoReciclaje>>()
    val puntos: LiveData<List<PuntoReciclaje>> = _puntos

    private val _puntosCercanos = MutableLiveData<List<PuntoReciclaje>>()
    val puntosCercanos: LiveData<List<PuntoReciclaje>> = _puntosCercanos

    private val _selectedPunto = MutableLiveData<PuntoReciclaje?>()
    val selectedPunto: LiveData<PuntoReciclaje?> = _selectedPunto

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadPuntos()
    }

    fun loadPuntos() {
        viewModelScope.launch {
            _isLoading.value = true

            mapaRepository.getPuntosReciclaje().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _puntos.value = resource.data ?: emptyList()
                        _isLoading.value = false
                    }
                    is Resource.Error -> {
                        _error.value = resource.message
                        _isLoading.value = false
                    }
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }

    fun loadPuntosCercanos(latitud: Double, longitud: Double, radio: Double = 5.0) {
        viewModelScope.launch {
            _isLoading.value = true

            mapaRepository.getPuntosCercanos(latitud, longitud, radio).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _puntosCercanos.value = resource.data ?: emptyList()
                        _isLoading.value = false
                    }
                    is Resource.Error -> {
                        _error.value = resource.message
                        _isLoading.value = false
                    }
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }

    fun getPuntoById(puntoId: Long) {
        viewModelScope.launch {
            _isLoading.value = true

            mapaRepository.getPuntoById(puntoId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _selectedPunto.value = resource.data
                        _isLoading.value = false
                    }
                    is Resource.Error -> {
                        _error.value = resource.message
                        _isLoading.value = false
                    }
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }

    fun selectPunto(punto: PuntoReciclaje?) {
        _selectedPunto.value = punto
    }

    fun clearError() {
        _error.value = null
    }
}