package com.lapmaster.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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

@Composable
fun PantallaGraficas(
    estado: EstadoGraficasUi,
    alSeleccionarAnio: (String) -> Unit
) {
    val series = estado.series
    val anios = estado.tanda
    val anioSeleccionado = estado.tandaSeleccionada

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = " Placeholder Gráficos",
            style = MaterialTheme.typography.displayLarge
        )
        androidx.compose.material3.TabRow(
            selectedTabIndex = anios.indexOf(anioSeleccionado).coerceAtLeast(0),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            anios.forEach { anio ->
                androidx.compose.material3.Tab(
                    selected = anio == anioSeleccionado,
                    onClick = { alSeleccionarAnio(anio) },
                    text = { Text(anio) }
                )
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
            Text(text = "Historial (agrupado por día)", style = MaterialTheme.typography.titleMedium)
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

@Composable
private fun GraficaLineas(
    series: List<SerieGraficaUi>,
    modifier: Modifier = Modifier
) {
    val todosLosValores = series.flatMap { it.valores }
    val maxY = (todosLosValores.maxOrNull() ?: 1f) + 2f
    val minY = (todosLosValores.minOrNull() ?: 0f) - 2f
    val rango = (maxY - minY).coerceAtLeast(1f)
    val pasosRejilla = 4

    Box(
        modifier = modifier
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
            drawLine(
                color = Color.LightGray.copy(alpha = 0.6f),
                start = androidx.compose.ui.geometry.Offset(0f, altoGrafica),
                end = androidx.compose.ui.geometry.Offset(anchoGrafica, altoGrafica),
                strokeWidth = 1.5.dp.toPx()
            )

            val serieMasLarga = series.maxOfOrNull { it.valores.size } ?: 0
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
