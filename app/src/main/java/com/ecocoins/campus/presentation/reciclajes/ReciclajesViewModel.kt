package com.ecocoins.campus.presentation.reciclajes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.Reciclaje
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.repository.ReciclajeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReciclajesViewModel @Inject constructor(
    private val reciclajeRepository: ReciclajeRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _reciclajes = MutableLiveData<Resource<List<Reciclaje>>>()
    val reciclajes: LiveData<Resource<List<Reciclaje>>> = _reciclajes

    private val _registrarResult = MutableLiveData<Resource<Reciclaje>>()
    val registrarResult: LiveData<Resource<Reciclaje>> = _registrarResult

    private val _validacionIA = MutableLiveData<Resource<Map<String, Any>>>()
    val validacionIA: LiveData<Resource<Map<String, Any>>> = _validacionIA

    init {
        cargarReciclajes()
    }

    fun cargarReciclajes() {
        viewModelScope.launch {
            _reciclajes.value = Resource.Loading()
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = reciclajeRepository.obtenerReciclajesUsuario(usuarioId)) {
                is Resource.Success -> {
                    _reciclajes.value = Resource.Success(result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _reciclajes.value = Resource.Error(
                        result.message ?: "Error al cargar reciclajes"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun registrarReciclaje(
        material: String,
        pesoKg: Double,
        ubicacion: String,
        contenedorId: String
    ) {
        viewModelScope.launch {
            _registrarResult.value = Resource.Loading()
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = reciclajeRepository.registrarReciclaje(
                usuarioId, material, pesoKg, ubicacion, contenedorId
            )) {
                is Resource.Success -> {
                    _registrarResult.value = Resource.Success(result.data!!)
                    cargarReciclajes()
                }
                is Resource.Error -> {
                    _registrarResult.value = Resource.Error(
                        result.message ?: "Error al registrar reciclaje"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun validarConIA(imageBase64: String) {
        viewModelScope.launch {
            _validacionIA.value = Resource.Loading()

            when (val result = reciclajeRepository.validarConIA(imageBase64)) {
                is Resource.Success -> {
                    _validacionIA.value = Resource.Success(result.data!!)
                }
                is Resource.Error -> {
                    _validacionIA.value = Resource.Error(
                        result.message ?: "Error al validar imagen"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun limpiarRegistrarResult() {
        _registrarResult.value = null
    }

    fun limpiarValidacionIA() {
        _validacionIA.value = null
    }

    fun refresh() {
        cargarReciclajes()
    }
}