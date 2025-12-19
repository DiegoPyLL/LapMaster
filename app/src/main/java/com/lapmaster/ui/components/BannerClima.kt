package com.lapmaster.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.lapmaster.ui.model.ClimaUi
import com.lapmaster.ui.model.GpsUi
import com.lapmaster.ui.theme.AmarilloCarreras
import com.lapmaster.ui.theme.CianCarreras
import com.lapmaster.ui.theme.RojoCarreras
import com.lapmaster.ui.theme.VerdeCarreras
import kotlin.math.roundToInt
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import androidx.compose.foundation.layout.width

// TODO (Implementar ubicación precisa, puede ser la api de OpenStreetMap)

@Composable
fun BannerClima(
    clima: ClimaUi,
    gps: GpsUi,
    modifier: Modifier = Modifier,
    rumboDispositivoGrados: Float = 0f
) {
    val degradado = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0D1B14),
            VerdeCarreras.copy(alpha = 0.6f),
            CianCarreras.copy(alpha = 0.5f)
        )
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0F0C))
    ) {
        Column(
            modifier = Modifier
                .background(degradado)
                .padding(vertical = 8.dp, horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EstadoGps(gps)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    EncabezadoClima(clima = clima)
                    TemperaturaActual(
                        clima,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 10.dp)
                    )
                }
                BrujulaViento(
                    grados = clima.direccionVientoGrados,
                    etiqueta = clima.direccionViento,
                    rumboDispositivoGrados = rumboDispositivoGrados,
                    tamano = 84.dp
                )
            }
            val metricaEspaciado = 6.dp
            val humedadValor = if (gps.tieneFijacion) "${clima.humedad}%" else "--"
            val metricasGrid = listOf(
                MetricaClimaItem(Icons.Outlined.WaterDrop, "Humedad del Aire", humedadValor),
                MetricaClimaItem(Icons.Outlined.Air, "Velocidad del Viento", vientoVelocidadTexto(clima)),
                MetricaClimaItem(Icons.Outlined.Speed, "Presión Atmosférica", clima.presion),
                MetricaClimaItem(Icons.Outlined.Cloud, "Nubosidad", clima.nubosidad)
            )
            val metricaLluvia = MetricaClimaItem(Icons.Outlined.WaterDrop, "Lluvia", clima.lluvia)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(metricaEspaciado)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(metricaEspaciado)
                ) {
                    metricasGrid.filterIndexed { index, _ -> index % 2 == 0 }
                        .forEach { item ->
                            MetricaClima(item, modifier = Modifier.fillMaxWidth())
                        }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(metricaEspaciado)
                ) {
                    metricasGrid.filterIndexed { index, _ -> index % 2 != 0 }
                        .forEach { item ->
                            MetricaClima(item, modifier = Modifier.fillMaxWidth())
                        }
                }
            }
            MetricaClima(metricaLluvia, modifier = Modifier.fillMaxWidth())
        }
    }
}



