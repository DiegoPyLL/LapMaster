package com.lapmaster.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lapmaster.ui.components.WeatherBanner
import com.lapmaster.ui.model.GpsUi
import com.lapmaster.ui.model.HandPreference
import com.lapmaster.ui.model.SectorUi
import com.lapmaster.ui.model.SectorsUiState
import com.lapmaster.ui.model.SettingsUiState
import com.lapmaster.ui.model.WeatherUi
import com.lapmaster.ui.theme.RacingRed

@Composable
fun SectorsScreen(
    state: SectorsUiState,
    settings: SettingsUiState,
    weather: WeatherUi,
    gps: GpsUi
) {
    val sectors = state.sectors
    val leftHanded = settings.handPreference == HandPreference.LEFT_HANDED

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WeatherBanner(weather = weather, gps = gps)
        Text(
            text = state.pilot?.let { "${it.name} #${it.number}" } ?: "Selecciona piloto",
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(18.dp))
        if (sectors.size >= 3 && state.pilot != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val order = if (leftHanded) listOf(2, 1, 0) else listOf(0, 1, 2)
                order.forEachIndexed { idx, index ->
                    val sector = sectors[index]
                    when (idx) {
                        0, 2 -> TallSector(label = sector.label, color = Color(sector.color))
                        else -> MiddleSector(label = sector.label, color = Color(sector.color))
                    }
                    if (idx < order.lastIndex) Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        SectorBar(sectors = sectors)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            sectors.forEach { sector ->
                Text(
                    text = String.format("%.2f", sector.time),
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = RacingRed,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .width(160.dp)
                .height(48.dp)
        ) {
            Text(text = "Finalizar", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TallSector(label: String, color: Color) {
    Box(
        modifier = Modifier
            .width(90.dp)
            .height(190.dp)
            .background(color, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MiddleSector(label: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(96.dp)
                .background(color, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = label, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SectorBar(sectors: List<SectorUi>) {
    val total = sectors.sumOf { it.time.toDouble() }.toFloat().coerceAtLeast(1f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(22.dp)
            .background(Color.Black.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        sectors.forEach { sector ->
            val weight = sector.time / total
            Box(
                modifier = Modifier
                    .weight(weight)
                    .fillMaxHeight()
                    .background(Color(sector.color))
            )
        }
    }
}
