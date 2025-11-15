package com.miempresa.ecocoinscampus.presentation.materiales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miempresa.ecocoinscampus.data.model.Material
import com.miempresa.ecocoinscampus.data.repository.MaterialRepository
import com.miempresa.ecocoinscampus.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MaterialesUiState(
    val isLoading: Boolean = false,
    val materials: List<Material> = emptyList(),
    val tiposMateriales: List<String> = emptyList(),
    val registroExitoso: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MaterialesViewModel @Inject constructor(
    private val materialRepository: MaterialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaterialesUiState())
    val uiState: StateFlow<MaterialesUiState> = _uiState.asStateFlow()

    init {
        loadMaterials()
        loadTiposMateriales()
    }

    private fun loadMaterials() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = materialRepository.getUserMaterials()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            materials = result.data
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                else -> {}
            }
        }
    }

    private fun loadTiposMateriales() {
        _uiState.update {
            it.copy(tiposMateriales = materialRepository.getTiposMateriales())
        }
    }

    fun registerMaterial(tipo: String, cantidad: Double, puntoRecoleccion: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, registroExitoso = false) }

            when (val result = materialRepository.registerMaterial(tipo, cantidad, puntoRecoleccion)) {
                is Result.Success -> {
                    // Recargar lista de materiales
                    loadMaterials()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            registroExitoso = true
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                else -> {}
            }
        }
    }

    fun resetRegistroExitoso() {
        _uiState.update { it.copy(registroExitoso = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}