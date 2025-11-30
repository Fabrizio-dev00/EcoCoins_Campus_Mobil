package com.ecocoins.campus.presentation.logros

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.Logro
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.repository.LogrosRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogrosViewModel @Inject constructor(
    private val logrosRepository: LogrosRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _logros = MutableLiveData<Resource<List<Logro>>>()
    val logros: LiveData<Resource<List<Logro>>> = _logros

    private val _resumen = MutableLiveData<Map<String, Any>>()
    val resumen: LiveData<Map<String, Any>> = _resumen

    private val _logroDesbloqueado = MutableLiveData<Logro?>()
    val logroDesbloqueado: LiveData<Logro?> = _logroDesbloqueado

    init {
        cargarLogros()
    }

    fun cargarLogros() {
        viewModelScope.launch {
            _logros.value = Resource.Loading()
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = logrosRepository.obtenerLogrosUsuario(usuarioId)) {
                is Resource.Success -> {
                    val data = result.data ?: return@launch

                    // Extraer logros
                    val logrosData = data["logros"] as? List<*>
                    val gson = Gson()
                    val logrosJson = gson.toJson(logrosData)
                    val logrosType = object : TypeToken<List<Logro>>() {}.type
                    val logrosList: List<Logro> = gson.fromJson(logrosJson, logrosType)

                    _logros.value = Resource.Success(logrosList)

                    // Extraer resumen
                    val resumenData = data["resumen"] as? Map<String, Any>
                    if (resumenData != null) {
                        _resumen.value = resumenData
                    }
                }
                is Resource.Error -> {
                    _logros.value = Resource.Error(result.message ?: "Error al cargar logros")
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun verificarLogros() {
        viewModelScope.launch {
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = logrosRepository.verificarLogros(usuarioId)) {
                is Resource.Success -> {
                    val data = result.data
                    val nuevosLogros = data?.get("nuevosLogrosDesbloqueados") as? List<*>

                    if (!nuevosLogros.isNullOrEmpty()) {
                        // Notificar que hay nuevos logros desbloqueados
                        cargarLogros()
                    }
                }
                is Resource.Error -> {
                    // Error silencioso, no molestamos al usuario
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun filtrarPorCategoria(categoria: String?): List<Logro> {
        val todosLosLogros = (_logros.value as? Resource.Success)?.data ?: emptyList()

        return if (categoria.isNullOrEmpty() || categoria == "TODOS") {
            todosLosLogros
        } else {
            todosLosLogros.filter { it.categoria == categoria }
        }
    }

    fun obtenerLogrosDesbloqueados(): List<Logro> {
        val todosLosLogros = (_logros.value as? Resource.Success)?.data ?: emptyList()
        return todosLosLogros.filter { it.desbloqueado == true }
    }

    fun obtenerLogrosBloqueados(): List<Logro> {
        val todosLosLogros = (_logros.value as? Resource.Success)?.data ?: emptyList()
        return todosLosLogros.filter { it.desbloqueado != true }
    }
}