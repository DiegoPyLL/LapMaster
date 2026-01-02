package com.lapmaster.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lapmaster.ui.model.ClimaUi
import com.lapmaster.ui.model.GpsUi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BannerClimaTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val baseClima = ClimaUi(
        ubicacion = "Mountain View",
        condicion = "Niebla",
        temperatura = "8.6°C",
        sensacionTermica = "6.8°C",
        direccionViento = "N",
        direccionVientoGrados = 0,
        velocidadViento = "11.1 km/h",
        rafagaViento = "13 km/h",
        presion = "1020 hPa",
        visibilidad = "10 km",
        nubosidad = "100%",
        lluvia = "0 mm",
        humedad = 86
    )
    private val gpsConFijacion = GpsUi(tieneFijacion = true, precisionMetros = 5f, rumboGrados = 0f)

    private fun setContent(clima: ClimaUi, gps: GpsUi) {
        composeTestRule.setContent {
            MaterialTheme {
                BannerClima(
                    clima = clima,
                    gps = gps,
                    rumboDispositivoGrados = gps.rumboGrados ?: 0f
                )
            }
        }
    }

    @Test
    fun showsHumidityValueWhenGpsHasFix() {
        setContent(
            clima = baseClima.copy(humedad = 73),
            gps = gpsConFijacion
        )

        composeTestRule.onNodeWithText("Humedad del Aire").assertExists()
        composeTestRule.onNodeWithText("73%", useUnmergedTree = true).assertExists()
    }

    @Test
    fun showsHumidityPlaceholderWhenGpsIsOff() {
        setContent(
            clima = baseClima.copy(humedad = 73),
            gps = GpsUi(tieneFijacion = false, precisionMetros = Float.MAX_VALUE, rumboGrados = null)
        )

        composeTestRule.onNodeWithText("Humedad del Aire").assertExists()
        composeTestRule.onNodeWithText("--", useUnmergedTree = true).assertExists()
    }

    @Test
    fun showsAllKeyMetricsAndTexts() {
        setContent(clima = baseClima, gps = gpsConFijacion)

        composeTestRule.onNodeWithText("Mountain View").assertExists()
        composeTestRule.onNodeWithText("Niebla").assertExists()
        composeTestRule.onNodeWithText("8.6°C").assertExists()
        composeTestRule.onNodeWithText("Sensacion 6.8°C").assertExists()

        composeTestRule.onNodeWithText("Direccion del Viento").assertExists()

        composeTestRule.onNodeWithText("Velocidad del Viento").assertExists()
        composeTestRule.onNodeWithText("11.1 km/h", useUnmergedTree = true).assertExists()

        composeTestRule.onNodeWithText("Presion Atmosferica").assertExists()
        composeTestRule.onNodeWithText("1020 hPa", useUnmergedTree = true).assertExists()

        composeTestRule.onNodeWithText("Nubosidad").assertExists()
        composeTestRule.onNodeWithText("100%", useUnmergedTree = true).assertExists()

        composeTestRule.onNodeWithText("Lluvia").assertExists()
        composeTestRule.onNodeWithText("0 mm", useUnmergedTree = true).assertExists()
    }
}
