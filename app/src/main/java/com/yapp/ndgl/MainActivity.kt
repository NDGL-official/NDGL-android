package com.yapp.ndgl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
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
}
