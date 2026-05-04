package com.example.gemainventory.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy

fun interface OnEmailSubmitListener {
    fun onSubmit(email: String)
}

class ForgotPasswordComposeHelper(private val composeView: ComposeView) {
    
    private val isLoading = mutableStateOf(false)
    private var onSubmitListener: OnEmailSubmitListener? = null
    private var onBackListener: OnActionClickListener? = null

    fun setOnSubmitListener(listener: OnEmailSubmitListener) {
        this.onSubmitListener = listener
    }

    fun setOnBackListener(listener: OnActionClickListener) {
        this.onBackListener = listener
    }

    fun setLoading(loading: Boolean) {
        isLoading.value = loading
    }

    init {
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ForgotPasswordScreen(
                    onSendCodeClick = { email -> onSubmitListener?.onSubmit(email) },
                    onBackClick = { onBackListener?.onClick() },
                    isLoading = isLoading.value
                )
            }
        }
    }
}
