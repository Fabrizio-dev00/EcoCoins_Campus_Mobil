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
class ReciclajesViewModel @Inject constructor(
    private val reciclajeRepository: ReciclajeRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _reciclajes = MutableLiveData<List<Reciclaje>>()
    val reciclajes: LiveData<List<Reciclaje>> = _reciclajes

    private val _registrarState = MutableLiveData<Resource<Reciclaje>?>()
    val registrarState: LiveData<Resource<Reciclaje>> = _registrarState as LiveData<Resource<Reciclaje>>

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadReciclajes()
    }

    fun loadReciclajes() {
        viewModelScope.launch {
            _isLoading.value = true

            val userId = userPreferences.userId.firstOrNull()

            if (userId != null) {
                reciclajeRepository.getReciclajesUsuario(userId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _reciclajes.value = resource.data ?: emptyList()
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

    fun registrarReciclaje(
        materialTipo: String,
        cantidad: Double,
        peso: Double,
        ubicacion: String?,
        codigoQR: String?
    ) {
        viewModelScope.launch {
            reciclajeRepository.registrarReciclaje(
                materialTipo = materialTipo,
                cantidad = cantidad,
                peso = peso,
                ubicacion = ubicacion,
                codigoQR = codigoQR
            ).collect { resource ->
                _registrarState.value = resource

                if (resource is Resource.Success) {
                    loadReciclajes() // Recargar la lista después de registrar
                }
            }
        }
    }

    fun resetRegistrarState() {
        _registrarState.value = null
    }

    fun clearError() {
        _error.value = null
    }
}