package com.lapmaster.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.res.Configuration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.lapmaster.ui.components.Cronometro
import com.lapmaster.ui.model.EstadoConfiguracionUi
import com.lapmaster.ui.model.EstadoVueltasUi
import com.lapmaster.ui.model.PreferenciaMano
import com.lapmaster.ui.model.VueltaPilotoUi
import com.lapmaster.ui.theme.VerdeCarreras
import com.lapmaster.ui.theme.RojoCarreras











@Composable
fun PantallaVueltas(
    estado: EstadoVueltasUi,
    configuraciones: EstadoConfiguracionUi,
    mostrarVolverMenu: Boolean,
    alVolverMenu: () -> Unit,
    alAlternarCronometro: (Int) -> Unit,
    alMarcarVuelta: (Int) -> Unit,
    alResetearCronometro: (Int) -> Unit,
    alTerminarTanda: () -> Unit
) {
    val esHorizontal = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val columnas = if (esHorizontal) 2 else 1

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val alturaMaxima = maxHeight
        val alturaCompacta = alturaMaxima < 640.dp
        val altoBotonAccion = if (alturaCompacta) 48.dp else 52.dp
        val margenBotonAccion = if (alturaCompacta) 8.dp else 12.dp
        val reservaAcciones = altoBotonAccion + margenBotonAccion
        val paddingHorizontalBase = if (alturaCompacta) 12.dp else 20.dp
        val paddingVerticalBase = if (alturaCompacta) 10.dp else 16.dp
        val cantidadPilotos = estado.pilotos.size.coerceAtLeast(1)
        val alturaDisponible = (alturaMaxima - (paddingVerticalBase * 2) - reservaAcciones).coerceAtLeast(0.dp)
        val alturaPorPiloto = alturaDisponible / cantidadPilotos
        val modoUltraCompacto = !esHorizontal && alturaPorPiloto < 150.dp
        val espacioEntre = when {
            modoUltraCompacto -> 6.dp
            alturaCompacta -> 8.dp
            else -> 12.dp
        }
        val paddingHorizontal = if (modoUltraCompacto) 10.dp else paddingHorizontalBase
        val paddingVertical = if (modoUltraCompacto) 8.dp else paddingVerticalBase
        val paddingInferior = paddingVertical + reservaAcciones

        Box(modifier = Modifier.fillMaxSize()) {
            if (esHorizontal) {
                val filas = estado.pilotos.chunked(columnas)
                val alturaDisponibleHorizontal = (alturaMaxima - reservaAcciones).coerceAtLeast(0.dp)
                val alturaPorFila = if (filas.isNotEmpty()) alturaDisponibleHorizontal / filas.size else alturaDisponibleHorizontal
                val alturaCompactaHorizontal = alturaPorFila < 230.dp
                val usarPesoFilas = filas.size > 1

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = paddingHorizontal,
                            end = paddingHorizontal,
                            top = paddingVertical,
                            bottom = paddingInferior
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(espacioEntre)
                ) {
                    filas.forEach { fila ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(if (usarPesoFilas) Modifier.weight(1f, fill = true) else Modifier.wrapContentHeight()),
                            horizontalArrangement = Arrangement.spacedBy(espacioEntre),
                            verticalAlignment = Alignment.Top
                        ) {
                            fila.forEachIndexed { indice, vuelta ->
                                val columnaDerecha = indice == columnas - 1
                                TarjetaVueltaPiloto(
                                    vuelta = vuelta,
                                    preferenciaMano = configuraciones.preferenciaMano,
                                    modoCompacto = alturaCompactaHorizontal,
                                    modoUltraCompacto = false,
                                    columnaDerecha = columnaDerecha,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    alAlternarCronometro = alAlternarCronometro,
                                    alMarcarVuelta = alMarcarVuelta,
                                    alResetearCronometro = alResetearCronometro
                                )
                            }
                            if (fila.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            } else {
                val modoCompactoTarjeta = alturaCompacta || modoUltraCompacto
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = paddingHorizontal,
                            end = paddingHorizontal,
                            top = paddingVertical,
                            bottom = paddingInferior
                        ),
                    verticalArrangement = Arrangement.spacedBy(espacioEntre),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    estado.pilotos.forEach { vuelta ->
                        TarjetaVueltaPiloto(
                            vuelta = vuelta,
                            preferenciaMano = configuraciones.preferenciaMano,
                            modoCompacto = modoCompactoTarjeta,
                            modoUltraCompacto = modoUltraCompacto,
                            columnaDerecha = false,
                            modifier = Modifier.fillMaxWidth(),
                            alAlternarCronometro = alAlternarCronometro,
                            alMarcarVuelta = alMarcarVuelta,
                            alResetearCronometro = alResetearCronometro
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 0.dp)
                        .padding(bottom = margenBotonAccion),
                    horizontalArrangement = if (mostrarVolverMenu) Arrangement.spacedBy(12.dp) else Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = alTerminarTanda,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RojoCarreras,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = if (mostrarVolverMenu) {
                            Modifier
                                .weight(1f)
                                .height(altoBotonAccion)
                        } else {
                            Modifier
                                .width(220.dp)
                                .height(altoBotonAccion)
                        }
                    ) {
                        Text(text = "Terminar tanda", fontWeight = FontWeight.Bold)
                    }

                    if (mostrarVolverMenu) {
                        Button(
                            onClick = alVolverMenu,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VerdeCarreras,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(altoBotonAccion)
                        ) {
                            Text(text = "Volver al menu", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaVueltaPiloto(
    vuelta: VueltaPilotoUi,
    preferenciaMano: PreferenciaMano,
    alAlternarCronometro: (Int) -> Unit,
    alMarcarVuelta: (Int) -> Unit,
    alResetearCronometro: (Int) -> Unit,
    modoCompacto: Boolean,
    modoUltraCompacto: Boolean,
    columnaDerecha: Boolean,
    modifier: Modifier = Modifier
) {
    val paddingCard = when {
        modoUltraCompacto -> 2.dp
        modoCompacto -> 4.dp
        else -> 8.dp
    }
    BoxWithConstraints(modifier = modifier.padding(paddingCard)) {
        val anchoMinimoFila = if (modoCompacto) 250.dp else 300.dp
        val layoutVertical = maxWidth < anchoMinimoFila
        val espacioVertical = when {
            modoUltraCompacto -> 2.dp
            modoCompacto -> 4.dp
            else -> 8.dp
        }
        val alturaBoton = when {
            modoUltraCompacto -> 44.dp
            modoCompacto -> 60.dp
            else -> 88.dp
        }
        val alturaTiempo = when {
            modoUltraCompacto -> 36.dp
            modoCompacto -> 48.dp
            else -> 72.dp
        }
        val anchoBoton = when {
            modoUltraCompacto -> 96.dp
            modoCompacto -> 112.dp
            else -> 144.dp
        }
        val anchoMinTiempo = when {
            modoUltraCompacto -> 110.dp
            modoCompacto -> 130.dp
            else -> 170.dp
        }
        val tamanoTiempo = when {
            layoutVertical && modoUltraCompacto -> 16.sp
            layoutVertical -> 20.sp
            modoUltraCompacto -> 18.sp
            modoCompacto -> 24.sp
            else -> 30.sp
        }
        val etiquetaBoton = if (vuelta.corriendo) {
            if (layoutVertical || modoCompacto) "Vuelta" else "Marcar vuelta"
        } else {
            "Iniciar"
        }
        val etiquetaPausa = if (vuelta.corriendo) {
            if (modoCompacto) "Pausa" else "Pausar"
        } else {
            if (modoCompacto) "Seguir" else "Continuar"
        }
        val estiloTitulo = when {
            modoUltraCompacto -> MaterialTheme.typography.titleSmall
            modoCompacto -> MaterialTheme.typography.titleMedium
            else -> MaterialTheme.typography.headlineSmall
        }
        val textoBoton = when {
            modoUltraCompacto -> 11.sp
            modoCompacto -> 12.sp
            else -> 14.sp
        }
        val espacioHorizontal = when {
            modoUltraCompacto -> 6.dp
            modoCompacto -> 8.dp
            else -> 12.dp
        }
        val distribucionVertical = if (modoCompacto) {
            Arrangement.spacedBy(espacioVertical)
        } else {
            Arrangement.SpaceBetween
        }
        val impar = vuelta.piloto.id % 2 != 0
        val botonPrimero = if (preferenciaMano == PreferenciaMano.ZURDO) !impar else impar

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = distribucionVertical,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val nombre = @Composable {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(vuelta.piloto.nombre)
                            }
                            append(" #")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(vuelta.piloto.numero)
                            }
                        },
                        style = estiloTitulo,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = if (modoCompacto) 1 else 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = if (columnaDerecha) TextAlign.End else TextAlign.Start,
                        modifier = Modifier.weight(1f)
                    )
                }

                val vueltas = @Composable {
                    Text(
                        text = "Vueltas: ${vuelta.vueltas}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                if (columnaDerecha) {
                    vueltas()
                    nombre()
                } else {
                    nombre()
                    vueltas()
                }
            }
            Spacer(modifier = Modifier.height(espacioVertical))
            if (layoutVertical) {
                Button(
                    onClick = {
                        if (vuelta.corriendo) alMarcarVuelta(vuelta.piloto.id) else alAlternarCronometro(vuelta.piloto.id)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(vuelta.piloto.color),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(alturaBoton)
                ) {
                    Text(text = etiquetaBoton, fontSize = textoBoton, textAlign = TextAlign.Center)
                }
                Spacer(modifier = Modifier.height(espacioVertical))
                Cronometro(
                    tiempoMs = vuelta.tiempoMs,
                    modifier = Modifier.fillMaxWidth(),
                    colorTexto = MaterialTheme.colorScheme.onBackground,
                    alto = alturaTiempo,
                    ancho = null,
                    tamanoTexto = tamanoTiempo
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(espacioHorizontal)
                ) {
                    val boton = @Composable {
                        Button(
                            onClick = {
                                if (vuelta.corriendo) alMarcarVuelta(vuelta.piloto.id) else alAlternarCronometro(vuelta.piloto.id)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(vuelta.piloto.color),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .height(alturaBoton)
                                .width(anchoBoton)
                        ) {
                            Text(text = etiquetaBoton, fontSize = textoBoton, textAlign = TextAlign.Center)
                        }
                    }
                    val cajaTiempo = @Composable {
                        Cronometro(
                            tiempoMs = vuelta.tiempoMs,
                            modifier = Modifier
                                .weight(1f)
                                .widthIn(min = anchoMinTiempo),
                            colorTexto = MaterialTheme.colorScheme.onBackground,
                            alto = alturaTiempo,
                            ancho = null,
                            tamanoTexto = tamanoTiempo
                        )
                    }

                    if (botonPrimero) {
                        boton()
                        cajaTiempo()
                    } else {
                        cajaTiempo()
                        boton()
                    }
                }
            }
            Spacer(modifier = Modifier.height(espacioVertical))
            if (layoutVertical) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { alAlternarCronometro(vuelta.piloto.id) },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                        modifier = Modifier.heightIn(min = if (modoUltraCompacto) 22.dp else if (modoCompacto) 28.dp else 32.dp)
                    ) {
                        Text(text = etiquetaPausa, fontSize = textoBoton)
                    }
                }
            } else {
                val accionBoton = @Composable {
                    TextButton(
                        onClick = { alAlternarCronometro(vuelta.piloto.id) },
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                        modifier = Modifier
                            .width(anchoBoton)
                            .heightIn(min = if (modoUltraCompacto) 22.dp else if (modoCompacto) 26.dp else 30.dp)
                    ) {
                        Text(
                            text = etiquetaPausa,
                            fontSize = textoBoton,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(espacioHorizontal),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    accionBoton()

                }
            }
        }
    }
}
