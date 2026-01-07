package com.lapmaster.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Brightness4
import androidx.compose.material.icons.outlined.Brightness7
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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

private const val PREFS_NAME = "lapmaster_prefs"
private const val PREF_PRIMER_USO = "primer_uso_visto"
private const val PREF_PRIMER_USO_VERSION_KEY = "primer_uso_version"
private const val PREF_PRIMER_USO_VERSION = 2


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
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    val mostrarInicial = remember(prefs) {
        deberiaMostrarPrimerUso(prefs)
    }
    var mostrarPrimerUso by remember {
        mutableStateOf(mostrarInicial)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val compacto = maxHeight < 700.dp
        val paddingHorizontal = if (compacto) 16.dp else 20.dp
        val espacioSecciones = if (compacto) 20.dp else 36.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = paddingHorizontal, end = paddingHorizontal)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(espacioSecciones),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (mostrarPrimerUso) {
                PrimerUsoDialog(
                    modoCompacto = compacto,
                    onCerrar = {
                        prefs.edit().putBoolean(PREF_PRIMER_USO, true).apply()
                        mostrarPrimerUso = false
                    }
                )
            }
            estado.acciones.forEach { accion ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (compacto) 8.dp else 12.dp)
                ) {
                    BotonMenu(accion)
                }
            }
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                ListaPilotos(
                    pilotos = estado.pilotos,
                    onAgregarPiloto = alAgregarPiloto,
                    onActualizarPilotoNombre = alActualizarPilotoNombre,
                    onActualizarPilotoNumero = alActualizarPilotoNumero,
                    onActualizarPilotoColor = alActualizarPilotoColor,
                    onConfirmarPiloto = alConfirmarPiloto,
                    onEliminarPiloto = alEliminarPiloto,
                    puedeAgregarMas = estado.pilotos.size < 4,
                    modoCompacto = compacto
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                FilaConfiguraciones(
                    configuraciones = configuraciones,
                    alAlternarTema = alAlternarTema,
                    alAlternarMano = alAlternarMano,
                    modoCompacto = compacto
                )
            }
        }
    }
}

