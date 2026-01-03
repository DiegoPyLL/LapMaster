package com.lapmaster.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.res.Configuration
import com.lapmaster.ui.components.Cronometro
import com.lapmaster.ui.model.EstadoConfiguracionUi
import com.lapmaster.ui.model.EstadoVueltasUi
import com.lapmaster.ui.model.PreferenciaMano
import com.lapmaster.ui.model.VueltaPilotoUi
import com.lapmaster.ui.theme.RojoCarreras
import com.lapmaster.ui.theme.VerdeCarreras









// todo hacer que en vertical aparezcan todos en vertical, una sola columna para todo

@Composable
fun PantallaVueltas(
    estado: EstadoVueltasUi,
    configuraciones: EstadoConfiguracionUi,
    mostrarVolverMenu: Boolean,
    alVolverMenu: () -> Unit,
    alAlternarCronometro: (Int) -> Unit,
    alMarcarVuelta: (Int) -> Unit,
    alResetearCronometro: (Int) -> Unit
) {
    val esHorizontal = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))
            if (esHorizontal) {
                estado.pilotos.chunked(2).forEach { fila ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        fila.forEach { vuelta ->
                            TarjetaVueltaPiloto(
                                vuelta = vuelta,
                                preferenciaMano = configuraciones.preferenciaMano,
                                modifier = Modifier.weight(1f),
                                alAlternarCronometro = alAlternarCronometro,
                                alMarcarVuelta = alMarcarVuelta,
                                alResetearCronometro = alResetearCronometro
                            )
                            if (fila.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            } else {
                estado.pilotos.forEach { vuelta ->
                    TarjetaVueltaPiloto(
                        vuelta = vuelta,
                        preferenciaMano = configuraciones.preferenciaMano,
                        modifier = Modifier.fillMaxWidth(),
                        alAlternarCronometro = alAlternarCronometro,
                        alMarcarVuelta = alMarcarVuelta,
                        alResetearCronometro = alResetearCronometro
                    )
                }
            }
        }

        if (mostrarVolverMenu) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(
                    onClick = alVolverMenu,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdeCarreras,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(200.dp)
                        .height(56.dp)
                ) {
                    Text(text = "Volver al menu", fontWeight = FontWeight.Bold)
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "${vuelta.piloto.nombre} #${vuelta.piloto.numero}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val boton = @Composable {
                val enMarcha = vuelta.corriendo
                val etiquetaBoton = if (enMarcha) "Marcar vuelta" else "Iniciar"

                Button(
                    onClick = {
                        if (enMarcha) alMarcarVuelta(vuelta.piloto.id) else alAlternarCronometro(vuelta.piloto.id)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(vuelta.piloto.color),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .height(72.dp)
                        .width(120.dp)
                ) {
                    Text(
                        text = etiquetaBoton,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
            val cajaTiempo = @Composable {
                Cronometro(
                    tiempoMs = vuelta.tiempoMs,
                    modifier = Modifier
                        .height(72.dp)
                        .width(130.dp),
                    colorTexto = MaterialTheme.colorScheme.onBackground
                )
            }

            val impar = vuelta.piloto.id % 2 != 0
            val botonPrimero = if (preferenciaMano == PreferenciaMano.ZURDO) !impar else impar
            if (botonPrimero) {
                boton()
                cajaTiempo()
            } else {
                cajaTiempo()
                boton()
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { alAlternarCronometro(vuelta.piloto.id) }) {
                Text(text = if (vuelta.corriendo) "Pausar" else "Continuar")
            }
            TextButton(onClick = { alResetearCronometro(vuelta.piloto.id) }) {
                Text(text = "Resetear")
            }
        }
    }
}