@Composable
private fun EncabezadoClima(clima: ClimaUi, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = AmarilloCarreras,
                modifier = Modifier.size(20.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = clima.ubicacion.ifBlank { "Ubicación actual" },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
                Text(
                    text = clima.condicion.ifBlank { "Cargando clima..." },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.88f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TemperaturaActual(clima: ClimaUi, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = clima.temperatura.ifBlank { "--°C" },
            color = Color.White,
            fontSize = 32.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Sensación ${clima.sensacionTermica.ifBlank { "--" }}",
            color = Color.White.copy(alpha = 0.82f),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun BrujulaViento(
    grados: Int?,
    etiqueta: String,
    rumboDispositivoGrados: Float,
    tamano: Dp
) {
    val destino = (grados ?: 0).toFloat()
    val rotacion by animateFloatAsState(
        targetValue = destino,
        animationSpec = tween(durationMillis = 700),
        label = "brujula-viento"
    )
    val vector = calcularVectorRelativo(rotacion, rumboDispositivoGrados)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Dirección del Viento",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.85f)
        )
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White.copy(alpha = 0.06f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Box(
                modifier = Modifier
                    .size(tamano)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val radio = size.minDimension / 2f
                    drawCircle(
                        color = Color.White.copy(alpha = 0.16f),
                        radius = radio * 0.96f,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.08f),
                        radius = radio * 0.7f,
                        style = Stroke(width = 1.dp.toPx())
                    )
                    val centro = Offset(size.width / 2f, size.height / 2f)
                    val largo = radio * 0.8f
                    val vectorSeguro = if (vector == Offset.Zero) Offset(0f, -1f) else vector
                    val destinoVector = centro + Offset(vectorSeguro.x * largo, vectorSeguro.y * largo)
                    // flecha según vector 2D normalizado
                    drawLine(
                        color = CianCarreras,
                        start = centro,
                        end = destinoVector,
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    // cabeza de flecha
                    val head = 12.dp.toPx()
                    val base = destinoVector - Offset(vectorSeguro.x * head, vectorSeguro.y * head)
                    val perpendicular = Offset(-vectorSeguro.y, vectorSeguro.x)
                    val left = base + Offset(perpendicular.x * head * 0.5f, perpendicular.y * head * 0.5f)
                    val right = base - Offset(perpendicular.x * head * 0.5f, perpendicular.y * head * 0.5f)
                    drawLine(
                        color = CianCarreras,
                        start = destinoVector,
                        end = left,
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = CianCarreras,
                        start = destinoVector,
                        end = right,
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.5f),
                        start = centro.copy(y = centro.y - radio * 0.95f),
                        end = centro.copy(y = centro.y - radio * 0.8f),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                Text(
                    text = etiqueta.ifBlank { "--" },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White
                )
            }
        }
    }
}




private fun calcularVectorRelativo(vientoGrados: Float, rumboDispositivoGrados: Float): Offset {
    // viento llega en grados desde el norte (dirección de donde viene); sumamos 180° para mostrar hacia dónde va
    // y ajustamos según rumbo del teléfono para que la flecha use el norte relativo al usuario.
    val ajuste = normalizarGrados(vientoGrados + 180f - rumboDispositivoGrados)
    val rad = ajuste / 180f * PI.toFloat()
    val x = sin(rad) // Este positivo hacia el este
    val y = -cos(rad) // Este negativo hacia el norte, invertido por canvas
    val magnitud = kotlin.math.sqrt(x * x + y * y).coerceAtLeast(0.0001f)
    return Offset(x / magnitud, y / magnitud)
}




private fun normalizarGrados(angulo: Float): Float {
    var a = angulo % 360f
    if (a < 0) a += 360f
    return a
}

private fun Float.format(decimales: Int): String {
    return String.format(Locale.US, "%.${decimales}f", this)
}

@Composable
private fun MetricaClimaFila(items: List<MetricaClimaItem>, compacto: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(if (compacto) 8.dp else 10.dp)
    ) {
        items.forEach { item ->
            MetricaClima(item, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun MetricaClima(item: MetricaClimaItem, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.heightIn(min = 64.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f))
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 6.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = item.icono,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = item.etiqueta,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.82f)
                )
                Text(
                    text = item.valor.ifBlank { "--" },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White
                )
            }
        }
    }
}




@Composable
private fun EstadoGps(gps: GpsUi) {
    val color = if (gps.tieneFijacion) VerdeCarreras else RojoCarreras
    val precision = if (gps.tieneFijacion && gps.precisionMetros != Float.MAX_VALUE) {
        "±${gps.precisionMetros.roundToInt()} m"
    } else {
        "Sin señal"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),  // más espacio
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.GpsFixed,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = if (gps.tieneFijacion) "GPS listo. \nPresisión: $precision" else "GPS sin señal",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }
        }
    }
}






private data class MetricaClimaItem(
    val icono: ImageVector,
    val etiqueta: String,
    val valor: String
)

private fun vientoTexto(clima: ClimaUi): String {
    val componentes = listOfNotNull(
        clima.direccionViento.takeIf { it.isNotBlank() && it != "--" },
        clima.velocidadViento.takeIf { it.isNotBlank() && it != "--" }
    )
    return when {
        componentes.isNotEmpty() -> componentes.joinToString(" · ")
        clima.rafagaViento.isNotBlank() && clima.rafagaViento != "--" -> "Ráfaga ${clima.rafagaViento}"
        else -> "--"
    }
}

private fun vientoVelocidadTexto(clima: ClimaUi): String {
    return clima.velocidadViento.ifBlank { "--" }
}
