package com.example.gemainventory.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.gemainventory.R
import com.example.gemainventory.databinding.DialogOnboardingBinding

class OnboardingDialog : DialogFragment() {

    private var _binding: DialogOnboardingBinding? = null
    private val binding get() = _binding!!

    // Callback interface to navigate
    var onGoToSettings: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen) // Fullscreen immersive
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGoConfig.setOnClickListener {
            // Mark onboarding as seen
            markAsSeen()
            dismiss()
            onGoToSettings?.invoke()
        }

        binding.btnDismiss.setOnClickListener {
            markAsSeen()
            dismiss()
        }
    }

    private fun markAsSeen() {
        val prefs = requireActivity().getSharedPreferences("GemaPrefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("has_seen_onboarding", true).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "OnboardingDialog"
    }
}
