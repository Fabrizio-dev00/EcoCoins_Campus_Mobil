package com.ecocoins.campus.presentation.scanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.Reciclaje
import com.ecocoins.campus.data.repository.ReciclajeRepository
import com.ecocoins.campus.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val reciclajeRepository: ReciclajeRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _scannedQRCode = MutableLiveData<String?>()
    val scannedQRCode: LiveData<String?> = _scannedQRCode

    private val _capturedPhotoUri = MutableLiveData<String?>()
    val capturedPhotoUri: LiveData<String?> = _capturedPhotoUri

    private val _selectedMaterial = MutableLiveData<String?>()
    val selectedMaterial: LiveData<String?> = _selectedMaterial

    private val _peso = MutableLiveData<Double>(0.0)
    val peso: LiveData<Double> = _peso

    private val _cantidad = MutableLiveData<Double>(1.0)
    val cantidad: LiveData<Double> = _cantidad

    private val _ubicacion = MutableLiveData<String?>()
    val ubicacion: LiveData<String?> = _ubicacion

    private val _registrarState = MutableLiveData<Resource<Reciclaje>>()
    val registrarState: LiveData<Resource<Reciclaje>> = _registrarState

    private val _isValidating = MutableLiveData<Boolean>()
    val isValidating: LiveData<Boolean> = _isValidating

    private val _validationResult = MutableLiveData<Boolean?>()
    val validationResult: LiveData<Boolean?> = _validationResult

    fun setScannedQRCode(qrCode: String) {
        _scannedQRCode.value = qrCode
    }

    fun setCapturedPhotoUri(uri: String) {
        _capturedPhotoUri.value = uri
    }

    fun setSelectedMaterial(material: String) {
        _selectedMaterial.value = material
    }

    fun setPeso(peso: Double) {
        _peso.value = peso
    }

    fun setCantidad(cantidad: Double) {
        _cantidad.value = cantidad
    }

    fun setUbicacion(ubicacion: String?) {
        _ubicacion.value = ubicacion
    }

    fun validateWithAI() {
        viewModelScope.launch {
            _isValidating.value = true

            // Simular validación con IA
            // En producción, aquí llamarías a un servicio de IA
            kotlinx.coroutines.delay(2000)

            // Por ahora, siempre valida como correcto
            _validationResult.value = true
            _isValidating.value = false
        }
    }

    fun registrarReciclaje() {
        viewModelScope.launch {
            val material = _selectedMaterial.value
            val pesoValue = _peso.value ?: 0.0
            val cantidadValue = _cantidad.value ?: 1.0
            val ubicacionValue = _ubicacion.value
            val qrCode = _scannedQRCode.value

            if (material != null && pesoValue > 0) {
                reciclajeRepository.registrarReciclaje(
                    materialTipo = material,
                    cantidad = cantidadValue,
                    peso = pesoValue,
                    ubicacion = ubicacionValue,
                    codigoQR = qrCode
                ).collect { resource ->
                    _registrarState.value = resource
                }
            }
        }
    }

    fun resetScannerFlow() {
        _scannedQRCode.value = null
        _capturedPhotoUri.value = null
        _selectedMaterial.value = null
        _peso.value = 0.0
        _cantidad.value = 1.0
        _ubicacion.value = null
        _registrarState.value = null
        _validationResult.value = null
    }

    fun resetRegistrarState() {
        _registrarState.value = null
    }
}