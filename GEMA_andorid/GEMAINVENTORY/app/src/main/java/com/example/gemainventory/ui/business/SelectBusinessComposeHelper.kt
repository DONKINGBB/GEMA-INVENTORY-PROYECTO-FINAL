package com.example.gemainventory.ui.business

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import com.example.gemainventory.ui.theme.GemaTheme

class SelectBusinessComposeHelper(private val composeView: ComposeView) {
    
    private var onCrearNegocio: java.util.function.Consumer<String> = java.util.function.Consumer { }
    private var onUnirNegocio: java.util.function.Consumer<String> = java.util.function.Consumer { }
    private var onScanQr: Runnable = Runnable { }
    
    var isLoading by mutableStateOf(false)
    var externalCode by mutableStateOf("")

    fun setListeners(
        onCrear: java.util.function.Consumer<String>,
        onUnir: java.util.function.Consumer<String>,
        onScan: Runnable
    ) {
        this.onCrearNegocio = onCrear
        this.onUnirNegocio = onUnir
        this.onScanQr = onScan
        update()
    }

    private fun update() {
        composeView.setContent {
            GemaTheme {
                SelectBusinessScreen(
                    onCrearNegocio = { onCrearNegocio.accept(it) },
                    onUnirNegocio = { onUnirNegocio.accept(it) },
                    onScanQr = { onScanQr.run() },
                    isLoading = isLoading,
                    externalCode = externalCode
                )
            }
        }
    }
    
    fun setLoadingState(loading: Boolean) {
        this.isLoading = loading
        update()
    }
    
    fun setQrCode(code: String) {
        this.externalCode = code
        update()
    }
}
