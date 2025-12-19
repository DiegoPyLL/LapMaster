package com.lapmaster.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lapmaster.ui.theme.GrisCarreras

@Composable
fun Cronometro(
    tiempoMs: Long,
    modifier: Modifier = Modifier,
    fondo: Color = GrisCarreras,
    colorTexto: Color = MaterialTheme.colorScheme.onBackground
) {
    Card(
        modifier = modifier
            .height(72.dp)
            .width(130.dp),
        colors = CardDefaults.cardColors(containerColor = fondo),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = formatearTiempo(tiempoMs),
                fontSize = 30.sp,
                fontFamily = FontFamily.Monospace,
                color = colorTexto
            )
        }
    }
}

private fun formatearTiempo(ms: Long): String {
    if (ms <= 0L) return "--:--.---"
    val totalSegundos = ms / 1000
    val minutos = totalSegundos / 60
    val segundos = totalSegundos % 60
    val milisegundos = (ms % 1000)
    return String.format("%d:%02d.%03d", minutos, segundos, milisegundos)
}

