package com.lapmaster.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.lapmaster.ui.components.BannerClima
import com.lapmaster.ui.model.ClimaUi
import com.lapmaster.ui.model.GpsUi



// todo ver posibilidad de usar un mapa meteorologico de las nubes, como UAV Forecast.
//  Reordenar componentes para que use la pantalla completa de forma optima
@Composable
fun PantallaClima(
    clima: ClimaUi,
    gps: GpsUi
) {
    val esHorizontal = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (esHorizontal) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BannerClima(
                clima = clima,
                gps = gps,
                rumboDispositivoGrados = gps.rumboGrados ?: 0f,
                esHorizontal = true,
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BannerClima(
                clima = clima,
                gps = gps,
                rumboDispositivoGrados = gps.rumboGrados ?: 0f,
                esHorizontal = false
            )
        }
    }
}
