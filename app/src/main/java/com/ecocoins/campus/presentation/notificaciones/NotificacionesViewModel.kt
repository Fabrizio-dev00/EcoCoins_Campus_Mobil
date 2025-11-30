package com.ecocoins.campus.presentation.notificaciones

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.Notificacion
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.repository.NotificacionesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificacionesViewModel @Inject constructor(
    private val notificacionesRepository: NotificacionesRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _notificaciones = MutableLiveData<Resource<List<Notificacion>>>()
    val notificaciones: LiveData<Resource<List<Notificacion>>> = _notificaciones

    private val _noLeidas = MutableLiveData<Int>()
    val noLeidas: LiveData<Int> = _noLeidas

    init {
        cargarNotificaciones()
        cargarContadorNoLeidas()
    }

    fun cargarNotificaciones() {
        viewModelScope.launch {
            _notificaciones.value = Resource.Loading()
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = notificacionesRepository.obtenerNotificaciones(usuarioId)) {
                is Resource.Success -> {
                    _notificaciones.value = Resource.Success(result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _notificaciones.value = Resource.Error(
                        result.message ?: "Error al cargar notificaciones"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun cargarContadorNoLeidas() {
        viewModelScope.launch {
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = notificacionesRepository.contarNoLeidas(usuarioId)) {
                is Resource.Success -> {
                    _noLeidas.value = result.data ?: 0
                }
                is Resource.Error -> {
                    _noLeidas.value = 0
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun marcarComoLeida(notificacionId: String) {
        viewModelScope.launch {
            when (notificacionesRepository.marcarComoLeida(notificacionId)) {
                is Resource.Success -> {
                    cargarNotificaciones()
                    cargarContadorNoLeidas()
                }
                is Resource.Error -> {
                    // Error silencioso
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun marcarTodasComoLeidas() {
        viewModelScope.launch {
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (notificacionesRepository.marcarTodasComoLeidas(usuarioId)) {
                is Resource.Success -> {
                    cargarNotificaciones()
                    cargarContadorNoLeidas()
                }
                is Resource.Error -> {
                    // Error silencioso
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun refresh() {
        cargarNotificaciones()
        cargarContadorNoLeidas()
    }
}