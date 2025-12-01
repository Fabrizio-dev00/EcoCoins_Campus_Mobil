package com.ecocoins.campus.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.User
import com.ecocoins.campus.utils.Result
import com.ecocoins.campus.data.repository.AuthRepository
import com.ecocoins.campus.data.repository.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _loginState = MutableStateFlow<Result<User>?>(null)
    val loginState: StateFlow<Result<User>?> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<Result<User>?>(null)
    val registerState: StateFlow<Result<User>?> = _registerState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val firebaseLoggedIn = firebaseAuthRepository.isUserLoggedIn()
            val localUser = userPreferences.getUser()
            _isLoggedIn.value = firebaseLoggedIn && localUser != null
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Result.Loading()

            // 1. Autenticar con Firebase
            val firebaseResult = firebaseAuthRepository.loginWithEmail(email, password)

            if (firebaseResult.isFailure) {
                _loginState.value = Result.Error(
                    message = firebaseResult.exceptionOrNull()?.message ?: "Error en Firebase Auth",
                    exception = firebaseResult.exceptionOrNull() as? Exception
                )
                return@launch
            }

            val firebaseUser = firebaseResult.getOrNull()!!
            userPreferences.saveFirebaseUid(firebaseUser.uid)

            // 2. Login en backend
            when (val result = authRepository.login(email, password)) {
                is Result.Success -> {
                    val userData = result.data
                    val user = User(
                        id = userData?.get("id") as? String ?: "",
                        nombre = userData?.get("nombre") as? String ?: "",
                        correo = userData?.get("correo") as? String ?: email,
                        rol = userData?.get("rol") as? String ?: "usuario",
                        ecoCoins = (userData?.get("ecoCoins") as? Number)?.toInt() ?: 0,
                        carrera = userData?.get("carrera") as? String,
                        telefono = userData?.get("telefono") as? String,
                        nivel = (userData?.get("nivel") as? Number)?.toInt() ?: 0,
                        totalReciclajes = (userData?.get("totalReciclajes") as? Number)?.toInt() ?: 0,
                        totalKgReciclados = (userData?.get("totalKgReciclados") as? Number)?.toDouble() ?: 0.0,
                        estado = userData?.get("estado") as? String ?: "activo",
                        firebaseUid = firebaseUser.uid,
                        email = email
                    )

                    userPreferences.saveUser(user)
                    _loginState.value = Result.Success(user)
                    _isLoggedIn.value = true
                }
                is Result.Error -> {
                    _loginState.value = Result.Error(
                        message = result.message,
                        exception = result.exception
                    )
                }
                is Result.Loading -> {
                    // Ya está en loading
                }
            }
        }
    }

    fun register(nombre: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = Result.Loading()

            // 1. Registrar en Firebase
            val firebaseResult = firebaseAuthRepository.registerWithEmail(email, password)

            if (firebaseResult.isFailure) {
                _registerState.value = Result.Error(
                    message = firebaseResult.exceptionOrNull()?.message ?: "Error en Firebase Auth",
                    exception = firebaseResult.exceptionOrNull() as? Exception
                )
                return@launch
            }

            val firebaseUser = firebaseResult.getOrNull()!!
            userPreferences.saveFirebaseUid(firebaseUser.uid)

            // 2. Registrar en backend
            when (val result = authRepository.register(nombre, email, password)) {
                is Result.Success -> {
                    val user = result.data
                    userPreferences.saveUser(user)
                    _registerState.value = Result.Success(user)
                    _isLoggedIn.value = true
                }
                is Result.Error -> {
                    _registerState.value = Result.Error(
                        message = result.message,
                        exception = result.exception
                    )
                }
                is Result.Loading -> {
                    // Ya está en loading
                }
            }
        }
    }

    suspend fun logout() {
        firebaseAuthRepository.logout()

        // Limpiar datos locales
        when (authRepository.logout()) {
            is Result.Success -> {
                _isLoggedIn.value = false
                _loginState.value = null
                _registerState.value = null
            }
            is Result.Error -> {
                // Error al limpiar, pero igual cerrar sesión localmente
                _isLoggedIn.value = false
            }
            is Result.Loading -> {
                // No debería llegar aquí
            }
        }
    }

    suspend fun getCurrentUser(): User? {
        return userPreferences.getUser()
    }

    fun clearLoginState() {
        _loginState.value = null
    }

    fun clearRegisterState() {
        _registerState.value = null
    }
}