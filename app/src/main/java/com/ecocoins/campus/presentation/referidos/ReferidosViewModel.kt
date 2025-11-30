package com.ecocoins.campus.presentation.referidos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.ReferidosInfo
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.repository.ReferidosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReferidosViewModel @Inject constructor(
    private val referidosRepository: ReferidosRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _referidosInfo = MutableLiveData<Resource<ReferidosInfo>>()
    val referidosInfo: LiveData<Resource<ReferidosInfo>> = _referidosInfo

    private val _codigoGenerado = MutableLiveData<Resource<String>>()
    val codigoGenerado: LiveData<Resource<String>> = _codigoGenerado

    private val _validacionCodigo = MutableLiveData<Resource<Boolean>>()
    val validacionCodigo: LiveData<Resource<Boolean>> = _validacionCodigo

    init {
        cargarReferidos()
    }

    fun cargarReferidos() {
        viewModelScope.launch {
            _referidosInfo.value = Resource.Loading()
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = referidosRepository.obtenerReferidos(usuarioId)) {
                is Resource.Success -> {
                    _referidosInfo.value = Resource.Success(result.data!!)
                }
                is Resource.Error -> {
                    _referidosInfo.value = Resource.Error(
                        result.message ?: "Error al cargar referidos"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun generarCodigo() {
        viewModelScope.launch {
            _codigoGenerado.value = Resource.Loading()
            val usuarioId = userPreferences.getUserId() ?: return@launch
            val nombre = userPreferences.getUser()?.nombre ?: return@launch

            when (val result = referidosRepository.generarCodigo(usuarioId, nombre)) {
                is Resource.Success -> {
                    _codigoGenerado.value = Resource.Success(result.data?.codigo ?: "")
                    cargarReferidos()
                }
                is Resource.Error -> {
                    _codigoGenerado.value = Resource.Error(
                        result.message ?: "Error al generar código"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun validarCodigo(codigo: String) {
        viewModelScope.launch {
            _validacionCodigo.value = Resource.Loading()

            when (val result = referidosRepository.validarCodigo(codigo)) {
                is Resource.Success -> {
                    _validacionCodigo.value = Resource.Success(true)
                }
                is Resource.Error -> {
                    _validacionCodigo.value = Resource.Error(result.message ?: "Código inválido")
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun compartirCodigo(): String {
        val info = (_referidosInfo.value as? Resource.Success)?.data
        val codigo = info?.codigoReferido ?: ""

        return "¡Únete a EcoCoins Campus! 🌱♻️\n\n" +
                "Usa mi código de referido: $codigo\n\n" +
                "Recicla, gana EcoCoins y canjea premios increíbles. " +
                "¡Juntos hacemos la diferencia! 🌍💚"
    }

    fun refresh() {
        cargarReferidos()
    }
}