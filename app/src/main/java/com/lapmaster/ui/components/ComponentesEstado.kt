package com.lapmaster.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.WindPower
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lapmaster.ui.model.ClimaUi
import com.lapmaster.ui.model.GpsUi
import com.lapmaster.ui.model.ResumenUi
import com.lapmaster.ui.theme.RojoCarreras
import com.lapmaster.ui.theme.VerdeCarreras

@Composable
fun BannerClima(clima: ClimaUi, gps: GpsUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A8DF0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = clima.ubicacion, color = Color.White, fontWeight = FontWeight.Bold)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Outlined.Cloud, contentDescription = null, tint = Color.White)
                    Text(text = "${clima.temperatura} • ${clima.condicion}", color = Color.White)
                }
                Text(
                    text = "Sensación: ${clima.sensacionTermica}",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Outlined.WindPower, contentDescription = null, tint = Color.White)
                    Text(text = "Viento ${clima.direccionViento} ${clima.velocidadViento}", color = Color.White)
                    Text(text = "Ráfaga ${clima.rafagaViento}", color = Color.White)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "Humedad ${clima.humedad}%", color = Color.White)
                    Text(text = "Presión ${clima.presion}", color = Color.White)
                    Text(text = "Vis ${clima.visibilidad}", color = Color.White)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "Nubes ${clima.nubosidad}", color = Color.White)
                    Text(text = "Lluvia ${clima.lluvia}", color = Color.White)
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.GpsFixed,
                    contentDescription = null,
                    tint = if (gps.tieneFijacion) VerdeCarreras else RojoCarreras,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = if (gps.tieneFijacion) "GPS ok (${gps.precisionMetros} m)" else "Sin señal",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun TarjetaResumen(resumen: ResumenUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ColumnaResumen(titulo = "Mejor vuelta", valor = resumen.mejorVuelta)
            ColumnaResumen(titulo = "Promedio", valor = resumen.promedioVuelta)
            ColumnaResumen(titulo = "Vueltas", valor = resumen.vueltasSesion.toString())
            ColumnaResumen(titulo = "Mejor del día", valor = resumen.mejorDelDia)
        }
    }
}

@Composable
private fun ColumnaResumen(titulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = titulo, style = MaterialTheme.typography.labelLarge)
        Text(
            text = valor,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}
