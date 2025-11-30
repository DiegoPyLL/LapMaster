package com.lapmaster.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val EsquemaColoresClaro = lightColorScheme(
    primary = VerdeCarreras,
    onPrimary = Color.Black,
    primaryContainer = VerdeCarrerasOscuro,
    onPrimaryContainer = Color.Black,
    secondary = AmarilloCarreras,
    onSecondary = Color.Black,
    background = SuperficieCarreras,
    onBackground = TextoCarreras,
    surface = Color.White,
    onSurface = TextoCarreras,
    tertiary = CianCarreras,
    onTertiary = Color.Black,
)

private val EsquemaColoresOscuro = darkColorScheme(
    primary = VerdeCarreras,
    onPrimary = Color.Black,
    primaryContainer = VerdeCarrerasOscuro,
    onPrimaryContainer = Color.Black,
    secondary = AmarilloCarreras,
    onSecondary = Color.Black,
    background = Color(0xFF0D0D0D),
    onBackground = Color(0xFFF5F5F5),
    surface = Color(0xFF111111),
    onSurface = Color(0xFFF5F5F5),
    tertiary = CianCarreras,
    onTertiary = Color.Black,
)

private val TipografiaCarreras = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun TemaLapMaster(
    usarTemaOscuro: Boolean,
    contenido: @Composable () -> Unit
) {
    val colores = if (usarTemaOscuro) EsquemaColoresOscuro else EsquemaColoresClaro

    MaterialTheme(
        colorScheme = colores,
        typography = TipografiaCarreras,
        shapes = Shapes(),
        content = contenido
    )
}
