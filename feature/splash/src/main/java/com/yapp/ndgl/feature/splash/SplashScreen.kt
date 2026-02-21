package com.yapp.ndgl.feature.splash

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yapp.ndgl.core.ui.R
import com.yapp.ndgl.core.ui.theme.NDGLTheme

@Composable
fun SplashRoute(
    viewmodel: SplashViewModel = hiltViewModel(),
    navigateToHome: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    DisposableEffect(Unit) {
        activity?.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
        )

        onDispose {
            activity?.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT,
                ),
                navigationBarStyle = SystemBarStyle.auto(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT,
                ),
            )
        }
    }

    viewmodel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SplashSideEffect.NavigateToHome -> {
                navigateToHome(sideEffect.isFirstUser)
            }
        }
    }

    SplashScreen()
}

@Composable
private fun SplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NDGLTheme.colors.green300),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_splash), contentDescription = null, tint = Color.Unspecified)
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    SplashScreen()
}
