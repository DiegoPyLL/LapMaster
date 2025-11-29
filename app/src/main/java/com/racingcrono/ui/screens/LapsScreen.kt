package com.racingcrono.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.sp
import com.racingcrono.ui.components.SummaryCard
import com.racingcrono.ui.components.WeatherBanner
import com.racingcrono.ui.model.GpsUi
import com.racingcrono.ui.model.HandPreference
import com.racingcrono.ui.model.LapsUiState
import com.racingcrono.ui.model.PilotLapUi
import com.racingcrono.ui.model.SettingsUiState
import com.racingcrono.ui.model.SummaryUi
import com.racingcrono.ui.model.WeatherUi
import com.racingcrono.ui.theme.RacingGray
import com.racingcrono.ui.theme.RacingGreen
import com.racingcrono.ui.theme.RacingRed

@Composable
fun LapsScreen(
    state: LapsUiState,
    weather: WeatherUi,
    gps: GpsUi,
    summary: SummaryUi,
    settings: SettingsUiState,
    onAddPilot: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(summary = summary)
        Spacer(modifier = Modifier.height(6.dp))
        state.pilots.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { pilot ->
                    PilotLapCard(
                        pilot = pilot,
                        leftHanded = settings.handPreference == HandPreference.LEFT_HANDED,
                        modifier = Modifier.weight(1f)
                    )
                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        AddPilotButton(onAddPilot = onAddPilot, enabled = state.pilots.size < 4)
        Spacer(modifier = Modifier.height(8.dp))
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
private fun PilotLapCard(
    pilot: PilotLapUi,
    leftHanded: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "${pilot.pilot.name} #${pilot.pilot.number}",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val button = @Composable {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(pilot.pilot.color),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .height(72.dp)
                        .width(120.dp)
                ) {
                    Text(
                        text = "Marcar Vuelta",
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
            val timeBox = @Composable {
                Card(
                    modifier = Modifier
                        .height(72.dp)
                        .width(130.dp),
                    colors = CardDefaults.cardColors(containerColor = RacingGray),
                    shape = RoundedCornerShape(4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = pilot.time,
                            fontSize = 30.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
            if (leftHanded) {
                button()
                timeBox()
            } else {
                timeBox()
                button()
            }
        }
    }
}

@Composable
private fun AddPilotButton(onAddPilot: () -> Unit, enabled: Boolean) {
    Button(
        onClick = onAddPilot,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = RacingGreen,
            contentColor = Color.Black,
            disabledContainerColor = RacingGray,
            disabledContentColor = Color.DarkGray
        ),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .width(200.dp)
            .height(48.dp)
    ) {
        Text(text = if (enabled) "Agregar piloto" else "Límite de 4 pilotos", fontWeight = FontWeight.Bold)
    }
}
