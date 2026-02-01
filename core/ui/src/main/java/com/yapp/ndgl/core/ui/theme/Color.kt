package com.yapp.ndgl.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class NDGLColors(
    val white: Color = Color.Unspecified,

    val green50: Color = Color.Unspecified,
    val green100: Color = Color.Unspecified,
    val green200: Color = Color.Unspecified,
    val green300: Color = Color.Unspecified,
    val green400: Color = Color.Unspecified,
    val green500: Color = Color.Unspecified,
    val green600: Color = Color.Unspecified,
    val green700: Color = Color.Unspecified,
    val green800: Color = Color.Unspecified,
    val green900: Color = Color.Unspecified,

    val black50: Color = Color.Unspecified,
    val black100: Color = Color.Unspecified,
    val black200: Color = Color.Unspecified,
    val black300: Color = Color.Unspecified,
    val black400: Color = Color.Unspecified,
    val black500: Color = Color.Unspecified,
    val black600: Color = Color.Unspecified,
    val black700: Color = Color.Unspecified,
    val black800: Color = Color.Unspecified,
    val black900: Color = Color.Unspecified,

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

    val etcGray: Color = Color.Unspecified,
    val etcGreen: Color = Color.Unspecified,
    val etcOrange: Color = Color.Unspecified,
    val etcPurple: Color = Color.Unspecified,
    val etcBlue: Color = Color.Unspecified,
)

internal val ndglColors = NDGLColors(
    white = Color(0xFFFFFFFF),

    green50 = Color(0xFFE9F8ED),
    green100 = Color(0xFFCFF1D8),
    green200 = Color(0xFFA3E4B3),
    green300 = Color(0xFF73D08B),
    green400 = Color(0xFF3EC45B),
    green500 = Color(0xFF15C32D),
    green600 = Color(0xFF10A425),
    green700 = Color(0xFF0C7F1D),
    green800 = Color(0xFF085C15),
    green900 = Color(0xFF04340C),

    black50 = Color(0xFFF5F5F5),
    black100 = Color(0xFFE6E6E6),
    black200 = Color(0xFFD9D9D9),
    black300 = Color(0xFFB3B3B3),
    black400 = Color(0xFF757575),
    black500 = Color(0xFF444444),
    black600 = Color(0xFF383838),
    black700 = Color(0xFF2C2C2C),
    black800 = Color(0xFF1E1E1E),
    black900 = Color(0xFF111111),

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

    etcGray = Color(0xFF444444),
    etcGreen = Color(0xFF15CD3F),
    etcOrange = Color(0xFFFF6C11),
    etcPurple = Color(0xFF5726E7),
    etcBlue = Color(0xFF2960EC),
)

val LocalNDGLColors = staticCompositionLocalOf { NDGLColors() }
