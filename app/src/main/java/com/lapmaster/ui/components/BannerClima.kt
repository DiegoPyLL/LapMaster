package com.lapmaster.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.lapmaster.ui.model.ClimaUi
import com.lapmaster.ui.model.GpsUi
import com.lapmaster.ui.theme.AmarilloCarreras
import com.lapmaster.ui.theme.CianCarreras
import com.lapmaster.ui.theme.RojoCarreras
import com.lapmaster.ui.theme.VerdeCarreras
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

// Implementar ubicación precisa, puede ser la api de OpenStreetMap

@Composable
fun BannerClima(
    clima: ClimaUi,
    gps: GpsUi,
    modifier: Modifier = Modifier,
    rumboDispositivoGrados: Float = 0f,
    esHorizontal: Boolean = false,
    pantallaCompleta: Boolean = false
) {
    val textoPrimario = MaterialTheme.colorScheme.onSurface
    val textoSecundario = textoPrimario.copy(alpha = 0.7f)
    val bordeClaro = textoPrimario.copy(alpha = 0.08f)
    val fondoTarjeta = if (pantallaCompleta) {
        MaterialTheme.colorScheme.background
    } else {
        Color(0xFFF7F7F7)
    }
    val fondoSeccion = MaterialTheme.colorScheme.surfaceVariant

    val metricaEspaciado = 6.dp
    val humedadValor = if (gps.tieneFijacion) "${clima.humedad}%" else "--"
    val altitudValor = if (gps.tieneFijacion) {
        gps.altitudMetros?.let { String.format(Locale.US, "%.0f m", it) } ?: "--"
    } else {
        "--"
    }
    val metricasGrid = listOf(
        MetricaClimaItem(Icons.Outlined.WaterDrop, "Humedad del Aire", humedadValor),
        MetricaClimaItem(Icons.Outlined.LocationOn, "Altitud", altitudValor),
        MetricaClimaItem(Icons.Outlined.Air, "Velocidad del Viento", vientoVelocidadTexto(clima)),
        MetricaClimaItem(Icons.Outlined.Speed, "Presion Atmosferica", clima.presion),
        MetricaClimaItem(Icons.Outlined.Cloud, "Nubosidad", clima.nubosidad)
    )
    val metricaLluvia = MetricaClimaItem(Icons.Outlined.WaterDrop, "Lluvia", clima.lluvia)

    Card(
        modifier = modifier,
        shape = if (pantallaCompleta) RoundedCornerShape(0.dp) else RoundedCornerShape(14.dp),
        border = if (pantallaCompleta) null else BorderStroke(1.dp, bordeClaro),
        colors = CardDefaults.cardColors(containerColor = fondoTarjeta)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val compacto = maxHeight < 620.dp
            val espaciado = if (compacto) 10.dp else 16.dp
            val paddingHorizontal = if (compacto) 16.dp else 22.dp
            val paddingVertical = if (compacto) 14.dp else 20.dp
            val tamanoBrujula = if (compacto) 88.dp else 110.dp
            val distribucion = if (pantallaCompleta && !compacto) {
                Arrangement.SpaceBetween
            } else {
                Arrangement.spacedBy(espaciado)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(fondoTarjeta)
                    .padding(horizontal = paddingHorizontal, vertical = paddingVertical),
                verticalArrangement = distribucion
            ) {
                EstadoGps(gps, textoPrimario)
                if (esHorizontal) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(espaciado),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(espaciado)
                        ) {
                            SeccionHeroClima(
                                clima = clima,
                                textoPrimario = textoPrimario,
                                textoSecundario = textoSecundario,
                                bordeClaro = bordeClaro,
                                fondoSeccion = fondoSeccion,
                                compacto = compacto
                            )
                            BrujulaViento(
                                grados = clima.direccionVientoGrados,
                                etiqueta = clima.direccionViento,
                                rumboDispositivoGrados = rumboDispositivoGrados,
                                tamano = tamanoBrujula,
                                textoPrimario = textoPrimario,
                                bordeClaro = bordeClaro
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(espaciado)
                        ) {
                            MetricaClimaColumna(metricasGrid, metricaEspaciado, textoPrimario, textoSecundario, bordeClaro)
                            MetricaClima(metricaLluvia, textoPrimario, textoSecundario, bordeClaro, modifier = Modifier.fillMaxWidth())
                        }
                    }
                } else {
                    SeccionHeroClima(
                        clima = clima,
                        textoPrimario = textoPrimario,
                        textoSecundario = textoSecundario,
                        bordeClaro = bordeClaro,
                        fondoSeccion = fondoSeccion,
                        compacto = compacto
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        BrujulaViento(
                            grados = clima.direccionVientoGrados,
                            etiqueta = clima.direccionViento,
                            rumboDispositivoGrados = rumboDispositivoGrados,
                            tamano = tamanoBrujula,
                            textoPrimario = textoPrimario,
                            bordeClaro = bordeClaro
                        )
                    }
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
                                    MetricaClima(item, textoPrimario, textoSecundario, bordeClaro, modifier = Modifier.fillMaxWidth())
                                }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(metricaEspaciado)
                        ) {
                            metricasGrid.filterIndexed { index, _ -> index % 2 != 0 }
                                .forEach { item ->
                                    MetricaClima(item, textoPrimario, textoSecundario, bordeClaro, modifier = Modifier.fillMaxWidth())
                                }
                        }
                    }
                    MetricaClima(metricaLluvia, textoPrimario, textoSecundario, bordeClaro, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun SeccionHeroClima(
    clima: ClimaUi,
    textoPrimario: Color,
    textoSecundario: Color,
    bordeClaro: Color,
    fondoSeccion: Color,
    compacto: Boolean
) {
    val padding = if (compacto) 10.dp else 14.dp
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = fondoSeccion,
        border = BorderStroke(1.dp, bordeClaro)
    ) {
        Column(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EncabezadoClima(clima = clima, textoPrimario = textoPrimario, textoSecundario = textoSecundario)
            TemperaturaActual(
                clima = clima,
                textoPrimario = textoPrimario,
                textoSecundario = textoSecundario,
                tamanoTexto = if (compacto) 30.sp else 36.sp
            )
        }
    }
}

