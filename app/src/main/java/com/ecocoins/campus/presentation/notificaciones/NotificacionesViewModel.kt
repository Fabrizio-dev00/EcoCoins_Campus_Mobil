package com.ecocoins.campus.presentation.notificaciones

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.Notificacion
import com.ecocoins.campus.data.repository.NotificacionesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificacionesViewModel @Inject constructor(
    private val repository: NotificacionesRepository
) : ViewModel() {

    private val _notificaciones = MutableLiveData<List<Notificacion>>()
    val notificaciones: LiveData<List<Notificacion>> = _notificaciones

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadNotificaciones() {
        viewModelScope.launch {
            _isLoading.value = true
            // Implementar llamada al repositorio
            _isLoading.value = false
        }
    }

    fun marcarComoLeida(notificacionId: Long) {
        viewModelScope.launch {
            // Implementar
        }
    }

    fun marcarTodasComoLeidas() {
        viewModelScope.launch {
            // Implementar
        }
    }
}