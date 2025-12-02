package com.ecocoins.campus.presentation.logros

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.Logro
import com.ecocoins.campus.data.model.LogrosResumen
import com.ecocoins.campus.data.repository.LogrosRepository
import com.ecocoins.campus.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogrosViewModel @Inject constructor(
    private val logrosRepository: LogrosRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _logros = MutableLiveData<List<Logro>>()
    val logros: LiveData<List<Logro>> = _logros

    private val _resumenLogros = MutableLiveData<LogrosResumen?>()
    val resumenLogros: LiveData<LogrosResumen?> = _resumenLogros

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadLogros()
        loadResumenLogros()
    }

    fun loadLogros() {
        viewModelScope.launch {
            _isLoading.value = true

            val userId = userPreferences.userId.firstOrNull()

            if (userId != null) {
                logrosRepository.getLogrosUsuario(userId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _logros.value = resource.data ?: emptyList()
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
            } else {
                _error.value = "Usuario no autenticado"
                _isLoading.value = false
            }
        }
    }

    private fun loadResumenLogros() {
        viewModelScope.launch {
            val userId = userPreferences.userId.firstOrNull()

            if (userId != null) {
                logrosRepository.getResumenLogros(userId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _resumenLogros.value = resource.data
                        }
                        is Resource.Error -> {
                            _error.value = resource.message
                        }
                        is Resource.Loading -> {}
                    }
                }
            }
        }
    }

    fun verificarLogros() {
        viewModelScope.launch {
            val userId = userPreferences.userId.firstOrNull()

            if (userId != null) {
                logrosRepository.verificarLogros(userId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            loadLogros() // Recargar logros después de verificar
                            loadResumenLogros()
                        }
                        is Resource.Error -> {
                            _error.value = resource.message
                        }
                        is Resource.Loading -> {}
                    }
                }
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}