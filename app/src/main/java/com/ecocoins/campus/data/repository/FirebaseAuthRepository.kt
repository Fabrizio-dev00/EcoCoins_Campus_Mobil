package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.User
import com.ecocoins.campus.data.model.ApiResponse
import com.ecocoins.campus.data.remote.ApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val apiService: ApiService
) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Registra usuario con Firebase y sincroniza con backend
     */
    suspend fun registrarConEmail(
        email: String,
        password: String,
        nombre: String,
        carrera: String
    ): Result<User> {
        return try {
            // 1. Crear en Firebase Auth
            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Error al crear usuario en Firebase"))

            // 2. Enviar email de verificación
            firebaseUser.sendEmailVerification().await()

            // 3. Obtener token
            val token = firebaseUser.getIdToken(false).await().token
                ?: return Result.failure(Exception("No se pudo obtener token"))

            // 4. Sincronizar con backend
            val request = mapOf(
                "firebaseUid" to firebaseUser.uid,
                "email" to email,
                "nombre" to nombre,
                "carrera" to carrera
            )

            val response = apiService.sincronizarUsuario(
                authHeader = "Bearer $token",
                request = request
            )

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                // Eliminar de Firebase si falla backend
                firebaseUser.delete().await()
                Result.failure(Exception(response.message ?: "Error al sincronizar"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Login con Firebase y obtiene perfil del backend
     */
    suspend fun loginConEmail(
        email: String,
        password: String
    ): Result<User> {
        return try {
            // 1. Autenticar en Firebase
            val authResult = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Error al iniciar sesión"))

            // 2. Verificar email
            if (!firebaseUser.isEmailVerified) {
                return Result.failure(Exception("Por favor verifica tu email"))
            }

            // 3. Obtener token
            val token = firebaseUser.getIdToken(false).await().token
                ?: return Result.failure(Exception("No se pudo obtener token"))

            // 4. Obtener perfil del backend
            val response = apiService.obtenerPerfil(
                authHeader = "Bearer $token"
            )

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener perfil"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun obtenerUsuarioActual(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    suspend fun obtenerToken(): String? {
        return try {
            firebaseAuth.currentUser?.getIdToken(false)?.await()?.token
        } catch (e: Exception) {
            null
        }
    }

    fun cerrarSesion() {
        firebaseAuth.signOut()
    }

    fun estaAutenticado(): Boolean {
        return firebaseAuth.currentUser != null
    }

    suspend fun reenviarEmailVerificacion(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return Result.failure(Exception("No hay usuario autenticado"))
            user.sendEmailVerification().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun enviarEmailRestablecimiento(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}