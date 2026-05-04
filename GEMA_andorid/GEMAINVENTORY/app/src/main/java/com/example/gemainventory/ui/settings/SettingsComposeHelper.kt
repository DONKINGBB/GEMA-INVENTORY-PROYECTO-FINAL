package com.example.gemainventory.ui.settings

import androidx.compose.ui.platform.ComposeView

object SettingsComposeHelper {
    @JvmStatic
    fun setCreateBusinessContent(
        composeView: ComposeView,
        darkTheme: Boolean,
        onConfirm: (String) -> Unit,
        onCancel: () -> Unit
    ) {
        composeView.setContent {
            CreateBusinessView(
                darkTheme = darkTheme,
                onConfirm = onConfirm,
                onCancel = onCancel
            )
        }
    }

    @JvmStatic
    fun setJoinBusinessContent(
        composeView: ComposeView,
        darkTheme: Boolean,
        onConfirm: (String) -> Unit,
        onScanQr: () -> Unit,
        onCancel: () -> Unit
    ) {
        composeView.setContent {
            JoinBusinessView(
                darkTheme = darkTheme,
                onConfirm = onConfirm,
                onScanQr = onScanQr,
                onCancel = onCancel
            )
        }
    }

    @JvmStatic
    fun setSwitchBusinessContent(
        composeView: ComposeView,
        darkTheme: Boolean,
        activeBusinessId: String,
        businesses: List<Map<String, String>>,
        onBusinessSelected: (String) -> Unit,
        onBack: () -> Unit
    ) {
        composeView.setContent {
            SwitchBusinessScreen(
                darkTheme = darkTheme,
                activeBusinessId = activeBusinessId,
                businesses = businesses,
                onBusinessSelected = onBusinessSelected,
                onBack = onBack
            )
        }
    }

    @JvmStatic
    fun setSettingsContent(
        composeView: ComposeView,
        darkTheme: Boolean,
        userName: String,
        userEmail: String,
        userPhotoUrl: String?,
        isBiometricEnabled: Boolean,
        showManageUsers: Boolean,
        onProfileClick: () -> Unit,
        onDarkModeToggle: (Boolean) -> Unit,
        onBiometricToggle: (Boolean) -> Unit,
        onCatalogClick: () -> Unit,
        onBusinessClick: () -> Unit,
        onJoinCreateClick: () -> Unit,
        onSwitchBusinessClick: () -> Unit,
        onManageUsersClick: () -> Unit,
        onNotificationsClick: () -> Unit,
        onManualClick: () -> Unit,
        onLogoutClick: () -> Unit
    ) {
        composeView.setContent {
            SettingsScreen(
                darkTheme = darkTheme,
                userName = userName,
                userEmail = userEmail,
                userPhotoUrl = userPhotoUrl,
                isBiometricEnabled = isBiometricEnabled,
                showManageUsers = showManageUsers,
                onProfileClick = onProfileClick,
                onDarkModeToggle = onDarkModeToggle,
                onBiometricToggle = onBiometricToggle,
                onCatalogClick = onCatalogClick,
                onBusinessClick = onBusinessClick,
                onJoinCreateClick = onJoinCreateClick,
                onSwitchBusinessClick = onSwitchBusinessClick,
                onManageUsersClick = onManageUsersClick,
                onNotificationsClick = onNotificationsClick,
                onManualClick = onManualClick,
                onLogoutClick = onLogoutClick
            )
        }
    }
}
