package com.lapmaster.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Brightness4
import androidx.compose.material.icons.outlined.Brightness7
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lapmaster.ui.components.BannerClima
import com.lapmaster.ui.model.AccionMenuUi
import com.lapmaster.ui.model.ClimaUi
import com.lapmaster.ui.model.EstadoConfiguracionUi
import com.lapmaster.ui.model.EstadoMenuUi
import com.lapmaster.ui.model.GpsUi
import com.lapmaster.ui.model.PilotoUi
import com.lapmaster.ui.model.PreferenciaMano
import com.lapmaster.ui.theme.AmarilloCarreras
import com.lapmaster.ui.theme.GrisCarreras
import com.lapmaster.ui.theme.VerdeCarreras

@Composable
fun PantallaMenu(
    estado: EstadoMenuUi,
    clima: ClimaUi,
    gps: GpsUi,
    configuraciones: EstadoConfiguracionUi,
    alAgregarPiloto: () -> Unit,
    alAlternarTema: () -> Unit,
    alAlternarMano: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Panel principal",
            style = MaterialTheme.typography.displayLarge
        )
        BannerClima(clima = clima, gps = gps)
        estado.acciones.forEach { accion -> BotonMenu(accion) }
        ListaPilotos(
            pilotos = estado.pilotos,
            onAgregarPiloto = alAgregarPiloto,
            puedeAgregarMas = estado.pilotos.size < 4
        )
        FilaConfiguraciones(
            configuraciones = configuraciones,
            alAlternarTema = alAlternarTema,
            alAlternarMano = alAlternarMano
        )
    }
}

@Composable
private fun BotonMenu(accion: AccionMenuUi) {
    Button(
        onClick = { },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(accion.color),
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(
            text = accion.titulo,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun ListaPilotos(
    pilotos: List<PilotoUi>,
    onAgregarPiloto: () -> Unit,
    puedeAgregarMas: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Pilotos activos", style = MaterialTheme.typography.titleMedium)
            pilotos.forEach { piloto ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(Color(piloto.color), RoundedCornerShape(4.dp))
                    )
                    Column {
                        Text(text = "${piloto.nombre} #${piloto.numero}", fontWeight = FontWeight.Bold)
                        Text(text = "Color asignado", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            BotonAgregarPilotoMenu(onAgregarPiloto = onAgregarPiloto, habilitado = puedeAgregarMas)
        }
    }
}

@Composable
private fun BotonAgregarPilotoMenu(onAgregarPiloto: () -> Unit, habilitado: Boolean) {
    Button(
        onClick = onAgregarPiloto,
        enabled = habilitado,
        colors = ButtonDefaults.buttonColors(
            containerColor = VerdeCarreras,
            contentColor = Color.Black,
            disabledContainerColor = GrisCarreras,
            disabledContentColor = Color.DarkGray
        ),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(text = if (habilitado) "Agregar piloto" else "Límite de 4 pilotos", fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FilaConfiguraciones(
    configuraciones: EstadoConfiguracionUi,
    alAlternarTema: () -> Unit,
    alAlternarMano: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Configuraciones rápidas", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (configuraciones.temaOscuro) Icons.Outlined.Brightness4 else Icons.Outlined.Brightness7,
                        contentDescription = null
                    )
                    Text(text = if (configuraciones.temaOscuro) "Tema oscuro" else "Tema claro")
                }
                Switch(
                    checked = configuraciones.temaOscuro,
                    onCheckedChange = { alAlternarTema() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = VerdeCarreras,
                        checkedTrackColor = VerdeCarreras.copy(alpha = 0.5f)
                    )
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Botones para ${if (configuraciones.preferenciaMano == PreferenciaMano.ZURDO) "zurdos" else "diestros"}"
                )
                Switch(
                    checked = configuraciones.preferenciaMano == PreferenciaMano.ZURDO,
                    onCheckedChange = { alAlternarMano() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AmarilloCarreras,
                        checkedTrackColor = AmarilloCarreras.copy(alpha = 0.6f)
                    )
                )
            }
            Text(
                text = "Orientación: ${configuraciones.preferenciaOrientacion.name.lowercase().replaceFirstChar { it.uppercase() }} (vertical u horizontal)",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
