package com.example.gemainventory.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy

fun interface OnLoginListener {
    fun onLogin(correo: String, contrasena: String)
}

fun interface OnActionClickListener {
    fun onClick()
}

class LoginComposeHelper(private val composeView: ComposeView) {
    
    private val isLoading = mutableStateOf(false)
    private var onLoginListener: OnLoginListener? = null
    private var onGoogleLoginListener: OnActionClickListener? = null
    private var onForgotPasswordListener: OnActionClickListener? = null
    private var onRegisterListener: OnActionClickListener? = null

    fun setOnLoginListener(listener: OnLoginListener) {
        this.onLoginListener = listener
    }

    fun setOnGoogleLoginListener(listener: OnActionClickListener) {
        this.onGoogleLoginListener = listener
    }

    fun setOnForgotPasswordListener(listener: OnActionClickListener) {
        this.onForgotPasswordListener = listener
    }

    fun setOnRegisterListener(listener: OnActionClickListener) {
        this.onRegisterListener = listener
    }

    fun setLoading(loading: Boolean) {
        isLoading.value = loading
    }

    init {
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LoginScreen(
                    onLoginClick = { c, p -> 
                        onLoginListener?.onLogin(c, p) 
                    },
                    onGoogleLoginClick = { onGoogleLoginListener?.onClick() },
                    onForgotPasswordClick = { onForgotPasswordListener?.onClick() },
                    onRegisterClick = { onRegisterListener?.onClick() },
                    isLoading = isLoading.value
                )
            }
        }
    }
}
