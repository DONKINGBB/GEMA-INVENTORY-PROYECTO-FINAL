package com.example.gemainventory.api

import android.content.Context
import android.content.Intent
import com.example.gemainventory.GemaApplication
import com.example.gemainventory.LoginActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Si es emulador: "http://10.0.2.2:8080/"
    // Si es físico: "http://192.168.0.X:8080/"
    const val BASE_URL = "https://gema-inventory-backend.onrender.com"

    private val authInterceptor = Interceptor { chain ->
        val reqBuilder = chain.request().newBuilder()
        try {
            val prefs = GemaApplication.instance.getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
            val token = prefs.getString("jwt_token", null)
            
            if (token != null && token.isNotEmpty()) {
                reqBuilder.addHeader("Authorization", "Bearer $token")
            }
        } catch (e: Exception) { }
        chain.proceed(reqBuilder.build())
    }

    private val errorInterceptor = Interceptor { chain ->
        val request = chain.request()
        val path = request.url.encodedPath
        val response = chain.proceed(request)
        
        if (!response.isSuccessful) {
            android.util.Log.e("API_ERROR", "Error en " + path + " | Código: " + response.code)
        }
        if (response.code == 401) {
            if (!path.contains("fcm-token") && !path.contains("auth/login") && !path.contains("auth/google")) {
                val prefs = GemaApplication.instance.getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
                val editor = prefs.edit()
                editor.remove("user_id")
                editor.remove("user_nombre")
                editor.remove("user_correo")
                editor.remove("jwt_token")
                editor.remove("id_negocio")
                editor.putBoolean("is_logged_in", false)
                editor.apply()
                
                val intent = Intent(GemaApplication.instance, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                GemaApplication.instance.startActivity(intent)
            }
        }
        response
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(errorInterceptor)
        .build()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }

    @JvmStatic
    fun getFullImageUrl(imagePath: String?): String? {
        if (imagePath.isNullOrBlank()) return null
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath
        }
        
        val cleanPath = if (imagePath.startsWith("/")) imagePath.substring(1) else imagePath
        return BASE_URL + cleanPath
    }
}