@Composable
private fun EncabezadoClima(
    clima: ClimaUi,
    textoPrimario: Color,
    textoSecundario: Color,
    modifier: Modifier = Modifier
) {
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
                    text = clima.ubicacion.ifBlank { "Ubicacion actual" },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = textoPrimario
                )
                Text(
                    text = clima.condicion.ifBlank { "Cargando clima..." },
                    style = MaterialTheme.typography.bodyLarge,
                    color = textoSecundario,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TemperaturaActual(
    clima: ClimaUi,
    textoPrimario: Color,
    textoSecundario: Color,
    modifier: Modifier = Modifier,
    tamanoTexto: androidx.compose.ui.unit.TextUnit = 32.sp
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = clima.temperatura.ifBlank { "--°C" },
            color = textoPrimario,
            fontSize = tamanoTexto,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Sensacion ${clima.sensacionTermica.ifBlank { "--" }}",
            color = textoSecundario,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun BrujulaViento(
    grados: Int?,
    etiqueta: String,
    rumboDispositivoGrados: Float,
    tamano: Dp,
    textoPrimario: Color,
    bordeClaro: Color
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
            text = "Direccion del Viento",
            style = MaterialTheme.typography.labelMedium,
            color = textoPrimario
        )
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.Black.copy(alpha = 0.03f),
            border = BorderStroke(1.dp, bordeClaro)
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
                        color = textoPrimario.copy(alpha = 0.12f),
                        radius = radio * 0.96f,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawCircle(
                        color = textoPrimario.copy(alpha = 0.08f),
                        radius = radio * 0.7f,
                        style = Stroke(width = 1.dp.toPx())
                    )
                    val centro = Offset(size.width / 2f, size.height / 2f)
                    val largo = radio * 0.8f
                    val vectorSeguro = if (vector == Offset.Zero) Offset(0f, -1f) else vector
                    val destinoVector = centro + Offset(vectorSeguro.x * largo, vectorSeguro.y * largo)
                    drawLine(
                        color = CianCarreras,
                        start = centro,
                        end = destinoVector,
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
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
                        color = textoPrimario.copy(alpha = 0.4f),
                        start = centro.copy(y = centro.y - radio * 0.95f),
                        end = centro.copy(y = centro.y - radio * 0.8f),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                Text(
                    text = etiqueta.ifBlank { "--" },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = textoPrimario
                )
            }
        }
    }
}

private fun calcularVectorRelativo(vientoGrados: Float, rumboDispositivoGrados: Float): Offset {
    val ajuste = normalizarGrados(vientoGrados + 180f - rumboDispositivoGrados)
    val rad = ajuste / 180f * PI.toFloat()
    val x = sin(rad)
    val y = -cos(rad)
    val magnitud = kotlin.math.sqrt(x * x + y * y).coerceAtLeast(0.0001f)
    return Offset(x / magnitud, y / magnitud)
}

private fun normalizarGrados(angulo: Float): Float {
    var a = angulo % 360f
    if (a < 0) a += 360f
    return a
}

@Composable
private fun MetricaClimaColumna(
    metricas: List<MetricaClimaItem>,
    espaciado: Dp,
    textoPrimario: Color,
    textoSecundario: Color,
    bordeClaro: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(espaciado)) {
        metricas.chunked(2).forEach { fila ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(espaciado)
            ) {
                fila.forEach { item ->
                    MetricaClima(item, textoPrimario, textoSecundario, bordeClaro, modifier = Modifier.weight(1f))
                }
                if (fila.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MetricaClima(
    item: MetricaClimaItem,
    textoPrimario: Color,
    textoSecundario: Color,
    bordeClaro: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.heightIn(min = 64.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = 0.03f),
        border = BorderStroke(1.dp, bordeClaro)
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
                tint = textoPrimario,
                modifier = Modifier.size(20.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = item.etiqueta,
                    style = MaterialTheme.typography.labelLarge,
                    color = textoSecundario
                )
                Text(
                    text = item.valor.ifBlank { "--" },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = textoPrimario
                )
            }
        }
    }
}

@Composable
private fun EstadoGps(gps: GpsUi, textoPrimario: Color) {
    val color = if (gps.tieneFijacion) VerdeCarreras else RojoCarreras
    val precision = if (gps.tieneFijacion && gps.precisionMetros != Float.MAX_VALUE) {
        "±${gps.precisionMetros.roundToInt()} m"
    } else {
        "Sin senal"
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
                .padding(horizontal = 16.dp, vertical = 10.dp),
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
                    text = if (gps.tieneFijacion) "GPS listo. \nPrecision: $precision" else "GPS sin senal",
                    style = MaterialTheme.typography.labelLarge,
                    color = textoPrimario
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

private fun vientoVelocidadTexto(clima: ClimaUi): String {
    return clima.velocidadViento.ifBlank { "--" }
}
