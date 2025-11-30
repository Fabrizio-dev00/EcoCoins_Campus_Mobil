package com.ecocoins.campus.presentation.ranking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.PosicionUsuario
import com.ecocoins.campus.data.model.RankingItem
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.repository.RankingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankingViewModel  @Inject constructor(
    private val rankingRepository: RankingRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _rankingList = MutableLiveData<Resource<List<RankingItem>>>()
    val rankingList: LiveData<Resource<List<RankingItem>>> = _rankingList

    private val _podio = MutableLiveData<Resource<List<RankingItem>>>()
    val podio: LiveData<Resource<List<RankingItem>>> = _podio

    private val _posicionUsuario = MutableLiveData<Resource<PosicionUsuario>>()
    val posicionUsuario: LiveData<Resource<PosicionUsuario>> = _posicionUsuario

    private val _periodoSeleccionado = MutableLiveData<String>()
    val periodoSeleccionado: LiveData<String> = _periodoSeleccionado

    init {
        _periodoSeleccionado.value = "SEMANAL"
        cargarRanking("SEMANAL")
    }

    fun cargarRanking(periodo: String) {
        viewModelScope.launch {
            _rankingList.value = Resource.Loading()
            _periodoSeleccionado.value = periodo

            when (val result = rankingRepository.obtenerRanking(periodo)) {
                is Resource.Success -> {
                    _rankingList.value = Resource.Success(result.data ?: emptyList())
                    cargarPodio(periodo)
                    cargarPosicionUsuario()
                }
                is Resource.Error -> {
                    _rankingList.value = Resource.Error(result.message ?: "Error al cargar ranking")
                }
                is Resource.Loading -> {
                    // Ya está en loading
                }
            }
        }
    }

    private fun cargarPodio(periodo: String) {
        viewModelScope.launch {
            when (val result = rankingRepository.obtenerPodio(periodo)) {
                is Resource.Success -> {
                    _podio.value = Resource.Success(result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _podio.value = Resource.Error(result.message ?: "Error al cargar podio")
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun cargarPosicionUsuario() {
        viewModelScope.launch {
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = rankingRepository.obtenerPosicionUsuario(usuarioId)) {
                is Resource.Success -> {
                    _posicionUsuario.value = Resource.Success(result.data!!)
                }
                is Resource.Error -> {
                    _posicionUsuario.value = Resource.Error(result.message ?: "Error")
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun cambiarPeriodo(periodo: String) {
        cargarRanking(periodo)
    }

    fun refresh() {
        cargarRanking(_periodoSeleccionado.value ?: "SEMANAL")
    }
}