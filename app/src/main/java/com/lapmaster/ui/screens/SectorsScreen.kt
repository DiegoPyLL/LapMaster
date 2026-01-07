package com.lapmaster.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.lapmaster.ui.model.VueltaSectoresUi
import com.lapmaster.ui.theme.AmarilloCarreras
import com.lapmaster.ui.theme.RojoCarreras
import com.lapmaster.ui.theme.VerdeCarreras
import kotlin.math.abs
import kotlin.math.roundToLong

@Composable
fun PantallaSectores(
    estado: EstadoSectoresUi,
    configuraciones: EstadoConfiguracionUi,
    alIniciarCronometro: () -> Unit,
    alMarcarSector: (Int) -> Unit,
    alAlternarPausa: () -> Unit,
    alReiniciarSectores: () -> Unit
) {
    val sectores = estado.sectores
    val zurdo = configuraciones.preferenciaMano == PreferenciaMano.ZURDO
    val siguienteIndice = sectores.indexOfFirst { it.tiempoMs == 0L }
    val sectoresCompletos = sectores.isNotEmpty() && siguienteIndice == -1

    LaunchedEffect(estado.piloto?.id, estado.inicioSistemaMs, estado.enPausa) {
        if (estado.piloto != null &&
            estado.inicioSistemaMs == null &&
            !estado.enPausa &&
            sectores.any { it.tiempoMs == 0L }) {
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
        val mostrarHistorial = estado.piloto != null
        val mostrarResumen = estado.historial.isNotEmpty()
        val filasHistorialVisibles = if (compacto) 2 else 3
        val altoFilaHistorial = if (compacto) 22.dp else 26.dp
        val altoTituloHistorial = if (compacto) 20.dp else 24.dp
        val altoEncabezadoHistorial = if (compacto) 18.dp else 20.dp
        val paddingHistorial = if (compacto) 8.dp else 12.dp
        val espacioHistorial = if (compacto) 4.dp else 6.dp
        val filasHistorial = estado.historial.size.coerceAtMost(filasHistorialVisibles)
        val altoListaHistorial = if (mostrarHistorial) {
            val filasExtra = (filasHistorial - 1).coerceAtLeast(0)
            (altoFilaHistorial * filasHistorial) + (espacioHistorial * filasExtra)
        } else {
            0.dp
        }
        val altoHistorial = if (mostrarHistorial) {
            altoTituloHistorial + altoEncabezadoHistorial + altoListaHistorial + paddingHistorial + (espacioHistorial * 2)
        } else {
            0.dp
        }
        val paddingResumen = if (compacto) 8.dp else 12.dp
        val espacioResumen = if (compacto) 4.dp else 6.dp
        val altoTituloResumen = if (compacto) 18.dp else 22.dp
        val altoFilaResumen = if (compacto) 22.dp else 26.dp
        val altoResumen = if (mostrarResumen) {
            (paddingResumen * 2) + altoTituloResumen + (altoFilaResumen * 2) + (espacioResumen * 2)
        } else {
            0.dp
        }
        val altoBloqueTotal = if (sectoresCompletos) {
            altoCronoTotal + if (compacto) 22.dp else 26.dp
        } else {
            0.dp
        }
        val separadoresBase = if (sectoresCompletos) 4 else 3
        val separadores = separadoresBase +
            (if (mostrarHistorial) 1 else 0) +
            (if (mostrarResumen) 1 else 0)
        val altoDisponible = (maxHeight - (paddingVertical * 2)).coerceAtLeast(0.dp)
        val altoFijo = altoHeader + altoBarra + altoCrono + altoBloqueTotal + altoHistorial + altoResumen +
            (espacioVertical * separadores)
        val altoBoton = (altoDisponible - altoFijo).coerceIn(56.dp, 160.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = paddingHorizontal, vertical = paddingVertical),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            val ordenSectores = if (zurdo) sectores.indices.reversed() else sectores.indices
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
                val puedePausar = estado.piloto != null &&
                    estado.inicioSistemaMs != null &&
                    sectores.any { it.tiempoMs == 0L }
                val puedeReiniciar = estado.piloto != null && (
                    estado.inicioSistemaMs != null ||
                        estado.historial.isNotEmpty() ||
                        sectores.any { it.tiempoMs > 0L } ||
                        estado.ultimoTiempoMs > 0L
                    )
                val textoPausa = when {
                    estado.enPausa && compacto -> "Seguir"
                    estado.enPausa -> "Continuar"
                    compacto -> "Pausa"
                    else -> "Pausar"
                }
                val colorPausa = if (estado.enPausa) VerdeCarreras else AmarilloCarreras
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = alAlternarPausa,
                        enabled = puedePausar,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorPausa,
                            contentColor = Color.Black,
                            disabledContainerColor = colorPausa.copy(alpha = 0.35f),
                            disabledContentColor = Color.Black.copy(alpha = 0.4f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 3.dp,
                            pressedElevation = 6.dp
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier.heightIn(min = altoBotonHeader)
                    ) {
                        Text(text = textoPausa, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = alReiniciarSectores,
                        enabled = puedeReiniciar,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RojoCarreras,
                            contentColor = Color.White,
                            disabledContainerColor = RojoCarreras.copy(alpha = 0.35f),
                            disabledContentColor = Color.White.copy(alpha = 0.6f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 3.dp,
                            pressedElevation = 6.dp
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier.heightIn(min = altoBotonHeader)
                    ) {
                        Text(text = "Reiniciar", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(espacioVertical))
            if (sectores.size >= 3 && estado.piloto != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(espacioHorizontal)
                ) {
                    ordenSectores.forEach { indiceSector ->
                        val sector = sectores[indiceSector]
                        val seleccionado = indiceSector == siguienteIndice
                        val habilitado = seleccionado && !estado.enPausa
                        val completado = sector.tiempoMs > 0L
                        val estadoEtiqueta = when {
                            completado -> "Listo"
                            seleccionado && estado.enPausa -> "Pausado"
                            habilitado -> if (estado.inicioSistemaMs == null) "Iniciar" else "Marcar"
                            else -> "Espera"
                        }
                        SectorBoton(
                            etiqueta = sector.etiqueta,
                            estado = estadoEtiqueta,
                            color = Color(sector.color),
                            habilitado = habilitado,
                            resaltado = seleccionado,
                            compacto = compacto,
                            onMarcar = { alMarcarSector(indiceSector) },
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
                ordenSectores.forEach { indice ->
                    val sector = sectores[indice]
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
            if (mostrarHistorial) {
                Spacer(modifier = Modifier.height(espacioVertical))
                HistorialSectores(
                    historial = estado.historial,
                    sectores = sectores,
                    compacto = compacto,
                    maxAltoLista = altoListaHistorial,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (mostrarResumen) {
                Spacer(modifier = Modifier.height(espacioVertical))
                ResumenSectores(
                    historial = estado.historial,
                    compacto = compacto,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SectorBoton(
    etiqueta: String,
    estado: String,
    color: Color,
    habilitado: Boolean,
    resaltado: Boolean,
    compacto: Boolean,
    onMarcar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val bordeActivo = if (resaltado) {
        Modifier.border(2.dp, Color.Black.copy(alpha = 0.25f), shape)
    } else {
        Modifier
    }
    val estiloEtiqueta = if (compacto) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleSmall
    val estiloEstado = if (compacto) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium
    Button(
        onClick = onMarcar,
        enabled = habilitado,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.Black,
            disabledContainerColor = color.copy(alpha = 0.35f),
            disabledContentColor = Color.Black.copy(alpha = 0.4f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (resaltado) 6.dp else 4.dp,
            pressedElevation = 8.dp,
            disabledElevation = 0.dp
        ),
        shape = shape,
        contentPadding = PaddingValues(
            horizontal = 8.dp,
            vertical = if (compacto) 4.dp else 6.dp
        ),
        modifier = modifier.then(bordeActivo)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(if (compacto) 2.dp else 4.dp)
        ) {
            Text(
                text = etiqueta,
                style = estiloEtiqueta,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = estado,
                style = estiloEstado,
                color = LocalContentColor.current.copy(
                    alpha = LocalContentColor.current.alpha * if (habilitado) 0.85f else 0.65f
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
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

@Composable
private fun HistorialSectores(
    historial: List<VueltaSectoresUi>,
    sectores: List<SectorUi>,
    compacto: Boolean,
    maxAltoLista: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val padding = if (compacto) 8.dp else 12.dp
    val altoFila = if (compacto) 22.dp else 26.dp
    val espacioFila = if (compacto) 4.dp else 6.dp
    val estiloTitulo = if (compacto) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleSmall
    val estiloEncabezado = MaterialTheme.typography.labelSmall
    val estiloFila = if (compacto) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium
    val anchoColumnaVuelta = if (compacto) 30.dp else 36.dp

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(espacioFila)
        ) {
            Text(
                text = "Historial de vueltas",
                style = estiloTitulo,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vueltas",
                    style = estiloEncabezado,
                    modifier = Modifier.width(anchoColumnaVuelta)
                )
                sectores.forEachIndexed { indice, _ ->
                    Text(
                        text = "S${indice + 1}",
                        style = estiloEncabezado,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
                Text(
                    text = "Total",
                    style = estiloEncabezado,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxAltoLista)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(espacioFila)
            ) {
                historial.forEach { vuelta ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = altoFila),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "V${vuelta.numero}",
                            style = estiloFila,
                            modifier = Modifier.width(anchoColumnaVuelta)
                        )
                        sectores.forEachIndexed { indice, _ ->
                            val tiempoMs = vuelta.tiemposMs.getOrNull(indice) ?: 0L
                            Text(
                                text = formatearTiempo(tiempoMs),
                                style = estiloFila,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Text(
                            text = formatearTiempo(vuelta.totalMs),
                            style = estiloFila,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumenSectores(
    historial: List<VueltaSectoresUi>,
    compacto: Boolean,
    modifier: Modifier = Modifier
) {
    val tiempos = historial.map { it.totalMs }.filter { it > 0L }
    val promedioMs = if (tiempos.isNotEmpty()) {
        (tiempos.sum().toDouble() / tiempos.size).roundToLong()
    } else {
        0L
    }
    val desviacionMediaMs = if (tiempos.size > 1) {
        val promedio = tiempos.sum().toDouble() / tiempos.size
        tiempos.map { abs(it - promedio) }.average().roundToLong()
    } else {
        0L
    }
    val mejorMs = tiempos.minOrNull() ?: 0L
    val vueltasTexto = if (tiempos.isNotEmpty()) "${tiempos.size}" else "--"

    val padding = if (compacto) 8.dp else 12.dp
    val espacio = if (compacto) 4.dp else 6.dp
    val altoFila = if (compacto) 22.dp else 26.dp
    val estiloTitulo = if (compacto) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleSmall
    val estiloEtiqueta = if (compacto) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium
    val estiloValor = if (compacto) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleSmall

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(espacio)
        ) {
            Text(
                text = "Resumen de vueltas",
                style = estiloTitulo,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = altoFila),
                horizontalArrangement = Arrangement.spacedBy(espacio),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ResumenItem(
                    etiqueta = "Promedio",
                    valor = formatearTiempo(promedioMs),
                    estiloEtiqueta = estiloEtiqueta,
                    estiloValor = estiloValor,
                    modifier = Modifier.weight(1f)
                )
                ResumenItem(
                    etiqueta = "Desv. media",
                    valor = formatearTiempo(desviacionMediaMs),
                    estiloEtiqueta = estiloEtiqueta,
                    estiloValor = estiloValor,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = altoFila),
                horizontalArrangement = Arrangement.spacedBy(espacio),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ResumenItem(
                    etiqueta = "Mejor",
                    valor = formatearTiempo(mejorMs),
                    estiloEtiqueta = estiloEtiqueta,
                    estiloValor = estiloValor,
                    modifier = Modifier.weight(1f)
                )
                ResumenItem(
                    etiqueta = "Vueltas",
                    valor = vueltasTexto,
                    estiloEtiqueta = estiloEtiqueta,
                    estiloValor = estiloValor,
                    modifier = Modifier.weight(1f),
                    usarMonospace = false
                )
            }
        }
    }
}

@Composable
private fun ResumenItem(
    etiqueta: String,
    valor: String,
    estiloEtiqueta: androidx.compose.ui.text.TextStyle,
    estiloValor: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier,
    usarMonospace: Boolean = true
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = etiqueta, style = estiloEtiqueta, textAlign = TextAlign.Center)
        Text(
            text = valor,
            style = estiloValor,
            fontFamily = if (usarMonospace) FontFamily.Monospace else null,
            textAlign = TextAlign.Center
        )
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
