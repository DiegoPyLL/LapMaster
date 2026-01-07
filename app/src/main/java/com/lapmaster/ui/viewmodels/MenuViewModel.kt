package com.lapmaster.ui.viewmodels

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lapmaster.apis.ClimaApi
import com.lapmaster.apis.ResultadoClima
import com.lapmaster.ui.model.ClimaUi
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
import com.lapmaster.ui.model.VueltaPilotoUi
import com.lapmaster.ui.model.crearTandaUi
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
    private val tandaInicial = crearTandaUi(id = 1, pilotos = pilotosIniciales)
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
            tandas = listOf(tandaInicial),
            tandaSeleccionadaId = tandaInicial.id,
            tandaActivaId = tandaInicial.id
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
    private val idleIntervalMs = 200L
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

    private fun cargarClima(latitud: Double, longitud: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val resultado = climaApi.obtenerClima(latitud, longitud, lang = "es")) {
                is ResultadoClima.Ok -> {
                    val datos = resultado.datos
                    val locale = Locale.getDefault()
                    val viento = datos.vientoDireccion?.let { convertirAGradosCardinal(it) } ?: "--"
                    val vientoVelocidad = datos.vientoVelocidadMs?.let {
                        String.Companion.format(locale, "%.1f km/h", it * 3.6f)
                    } ?: "--"
                    val rafaga = datos.vientoRafagaMs?.let {
                        String.Companion.format(locale, "%.1f km/h", it * 3.6f)
                    } ?: "--"
                    val presion = datos.presion?.let { "$it hPa" } ?: "--"
                    val visibilidad = datos.visibilidad?.let {
                        String.Companion.format(locale, "%.1f km", it / 1000f)
                    } ?: "--"
                    val nubosidad = datos.nubosidad?.let { "$it%" } ?: "--"
                    val lluvia = datos.lluviaMmHora?.let {
                        String.Companion.format(locale, "%.1f mm/h", it)
                    } ?: "--"

                    _estadoUi.update { estado ->
                        estado.copy(
                            clima = estado.clima.copy(
                                ubicacion = datos.ubicacion.ifBlank { "Ubicación actual" },
                                condicion = datos.descripcion.ifBlank { "Sin datos" },
                                temperatura = if (datos.temperaturaC.isFinite()) {
                                    String.Companion.format(locale, "%.1f°C", datos.temperaturaC)
                                } else "--",
                                sensacionTermica = datos.sensacionTermicaC?.let {
                                    String.Companion.format(locale, "%.1f°C", it)
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
                val estadoActual = _estadoUi.value
                val hayCorriendo = estadoActual.vueltas.pilotos.any { it.corriendo }
                val sectoresActivos = estadoActual.sectores.inicioSistemaMs != null &&
                    !estadoActual.sectores.enPausa &&
                    estadoActual.sectores.sectores.any { it.tiempoMs == 0L }
                delay(if (hayCorriendo || sectoresActivos) tickIntervalMs else idleIntervalMs)
                if (!hayCorriendo && !sectoresActivos) continue

                val ahora = SystemClock.elapsedRealtime()
                _estadoUi.update { estado ->
                    val hayCorriendoActual = estado.vueltas.pilotos.any { it.corriendo }
                    val sectoresActivosActual = estado.sectores.inicioSistemaMs != null &&
                        !estado.sectores.enPausa &&
                        estado.sectores.sectores.any { it.tiempoMs == 0L }
                    if (!hayCorriendoActual && !sectoresActivosActual) return@update estado

                    val pilotosActualizados = if (hayCorriendoActual) {
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

                    val sectoresActualizados = if (sectoresActivosActual) {
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
