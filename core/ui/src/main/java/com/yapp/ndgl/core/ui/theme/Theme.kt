package com.yapp.ndgl.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

object NDGLTheme {
    val colors: NDGLColors
        @Composable
        @ReadOnlyComposable
        get() = LocalNDGLColors.current

    val typography: NDGLTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalNDGLTypography.current
}

@Composable
fun NDGLTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalNDGLColors provides ndglColors,
        LocalNDGLTypography provides ndglTypography,
    ) {
        val colorScheme = lightColorScheme(
            primary = NDGLTheme.colors.green500,
            secondary = NDGLTheme.colors.black500,
            background = NDGLTheme.colors.white,
            surface = NDGLTheme.colors.white,
        )

        val typography = Typography(
            headlineLarge = NDGLTheme.typography.titleLgBold,
            headlineMedium = NDGLTheme.typography.titleMdBold,
            titleLarge = NDGLTheme.typography.subtitleLgSemiBold,
            titleMedium = NDGLTheme.typography.subtitleMdSemiBold,
            bodyLarge = NDGLTheme.typography.bodyLgMedium,
            bodyMedium = NDGLTheme.typography.bodyMdMedium,
            bodySmall = NDGLTheme.typography.bodyMdRegular,
            labelLarge = NDGLTheme.typography.bodySmSemiBold,
            labelMedium = NDGLTheme.typography.bodySmMedium,
            labelSmall = NDGLTheme.typography.bodySmRegular,
        )

        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content,
        )
    }
}
