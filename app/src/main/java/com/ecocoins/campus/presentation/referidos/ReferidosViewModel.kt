package com.ecocoins.campus.presentation.referidos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.CodigoReferidoResponse
import com.ecocoins.campus.data.model.ReferidosInfo
import com.ecocoins.campus.data.repository.ReferidosRepository
import com.ecocoins.campus.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReferidosViewModel @Inject constructor(
    private val referidosRepository: ReferidosRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _referidosInfo = MutableLiveData<ReferidosInfo?>()
    val referidosInfo: LiveData<ReferidosInfo?> = _referidosInfo

    private val _generarCodigoState = MutableLiveData<Resource<CodigoReferidoResponse>>()
    val generarCodigoState: LiveData<Resource<CodigoReferidoResponse>> = _generarCodigoState

    private val _usarCodigoState = MutableLiveData<Resource<String>>()
    val usarCodigoState: LiveData<Resource<String>> = _usarCodigoState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadReferidosInfo()
    }

    fun loadReferidosInfo() {
        viewModelScope.launch {
            _isLoading.value = true

            val userId = userPreferences.userId.firstOrNull()

            if (userId != null) {
                referidosRepository.getReferidosInfo(userId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _referidosInfo.value = resource.data
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

    fun generarCodigoReferido() {
        viewModelScope.launch {
            val userId = userPreferences.userId.firstOrNull()

            if (userId != null) {
                referidosRepository.generarCodigoReferido(userId).collect { resource ->
                    _generarCodigoState.value = resource

                    if (resource is Resource.Success) {
                        loadReferidosInfo() // Recargar info
                    }
                }
            } else {
                _error.value = "Usuario no autenticado"
            }
        }
    }

    fun usarCodigoReferido(codigo: String) {
        viewModelScope.launch {
            val userId = userPreferences.userId.firstOrNull()

            if (userId != null) {
                referidosRepository.usarCodigoReferido(userId, codigo).collect { resource ->
                    _usarCodigoState.value = resource
                }
            } else {
                _error.value = "Usuario no autenticado"
            }
        }
    }

    fun resetGenerarCodigoState() {
        _generarCodigoState.value = null
    }

    fun resetUsarCodigoState() {
        _usarCodigoState.value = null
    }

    fun clearError() {
        _error.value = null
    }
}