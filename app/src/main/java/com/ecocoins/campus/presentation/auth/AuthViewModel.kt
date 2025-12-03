package com.ecocoins.campus.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.User
import com.ecocoins.campus.data.model.RegisterRequest
import com.ecocoins.campus.data.model.LoginRequest
import com.ecocoins.campus.data.repository.AuthRepository
import com.ecocoins.campus.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * Registrar usuario con Firebase + MongoDB
     * 1. Crea usuario en Firebase Authentication
     * 2. Sincroniza datos en MongoDB
     */
    fun register(
        nombre: String,
        email: String,
        password: String,
        carrera: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                // Validaciones básicas
                if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
                    _authState.value = AuthState.Error("Todos los campos son obligatorios")
                    return@launch
                }

                if (password.length < 6) {
                    _authState.value = AuthState.Error("La contraseña debe tener al menos 6 caracteres")
                    return@launch
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    _authState.value = AuthState.Error("Correo electrónico inválido")
                    return@launch
                }

                // PASO 1: Crear usuario en Firebase
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser == null) {
                    _authState.value = AuthState.Error("Error al crear usuario en Firebase")
                    return@launch
                }

                // PASO 2: Sincronizar con MongoDB
                val registerRequest = RegisterRequest(
                    nombre = nombre,
                    email = email,
                    password = password,
                    carrera = carrera,
                    firebaseUid = firebaseUser.uid
                )

                authRepository.register(registerRequest).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            // Usuario registrado exitosamente, queda autenticado
                            val user = resource.data
                            if (user != null) {
                                _authState.value = AuthState.Success(
                                    user = user,
                                    message = "Usuario registrado exitosamente"
                                )
                            } else {
                                _authState.value = AuthState.Error("No se recibieron datos del usuario")
                            }
                        }
                        is Resource.Error -> {
                            // Si falla MongoDB, eliminar usuario de Firebase para mantener consistencia
                            try {
                                firebaseUser.delete().await()
                            } catch (deleteError: Exception) {
                                // Log del error pero continuar con el error original
                            }
                            _authState.value = AuthState.Error(
                                resource.message ?: "Error al registrar usuario"
                            )
                        }
                        is Resource.Loading -> {
                            _authState.value = AuthState.Loading
                        }
                    }
                }

            } catch (e: FirebaseAuthException) {
                val errorMessage = when (e.errorCode) {
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "Este correo ya está registrado"
                    "ERROR_WEAK_PASSWORD" -> "La contraseña es muy débil"
                    "ERROR_INVALID_EMAIL" -> "Correo electrónico inválido"
                    else -> "Error de Firebase: ${e.message}"
                }
                _authState.value = AuthState.Error(errorMessage)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "Error inesperado al registrar"
                )
            }
        }
    }

    /**
     * Login con Firebase + MongoDB
     * 1. Autentica con Firebase
     * 2. Obtiene datos de usuario desde MongoDB
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                // Validaciones
                if (email.isBlank() || password.isBlank()) {
                    _authState.value = AuthState.Error("Email y contraseña son obligatorios")
                    return@launch
                }

                // PASO 1: Login con Firebase
                val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser == null) {
                    _authState.value = AuthState.Error("Error al iniciar sesión con Firebase")
                    return@launch
                }

                // PASO 2: Obtener datos del usuario desde MongoDB
                val loginRequest = LoginRequest(email = email, password = password)

                authRepository.login(loginRequest).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val user = resource.data
                            if (user != null) {
                                _authState.value = AuthState.Success(
                                    user = user,
                                    message = "Login exitoso"
                                )
                            } else {
                                _authState.value = AuthState.Error("No se encontraron datos del usuario")
                            }
                        }
                        is Resource.Error -> {
                            _authState.value = AuthState.Error(
                                resource.message ?: "Error al obtener datos del usuario"
                            )
                        }
                        is Resource.Loading -> {
                            _authState.value = AuthState.Loading
                        }
                    }
                }

            } catch (e: FirebaseAuthException) {
                val errorMessage = when (e.errorCode) {
                    "ERROR_INVALID_CREDENTIAL" -> "Credenciales inválidas"
                    "ERROR_USER_NOT_FOUND" -> "Usuario no encontrado"
                    "ERROR_WRONG_PASSWORD" -> "Contraseña incorrecta"
                    "ERROR_USER_DISABLED" -> "Usuario deshabilitado"
                    else -> "Error de Firebase: ${e.message}"
                }
                _authState.value = AuthState.Error(errorMessage)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "Error inesperado al iniciar sesión"
                )
            }
        }
    }

    /**
     * Cerrar sesión (Firebase + Local)
     */
    fun logout() {
        viewModelScope.launch {
            try {
                firebaseAuth.signOut()
                authRepository.logout()
                _authState.value = AuthState.Initial
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error al cerrar sesión")
            }
        }
    }

    /**
     * Resetear estado
     */
    fun resetState() {
        _authState.value = AuthState.Initial
    }

    /**
     * Verificar si hay sesión activa
     */
    fun checkAuthStatus() {
        viewModelScope.launch {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                // Usuario autenticado con Firebase, cargar datos desde MongoDB
                try {
                    val loginRequest = LoginRequest(
                        email = currentUser.email ?: "",
                        password = "" // No necesitamos password para verificar sesión
                    )

                    authRepository.login(loginRequest).collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                resource.data?.let { user ->
                                    _authState.value = AuthState.Success(
                                        user = user,
                                        message = "Sesión activa"
                                    )
                                }
                            }
                            else -> {
                                _authState.value = AuthState.Initial
                            }
                        }
                    }
                } catch (e: Exception) {
                    _authState.value = AuthState.Initial
                }
            } else {
                _authState.value = AuthState.Initial
            }
        }
    }
}

/**
 * Estados de autenticación
 */
sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(
        val user: User,
        val message: String
    ) : AuthState()
    data class Error(val message: String) : AuthState()
}
