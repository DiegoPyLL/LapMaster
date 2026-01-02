package com.lapmaster.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lapmaster.ui.model.AccionMenuUi
import com.lapmaster.ui.model.EstadoConfiguracionUi
import com.lapmaster.ui.model.EstadoMenuUi
import com.lapmaster.ui.model.PilotoUi
import com.lapmaster.ui.model.PreferenciaMano
import com.lapmaster.ui.model.paletaPilotos
import com.lapmaster.ui.theme.AmarilloCarreras
import com.lapmaster.ui.theme.GrisCarreras
import com.lapmaster.ui.theme.RojoCarreras
import com.lapmaster.ui.theme.VerdeCarreras

@Composable
fun PantallaMenu(
    estado: EstadoMenuUi,
    configuraciones: EstadoConfiguracionUi,
    alAgregarPiloto: () -> Unit,
    alActualizarPilotoNombre: (Int, String) -> Unit,
    alActualizarPilotoNumero: (Int, String) -> Unit,
    alActualizarPilotoColor: (Int, Long) -> Unit,
    alConfirmarPiloto: (Int) -> Unit,
    alEliminarPiloto: (Int) -> Unit,
    alAlternarTema: () -> Unit,
    alAlternarMano: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(36.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
        estado.acciones.forEach { accion ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                BotonMenu(accion)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            ListaPilotos(
                pilotos = estado.pilotos,
                onAgregarPiloto = alAgregarPiloto,
                onActualizarPilotoNombre = alActualizarPilotoNombre,
                onActualizarPilotoNumero = alActualizarPilotoNumero,
                onActualizarPilotoColor = alActualizarPilotoColor,
                onConfirmarPiloto = alConfirmarPiloto,
                onEliminarPiloto = alEliminarPiloto,
                puedeAgregarMas = estado.pilotos.size < 4
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            FilaConfiguraciones(
                configuraciones = configuraciones,
                alAlternarTema = alAlternarTema,
                alAlternarMano = alAlternarMano
            )
        }
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
    onActualizarPilotoNombre: (Int, String) -> Unit,
    onActualizarPilotoNumero: (Int, String) -> Unit,
    onActualizarPilotoColor: (Int, Long) -> Unit,
    onConfirmarPiloto: (Int) -> Unit,
    onEliminarPiloto: (Int) -> Unit,
    puedeAgregarMas: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Pilotos activos", style = MaterialTheme.typography.titleMedium)
            pilotos.forEach { piloto ->
                EditorPiloto(
                    piloto = piloto,
                    onActualizarNombre = { onActualizarPilotoNombre(piloto.id, it) },
                    onActualizarNumero = { onActualizarPilotoNumero(piloto.id, it) },
                    onActualizarColor = { onActualizarPilotoColor(piloto.id, it) },
                    onConfirmar = { onConfirmarPiloto(piloto.id) },
                    onEliminar = { onEliminarPiloto(piloto.id) }
                )
            }
            BotonAgregarPilotoMenu(onAgregarPiloto = onAgregarPiloto, habilitado = puedeAgregarMas)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorPiloto(
    piloto: PilotoUi,
    onActualizarNombre: (String) -> Unit,
    onActualizarNumero: (String) -> Unit,
    onActualizarColor: (Long) -> Unit,
    onConfirmar: () -> Unit,
    onEliminar: () -> Unit
) {
    val titulo = if (piloto.confirmado && piloto.nombre.isNotBlank()) {
        piloto.nombre
    } else {
        "Piloto ${piloto.id}"
    }
    val puedeConfirmar = piloto.nombre.isNotBlank()
    var editandoNombre by remember(piloto.id) { mutableStateOf(!piloto.confirmado) }
    val requiereConfirmacion = editandoNombre || !piloto.confirmado
    val textoAccion = if (requiereConfirmacion) "Confirmar" else "Editar"
    val habilitarAccion = if (requiereConfirmacion) puedeConfirmar else true

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (requiereConfirmacion) {
                            onConfirmar()
                            editandoNombre = false
                        } else {
                            editandoNombre = true
                        }
                    },
                    enabled = habilitarAccion,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdeCarreras,
                        contentColor = Color.Black,
                        disabledContainerColor = GrisCarreras,
                        disabledContentColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(text = textoAccion)
                }
                TextButton(onClick = onEliminar) {
                    Text(text = "Eliminar", color = RojoCarreras, fontWeight = FontWeight.SemiBold)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 60.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(Color(piloto.color), RoundedCornerShape(4.dp))
                )
                if (editandoNombre) {
                    OutlinedTextField(
                        value = piloto.numero,
                        onValueChange = { valor ->
                            val filtrado = valor.filter { it.isDigit() }.take(3)
                            onActualizarNumero(filtrado)
                        },
                        label = { Text("Número") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.titleMedium,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = AmarilloCarreras,
                            cursorColor = AmarilloCarreras
                        ),
                        modifier = Modifier
                            .weight(0.4f)
                            .widthIn(min = 0.dp)
                            .heightIn(min = 60.dp)
                    )
                    OutlinedTextField(
                        value = piloto.nombre,
                        onValueChange = { valor ->
                            onActualizarNombre(valor.take(30))
                        },
                        label = { Text("Nombre") },
                        placeholder = { Text("Nombre del piloto") },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleMedium,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = VerdeCarreras,
                            cursorColor = VerdeCarreras
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(min = 0.dp)
                            .heightIn(min = 60.dp)
                    )
                } else {
                    Text(
                        text = piloto.numero.ifBlank { "---" },
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(0.4f)
                            .clickable { editandoNombre = true }
                    )
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { editandoNombre = true }
                    )
                }

            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Color", style = MaterialTheme.typography.labelLarge)
                paletaPilotos.forEach { color ->
                    ChipColorPiloto(
                        color = color,
                        seleccionado = piloto.color == color,
                        onClick = { onActualizarColor(color) }
                    )
                }
            }
        }
    }
}


@Composable
private fun ChipColorPiloto(
    color: Long,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val borde = if (seleccionado) MaterialTheme.colorScheme.onSurface else Color.Transparent
    Box(
        modifier = Modifier
            .size(26.dp)
            .background(Color(color), RoundedCornerShape(6.dp))
            .border(2.dp, borde, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
    )
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
        Text(text = if (habilitado) "Agregar piloto" else "Limite de 4 pilotos", fontWeight = FontWeight.Bold)
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
            Text(text = "Configuraciones rapidas", style = MaterialTheme.typography.titleMedium)
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
                text = "Orientacion: ${configuraciones.preferenciaOrientacion.name.lowercase().replaceFirstChar { it.uppercase() }} (vertical u horizontal)",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
