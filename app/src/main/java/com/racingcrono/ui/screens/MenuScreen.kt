package com.racingcrono.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Brightness4
import androidx.compose.material.icons.outlined.Brightness7
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.racingcrono.ui.model.HandPreference
import com.racingcrono.ui.model.MenuActionUi
import com.racingcrono.ui.model.MenuUiState
import com.racingcrono.ui.model.PilotUi
import com.racingcrono.ui.model.SettingsUiState
import com.racingcrono.ui.theme.RacingGray
import com.racingcrono.ui.theme.RacingGreen
import com.racingcrono.ui.theme.RacingYellow

@Composable
fun MenuScreen(
    state: MenuUiState,
    weather: com.racingcrono.ui.model.WeatherUi,
    gps: com.racingcrono.ui.model.GpsUi,
    settings: SettingsUiState,
    onAddPilot: () -> Unit,
    onToggleTheme: () -> Unit,
    onToggleHand: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Panel principal",
            style = MaterialTheme.typography.displayLarge
        )
        com.racingcrono.ui.components.WeatherBanner(weather = weather, gps = gps)
        state.actions.forEach { action -> MenuButton(action) }
        PilotList(pilots = state.pilots, onAddPilot = onAddPilot, canAddMore = state.pilots.size < 4)
        SettingsRow(
            settings = settings,
            onToggleTheme = onToggleTheme,
            onToggleHand = onToggleHand
        )
    }
}

@Composable
private fun MenuButton(action: MenuActionUi) {
    Button(
        onClick = { },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(action.color),
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(
            text = action.title,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun PilotList(pilots: List<PilotUi>, onAddPilot: () -> Unit, canAddMore: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Pilotos activos", style = MaterialTheme.typography.titleMedium)
            pilots.forEach { pilot ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(Color(pilot.color), RoundedCornerShape(4.dp))
                    )
                    Column {
                        Text(text = "${pilot.name} #${pilot.number}", fontWeight = FontWeight.Bold)
                        Text(text = "Color asignado", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            AddPilotButton(onAddPilot = onAddPilot, enabled = canAddMore)
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
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(text = if (enabled) "Agregar piloto" else "Límite de 4 pilotos", fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SettingsRow(
    settings: SettingsUiState,
    onToggleTheme: () -> Unit,
    onToggleHand: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Configuraciones rápidas", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = if (settings.darkTheme) Icons.Outlined.Brightness4 else Icons.Outlined.Brightness7,
                        contentDescription = null
                    )
                    Text(text = if (settings.darkTheme) "Tema oscuro" else "Tema claro")
                }
                Switch(
                    checked = settings.darkTheme,
                    onCheckedChange = { onToggleTheme() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = RacingGreen,
                        checkedTrackColor = RacingGreen.copy(alpha = 0.5f)
                    )
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Botones para ${if (settings.handPreference == HandPreference.LEFT_HANDED) "zurdos" else "diestros"}")
                Switch(
                    checked = settings.handPreference == HandPreference.LEFT_HANDED,
                    onCheckedChange = { onToggleHand() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = RacingYellow,
                        checkedTrackColor = RacingYellow.copy(alpha = 0.6f)
                    )
                )
            }
            Text(
                text = "Orientación: ${settings.orientationPref.name.lowercase().replaceFirstChar { it.uppercase() }} (vertical u horizontal)",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
