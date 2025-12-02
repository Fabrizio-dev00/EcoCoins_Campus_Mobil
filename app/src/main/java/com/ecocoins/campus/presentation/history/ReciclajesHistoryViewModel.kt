package com.ecocoins.campus.presentation.reciclajes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.Reciclaje
import com.ecocoins.campus.data.repository.ReciclajeRepository
import com.ecocoins.campus.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReciclajesHistoryViewModel @Inject constructor(
    private val reciclajeRepository: ReciclajeRepository
) : ViewModel() {

    private val _historial = MutableLiveData<List<Reciclaje>>()
    val historial: LiveData<List<Reciclaje>> = _historial

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _currentPage = MutableLiveData(0)
    val currentPage: LiveData<Int> = _currentPage

    private val pageSize = 20

    init {
        loadHistorial()
    }

    fun loadHistorial() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            reciclajeRepository.getHistorialReciclajes(_currentPage.value ?: 0, pageSize)
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _historial.value = resource.data ?: emptyList()
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

    fun loadMoreHistorial() {
        val nextPage = (_currentPage.value ?: 0) + 1
        _currentPage.value = nextPage

        viewModelScope.launch {
            reciclajeRepository.getHistorialReciclajes(nextPage, pageSize)
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val currentList = _historial.value ?: emptyList()
                            val newList = resource.data ?: emptyList()
                            _historial.value = currentList + newList
                        }
                        is Resource.Error -> {
                            _error.value = resource.message
                        }
                        else -> {}
                    }
                }
        }
    }

    fun refreshHistorial() {
        _currentPage.value = 0
        loadHistorial()
    }
}