package com.lapmaster.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lapmaster.ui.components.TarjetaResumen
import com.lapmaster.ui.model.EstadoConfiguracionUi
import com.lapmaster.ui.model.EstadoVueltasUi
import com.lapmaster.ui.model.GpsUi
import com.lapmaster.ui.model.PreferenciaMano
import com.lapmaster.ui.model.ResumenUi
import com.lapmaster.ui.model.VueltaPilotoUi
import com.lapmaster.ui.model.ClimaUi
import com.lapmaster.ui.theme.GrisCarreras
import com.lapmaster.ui.theme.RojoCarreras
import com.lapmaster.ui.theme.VerdeCarreras

@Composable
fun PantallaVueltas(
    estado: EstadoVueltasUi,
    clima: ClimaUi,
    gps: GpsUi,
    resumen: ResumenUi,
    configuraciones: EstadoConfiguracionUi,
    alAgregarPiloto: () -> Unit,
    alAlternarCronometro: (Int) -> Unit,
    alMarcarVuelta: (Int) -> Unit,
    alResetearCronometro: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TarjetaResumen(resumen = resumen)
        Spacer(modifier = Modifier.height(6.dp))
        estado.pilotos.chunked(2).forEach { fila ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                fila.forEach { vuelta ->
                    TarjetaVueltaPiloto(
                        vuelta = vuelta,
                        zurdo = configuraciones.preferenciaMano == PreferenciaMano.ZURDO,
                        modifier = Modifier.weight(1f),
                        alAlternarCronometro = alAlternarCronometro,
                        alMarcarVuelta = alMarcarVuelta,
                        alResetearCronometro = alResetearCronometro
                    )
                    if (fila.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        BotonAgregarPiloto(onAgregar = alAgregarPiloto, habilitado = estado.pilotos.size < 4)
        Spacer(modifier = Modifier.height(8.dp))
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
private fun TarjetaVueltaPiloto(
    vuelta: VueltaPilotoUi,
    zurdo: Boolean,
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
            style = MaterialTheme.typography.headlineMedium,
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
                        text = "Marcar Vuelta",
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
            val cajaTiempo = @Composable {
                Card(
                    modifier = Modifier
                        .height(72.dp)
                        .width(130.dp),
                    colors = CardDefaults.cardColors(containerColor = GrisCarreras),
                    shape = RoundedCornerShape(4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = formatearTiempo(vuelta.tiempoMs),
                            fontSize = 30.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
            if (zurdo) {
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

@Composable
private fun BotonAgregarPiloto(onAgregar: () -> Unit, habilitado: Boolean) {
    Button(
        onClick = onAgregar,
        enabled = habilitado,
        colors = ButtonDefaults.buttonColors(
            containerColor = VerdeCarreras,
            contentColor = Color.Black,
            disabledContainerColor = GrisCarreras,
            disabledContentColor = Color.DarkGray
        ),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .width(200.dp)
            .height(48.dp)
    ) {
        Text(text = if (habilitado) "Agregar piloto" else "Límite de 4 pilotos", fontWeight = FontWeight.Bold)
    }
}

private fun formatearTiempo(ms: Long): String {
    if (ms <= 0L) return "--:--.--"
    val totalSegundos = ms / 1000
    val minutos = totalSegundos / 60
    val segundos = totalSegundos % 60
    val centesimas = (ms % 1000) / 10
    return String.format("%d:%02d.%02d", minutos, segundos, centesimas)
}
