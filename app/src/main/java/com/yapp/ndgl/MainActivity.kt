package com.yapp.ndgl

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.yapp.ndgl.core.ui.designsystem.UserGuideModal
import com.yapp.ndgl.core.ui.theme.NDGLTheme
import com.yapp.ndgl.core.ui.util.launchBrowser
import com.yapp.ndgl.feature.splash.SplashRoute
import com.yapp.ndgl.navigation.AppScreen
import com.yapp.ndgl.ui.NDGLApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        requestNotificationPermission()
        enableEdgeToEdge()

        setContent {
            NDGLTheme {
                var currentScreen by rememberSaveable { mutableStateOf(AppScreen.Splash) }
                var showUserGuideModal by rememberSaveable { mutableStateOf(false) }

                AnimatedContent(
                    targetState = currentScreen,
                ) { screen ->
                    when (screen) {
                        AppScreen.Splash -> {
                            SplashRoute(
                                navigateToHome = { isFirstUser ->
                                    showUserGuideModal = isFirstUser
                                    currentScreen = AppScreen.Main
                                },
                            )
                        }

                        AppScreen.Main -> {
                            NDGLApp()
                        }
                    }
                }

                if (showUserGuideModal) {
                    UserGuideModal(
                        onConfirmClick = {
                            showUserGuideModal = false
                        },
                        onTermsClick = {
                            launchBrowser(BuildConfig.NDGL_TERMS_URL)
                        },
                    )
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
