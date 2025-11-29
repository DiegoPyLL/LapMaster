package com.racingcrono.ui.model

// Guardamos colores como ARGB Long para no acoplar el ViewModel a Compose.
data class PilotUi(
    val id: Int,
    val name: String,
    val number: String,
    val color: Long
)

data class PilotLapUi(
    val pilot: PilotUi,
    val time: String
)

data class MenuActionUi(val title: String, val color: Long)

data class SectorUi(val label: String, val time: Float, val color: Long)

data class ChartSeriesUi(val name: String, val color: Long, val values: List<Float>)

data class HistoryEntryUi(
    val dayLabel: String,
    val bestLap: String,
    val laps: Int
)

data class WeatherUi(
    val location: String,
    val condition: String,
    val temperature: String,
    val windDirection: String,
    val windSpeed: String,
    val humidity: Int
)

data class GpsUi(val hasFix: Boolean, val accuracyMeters: Float)

data class SummaryUi(
    val bestLap: String,
    val averageLap: String,
    val sessionLaps: Int,
    val dayBest: String,
    val dayAverage: String
)

enum class HandPreference { RIGHT_HANDED, LEFT_HANDED }
enum class OrientationPref { AUTO, VERTICAL, HORIZONTAL }

data class SettingsUiState(
    val darkTheme: Boolean = false,
    val handPreference: HandPreference = HandPreference.RIGHT_HANDED,
    val orientationPref: OrientationPref = OrientationPref.AUTO
)

data class LapsUiState(
    val pilots: List<PilotLapUi> = emptyList()
)

data class MenuUiState(
    val actions: List<MenuActionUi> = emptyList(),
    val pilots: List<PilotUi> = emptyList()
)

data class SectorsUiState(
    val pilot: PilotUi? = null,
    val sectors: List<SectorUi> = emptyList()
)

data class GraphUiState(
    val years: List<String> = emptyList(),
    val selectedYear: String = "",
    val series: List<ChartSeriesUi> = emptyList(),
    val history: List<HistoryEntryUi> = emptyList()
)

data class AppUiState(
    val selectedScreen: Screen = Screen.LAPS,
    val laps: LapsUiState = LapsUiState(),
    val menu: MenuUiState = MenuUiState(),
    val sectors: SectorsUiState = SectorsUiState(),
    val graphs: GraphUiState = GraphUiState(),
    val weather: WeatherUi = WeatherUi("", "", "", "", "", 0),
    val gps: GpsUi = GpsUi(false, 0f),
    val summary: SummaryUi = SummaryUi("-", "-", 0, "-", "-"),
    val settings: SettingsUiState = SettingsUiState()
)

enum class Screen { LAPS, MENU, SECTORS, GRAPHS }
