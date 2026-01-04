package com.lapmaster

import android.Manifest
import android.os.Bundle
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.content.res.Configuration
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Cloud
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.lapmaster.ui.viewmodels.ModeloVistaLapMaster
import com.lapmaster.ui.model.Pantalla
import com.lapmaster.ui.screens.PantallaGraficas
import com.lapmaster.ui.screens.PantallaClima
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
    private var sensorManager: SensorManager? = null
    private var sensorRotacion: Sensor? = null
    private val matrizRotacion = FloatArray(9)
    private val matrizRotacionAjustada = FloatArray(9)
    private val orientacion = FloatArray(3)

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
            modeloVista.gps.alActualizarEstadoGps(
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
        setContent {
            AplicacionLapMaster(modeloVista = modeloVista)
        }
        inicializarProveedorGps()
        inicializarSensorBrjula()
    }

    override fun onStart() {
        super.onStart()
        solicitarPermisoGpsSiEsNecesario()
    }

    override fun onResume() {
        super.onResume()
        registrarSensorBrjula()
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(listenerBrjula)
    }

    private fun inicializarProveedorGps() {
        proveedorGps = ProveedorGpsNativo(
            context = this,
            owner = this,
            onGpsUpdate = { gps ->
                modeloVista.gps.alActualizarEstadoGps(
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

    private fun inicializarSensorBrjula() {
        sensorManager = getSystemService(SensorManager::class.java)
        sensorRotacion = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    }

    private fun registrarSensorBrjula() {
        val manager = sensorManager ?: return
        val sensor = sensorRotacion ?: return
        manager.registerListener(
            listenerBrjula,
            sensor,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    private val listenerBrjula = object : SensorEventListener {
        @RequiresApi(Build.VERSION_CODES.R)
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) return
            SensorManager.getRotationMatrixFromVector(matrizRotacion, event.values)
            ajustarMatrizPorRotacionPantalla()
            SensorManager.getOrientation(matrizRotacionAjustada, orientacion)
            val azimutRad = orientacion[0]
            val azimutDeg = Math.toDegrees(azimutRad.toDouble()).toFloat()
            modeloVista.gps.alActualizarRumbo(azimutDeg)
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun ajustarMatrizPorRotacionPantalla() {
        // todo reemplazar por una versión no depreciada
        val rotation = windowManager.defaultDisplay?.rotation ?: display?.rotation ?: Surface.ROTATION_0
        when (rotation) {
            Surface.ROTATION_90 -> SensorManager.remapCoordinateSystem(
                matrizRotacion,
                SensorManager.AXIS_Y,
                SensorManager.AXIS_MINUS_X,
                matrizRotacionAjustada
            )
            Surface.ROTATION_180 -> SensorManager.remapCoordinateSystem(
                matrizRotacion,
                SensorManager.AXIS_MINUS_X,
                SensorManager.AXIS_MINUS_Y,
                matrizRotacionAjustada
            )
            Surface.ROTATION_270 -> SensorManager.remapCoordinateSystem(
                matrizRotacion,
                SensorManager.AXIS_MINUS_Y,
                SensorManager.AXIS_X,
                matrizRotacionAjustada
            )
            else -> System.arraycopy(matrizRotacion, 0, matrizRotacionAjustada, 0, matrizRotacion.size)
        }
    }
}

@Composable
private fun AplicacionLapMaster(modeloVista: ModeloVistaLapMaster) {
    val estadoUi by modeloVista.estadoUi.collectAsState()
    val pantallaSeleccionada = estadoUi.pantallaSeleccionada
    val configuracion = LocalConfiguration.current
    val esHorizontal = configuracion.orientation == Configuration.ORIENTATION_LANDSCAPE
    val hayCronometroCorriendo = estadoUi.vueltas.pilotos.any { it.corriendo }
    val ocultarBarra = esHorizontal && pantallaSeleccionada == Pantalla.VUELTAS && hayCronometroCorriendo
    val insetsContenido = if (ocultarBarra) {
        WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
    } else {
        WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
    }

    TemaLapMaster(usarTemaOscuro = estadoUi.configuraciones.temaOscuro) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Scaffold(
                contentWindowInsets = insetsContenido,
                topBar = {
                    if (!ocultarBarra) {
                        BarraMenuSuperior(
                            pantallaSeleccionada = pantallaSeleccionada,
                            alSeleccionar = { modeloVista.navegacion.alSeleccionarPantalla(it) }
                        )
                    }
                }
            ) { rellenoInterno ->
                Box(modifier = Modifier.padding(rellenoInterno)) {
                    when (pantallaSeleccionada) {
                        Pantalla.VUELTAS -> PantallaVueltas(
                            estado = estadoUi.vueltas,
                            configuraciones = estadoUi.configuraciones,
                            mostrarVolverMenu = ocultarBarra,
                            alVolverMenu = { modeloVista.navegacion.alSeleccionarPantalla(Pantalla.MENU) },
                            alAlternarCronometro = { modeloVista.vueltas.alAlternarCronometro(it) },
                            alMarcarVuelta = { modeloVista.vueltas.alMarcarVuelta(it) },
                            alResetearCronometro = { modeloVista.vueltas.alResetearCronometro(it) }
                        )

                        Pantalla.MENU -> PantallaMenu(
                            estado = estadoUi.menu,
                            configuraciones = estadoUi.configuraciones,
                            alAgregarPiloto = { modeloVista.menu.alAgregarPiloto() },
                            alActualizarPilotoNombre = { id, nombre ->
                                modeloVista.menu.alActualizarPilotoNombre(id, nombre)
                            },
                            alActualizarPilotoNumero = { id, numero ->
                                modeloVista.menu.alActualizarPilotoNumero(id, numero)
                            },
                            alActualizarPilotoColor = { id, color ->
                                modeloVista.menu.alActualizarPilotoColor(id, color)
                            },
                            alConfirmarPiloto = { modeloVista.menu.alConfirmarPiloto(it) },
                            alEliminarPiloto = { modeloVista.menu.alEliminarPiloto(it) },
                            alAlternarTema = { modeloVista.menu.alAlternarTema() },
                            alAlternarMano = { modeloVista.menu.alAlternarPreferenciaMano() }
                        )

                        Pantalla.SECTORES -> PantallaSectores(
                            estado = estadoUi.sectores,
                            configuraciones = estadoUi.configuraciones,
                            alIniciarCronometro = { modeloVista.sectores.alIniciarCronometro() },
                            alMarcarSector = { modeloVista.sectores.alMarcarSector(it) },
                            alReiniciarSectores = { modeloVista.sectores.alReiniciarSectores() }
                        )

                        Pantalla.CLIMA -> PantallaClima(
                            clima = estadoUi.clima,
                            gps = estadoUi.gps
                        )

                        // todo cambiar el nombre de la funcion para quitar anio,
                        //  ya no se usa
                        Pantalla.GRAFICAS -> PantallaGraficas(
                            estado = estadoUi.graficas,
                            alSeleccionarAnio = { modeloVista.graficas.alSeleccionarAnio(it) }
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

    val esHorizontal = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val altoTab = if (esHorizontal) 46.dp else 54.dp
    val tamanoTexto = if (esHorizontal) 11.sp else 9.sp
    val tamanoIcono = if (esHorizontal) 19.dp else 18.dp

    val elementos = listOf(
        ElementoNavegacion(Pantalla.MENU, "Menú", Icons.Outlined.Menu),
        ElementoNavegacion(Pantalla.CLIMA, "Clima", Icons.Outlined.Cloud),
        ElementoNavegacion(Pantalla.VUELTAS, "Tiempos", Icons.Outlined.Timer),
        ElementoNavegacion(Pantalla.SECTORES, "Sectores", Icons.Outlined.ListAlt),
        ElementoNavegacion(Pantalla.GRAFICAS, "Gráficos", Icons.Outlined.Analytics)
    )
    val indiceSeleccionado = elementos.indexOfFirst { it.pantalla == pantallaSeleccionada }.coerceAtLeast(0)



    // todo modificar el ancho de los íconos para que permita usar todo el cuadro, tiene mucho padding
    TabRow(
        selectedTabIndex = indiceSeleccionado,
        containerColor = Color(0xFF101010),
        contentColor = Color.White,
        modifier = Modifier
            .statusBarsPadding()
            .height(altoTab),
        divider = {},
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
                modifier = Modifier.height(altoTab),
                selected = seleccionada,
                onClick = { alSeleccionar(elemento.pantalla) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.LightGray,
                text = { Text(elemento.etiqueta, fontSize = tamanoTexto) },
                icon = {
                    Icon(
                        imageVector = elemento.icono,
                        contentDescription = elemento.etiqueta,
                        modifier = Modifier.size(tamanoIcono)
                    )
                }
            )
        }
    }
}
