package com.yapp.ndgl.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class NDGLColors(
    val white: Color = Color.Unspecified,

    val primary50: Color = Color.Unspecified,
    val primary100: Color = Color.Unspecified,
    val primary200: Color = Color.Unspecified,
    val primary300: Color = Color.Unspecified,
    val primary400: Color = Color.Unspecified,
    val primary500: Color = Color.Unspecified,
    val primary600: Color = Color.Unspecified,
    val primary700: Color = Color.Unspecified,
    val primary800: Color = Color.Unspecified,
    val primary900: Color = Color.Unspecified,

    val secondary50: Color = Color.Unspecified,
    val secondary100: Color = Color.Unspecified,
    val secondary200: Color = Color.Unspecified,
    val secondary300: Color = Color.Unspecified,
    val secondary400: Color = Color.Unspecified,
    val secondary500: Color = Color.Unspecified,
    val secondary600: Color = Color.Unspecified,
    val secondary700: Color = Color.Unspecified,
    val secondary800: Color = Color.Unspecified,
    val secondary900: Color = Color.Unspecified,

    val red50: Color = Color.Unspecified,
    val red100: Color = Color.Unspecified,
    val red200: Color = Color.Unspecified,
    val red300: Color = Color.Unspecified,
    val red400: Color = Color.Unspecified,
    val red500: Color = Color.Unspecified,
    val red600: Color = Color.Unspecified,
    val red700: Color = Color.Unspecified,
    val red800: Color = Color.Unspecified,
    val red900: Color = Color.Unspecified,
)

internal val ndglColors = NDGLColors(
    white = Color(0xFFFFFFFF),

    primary50 = Color(0xFFF0FFF4),
    primary100 = Color(0xFFDCFFE4),
    primary200 = Color(0xFFBEF5CB),
    primary300 = Color(0xFF85E89D),
    primary400 = Color(0xFF18B368),
    primary500 = Color(0xFF28A745),
    primary600 = Color(0xFF22863A),
    primary700 = Color(0xFF176F2C),
    primary800 = Color(0xFF165C26),
    primary900 = Color(0xFF144620),

    secondary50 = Color(0xFFF5F5F5),
    secondary100 = Color(0xFFE6E6E6),
    secondary200 = Color(0xFFD9D9D9),
    secondary300 = Color(0xFFB3B3B3),
    secondary400 = Color(0xFF757575),
    secondary500 = Color(0xFF444444),
    secondary600 = Color(0xFF383838),
    secondary700 = Color(0xFF2C2C2C),
    secondary800 = Color(0xFF1E1E1E),
    secondary900 = Color(0xFF111111),

    red50 = Color(0xFFFEF2F2),
    red100 = Color(0xFFFFE2E2),
    red200 = Color(0xFFFFC9C9),
    red300 = Color(0xFFFFA2A2),
    red400 = Color(0xFFFF6467),
    red500 = Color(0xFFFB2C36),
    red600 = Color(0xFFE7000B),
    red700 = Color(0xFFC10007),
    red800 = Color(0xFF9F0712),
    red900 = Color(0xFF82181A),
)

val LocalNDGLColors = staticCompositionLocalOf { NDGLColors() }
