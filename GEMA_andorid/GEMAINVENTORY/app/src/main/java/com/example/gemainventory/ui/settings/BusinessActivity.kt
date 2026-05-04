package com.example.gemainventory.ui.settings

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import com.example.gemainventory.api.RetrofitClient
import com.example.gemainventory.model.NegocioDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BusinessActivity : AppCompatActivity() {

    private var currentBusiness by mutableStateOf<NegocioDto?>(null)
    private var isLoading by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val prefs = getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        val userRole = prefs.getInt("user_rol", 1)
        val isDarkMode = prefs.getBoolean("DarkMode", false)

        setContent {
            BusinessScreen(
                darkTheme = isDarkMode,
                currentBusiness = currentBusiness,
                userRole = userRole,
                isLoading = isLoading,
                onBackClick = { onBackPressed() },
                onUpdateName = { updateBusinessName(it) }
            )
        }

        fetchMyBusiness()
    }

    private fun fetchMyBusiness() {
        isLoading = true
        RetrofitClient.instance.getMiNegocio().enqueue(object : Callback<NegocioDto> {
            override fun onResponse(call: Call<NegocioDto>, response: Response<NegocioDto>) {
                isLoading = false
                if (response.isSuccessful && response.body() != null) {
                    currentBusiness = response.body()
                } else {
                    Toast.makeText(this@BusinessActivity, "Error al cargar negocio: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NegocioDto>, t: Throwable) {
                isLoading = false
                Toast.makeText(this@BusinessActivity, "Fallo de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun updateBusinessName(nuevoNombre: String) {
        val currentId = currentBusiness?.idNegocio ?: return
        
        val body = HashMap<String, String>()
        body["nombre"] = nuevoNombre

        RetrofitClient.instance.updateNegocio(currentId, body).enqueue(object : Callback<NegocioDto> {
            override fun onResponse(call: Call<NegocioDto>, response: Response<NegocioDto>) {
                if (response.isSuccessful && response.body() != null) {
                    // Update state to trigger recomposition
                    currentBusiness = response.body()
                    Toast.makeText(this@BusinessActivity, "Actualizado exitosamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@BusinessActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NegocioDto>, t: Throwable) {
                Toast.makeText(this@BusinessActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
