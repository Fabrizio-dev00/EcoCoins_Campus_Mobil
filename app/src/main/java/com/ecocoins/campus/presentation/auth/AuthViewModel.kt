package com.ecocoins.campus.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.User
import com.ecocoins.campus.data.repository.AuthRepository
import com.ecocoins.campus.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableLiveData<Resource<User>>()
    val loginState: LiveData<Resource<User>> = _loginState

    private val _registerState = MutableLiveData<Resource<User>>()
    val registerState: LiveData<Resource<User>> = _registerState

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val loggedIn = authRepository.isLoggedIn().firstOrNull() ?: false
            _isLoggedIn.value = loggedIn
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password).collect { resource ->
                _loginState.value = resource
            }
        }
    }

    fun register(nombre: String, email: String, password: String, carrera: String) {
        viewModelScope.launch {
            authRepository.register(nombre, email, password, carrera).collect { resource ->
                _registerState.value = resource
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _isLoggedIn.value = false
        }
    }

    fun resetLoginState() {
        _loginState.value = null
    }

    fun resetRegisterState() {
        _registerState.value = null
    }
}