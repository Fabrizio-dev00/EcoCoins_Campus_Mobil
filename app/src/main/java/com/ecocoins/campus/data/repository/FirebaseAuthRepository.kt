package com.ecocoins.campus.data.repository

import android.util.Log
import com.ecocoins.campus.data.model.User
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
            Log.d("FirebaseAuth", "🔵 Iniciando registro - Email: $email")

            // 1. Crear en Firebase Auth
            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val firebaseUser = authResult.user
            if (firebaseUser == null) {
                Log.e("FirebaseAuth", "❌ Error: Usuario de Firebase es null")
                return Result.failure(Exception("Error al crear usuario en Firebase"))
            }

            Log.d("FirebaseAuth", "✅ Usuario creado en Firebase - UID: ${firebaseUser.uid}")

            // 2. Enviar email de verificación
            try {
                firebaseUser.sendEmailVerification().await()
                Log.d("FirebaseAuth", "📧 Email de verificación enviado")
            } catch (e: Exception) {
                Log.w("FirebaseAuth", "⚠️ No se pudo enviar email de verificación: ${e.message}")
            }

            // 3. Obtener token
            val token = firebaseUser.getIdToken(false).await().token
            if (token == null) {
                Log.e("FirebaseAuth", "❌ Error: No se pudo obtener token")
                // Eliminar usuario de Firebase si falla
                firebaseUser.delete().await()
                return Result.failure(Exception("No se pudo obtener token"))
            }

            Log.d("FirebaseAuth", "🔑 Token obtenido: ${token.take(20)}...")

            // 4. Sincronizar con backend
            val request = mapOf(
                "firebaseUid" to firebaseUser.uid,
                "email" to email,
                "nombre" to nombre,
                "carrera" to carrera
            )

            Log.d("FirebaseAuth", "🔄 Sincronizando con backend...")
            Log.d("FirebaseAuth", "📦 Request: $request")

            val response = apiService.sincronizarUsuario(
                authHeader = "Bearer $token",
                request = request
            )

            Log.d("FirebaseAuth", "📡 Response Code: ${response.code()}")
            Log.d("FirebaseAuth", "📡 Response Body: ${response.body()}")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    Log.d("FirebaseAuth", "✅ Usuario sincronizado con MongoDB exitosamente")
                    Log.d("FirebaseAuth", "👤 Usuario: ${apiResponse.data}")
                    Result.success(apiResponse.data)
                } else {
                    Log.e("FirebaseAuth", "❌ Error en respuesta del backend: ${apiResponse.message}")
                    // Eliminar de Firebase si falla backend
                    firebaseUser.delete().await()
                    Result.failure(Exception(apiResponse.message ?: "Error al sincronizar con backend"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("FirebaseAuth", "❌ Error HTTP ${response.code()}: $errorBody")
                // Eliminar de Firebase si falla backend
                firebaseUser.delete().await()
                Result.failure(Exception("Error en el servidor: ${response.code()} - $errorBody"))
            }

        } catch (e: Exception) {
            Log.e("FirebaseAuth", "❌ Excepción durante registro: ${e.message}", e)
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
            Log.d("FirebaseAuth", "🔵 Iniciando login - Email: $email")

            // 1. Autenticar en Firebase
            val authResult = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            val firebaseUser = authResult.user
            if (firebaseUser == null) {
                Log.e("FirebaseAuth", "❌ Error: Usuario es null después del login")
                return Result.failure(Exception("Error al iniciar sesión"))
            }

            Log.d("FirebaseAuth", "✅ Login exitoso - UID: ${firebaseUser.uid}")

            // 2. ⭐ VERIFICACIÓN DE EMAIL DESACTIVADA PARA DESARROLLO
            // Comentado para permitir login sin verificar email
            /*
            if (!firebaseUser.isEmailVerified) {
                Log.w("FirebaseAuth", "⚠️ Email no verificado")
                return Result.failure(Exception("Por favor verifica tu email"))
            }
            */
            // Solo mostrar advertencia en logs pero continuar
            if (!firebaseUser.isEmailVerified) {
                Log.w("FirebaseAuth", "⚠️ Email no verificado (continuando de todas formas)")
            } else {
                Log.d("FirebaseAuth", "✅ Email verificado")
            }

            // 3. Obtener token
            val token = firebaseUser.getIdToken(false).await().token
            if (token == null) {
                Log.e("FirebaseAuth", "❌ No se pudo obtener token")
                return Result.failure(Exception("No se pudo obtener token"))
            }

            Log.d("FirebaseAuth", "🔑 Token obtenido")

            // 4. Obtener perfil del backend
            val response = apiService.obtenerPerfil(
                authHeader = "Bearer $token"
            )

            Log.d("FirebaseAuth", "📡 Perfil obtenido: $response")

            if (response.success && response.data != null) {
                Log.d("FirebaseAuth", "✅ Login completo")
                Result.success(response.data)
            } else {
                Log.e("FirebaseAuth", "❌ Error al obtener perfil: ${response.message}")
                Result.failure(Exception(response.message ?: "Error al obtener perfil"))
            }

        } catch (e: Exception) {
            Log.e("FirebaseAuth", "❌ Excepción durante login: ${e.message}", e)
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
            Log.e("FirebaseAuth", "❌ Error al obtener token: ${e.message}")
            null
        }
    }

    fun cerrarSesion() {
        Log.d("FirebaseAuth", "👋 Cerrando sesión")
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
            Log.d("FirebaseAuth", "📧 Email de verificación reenviado")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "❌ Error al reenviar email: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun enviarEmailRestablecimiento(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Log.d("FirebaseAuth", "📧 Email de restablecimiento enviado")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "❌ Error al enviar email de restablecimiento: ${e.message}")
            Result.failure(e)
        }
    }
}