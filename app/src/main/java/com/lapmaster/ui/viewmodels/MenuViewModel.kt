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
                SectorUi("Sector 1", 0L, 0xFF00DB54L),
                SectorUi("Sector 2", 0L, 0xFF00D7F8L),
                SectorUi("Sector 3", 0L, 0xFF00DB54L)
            )
        ),

        graficas = EstadoGraficasUi(
            tanda = listOf("1ra Tanda", "2da Tanda", "3ra Tanda"),
            tandaSeleccionada = "1ra Tanda",
            series = listOf(
                SerieGraficaUi(
                    "Piloto 1",
                    0xFFFF5E5EL,
                    listOf(
                        7.9f,
                        6.8f, 6.1f, 5.3f, 5.9f, 5.2f, 4.9f, 5.6f
                    )
                ),
                SerieGraficaUi(
                    "Piloto 2",
                    0xFF53E38EL,
                    listOf(
                        9.4f,
                        7.1f, 6.7f, 5.2f, 5.8f, 5.6f, 5.3f, 5.0f
                    )
                ),
                SerieGraficaUi(
                    "Piloto 3",
                    0xFFFFA726L,
                    listOf(
                        9.8f, 8.9f,
                        7.0f, 6.5f, 5.6f, 4.6f, 4.1f, 4.5f, 4.2f
                    )
                ),
                SerieGraficaUi(
                    "Piloto 4",
                    0xFF3BA3FFL,
                    listOf(
                        7.0f, 6.5f, 5.6f, 4.6f, 4.1f, 4.5f, 4.2f
                    )
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
            PilotoUi(id = 1, nombre = "Piloto N°1", numero = "101", color = paletaPilotos[0], confirmado = false)
        )
    }

    // todo revisar el locale.us para ajustarlo al pais?
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
                    val sectoresActivos = estado.sectores.inicioSistemaMs != null &&
                        estado.sectores.sectores.any { it.tiempoMs == 0L }
                    if (!hayCorriendo && !sectoresActivos) return@update estado

                    val pilotosActualizados = if (hayCorriendo) {
                        estado.vueltas.pilotos.map { vuelta ->
                            if (!vuelta.corriendo) return@map vuelta
                            val inicio = vuelta.inicioSistemaMs ?: ahora
                            val nuevoTiempo = vuelta.tiempoMs + (ahora - inicio)
                            vuelta.copy(
                                tiempoMs = nuevoTiempo,
                                inicioSistemaMs = ahora
                            )
                        }
                    } else {
                        estado.vueltas.pilotos
                    }

                    val sectoresActualizados = if (sectoresActivos) {
                        val inicio = estado.sectores.inicioSistemaMs ?: ahora
                        estado.sectores.copy(
                            inicioSistemaMs = inicio,
                            tiempoActualMs = ahora - inicio
                        )
                    } else {
                        estado.sectores
                    }

                    estado.copy(
                        vueltas = estado.vueltas.copy(pilotos = pilotosActualizados),
                        sectores = sectoresActualizados
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
