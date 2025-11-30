package com.ecocoins.campus.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.model.User
import com.ecocoins.campus.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val notificacionesRepository: NotificacionesRepository,
    private val reciclajeRepository: ReciclajeRepository,
    private val logrosRepository: LogrosRepository
) : ViewModel() {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _notificacionesNoLeidas = MutableLiveData<Int>()
    val notificacionesNoLeidas: LiveData<Int> = _notificacionesNoLeidas

    private val _estadisticasRapidas = MutableLiveData<Map<String, Any>>()
    val estadisticasRapidas: LiveData<Map<String, Any>> = _estadisticasRapidas

    init {
        cargarUsuario()
        cargarNotificacionesNoLeidas()
        cargarEstadisticasRapidas()
    }

    private fun cargarUsuario() {
        _currentUser.value = userPreferences.getUser()
    }

    private fun cargarNotificacionesNoLeidas() {
        viewModelScope.launch {
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = notificacionesRepository.contarNoLeidas(usuarioId)) {
                is Resource.Success -> {
                    _notificacionesNoLeidas.value = result.data ?: 0
                }
                is Resource.Error -> {
                    _notificacionesNoLeidas.value = 0
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun cargarEstadisticasRapidas() {
        viewModelScope.launch {
            val usuarioId = userPreferences.getUserId() ?: return@launch

            // Cargar reciclajes recientes
            when (val reciclajes = reciclajeRepository.obtenerReciclajesUsuario(usuarioId)) {
                is Resource.Success -> {
                    val totalReciclajes = reciclajes.data?.size ?: 0
                    val totalKg = reciclajes.data?.sumOf { it.pesoKg } ?: 0.0

                    _estadisticasRapidas.value = mapOf(
                        "totalReciclajes" to totalReciclajes,
                        "totalKg" to totalKg,
                        "ecoCoins" to (userPreferences.getUser()?.ecoCoins ?: 0)
                    )
                }
                else -> {
                    _estadisticasRapidas.value = mapOf(
                        "totalReciclajes" to 0,
                        "totalKg" to 0.0,
                        "ecoCoins" to (userPreferences.getUser()?.ecoCoins ?: 0)
                    )
                }
            }
        }
    }

    fun verificarLogrosNuevos() {
        viewModelScope.launch {
            val usuarioId = userPreferences.getUserId() ?: return@launch
            logrosRepository.verificarLogros(usuarioId)
        }
    }

    fun refresh() {
        cargarUsuario()
        cargarNotificacionesNoLeidas()
        cargarEstadisticasRapidas()
    }

    fun actualizarUsuario(user: User) {
        userPreferences.saveUser(user)
        _currentUser.value = user
    }
}