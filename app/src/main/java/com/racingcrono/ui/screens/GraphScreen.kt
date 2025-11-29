package com.racingcrono.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.racingcrono.ui.model.ChartSeriesUi
import com.racingcrono.ui.model.GraphUiState
import com.racingcrono.ui.model.HistoryEntryUi
import com.racingcrono.ui.theme.GraphBackground
import com.racingcrono.ui.theme.GraphGrid

@Composable
fun GraphScreen(
    state: GraphUiState,
    onYearSelected: (String) -> Unit
) {
    val series = state.series
    val years = state.years
    val selectedYear = state.selectedYear

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Gráficos",
            style = MaterialTheme.typography.displayLarge
        )
        androidx.compose.material3.TabRow(
            selectedTabIndex = years.indexOf(selectedYear).coerceAtLeast(0),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            years.forEachIndexed { index, year ->
                androidx.compose.material3.Tab(
                    selected = year == selectedYear,
                    onClick = { onYearSelected(year) },
                    text = { Text(year) }
                )
            }
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = GraphBackground),
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
                LineChart(
                    series = series,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Legend(series = series)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        HistoryList(history = state.history)
    }
}

@Composable
private fun Legend(series: List<ChartSeriesUi>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        series.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(item.color), RoundedCornerShape(50))
                )
                Text(
                    text = item.name,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun HistoryList(history: List<HistoryEntryUi>) {
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
            history.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = item.dayLabel, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${item.bestLap} • ${item.laps} vueltas",
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun LineChart(
    series: List<ChartSeriesUi>,
    modifier: Modifier = Modifier
) {
    val allValues = series.flatMap { it.values }
    val maxY = (allValues.maxOrNull() ?: 1f) + 2f
    val minY = (allValues.minOrNull() ?: 0f) - 2f
    val range = (maxY - minY).coerceAtLeast(1f)
    val gridSteps = 4

    Box(
        modifier = modifier
            .background(GraphBackground, RoundedCornerShape(12.dp))
            .border(1.dp, GraphGrid, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val chartWidth = size.width
            val chartHeight = size.height

            val gridSpacing = chartHeight / gridSteps
            for (i in 0..gridSteps) {
                val y = chartHeight - (gridSpacing * i)
                drawLine(
                    color = GraphGrid,
                    start = androidx.compose.ui.geometry.Offset(0f, y),
                    end = androidx.compose.ui.geometry.Offset(chartWidth, y),
                    strokeWidth = 1.dp.toPx()
                )
            }
            drawLine(
                color = Color.LightGray.copy(alpha = 0.6f),
                start = androidx.compose.ui.geometry.Offset(0f, chartHeight),
                end = androidx.compose.ui.geometry.Offset(chartWidth, chartHeight),
                strokeWidth = 1.5.dp.toPx()
            )

            val longestSeries = series.maxOfOrNull { it.values.size } ?: 0
            val xStep = if (longestSeries > 1) chartWidth / (longestSeries - 1) else chartWidth

            series.forEach { item ->
                val points = item.values.mapIndexed { index, value ->
                    val x = xStep * index
                    val normalized = (value - minY) / range
                    val y = chartHeight - (normalized * chartHeight)
                    androidx.compose.ui.geometry.Offset(x, y)
                }

                for (i in 0 until points.size - 1) {
                    drawLine(
                        color = Color(item.color),
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 3.dp.toPx()
                    )
                }

                points.forEach { point ->
                    drawCircle(
                        color = Color(item.color),
                        radius = 4.dp.toPx(),
                        center = point,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }
    }
}
