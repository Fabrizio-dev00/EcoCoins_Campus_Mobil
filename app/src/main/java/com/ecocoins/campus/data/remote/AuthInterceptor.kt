package com.ecocoins.campus.data.remote

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Rutas que NO necesitan token
        val publicPaths = listOf(
            "/api/auth/",
            "/api/recompensas",
            "/api/estadisticas",
            "/api/reciclajes/validar-ia"
        )

        val isPublicPath = publicPaths.any { originalRequest.url.encodedPath.contains(it) }

        // Si es ruta pública, continuar sin token
        if (isPublicPath) {
            return chain.proceed(originalRequest)
        }

        // Obtener token de Firebase
        val token = runBlocking {
            try {
                FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()?.token
            } catch (e: Exception) {
                null
            }
        }

        // Si no hay token, continuar sin él (el backend podría responder 401)
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        // Agregar token al header
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
