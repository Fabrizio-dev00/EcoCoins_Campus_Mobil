package com.ecocoins.campus.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.model.User
import com.ecocoins.campus.data.repository.AuthRepository
import com.ecocoins.campus.data.repository.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val userPreferences: UserPreferences
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
        _isLoggedIn.value = firebaseAuthRepository.isUserLoggedIn() &&
                userPreferences.isLoggedIn()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()

            // 1. Autenticar con Firebase
            val firebaseResult = firebaseAuthRepository.loginWithEmail(email, password)

            if (firebaseResult.isFailure) {
                _loginState.value = Resource.Error(
                    firebaseResult.exceptionOrNull()?.message ?: "Error en Firebase Auth"
                )
                return@launch
            }

            val firebaseUser = firebaseResult.getOrNull()!!
            userPreferences.saveFirebaseUid(firebaseUser.uid)

            // 2. Login en backend
            when (val result = authRepository.login(email, password)) {
                is Resource.Success -> {
                    val userData = result.data
                    val user = User(
                        id = userData?.get("id") as? String ?: "",
                        nombre = userData?.get("nombre") as? String ?: "",
                        correo = userData?.get("correo") as? String ?: email,
                        rol = userData?.get("rol") as? String ?: "usuario",
                        ecoCoins = (userData?.get("ecoCoins") as? Double)?.toInt() ?: 0,
                        carrera = userData?.get("carrera") as? String,
                        telefono = userData?.get("telefono") as? String,
                        nivel = (userData?.get("nivel") as? Double)?.toInt() ?: 0,
                        totalReciclajes = (userData?.get("totalReciclajes") as? Double)?.toInt() ?: 0,
                        totalKgReciclados = userData?.get("totalKgReciclados") as? Double ?: 0.0,
                        estado = userData?.get("estado") as? String ?: "activo",
                        firebaseUid = firebaseUser.uid,
                        email = email
                    )

                    userPreferences.saveUser(user)
                    _loginState.value = Resource.Success(user)
                    _isLoggedIn.value = true
                }
                is Resource.Error -> {
                    _loginState.value = Resource.Error(result.message ?: "Error en login")
                }
                is Resource.Loading -> {
                    // Ya está en loading
                }
            }
        }
    }

    fun register(nombre: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = Resource.Loading()

            // 1. Registrar en Firebase
            val firebaseResult = firebaseAuthRepository.registerWithEmail(email, password)

            if (firebaseResult.isFailure) {
                _registerState.value = Resource.Error(
                    firebaseResult.exceptionOrNull()?.message ?: "Error en Firebase Auth"
                )
                return@launch
            }

            val firebaseUser = firebaseResult.getOrNull()!!
            userPreferences.saveFirebaseUid(firebaseUser.uid)

            // 2. Registrar en backend
            when (val result = authRepository.register(nombre, email, password)) {
                is Resource.Success -> {
                    val user = result.data!!
                    userPreferences.saveUser(user)
                    _registerState.value = Resource.Success(user)
                    _isLoggedIn.value = true
                }
                is Resource.Error -> {
                    _registerState.value = Resource.Error(result.message ?: "Error en registro")
                }
                is Resource.Loading -> {
                    // Ya está en loading
                }
            }
        }
    }

    fun logout() {
        firebaseAuthRepository.logout()
        _isLoggedIn.value = false
    }

    fun getCurrentUser(): User? {
        return userPreferences.getUser()
    }
}
