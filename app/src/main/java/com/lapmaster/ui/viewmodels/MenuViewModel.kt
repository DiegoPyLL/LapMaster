package com.lapmaster.ui.viewmodels

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lapmaster.apis.ClimaApi
import com.lapmaster.apis.ResultadoClima
import com.lapmaster.ui.model.ClimaUi
import com.lapmaster.ui.model.EntradaHistorialUi
import com.lapmaster.ui.model.EstadoAplicacionUi
import com.lapmaster.ui.model.EstadoConfiguracionUi
import com.lapmaster.ui.model.EstadoGraficasUi
import com.lapmaster.ui.model.EstadoMenuUi
import com.lapmaster.ui.model.EstadoSectoresUi
import com.lapmaster.ui.model.EstadoVueltasUi
import com.lapmaster.ui.model.GpsUi
import com.lapmaster.ui.model.Pantalla
import com.lapmaster.ui.model.PilotoUi
import com.lapmaster.ui.model.PreferenciaMano
import com.lapmaster.ui.model.ResumenUi
import com.lapmaster.ui.model.SectorUi
import com.lapmaster.ui.model.SerieGraficaUi
import com.lapmaster.ui.model.VueltaPilotoUi
import com.lapmaster.ui.model.paletaPilotos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class ModeloVistaLapMaster : ViewModel() {

    private val pilotosIniciales = crearPilotosIniciales()
    private var siguienteIdPiloto = (pilotosIniciales.maxOfOrNull { it.id } ?: 0) + 1
    private fun consumirSiguienteId(): Int {
        val id = siguienteIdPiloto
        siguienteIdPiloto += 1
        return id
    }

    private val estadoInicial = EstadoAplicacionUi(
        pantallaSeleccionada = Pantalla.MENU,
        vueltas = EstadoVueltasUi(
            pilotos = pilotosIniciales.map { VueltaPilotoUi(it, tiempoMs = 0L, corriendo = false) }
        ),
        menu = EstadoMenuUi(
            acciones = emptyList(),
            pilotos = pilotosIniciales
        ),
        sectores = EstadoSectoresUi(
            piloto = pilotosIniciales.firstOrNull(),
            sectores = listOf(
                SectorUi("Sector 1", 0f, 0xFF00DB54L),
                SectorUi("Sector 2", 0f, 0xFF00D7F8L),
                SectorUi("Sector 3", 0f, 0xFF00DB54L)
            )
        ),
        graficas = EstadoGraficasUi(
            anios = listOf("2025", "2024", "2023", "2022", "2021", "2020"),
            anioSeleccionado = "2025",
            series = listOf(
                SerieGraficaUi(
                    "Piastri",
                    0xFFFF5E5EL,
                    listOf(4f, 6f, 8f, 10f, 12f, 14f, 16f, 18f, 16f, 14f)
                ),
                SerieGraficaUi(
                    "Norris",
                    0xFF53E38EL,
                    listOf(0f, 2f, 4f, 8f, 10f, 12f, 14f, 18f, 20f, 22f)
                ),
                SerieGraficaUi(
                    "Verstappen",
                    0xFFFFA726L,
                    listOf(-2f, -1f, 2f, 3f, 6f, 7f, 10f, 11f, 14f, 16f)
                ),
                SerieGraficaUi(
                    "Russell",
                    0xFF3BA3FFL,
                    listOf(-6f, -4f, -2f, 0f, 1f, 2f, 1f, 0f, -1f, -2f)
                )
            ),
            historial = listOf(
                EntradaHistorialUi(etiquetaDia = "Hoy", mejorVuelta = "1:30.12", vueltas = 12),
                EntradaHistorialUi(etiquetaDia = "Ayer", mejorVuelta = "1:31.05", vueltas = 18),
                EntradaHistorialUi(etiquetaDia = "27/10", mejorVuelta = "1:30.80", vueltas = 10)
            )
        ),
        clima = ClimaUi(
            ubicacion = "Esperando GPS",
            condicion = "Cargando clima...",
            temperatura = "--",
            sensacionTermica = "--",
            direccionViento = "--",
            velocidadViento = "--",
            rafagaViento = "--",
            presion = "--",
            visibilidad = "--",
            nubosidad = "--",
            lluvia = "--",
            humedad = 0
        ),
        gps = GpsUi(false, 0f),
        resumen = ResumenUi(
            mejorVuelta = "1:30.12",
            promedioVuelta = "1:32.40",
            vueltasSesion = 12,
            mejorDelDia = "1:29.88",
            promedioDia = "1:33.05"
        ),
        configuraciones = EstadoConfiguracionUi(
            temaOscuro = false,
            preferenciaMano = PreferenciaMano.DIESTRO
        )
    )

    private val _estadoUi = MutableStateFlow(estadoInicial)
    val estadoUi: StateFlow<EstadoAplicacionUi> = _estadoUi
    private val tickIntervalMs = 10L
    private val climaApi = ClimaApi()

    val navegacion = NavegacionActions(_estadoUi::update)
    val menu = MenuActions(_estadoUi::update, ::consumirSiguienteId, paletaPilotos)
    val vueltas = VueltasActions(_estadoUi::update)
    val sectores = SectoresActions(_estadoUi::update)
    val graficas = GraficasActions(_estadoUi::update)
    val gps = GpsActions(_estadoUi::update, ::cargarClima)

    init {
        iniciarTickerCronometros()
    }

    private fun crearPilotosIniciales(): List<PilotoUi> {
        return listOf(
            PilotoUi(id = 1, nombre = "Piastri", numero = "81", color = paletaPilotos[1], confirmado = true),
            PilotoUi(id = 2, nombre = "Norris", numero = "4", color = paletaPilotos[0], confirmado = true)
        )
    }

    private fun cargarClima(latitud: Double, longitud: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val resultado = climaApi.obtenerClima(latitud, longitud, lang = "es")) {
                is ResultadoClima.Ok -> {
                    val datos = resultado.datos
                    val viento = datos.vientoDireccion?.let { convertirAGradosCardinal(it) } ?: "--"
                    val vientoVelocidad = datos.vientoVelocidadMs?.let {
                        String.Companion.format(Locale.US, "%.1f km/h", it * 3.6f)
                    } ?: "--"
                    val rafaga = datos.vientoRafagaMs?.let {
                        String.Companion.format(Locale.US, "%.1f km/h", it * 3.6f)
                    } ?: "--"
                    val presion = datos.presion?.let { "$it hPa" } ?: "--"
                    val visibilidad = datos.visibilidad?.let {
                        String.Companion.format(Locale.US, "%.1f km", it / 1000f)
                    } ?: "--"
                    val nubosidad = datos.nubosidad?.let { "$it%" } ?: "--"
                    val lluvia = datos.lluviaMmHora?.let {
                        String.Companion.format(Locale.US, "%.1f mm/h", it)
                    } ?: "--"

                    _estadoUi.update { estado ->
                        estado.copy(
                            clima = estado.clima.copy(
                                ubicacion = datos.ubicacion.ifBlank { "Ubicación actual" },
                                condicion = datos.descripcion.ifBlank { "Sin datos" },
                                temperatura = if (datos.temperaturaC.isFinite()) {
                                    String.Companion.format(Locale.US, "%.1f°C", datos.temperaturaC)
                                } else "--",
                                sensacionTermica = datos.sensacionTermicaC?.let {
                                    String.Companion.format(Locale.US, "%.1f°C", it)
                                } ?: "--",
                                direccionVientoGrados = datos.vientoDireccion,
                                direccionViento = viento,
                                velocidadViento = vientoVelocidad,
                                rafagaViento = rafaga,
                                presion = presion,
                                visibilidad = visibilidad,
                                nubosidad = nubosidad,
                                lluvia = lluvia,
                                humedad = datos.humedad ?: estado.clima.humedad
                            )
                        )
                    }
                }

                is ResultadoClima.Error -> {
                    _estadoUi.update { estado ->
                        estado.copy(
                            clima = estado.clima.copy(
                                condicion = "Clima: ${resultado.mensaje}",
                                temperatura = "--",
                                sensacionTermica = "--",
                                direccionVientoGrados = null,
                                direccionViento = "--",
                                velocidadViento = "--",
                                rafagaViento = "--",
                                presion = "--",
                                visibilidad = "--",
                                nubosidad = "--",
                                lluvia = "--"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun iniciarTickerCronometros() {
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                delay(tickIntervalMs)
                val ahora = SystemClock.elapsedRealtime()
                _estadoUi.update { estado ->
                    val hayCorriendo = estado.vueltas.pilotos.any { it.corriendo }
                    if (!hayCorriendo) return@update estado

                    val pilotosActualizados = estado.vueltas.pilotos.map { vuelta ->
                        if (!vuelta.corriendo) return@map vuelta
                        val inicio = vuelta.inicioSistemaMs ?: ahora
                        val nuevoTiempo = vuelta.tiempoMs + (ahora - inicio)
                        vuelta.copy(
                            tiempoMs = nuevoTiempo,
                            inicioSistemaMs = ahora
                        )
                    }
                    estado.copy(
                        vueltas = estado.vueltas.copy(pilotos = pilotosActualizados)
                    )
                }
            }
        }
    }

    private fun convertirAGradosCardinal(grados: Int): String {
        val direcciones = listOf("N", "NE", "E", "SE", "S", "SO", "O", "NO")
        val indice = ((grados % 360) / 45.0).toInt()
        return direcciones[indice.coerceIn(0, direcciones.lastIndex)]
    }
}
