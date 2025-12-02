package com.ecocoins.campus.presentation.perfil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.User
import com.ecocoins.campus.data.repository.PerfilRepository
import com.ecocoins.campus.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val perfilRepository: PerfilRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _updateState = MutableLiveData<Resource<User>>()
    val updateState: LiveData<Resource<User>> = _updateState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadPerfil()
    }

    fun loadPerfil() {
        viewModelScope.launch {
            _isLoading.value = true

            val userId = userPreferences.userId.firstOrNull()

            if (userId != null) {
                perfilRepository.getPerfil(userId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _user.value = resource.data
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
            } else {
                _error.value = "Usuario no autenticado"
                _isLoading.value = false
            }
        }
    }

    fun updatePerfil(user: User) {
        viewModelScope.launch {
            val userId = userPreferences.userId.firstOrNull()

            if (userId != null) {
                perfilRepository.updatePerfil(userId, user).collect { resource ->
                    _updateState.value = resource

                    if (resource is Resource.Success) {
                        loadPerfil() // Recargar perfil
                    }
                }
            } else {
                _error.value = "Usuario no autenticado"
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = null
    }

    fun clearError() {
        _error.value = null
    }
}