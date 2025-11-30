package com.ecocoins.campus.presentation.recompensas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.Canje
import com.ecocoins.campus.data.model.Recompensa
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.repository.RecompensasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecompensasViewModel @Inject constructor(
    private val recompensasRepository: RecompensasRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _recompensas = MutableLiveData<Resource<List<Recompensa>>>()
    val recompensas: LiveData<Resource<List<Recompensa>>> = _recompensas

    private val _recompensaSeleccionada = MutableLiveData<Resource<Recompensa>>()
    val recompensaSeleccionada: LiveData<Resource<Recompensa>> = _recompensaSeleccionada

    private val _canjearResult = MutableLiveData<Resource<Canje>>()
    val canjearResult: LiveData<Resource<Canje>> = _canjearResult

    private val _canjes = MutableLiveData<Resource<List<Canje>>>()
    val canjes: LiveData<Resource<List<Canje>>> = _canjes

    private val _canjeSeleccionado = MutableLiveData<Resource<Canje>>()
    val canjeSeleccionado: LiveData<Resource<Canje>> = _canjeSeleccionado

    init {
        cargarRecompensas()
        cargarCanjes()
    }

    fun cargarRecompensas() {
        viewModelScope.launch {
            _recompensas.value = Resource.Loading()

            when (val result = recompensasRepository.obtenerRecompensas()) {
                is Resource.Success -> {
                    _recompensas.value = Resource.Success(result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _recompensas.value = Resource.Error(
                        result.message ?: "Error al cargar recompensas"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun cargarRecompensa(recompensaId: String) {
        viewModelScope.launch {
            _recompensaSeleccionada.value = Resource.Loading()

            when (val result = recompensasRepository.obtenerRecompensa(recompensaId)) {
                is Resource.Success -> {
                    _recompensaSeleccionada.value = Resource.Success(result.data!!)
                }
                is Resource.Error -> {
                    _recompensaSeleccionada.value = Resource.Error(
                        result.message ?: "Error al cargar recompensa"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun canjearRecompensa(recompensaId: String) {
        viewModelScope.launch {
            _canjearResult.value = Resource.Loading()
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = recompensasRepository.canjearRecompensa(usuarioId, recompensaId)) {
                is Resource.Success -> {
                    _canjearResult.value = Resource.Success(result.data!!)
                    cargarRecompensas()
                    cargarCanjes()
                }
                is Resource.Error -> {
                    _canjearResult.value = Resource.Error(
                        result.message ?: "Error al canjear recompensa"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun cargarCanjes(estado: String? = null) {
        viewModelScope.launch {
            _canjes.value = Resource.Loading()
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = recompensasRepository.obtenerCanjesUsuario(usuarioId, estado)) {
                is Resource.Success -> {
                    _canjes.value = Resource.Success(result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _canjes.value = Resource.Error(
                        result.message ?: "Error al cargar canjes"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun cargarCanje(canjeId: String) {
        viewModelScope.launch {
            _canjeSeleccionado.value = Resource.Loading()

            when (val result = recompensasRepository.obtenerCanje(canjeId)) {
                is Resource.Success -> {
                    _canjeSeleccionado.value = Resource.Success(result.data!!)
                }
                is Resource.Error -> {
                    _canjeSeleccionado.value = Resource.Error(
                        result.message ?: "Error al cargar canje"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun filtrarRecompensasDisponibles(): List<Recompensa> {
        val todasLasRecompensas = (_recompensas.value as? Resource.Success)?.data ?: emptyList()
        val ecoCoinsUsuario = userPreferences.getUser()?.ecoCoins ?: 0

        return todasLasRecompensas.filter {
            it.disponible && it.stock > 0 && it.precioEcoCoins <= ecoCoinsUsuario
        }
    }

    fun filtrarCanjesPorEstado(estado: String): List<Canje> {
        val todosLosCanjes = (_canjes.value as? Resource.Success)?.data ?: emptyList()
        return todosLosCanjes.filter { it.estado == estado }
    }

    fun limpiarCanjearResult() {
        _canjearResult.value = null
    }

    fun refresh() {
        cargarRecompensas()
        cargarCanjes()
    }
}