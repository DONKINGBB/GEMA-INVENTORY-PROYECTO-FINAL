package com.example.gemainventory.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy

fun interface OnResetSubmitListener {
    fun onSubmit(code: String, newPassword: String)
}

class ResetPasswordComposeHelper(private val composeView: ComposeView, private val email: String) {
    
    private val isLoading = mutableStateOf(false)
    private var onSubmitListener: OnResetSubmitListener? = null
    private var onBackListener: OnActionClickListener? = null

    fun setOnSubmitListener(listener: OnResetSubmitListener) {
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
                ResetPasswordScreen(
                    email = email,
                    onResetClick = { c, p -> onSubmitListener?.onSubmit(c, p) },
                    onBackClick = { onBackListener?.onClick() },
                    isLoading = isLoading.value
                )
            }
        }
    }
}
