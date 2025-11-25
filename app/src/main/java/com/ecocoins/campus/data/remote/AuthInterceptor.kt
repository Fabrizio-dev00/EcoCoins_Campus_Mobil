package com.ecocoins.campus.data.remote

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Rutas públicas (no requieren token)
        val publicPaths = listOf(
            "/api/auth/health",
            "/api/recompensas",
            "/api/estadisticas"
        )

        val isPublicPath = publicPaths.any {
            originalRequest.url.encodedPath.startsWith(it)
        }

        if (isPublicPath || originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }

        // Obtener token de Firebase
        val token = runBlocking {
            try {
                FirebaseAuth.getInstance()
                    .currentUser
                    ?.getIdToken(false)
                    ?.await()
                    ?.token
            } catch (e: Exception) {
                null
            }
        }

        if (token == null) {
            return chain.proceed(originalRequest)
        }

        // Agregar token al header
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(newRequest)
    }
}