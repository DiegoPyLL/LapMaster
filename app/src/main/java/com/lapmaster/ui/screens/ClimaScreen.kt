package com.lapmaster.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.lapmaster.ui.components.BannerClima
import com.lapmaster.ui.model.ClimaUi
import com.lapmaster.ui.model.GpsUi

@Composable
fun PantallaClima(
    clima: ClimaUi,
    gps: GpsUi
) {
    val esHorizontal = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    BannerClima(
        clima = clima,
        gps = gps,
        rumboDispositivoGrados = gps.rumboGrados ?: 0f,
        esHorizontal = esHorizontal,
        pantallaCompleta = true,
        modifier = Modifier.fillMaxSize()
    )
}
