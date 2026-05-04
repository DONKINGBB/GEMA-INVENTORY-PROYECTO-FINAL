package com.example.gemainventory.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy

fun interface OnRegisterListener {
    fun onRegister(nombre: String, correo: String, contrasena: String, direccion: String, telefono: String)
}

fun interface OnBackClickListener {
    fun onBack()
}

class RegisterComposeHelper(private val composeView: ComposeView) {
    
    private val isLoading = mutableStateOf(false)
    private var onRegisterListener: OnRegisterListener? = null
    private var onBackToLogin: OnBackClickListener? = null

    fun setOnRegisterListener(listener: OnRegisterListener) {
        this.onRegisterListener = listener
    }

    fun setOnBackToLogin(listener: OnBackClickListener) {
        this.onBackToLogin = listener
    }

    fun setLoading(loading: Boolean) {
        isLoading.value = loading
    }

    init {
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                RegisterScreen(
                    onRegisterClick = { n, c, p, d, t -> 
                        onRegisterListener?.onRegister(n, c, p, d, t) 
                    },
                    onBackToLogin = { onBackToLogin?.onBack() },
                    isLoading = isLoading.value
                )
            }
        }
    }
}
