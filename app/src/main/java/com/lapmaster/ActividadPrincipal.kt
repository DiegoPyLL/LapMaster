package com.lapmaster

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.lapmaster.ui.ModeloVistaLapMaster
import com.lapmaster.ui.model.Pantalla
import com.lapmaster.ui.screens.PantallaGraficas
import com.lapmaster.ui.screens.PantallaMenu
import com.lapmaster.ui.screens.PantallaSectores
import com.lapmaster.ui.screens.PantallaVueltas
import com.lapmaster.ui.theme.TemaLapMaster
import com.lapmaster.ui.theme.VerdeCarrerasOscuro

private data class ElementoNavegacion(
    val pantalla: Pantalla,
    val etiqueta: String,
    val icono: ImageVector
)

class ActividadPrincipal : ComponentActivity() {
    private val modeloVista: ModeloVistaLapMaster by viewModels()
    private lateinit var proveedorGps: ProveedorGpsNativo

    private val permisosUbicacion = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val solicitudPermisoGps = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultados ->
        val concedido = resultados[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            resultados[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (concedido) {
            proveedorGps.iniciarSiPermiso()
        } else {
            modeloVista.alActualizarEstadoGps(
                tieneFijacion = false,
                precisionMetros = Float.MAX_VALUE,
                latitud = null,
                longitud = null
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = android.graphics.Color.BLACK
        inicializarProveedorGps()
        solicitarPermisoGpsSiEsNecesario()
        setContent {
            AplicacionLapMaster(modeloVista = modeloVista)
        }
    }

    private fun inicializarProveedorGps() {
        proveedorGps = ProveedorGpsNativo(
            context = this,
            owner = this,
            onGpsUpdate = { gps ->
                modeloVista.alActualizarEstadoGps(
                    gps.tieneFijacion,
                    gps.precisionMetros,
                    gps.latitud,
                    gps.longitud
                )
            }
        )
    }

    private fun solicitarPermisoGpsSiEsNecesario() {
        if (proveedorGps.tienePermisoUbicacion()) {
            proveedorGps.iniciarSiPermiso()
        } else {
            solicitudPermisoGps.launch(permisosUbicacion)
        }
    }
}

@Composable
private fun AplicacionLapMaster(modeloVista: ModeloVistaLapMaster) {
    val estadoUi by modeloVista.estadoUi.collectAsState()
    val pantallaSeleccionada = estadoUi.pantallaSeleccionada

    TemaLapMaster(usarTemaOscuro = estadoUi.configuraciones.temaOscuro) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Scaffold(
                topBar = {
                    BarraMenuSuperior(
                        pantallaSeleccionada = pantallaSeleccionada,
                        alSeleccionar = { modeloVista.alSeleccionarPantalla(it) }
                    )
                }
            ) { rellenoInterno ->
                Box(modifier = Modifier.padding(rellenoInterno)) {
                    when (pantallaSeleccionada) {
                        Pantalla.VUELTAS -> PantallaVueltas(
                            estado = estadoUi.vueltas,
                            clima = estadoUi.clima,
                            gps = estadoUi.gps,
                            resumen = estadoUi.resumen,
                            configuraciones = estadoUi.configuraciones,
                            alAgregarPiloto = { modeloVista.alAgregarPiloto() }
                        )

                        Pantalla.MENU -> PantallaMenu(
                            estado = estadoUi.menu,
                            clima = estadoUi.clima,
                            gps = estadoUi.gps,
                            configuraciones = estadoUi.configuraciones,
                            alAgregarPiloto = { modeloVista.alAgregarPiloto() },
                            alAlternarTema = { modeloVista.alAlternarTema() },
                            alAlternarMano = { modeloVista.alAlternarPreferenciaMano() }
                        )

                        Pantalla.SECTORES -> PantallaSectores(
                            estado = estadoUi.sectores,
                            configuraciones = estadoUi.configuraciones
                        )

                        Pantalla.GRAFICAS -> PantallaGraficas(
                            estado = estadoUi.graficas,
                            alSeleccionarAnio = { modeloVista.alSeleccionarAnio(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BarraMenuSuperior(
    pantallaSeleccionada: Pantalla,
    alSeleccionar: (Pantalla) -> Unit
) {
    val elementos = listOf(
        ElementoNavegacion(Pantalla.MENU, "Menú", Icons.Outlined.Menu),
        ElementoNavegacion(Pantalla.VUELTAS, "Tiempos", Icons.Outlined.Timer),
        ElementoNavegacion(Pantalla.SECTORES, "Sectores", Icons.Outlined.ListAlt),
        ElementoNavegacion(Pantalla.GRAFICAS, "Gráficos", Icons.Outlined.Analytics)
    )
    val indiceSeleccionado = elementos.indexOfFirst { it.pantalla == pantallaSeleccionada }.coerceAtLeast(0)

    TabRow(
        selectedTabIndex = indiceSeleccionado,
        containerColor = Color(0xFF101010),
        contentColor = Color.White,
        modifier = Modifier.statusBarsPadding(),
        indicator = { posicionesPestanas ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(posicionesPestanas[indiceSeleccionado]),
                color = VerdeCarrerasOscuro
            )
        }
    ) {
        elementos.forEach { elemento ->
            val seleccionada = elemento.pantalla == pantallaSeleccionada
            Tab(
                selected = seleccionada,
                onClick = { alSeleccionar(elemento.pantalla) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.LightGray,
                text = { Text(elemento.etiqueta, fontSize = 13.sp) },
                icon = { Icon(imageVector = elemento.icono, contentDescription = elemento.etiqueta) }
            )
        }
    }
}
