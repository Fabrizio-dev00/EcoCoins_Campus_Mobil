package com.ecocoins.campus.presentation.recompensas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.Canje
import com.ecocoins.campus.data.model.Recompensa
import com.ecocoins.campus.data.repository.RecompensasRepository
import com.ecocoins.campus.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecompensasViewModel @Inject constructor(
    private val recompensasRepository: RecompensasRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _recompensas = MutableLiveData<List<Recompensa>>()
    val recompensas: LiveData<List<Recompensa>> = _recompensas

    private val _selectedRecompensa = MutableLiveData<Recompensa?>()
    val selectedRecompensa: LiveData<Recompensa?> = _selectedRecompensa

    private val _canjeState = MutableLiveData<Resource<Canje>>()
    val canjeState: LiveData<Resource<Canje>> = _canjeState

    private val _ecoCoins = MutableLiveData<Long>()
    val ecoCoins: LiveData<Long> = _ecoCoins

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadRecompensas()
        loadEcoCoins()
    }

    private fun loadEcoCoins() {
        viewModelScope.launch {
            val coins = userPreferences.userEcoCoins.firstOrNull() ?: 0L
            _ecoCoins.value = coins
        }
    }

    fun loadRecompensas() {
        viewModelScope.launch {
            _isLoading.value = true

            recompensasRepository.getRecompensasDisponibles().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _recompensas.value = resource.data ?: emptyList()
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

    fun getRecompensaById(recompensaId: Long) {
        viewModelScope.launch {
            _isLoading.value = true

            recompensasRepository.getRecompensaById(recompensaId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _selectedRecompensa.value = resource.data
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

    fun canjearRecompensa(recompensaId: Long) {
        viewModelScope.launch {
            val userId = userPreferences.userId.firstOrNull()

            if (userId != null) {
                recompensasRepository.canjearRecompensa(userId, recompensaId).collect { resource ->
                    _canjeState.value = resource

                    if (resource is Resource.Success) {
                        loadRecompensas() // Recargar recompensas
                        loadEcoCoins() // Actualizar EcoCoins
                    }
                }
            } else {
                _error.value = "Usuario no autenticado"
            }
        }
    }

    fun resetCanjeState() {
        _canjeState.value = null
    }

    fun clearError() {
        _error.value = null
    }
}