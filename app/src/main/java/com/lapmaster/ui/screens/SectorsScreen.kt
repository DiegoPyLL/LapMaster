package com.lapmaster.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.lapmaster.ui.model.EstadoConfiguracionUi
import com.lapmaster.ui.model.EstadoSectoresUi
import com.lapmaster.ui.model.PreferenciaMano
import com.lapmaster.ui.model.SectorUi
import com.lapmaster.ui.theme.RojoCarreras

@Composable
fun PantallaSectores(
    estado: EstadoSectoresUi,
    configuraciones: EstadoConfiguracionUi,
) {
    val sectores = estado.sectores
    val zurdo = configuraciones.preferenciaMano == PreferenciaMano.ZURDO

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = estado.piloto?.let { "${it.nombre} #${it.numero}" } ?: "Selecciona piloto",
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(18.dp))
        if (sectores.size >= 3 && estado.piloto != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val orden = if (zurdo) listOf(2, 1, 0) else listOf(0, 1, 2)
                orden.forEachIndexed { indice, indiceSector ->
                    val sector = sectores[indiceSector]
                    when (indice) {
                        0, 2 -> SectorAlto(etiqueta = sector.etiqueta, color = Color(sector.color))
                        else -> SectorMedio(etiqueta = sector.etiqueta, color = Color(sector.color))
                    }
                    if (indice < orden.lastIndex) Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        BarraSectores(sectores = sectores)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            sectores.forEach { sector ->
                Text(
                    text = String.format("%.2f", sector.tiempo),
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = RojoCarreras,
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
private fun SectorAlto(etiqueta: String, color: Color) {
    Box(
        modifier = Modifier
            .width(90.dp)
            .height(190.dp)
            .background(color, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = etiqueta,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SectorMedio(etiqueta: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(96.dp)
                .background(color, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = etiqueta, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BarraSectores(sectores: List<SectorUi>) {
    val total = sectores.sumOf { it.tiempo.toDouble() }.toFloat().coerceAtLeast(1f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(22.dp)
            .background(Color.Black.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        sectores.forEach { sector ->
            val peso = sector.tiempo / total
            Box(
                modifier = Modifier
                    .weight(peso)
                    .fillMaxHeight()
                    .background(Color(sector.color))
            )
        }
    }
}