@Composable
private fun PrimerUsoDialog(
    modoCompacto: Boolean,
    onCerrar: () -> Unit
) {
    val paddingCard = if (modoCompacto) 16.dp else 20.dp
    val espacio = if (modoCompacto) 10.dp else 14.dp
    val altoBoton = if (modoCompacto) 40.dp else 44.dp
    val anchoDialogo = if (modoCompacto) 0.92f else 0.88f

    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(anchoDialogo)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(VerdeCarreras)
                )
                Column(
                    modifier = Modifier.padding(paddingCard),
                    verticalArrangement = Arrangement.spacedBy(espacio)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = VerdeCarreras.copy(alpha = 0.18f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Flag,
                                contentDescription = null,
                                tint = VerdeCarreras,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                        ) {
                            Text(
                                text = "LapMaster en resumen",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Lo que ya puede hacer la app",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        IconButton(onClick = onCerrar) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Cerrar"
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .heightIn(max = if (modoCompacto) 220.dp else 260.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            BulletItem("Vueltas multi‑piloto (hasta 4): iniciar/pausar, marcar, reset.")
                            BulletItem("Sectores (3) con tiempo total y último tiempo.")
                            BulletItem("Gestión de pilotos: nombre, número, color, confirmar, eliminar.")
                            BulletItem("Gráficos por tanda con grilla y leyenda por piloto.")
                            BulletItem("Finalizar tanda y abrir una nueva automáticamente.")
                            BulletItem("Clima full‑screen con métricas y brújula de viento.")
                            BulletItem("GPS: estado, precisión, rumbo y ubicación.")
                            BulletItem("Tema claro/oscuro y preferencia diestro/zurdo.")
                            BulletItem("Navegación por pestañas y layout adaptable.")
                            BulletItem("Pantalla siempre encendida con cronómetros activos.")
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Proximos pasos:",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Comentarios y sugerencias se solicitaran el 12 de enero por medio de un formulario enviado a tu correo. ",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Button(
                        onClick = onCerrar,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdeCarreras,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(altoBoton)
                    ) {
                        Text(text = "Listo", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun BulletItem(texto: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(VerdeCarreras, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Unit
        }
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun deberiaMostrarPrimerUso(prefs: android.content.SharedPreferences): Boolean {
    val versionGuardada = prefs.getInt(PREF_PRIMER_USO_VERSION_KEY, 0)
    if (versionGuardada < PREF_PRIMER_USO_VERSION) {
        prefs.edit()
            .putBoolean(PREF_PRIMER_USO, false)
            .putInt(PREF_PRIMER_USO_VERSION_KEY, PREF_PRIMER_USO_VERSION)
            .apply()
    }
    return !prefs.getBoolean(PREF_PRIMER_USO, false)
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
    puedeAgregarMas: Boolean,
    modoCompacto: Boolean
) {
    val paddingCard = if (modoCompacto) 10.dp else 12.dp
    val espacioCard = if (modoCompacto) 8.dp else 12.dp
    val pilotosOrdenados = pilotos.sortedWith(
        compareBy<PilotoUi> { it.confirmado }.thenByDescending { it.id }
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(paddingCard),
            verticalArrangement = Arrangement.spacedBy(espacioCard)
        ) {
            Text(text = "Pilotos activos", style = MaterialTheme.typography.titleMedium)
            BotonAgregarPilotoMenu(
                onAgregarPiloto = onAgregarPiloto,
                habilitado = puedeAgregarMas,
                modoCompacto = modoCompacto
            )
            pilotosOrdenados.forEach { piloto ->
                EditorPiloto(
                    piloto = piloto,
                    onActualizarNombre = { onActualizarPilotoNombre(piloto.id, it) },
                    onActualizarNumero = { onActualizarPilotoNumero(piloto.id, it) },
                    onActualizarColor = { onActualizarPilotoColor(piloto.id, it) },
                    onConfirmar = { onConfirmarPiloto(piloto.id) },
                    onEliminar = { onEliminarPiloto(piloto.id) },
                    modoCompacto = modoCompacto
                )
            }
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
    onEliminar: () -> Unit,
    modoCompacto: Boolean
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
    val altoBoton = if (modoCompacto) 32.dp else 36.dp
    val altoCampo = if (modoCompacto) 48.dp else 60.dp
    val espacioInterno = if (modoCompacto) 6.dp else 10.dp
    val tamanoColor = if (modoCompacto) 16.dp else 18.dp
    val paddingBoton = if (modoCompacto) {
        PaddingValues(horizontal = 10.dp, vertical = 0.dp)
    } else {
        PaddingValues(horizontal = 12.dp, vertical = 2.dp)
    }
    val textoBoton = if (modoCompacto) 12.sp else 14.sp

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (modoCompacto) 8.dp else 10.dp),
            verticalArrangement = Arrangement.spacedBy(espacioInterno)
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
                    contentPadding = paddingBoton,
                    modifier = Modifier.height(altoBoton)
                ) {
                    Text(text = textoAccion, fontSize = textoBoton)
                }
                TextButton(
                    onClick = onEliminar,
                    modifier = Modifier.heightIn(min = altoBoton)
                ) {
                    Text(text = "Eliminar", color = RojoCarreras, fontWeight = FontWeight.SemiBold)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = altoCampo),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(tamanoColor)
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
                            .heightIn(min = altoCampo)
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
                            .heightIn(min = altoCampo)
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
            if (editandoNombre) {
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
                            onClick = { onActualizarColor(color) },
                            modoCompacto = modoCompacto
                        )
                    }
                }
            }
        }
    }
}

// todo quitar colores, dejar 7 colores
@Composable
private fun ChipColorPiloto(
    color: Long,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modoCompacto: Boolean
) {
    val borde = if (seleccionado) MaterialTheme.colorScheme.onSurface else Color.Transparent
    val tamano = if (modoCompacto) 22.dp else 26.dp
    Box(
        modifier = Modifier
            .size(tamano)
            .background(Color(color), RoundedCornerShape(6.dp))
            .border(2.dp, borde, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
    )
}

@Composable
private fun BotonAgregarPilotoMenu(
    onAgregarPiloto: () -> Unit,
    habilitado: Boolean,
    modoCompacto: Boolean
) {
    val altoBoton = if (modoCompacto) 44.dp else 48.dp
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
            .height(altoBoton)
    ) {
        Text(text = if (habilitado) "Agregar piloto" else "Limite de 4 pilotos", fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FilaConfiguraciones(
    configuraciones: EstadoConfiguracionUi,
    alAlternarTema: () -> Unit,
    alAlternarMano: () -> Unit,
    modoCompacto: Boolean
) {
    val paddingCard = if (modoCompacto) 10.dp else 12.dp
    val espacioCard = if (modoCompacto) 8.dp else 12.dp
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingCard),
            verticalArrangement = Arrangement.spacedBy(espacioCard)
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
                    text = "Por Implementar\nBotones para ${if (configuraciones.preferenciaMano == PreferenciaMano.ZURDO) "zurdos" else "diestros"}"
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
        }
    }
}
