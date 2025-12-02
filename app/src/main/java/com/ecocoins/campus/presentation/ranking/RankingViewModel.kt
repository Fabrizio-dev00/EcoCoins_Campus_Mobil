package com.ecocoins.campus.presentation.ranking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.PosicionUsuario
import com.ecocoins.campus.data.model.RankingItem
import com.ecocoins.campus.data.repository.RankingRepository
import com.ecocoins.campus.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val rankingRepository: RankingRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _ranking = MutableLiveData<List<RankingItem>>()
    val ranking: LiveData<List<RankingItem>> = _ranking

    private val _posicionUsuario = MutableLiveData<PosicionUsuario?>()
    val posicionUsuario: LiveData<PosicionUsuario?> = _posicionUsuario

    private val _tipoRanking = MutableLiveData<String>("global")
    val tipoRanking: LiveData<String> = _tipoRanking

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadRanking()
        loadPosicionUsuario()
    }

    fun loadRanking(tipo: String = "global") {
        viewModelScope.launch {
            _isLoading.value = true
            _tipoRanking.value = tipo

            val flow = when (tipo) {
                "semanal" -> rankingRepository.getRankingSemanal()
                "mensual" -> rankingRepository.getRankingMensual()
                else -> rankingRepository.getRankingGlobal()
            }

            flow.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _ranking.value = resource.data ?: emptyList()
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
        }
    }

    private fun loadPosicionUsuario() {
        viewModelScope.launch {
            val userId = userPreferences.userId.firstOrNull()

            if (userId != null) {
                rankingRepository.getPosicionUsuario(userId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _posicionUsuario.value = resource.data
                        }
                        is Resource.Error -> {
                            _error.value = resource.message
                        }
                        is Resource.Loading -> {}
                    }
                }
            }
        }
    }

    fun refreshRanking() {
        loadRanking(_tipoRanking.value ?: "global")
        loadPosicionUsuario()
    }

    fun clearError() {
        _error.value = null
    }
}