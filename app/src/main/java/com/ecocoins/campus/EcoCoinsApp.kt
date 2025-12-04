package com.ecocoins.campus

import android.app.Application
import android.util.Log
import com.ecocoins.campus.data.remote.FirebaseOkHttpClientProvider
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EcoCoinsApp : Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            // ⭐ Inicializa Firebase con OkHttpClient personalizado
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId(getString(R.string.google_app_id))
                    .setApiKey(getString(R.string.google_api_key))
                    .setProjectId(getString(R.string.project_id))
                    .build()

                FirebaseApp.initializeApp(this, options)
            }

            Log.d("FIREBASE_INIT", "✅ Firebase inicializado")

            // ⭐ Deshabilita verificación para testing
            if (BuildConfig.DEBUG) {
                FirebaseAuth.getInstance().firebaseAuthSettings.apply {
                    setAppVerificationDisabledForTesting(true)
                }
                Log.d("FIREBASE_INIT", "✅ AppCheck deshabilitado")
            }

        } catch (e: Exception) {
            Log.e("FIREBASE_INIT", "❌ Error: ${e.message}", e)
        }
    }
}
