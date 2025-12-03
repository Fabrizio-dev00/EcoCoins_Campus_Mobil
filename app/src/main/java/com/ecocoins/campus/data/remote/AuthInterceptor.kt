package com.ecocoins.campus.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class AuthInterceptor : Interceptor {

    private val TAG = "AuthInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Obtener el usuario actual de Firebase
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            Log.w(TAG, "⚠️ No hay usuario autenticado - Enviando petición sin token")
            return chain.proceed(originalRequest)
        }

        // Obtener el token de forma bloqueante
        var token: String? = null
        val latch = CountDownLatch(1)

        currentUser.getIdToken(false).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                token = task.result?.token
                Log.d(TAG, "✅ Token obtenido exitosamente")
            } else {
                Log.e(TAG, "❌ Error al obtener token: ${task.exception?.message}")
            }
            latch.countDown()
        }

        // Esperar máximo 5 segundos por el token
        try {
            latch.await(5, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            Log.e(TAG, "❌ Timeout esperando token")
        }

        // Construir la nueva petición con el token si existe
        val newRequest = if (token != null) {
            Log.d(TAG, "🔐 Agregando token a la petición: ${originalRequest.url}")
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            Log.w(TAG, "⚠️ Token no disponible - Enviando petición sin autenticación")
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}
