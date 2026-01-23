package com.yapp.ndgl.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.yapp.ndgl.core.ui.R

private val ColorScheme
    @Composable get() = lightColorScheme(
        primary = colorResource(R.color.primary_500),
        secondary = colorResource(R.color.secondary_500),
        background = colorResource(R.color.white),
        surface = colorResource(R.color.white)
    )


@Composable
fun NDGLTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColorScheme,
        content = content
    )
}
