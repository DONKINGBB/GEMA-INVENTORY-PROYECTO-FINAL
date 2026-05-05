package com.example.gemainventory.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import com.example.gemainventory.ui.theme.GemaTheme

class OnboardingDialog : DialogFragment() {

    // Callback interface to navigate
    var onGoToSettings: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                GemaTheme {
                    OnboardingScreen(
                        onGoToConfig = {
                            markAsSeen()
                            dismiss()
                            onGoToSettings?.invoke()
                        },
                        onDismiss = {
                            markAsSeen()
                            dismiss()
                        }
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Usar un estilo que permita pantalla completa y sea inmersivo
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
    }

    private fun markAsSeen() {
        val prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("has_seen_onboarding", true).apply()
    }

    companion object {
        const val TAG = "OnboardingDialog"
    }
}
