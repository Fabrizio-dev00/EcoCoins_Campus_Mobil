package com.ecocoins.campus.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // TODO: Cargar configuraciones desde SharedPreferences o DataStore
            _uiState.update {
                it.copy(
                    notificacionesActivas = true,
                    emailNotificacionesActivas = false,
                    recordatoriosActivos = true,
                    modoOscuroActivo = false
                )
            }
        }
    }

    fun toggleNotificaciones(enabled: Boolean) {
        viewModelScope.launch {
            // TODO: Guardar en SharedPreferences/DataStore
            _uiState.update { it.copy(notificacionesActivas = enabled) }
        }
    }

    fun toggleEmailNotificaciones(enabled: Boolean) {
        viewModelScope.launch {
            // TODO: Guardar en SharedPreferences/DataStore
            _uiState.update { it.copy(emailNotificacionesActivas = enabled) }
        }
    }

    fun toggleRecordatorios(enabled: Boolean) {
        viewModelScope.launch {
            // TODO: Guardar en SharedPreferences/DataStore
            _uiState.update { it.copy(recordatoriosActivos = enabled) }
        }
    }

    fun toggleModoOscuro(enabled: Boolean) {
        viewModelScope.launch {
            // TODO: Guardar en SharedPreferences/DataStore y aplicar tema
            _uiState.update { it.copy(modoOscuroActivo = enabled) }
        }
    }
}

data class SettingsUiState(
    val notificacionesActivas: Boolean = true,
    val emailNotificacionesActivas: Boolean = false,
    val recordatoriosActivos: Boolean = true,
    val modoOscuroActivo: Boolean = false
)