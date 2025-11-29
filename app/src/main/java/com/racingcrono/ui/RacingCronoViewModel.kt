package com.racingcrono.ui

import androidx.lifecycle.ViewModel
import com.racingcrono.ui.model.AppUiState
import com.racingcrono.ui.model.ChartSeriesUi
import com.racingcrono.ui.model.GpsUi
import com.racingcrono.ui.model.GraphUiState
import com.racingcrono.ui.model.HandPreference
import com.racingcrono.ui.model.LapsUiState
import com.racingcrono.ui.model.MenuActionUi
import com.racingcrono.ui.model.MenuUiState
import com.racingcrono.ui.model.PilotLapUi
import com.racingcrono.ui.model.PilotUi
import com.racingcrono.ui.model.Screen
import com.racingcrono.ui.model.SectorUi
import com.racingcrono.ui.model.SectorsUiState
import com.racingcrono.ui.model.SettingsUiState
import com.racingcrono.ui.model.SummaryUi
import com.racingcrono.ui.model.WeatherUi
import com.racingcrono.ui.model.HistoryEntryUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class RacingCronoViewModel : ViewModel() {

    private val pilotPalette = listOf(
        0xFF00DB54L, // green
        0xFFFF5E5EL, // red
        0xFF3BA3FFL, // blue
        0xFFFFA726L  // orange
    )

    private var nextPilotId = 3

    private val initialPilots = listOf(
        PilotUi(id = 1, name = "Piastri", number = "81", color = pilotPalette[1]),
        PilotUi(id = 2, name = "Norris", number = "4", color = pilotPalette[0])
    )

    private val initialState = AppUiState(
        selectedScreen = Screen.LAPS,
        laps = LapsUiState(
            pilots = listOf(
                PilotLapUi(initialPilots[0], "1:32.27"),
                PilotLapUi(initialPilots[1], "1:31.77")
            )
        ),
        menu = MenuUiState(
            actions = listOf(
                MenuActionUi("Tomar tiempo a 4 pilotos", 0xFF00DB54L),
                MenuActionUi("Tomar tiempo por sectores", 0xFF00DB54L),
                MenuActionUi("Historial", 0xFF00DB54L),
                MenuActionUi("Configuración", 0xFFECC43BL)
            ),
            pilots = initialPilots
        ),
        sectors = SectorsUiState(
            pilot = initialPilots.first(),
            sectors = listOf(
                SectorUi("Sector 1", 20.81f, 0xFF00DB54L),
                SectorUi("Sector 2", 30.18f, 0xFF00D7F8L),
                SectorUi("Sector 3", 22.27f, 0xFF00DB54L)
            )
        ),
        graphs = GraphUiState(
            years = listOf("2025", "2024", "2023", "2022", "2021", "2020"),
            selectedYear = "2025",
            series = listOf(
                ChartSeriesUi("Piastri", 0xFFFF5E5EL, listOf(4f, 6f, 8f, 10f, 12f, 14f, 16f, 18f, 16f, 14f)),
                ChartSeriesUi("Norris", 0xFF53E38EL, listOf(0f, 2f, 4f, 8f, 10f, 12f, 14f, 18f, 20f, 22f)),
                ChartSeriesUi("Verstappen", 0xFFFFA726L, listOf(-2f, -1f, 2f, 3f, 6f, 7f, 10f, 11f, 14f, 16f)),
                ChartSeriesUi("Russell", 0xFF3BA3FFL, listOf(-6f, -4f, -2f, 0f, 1f, 2f, 1f, 0f, -1f, -2f))
            ),
            history = listOf(
                HistoryEntryUi(dayLabel = "Hoy", bestLap = "1:30.12", laps = 12),
                HistoryEntryUi(dayLabel = "Ayer", bestLap = "1:31.05", laps = 18),
                HistoryEntryUi(dayLabel = "27/10", bestLap = "1:30.80", laps = 10)
            )
        ),
        weather = WeatherUi(
            location = "Circuito Las Américas",
            condition = "Parcialmente soleado",
            temperature = "28°C",
            windDirection = "NE",
            windSpeed = "18 km/h",
            humidity = 54
        ),
        gps = GpsUi(hasFix = true, accuracyMeters = 4.5f),
        summary = SummaryUi(
            bestLap = "1:30.12",
            averageLap = "1:32.40",
            sessionLaps = 12,
            dayBest = "1:29.88",
            dayAverage = "1:33.05"
        ),
        settings = SettingsUiState(
            darkTheme = false,
            handPreference = HandPreference.RIGHT_HANDED
        )
    )

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<AppUiState> = _uiState

    fun onScreenSelected(screen: Screen) {
        _uiState.update { it.copy(selectedScreen = screen) }
    }

    fun onYearSelected(year: String) {
        _uiState.update { state ->
            if (year in state.graphs.years) {
                state.copy(graphs = state.graphs.copy(selectedYear = year))
            } else state
        }
    }

    fun onToggleTheme() {
        _uiState.update { state ->
            state.copy(settings = state.settings.copy(darkTheme = !state.settings.darkTheme))
        }
    }

    fun onToggleHandPreference() {
        _uiState.update { state ->
            val next = if (state.settings.handPreference == HandPreference.RIGHT_HANDED) {
                HandPreference.LEFT_HANDED
            } else HandPreference.RIGHT_HANDED
            state.copy(settings = state.settings.copy(handPreference = next))
        }
    }

    fun onAddPilot() {
        _uiState.update { state ->
            if (state.menu.pilots.size >= 4) return@update state

            val color = pilotPalette[state.menu.pilots.size % pilotPalette.size]
            val newPilot = PilotUi(
                id = nextPilotId++,
                name = "Piloto #${nextPilotId - 1}",
                number = "${80 + nextPilotId}",
                color = color
            )
            val updatedPilots = state.menu.pilots + newPilot
            val updatedLaps = state.laps.copy(
                pilots = (state.laps.pilots + PilotLapUi(newPilot, "--:--.--")).take(4)
            )
            val updatedSectors = if (state.sectors.pilot == null) {
                state.sectors.copy(pilot = newPilot)
            } else state.sectors
            val updatedMenu = state.menu.copy(pilots = updatedPilots)

            state.copy(menu = updatedMenu, laps = updatedLaps, sectors = updatedSectors)
        }
    }
}
