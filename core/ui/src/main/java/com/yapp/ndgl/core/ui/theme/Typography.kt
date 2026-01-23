package com.yapp.ndgl.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.yapp.ndgl.core.ui.R

private val Pretendard = FontFamily(
    Font(R.font.pretendard, FontWeight.Normal),
    Font(R.font.pretendard, FontWeight.Medium),
    Font(R.font.pretendard, FontWeight.SemiBold),
    Font(R.font.pretendard, FontWeight.Bold),
)

@Immutable
data class NDGLTypography(
    val titleLgBold: TextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
    ),
    val titleLgSemiBold: TextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
    ),

    val titleMdBold: TextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
    ),
    val titleMdSemiBold: TextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
    ),

    val subtitleLgSemiBold: TextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
    ),
    val subtitleLgMedium: TextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
    ),

    val subtitleMdBold: TextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
    ),
    val subtitleMdSemiBold: TextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
    ),
    val subtitleMdMedium: TextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
    ),

    val bodyLgSemiBold: TextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
    ),
    val bodyLgMedium: TextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
    ),
    val bodyLgRegular: TextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),

    val bodyMdSemiBold: TextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
    ),
    val bodyMdMedium: TextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),
    val bodyMdRegular: TextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ),

    val bodySmSemiBold: TextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
    ),
    val bodySmMedium: TextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
    ),
    val bodySmRegular: TextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
    ),
)

internal val ndglTypography = NDGLTypography()

val LocalNDGLTypography = staticCompositionLocalOf { NDGLTypography() }
