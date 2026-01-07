package com.lapmaster.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lapmaster.ui.model.EntradaHistorialUi
import com.lapmaster.ui.model.EstadoGraficasUi
import com.lapmaster.ui.model.SerieGraficaUi
import com.lapmaster.ui.theme.FondoGrafica
import com.lapmaster.ui.theme.RejillaGrafica
import java.util.Locale
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun PantallaGraficas(
    estado: EstadoGraficasUi,
    alSeleccionarTanda: (Int) -> Unit
) {
    val tandas = estado.tandas
    val indiceSeleccionado = tandas.indexOfFirst { it.id == estado.tandaSeleccionadaId }.let { indice ->
        if (indice >= 0) indice else 0
    }
    val tandaSeleccionada = tandas.getOrNull(indiceSeleccionado)
    val series = tandaSeleccionada?.series.orEmpty()
    val sinDatos = series.isEmpty() || series.all { it.valores.isEmpty() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Graficos de vueltas",
            style = MaterialTheme.typography.displayLarge
        )
        if (tandas.isEmpty()) {
            Text(
                text = "Sin tandas registradas",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            androidx.compose.material3.TabRow(
                selectedTabIndex = indiceSeleccionado,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tandas.forEach { tanda ->
                    val etiqueta = if (tanda.id == estado.tandaActivaId) {
                        "${tanda.nombre} (activa)"
                    } else {
                        tanda.nombre
                    }
                    androidx.compose.material3.Tab(
                        selected = tanda.id == tandaSeleccionada?.id,
                        onClick = { alSeleccionarTanda(tanda.id) },
                        text = { Text(etiqueta) }
                    )
                }
            }
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = FondoGrafica),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (sinDatos) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sin vueltas registradas en esta tanda",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    GraficaLineas(
                        series = series,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Leyenda(series = series)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        ListaHistorial(historial = estado.historial)
    }
}

@Composable
private fun Leyenda(series: List<SerieGraficaUi>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        series.forEach { elemento ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(elemento.color), RoundedCornerShape(50))
                )
                Text(
                    text = elemento.nombre,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun ListaHistorial(historial: List<EntradaHistorialUi>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Historial de tandas", style = MaterialTheme.typography.titleMedium)
            if (historial.isEmpty()) {
                Text(
                    text = "Aun no hay tandas finalizadas",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            } else {
                historial.forEach { elemento ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = elemento.etiquetaDia, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${elemento.mejorVuelta} • ${elemento.vueltas} vueltas",
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GraficaLineas(
    series: List<SerieGraficaUi>,
    modifier: Modifier = Modifier
) {
    val todosLosValores = series.flatMap { it.valores }
    val maxValor = todosLosValores.maxOrNull() ?: 1f
    val minValor = todosLosValores.minOrNull() ?: 0f
    val padding = if (maxValor == minValor) 1f else (maxValor - minValor) * 0.15f
    val maxY = (maxValor + padding).coerceAtLeast(1f)
    val minY = (minValor - padding).coerceAtLeast(0f)
    val rango = (maxY - minY).coerceAtLeast(1f)
    val pasosRejilla = 4
    val valoresRejilla = (0..pasosRejilla).map { indice ->
        maxY - (rango / pasosRejilla) * indice
    }

    Row(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            valoresRejilla.forEach { valor ->
                Text(
                    text = formatearTiempoGrafica(valor),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.75f),
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(FondoGrafica, RoundedCornerShape(12.dp))
                .border(1.dp, RejillaGrafica, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val anchoGrafica = size.width
                val altoGrafica = size.height

                val separacionRejilla = altoGrafica / pasosRejilla
                for (i in 0..pasosRejilla) {
                    val y = altoGrafica - (separacionRejilla * i)
                    drawLine(
                        color = RejillaGrafica,
                        start = androidx.compose.ui.geometry.Offset(0f, y),
                        end = androidx.compose.ui.geometry.Offset(anchoGrafica, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                val serieMasLarga = series.maxOfOrNull { it.valores.size } ?: 0
                val lineasX = if (serieMasLarga > 1) min(serieMasLarga - 1, 6) else 0
                if (lineasX > 0) {
                    for (i in 0..lineasX) {
                        val x = anchoGrafica * (i / lineasX.toFloat())
                        drawLine(
                            color = RejillaGrafica.copy(alpha = 0.6f),
                            start = androidx.compose.ui.geometry.Offset(x, 0f),
                            end = androidx.compose.ui.geometry.Offset(x, altoGrafica),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }

                val pasoX = if (serieMasLarga > 1) anchoGrafica / (serieMasLarga - 1) else anchoGrafica
                series.forEach { elemento ->
                    val puntos = elemento.valores.mapIndexed { indice, valor ->
                        val x = pasoX * indice
                        val normalizado = (valor - minY) / rango
                        val y = altoGrafica - (normalizado * altoGrafica)
                        androidx.compose.ui.geometry.Offset(x, y)
                    }

                    for (i in 0 until puntos.size - 1) {
                        drawLine(
                            color = Color(elemento.color),
                            start = puntos[i],
                            end = puntos[i + 1],
                            strokeWidth = 3.dp.toPx()
                        )
                    }

                    puntos.forEach { punto ->
                        drawCircle(
                            color = Color(elemento.color),
                            radius = 4.dp.toPx(),
                            center = punto,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}

private fun formatearTiempoGrafica(segundos: Float): String {
    if (!segundos.isFinite() || segundos < 0f) return "--"
    val totalMs = (segundos * 1000f).roundToInt().coerceAtLeast(0)
    val minutos = totalMs / 60000
    val segundosEnteros = (totalMs / 1000) % 60
    val decimas = (totalMs % 1000) / 100
    return if (minutos > 0) {
        String.format(Locale.US, "%d:%02d.%d", minutos, segundosEnteros, decimas)
    } else {
        String.format(Locale.US, "%d.%d s", segundosEnteros, decimas)
    }
}
