package com.ecocoins.campus.presentation.reciclajes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.Reciclaje
import com.ecocoins.campus.data.repository.ReciclajeRepository
import com.ecocoins.campus.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReciclajesHistoryViewModel @Inject constructor(
    private val reciclajeRepository: ReciclajeRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _historial = MutableLiveData<List<Reciclaje>>()
    val historial: LiveData<List<Reciclaje>> = _historial

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    internal var currentPage = 0
    private val pageSize = 20

    init {
        loadHistorial()
    }

    fun loadHistorial() {
        viewModelScope.launch {
            _isLoading.value = true

            val userId = userPreferences.userId.firstOrNull()

            if (userId != null) {
                reciclajeRepository.getHistorialReciclajes(userId, currentPage, pageSize).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _historial.value = resource.data ?: emptyList()
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

    fun loadMoreHistorial() {
        currentPage++
        loadHistorial()
    }

    fun refreshHistorial() {
        currentPage = 0
        loadHistorial()
    }

    fun clearError() {
        _error.value = null
    }
}