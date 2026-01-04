package com.lapmaster.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lapmaster.ui.components.Cronometro
import com.lapmaster.ui.model.EstadoConfiguracionUi
import com.lapmaster.ui.model.EstadoSectoresUi
import com.lapmaster.ui.model.PreferenciaMano
import com.lapmaster.ui.model.SectorUi
import com.lapmaster.ui.theme.RojoCarreras

@Composable
fun PantallaSectores(
    estado: EstadoSectoresUi,
    configuraciones: EstadoConfiguracionUi,
    alIniciarCronometro: () -> Unit,
    alMarcarSector: (Int) -> Unit,
    alReiniciarSectores: () -> Unit
) {
    val sectores = estado.sectores
    val zurdo = configuraciones.preferenciaMano == PreferenciaMano.ZURDO
    val siguienteIndice = sectores.indexOfFirst { it.tiempoMs == 0L }
    val sectoresCompletos = sectores.isNotEmpty() && siguienteIndice == -1

    LaunchedEffect(estado.piloto?.id, estado.inicioSistemaMs) {
        if (estado.piloto != null && estado.inicioSistemaMs == null && sectores.any { it.tiempoMs == 0L }) {
            alIniciarCronometro()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val compacto = maxHeight < 620.dp
        val paddingHorizontal = if (compacto) 16.dp else 24.dp
        val paddingVertical = if (compacto) 12.dp else 18.dp
        val espacioVertical = if (compacto) 8.dp else 12.dp
        val espacioHorizontal = if (compacto) 8.dp else 12.dp
        val altoBarra = if (compacto) 18.dp else 22.dp
        val altoCrono = if (compacto) 46.dp else 54.dp
        val altoCronoTotal = if (compacto) 56.dp else 64.dp
        val altoBotonHeader = if (compacto) 32.dp else 36.dp
        val altoHeader = if (compacto) 56.dp else 64.dp
        val altoBloqueTotal = if (sectoresCompletos) {
            altoCronoTotal + if (compacto) 22.dp else 26.dp
        } else {
            0.dp
        }
        val separadores = if (sectoresCompletos) 4 else 3
        val altoDisponible = (maxHeight - (paddingVertical * 2)).coerceAtLeast(0.dp)
        val altoFijo = altoHeader + altoBarra + altoCrono + altoBloqueTotal +
            (espacioVertical * separadores)
        val altoBoton = (altoDisponible - altoFijo).coerceIn(56.dp, 160.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = paddingHorizontal, vertical = paddingVertical),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(altoHeader),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = estado.piloto?.let { "${it.nombre} #${it.numero}" } ?: "Selecciona piloto",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Ultimo",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = formatearTiempo(estado.ultimoTiempoMs),
                            style = MaterialTheme.typography.labelLarge,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                Button(
                    onClick = alReiniciarSectores,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RojoCarreras,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.heightIn(min = altoBotonHeader)
                ) {
                    Text(text = "Finalizar", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(espacioVertical))
            if (sectores.size >= 3 && estado.piloto != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(espacioHorizontal)
                ) {
                    val orden = if (zurdo) listOf(2, 1, 0) else listOf(0, 1, 2)
                    orden.forEach { indiceSector ->
                        val sector = sectores[indiceSector]
                        val habilitado = indiceSector == siguienteIndice
                        val onMarcar = { if (habilitado) alMarcarSector(indiceSector) }
                        SectorBoton(
                            etiqueta = sector.etiqueta,
                            color = Color(sector.color),
                            habilitado = habilitado,
                            onMarcar = onMarcar,
                            modifier = Modifier
                                .weight(1f)
                                .height(altoBoton)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(espacioVertical))
            BarraSectores(
                sectores = sectores,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(altoBarra)
            )
            Spacer(modifier = Modifier.height(espacioVertical))
            val acumuladoMs = sectores.sumOf { it.tiempoMs }
            val tiempoSectorActualMs = if (estado.inicioSistemaMs != null) {
                (estado.tiempoActualMs - acumuladoMs).coerceAtLeast(0L)
            } else {
                0L
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(espacioHorizontal),
                verticalAlignment = Alignment.CenterVertically
            ) {
                sectores.forEachIndexed { indice, sector ->
                    val tiempoMs = when {
                        sector.tiempoMs > 0L -> sector.tiempoMs
                        indice == siguienteIndice && estado.inicioSistemaMs != null -> tiempoSectorActualMs
                        else -> 0L
                    }
                    Cronometro(
                        tiempoMs = tiempoMs,
                        fondo = Color(sector.color),
                        colorTexto = Color.Black,
                        alto = altoCrono,
                        ancho = null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            if (sectoresCompletos) {
                Spacer(modifier = Modifier.height(espacioVertical))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(if (compacto) 6.dp else 8.dp)
                ) {
                    Text(
                        text = "Tiempo total",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Monospace
                    )
                    Cronometro(
                        tiempoMs = sectores.sumOf { it.tiempoMs },
                        alto = altoCronoTotal,
                        ancho = null,
                        modifier = Modifier.fillMaxWidth(0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectorBoton(
    etiqueta: String,
    color: Color,
    habilitado: Boolean,
    onMarcar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onMarcar,
        enabled = habilitado,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.Black,
            disabledContainerColor = color.copy(alpha = 0.4f),
            disabledContentColor = Color.DarkGray
        ),
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = etiqueta,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BarraSectores(
    sectores: List<SectorUi>,
    modifier: Modifier = Modifier
) {
    val totalMs = sectores.sumOf { it.tiempoMs }
    val pesos = if (totalMs <= 0L) {
        sectores.map { 1f }
    } else {
        sectores.map { sector ->
            val peso = sector.tiempoMs.toFloat() / totalMs
            peso.coerceAtLeast(0.05f)
        }
    }
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        sectores.forEachIndexed { indice, sector ->
            val peso = pesos.getOrElse(indice) { 1f }
            Box(
                modifier = Modifier
                    .weight(peso)
                    .fillMaxHeight()
                    .background(Color(sector.color))
            )
        }
    }
}

private fun formatearTiempo(ms: Long): String {
    if (ms <= 0L) return "--:--.---"
    val totalSegundos = ms / 1000
    val minutos = totalSegundos / 60
    val segundos = totalSegundos % 60
    val milisegundos = ms % 1000
    return String.format("%d:%02d.%03d", minutos, segundos, milisegundos)
}
