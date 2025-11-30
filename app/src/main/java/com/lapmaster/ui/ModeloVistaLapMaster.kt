package com.lapmaster.ui

import androidx.lifecycle.ViewModel
import com.lapmaster.apis.ClimaApi
import com.lapmaster.apis.ResultadoClima
import com.lapmaster.ui.model.AccionMenuUi
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
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ModeloVistaLapMaster : ViewModel() {

    private val paletaPilotos = listOf(
        0xFF00DB54L, // verde
        0xFFFF5E5EL, // rojo
        0xFF3BA3FFL, // azul
        0xFFFFA726L  // naranja
    )

    private var siguienteIdPiloto = 3

    private val pilotosIniciales = listOf(
        PilotoUi(id = 1, nombre = "Piastri", numero = "81", color = paletaPilotos[1]),
        PilotoUi(id = 2, nombre = "Norris", numero = "4", color = paletaPilotos[0])
    )

    private val estadoInicial = EstadoAplicacionUi(
        pantallaSeleccionada = Pantalla.VUELTAS,
        vueltas = EstadoVueltasUi(
            pilotos = listOf(
                VueltaPilotoUi(pilotosIniciales[0], "1:32.27"),
                VueltaPilotoUi(pilotosIniciales[1], "1:31.77")
            )
        ),
        menu = EstadoMenuUi(
            acciones = listOf(
                AccionMenuUi("Tomar tiempo a 4 pilotos", 0xFF00DB54L),
                AccionMenuUi("Tomar tiempo por sectores", 0xFF00DB54L),
                AccionMenuUi("Historial", 0xFF00DB54L),
                AccionMenuUi("Configuración", 0xFFECC43BL)
            ),
            pilotos = pilotosIniciales
        ),
        sectores = EstadoSectoresUi(
            piloto = pilotosIniciales.first(),
            sectores = listOf(
                SectorUi("Sector 1", 20.81f, 0xFF00DB54L),
                SectorUi("Sector 2", 30.18f, 0xFF00D7F8L),
                SectorUi("Sector 3", 22.27f, 0xFF00DB54L)
            )
        ),
        graficas = EstadoGraficasUi(
            anios = listOf("2025", "2024", "2023", "2022", "2021", "2020"),
            anioSeleccionado = "2025",
            series = listOf(
                SerieGraficaUi("Piastri", 0xFFFF5E5EL, listOf(4f, 6f, 8f, 10f, 12f, 14f, 16f, 18f, 16f, 14f)),
                SerieGraficaUi("Norris", 0xFF53E38EL, listOf(0f, 2f, 4f, 8f, 10f, 12f, 14f, 18f, 20f, 22f)),
                SerieGraficaUi("Verstappen", 0xFFFFA726L, listOf(-2f, -1f, 2f, 3f, 6f, 7f, 10f, 11f, 14f, 16f)),
                SerieGraficaUi("Russell", 0xFF3BA3FFL, listOf(-6f, -4f, -2f, 0f, 1f, 2f, 1f, 0f, -1f, -2f))
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

    fun alSeleccionarPantalla(pantalla: Pantalla) {
        _estadoUi.update { it.copy(pantallaSeleccionada = pantalla) }
    }

    fun alSeleccionarAnio(anio: String) {
        _estadoUi.update { estado ->
            if (anio in estado.graficas.anios) {
                estado.copy(graficas = estado.graficas.copy(anioSeleccionado = anio))
            } else estado
        }
    }

    fun alAlternarTema() {
        _estadoUi.update { estado ->
            estado.copy(
                configuraciones = estado.configuraciones.copy(temaOscuro = !estado.configuraciones.temaOscuro)
            )
        }
    }

    fun alAlternarPreferenciaMano() {
        _estadoUi.update { estado ->
            val siguiente = if (estado.configuraciones.preferenciaMano == PreferenciaMano.DIESTRO) {
                PreferenciaMano.ZURDO
            } else PreferenciaMano.DIESTRO
            estado.copy(configuraciones = estado.configuraciones.copy(preferenciaMano = siguiente))
        }
    }

    fun alAgregarPiloto() {
        _estadoUi.update { estado ->
            if (estado.menu.pilotos.size >= 4) return@update estado

            val color = paletaPilotos[estado.menu.pilotos.size % paletaPilotos.size]
            val nuevoPiloto = PilotoUi(
                id = siguienteIdPiloto++,
                nombre = "Piloto #${siguienteIdPiloto - 1}",
                numero = "${80 + siguienteIdPiloto}",
                color = color
            )
            val pilotosActualizados = estado.menu.pilotos + nuevoPiloto
            val vueltasActualizadas = estado.vueltas.copy(
                pilotos = (estado.vueltas.pilotos + VueltaPilotoUi(nuevoPiloto, "--:--.--")).take(4)
            )
            val sectoresActualizados = if (estado.sectores.piloto == null) {
                estado.sectores.copy(piloto = nuevoPiloto)
            } else estado.sectores
            val menuActualizado = estado.menu.copy(pilotos = pilotosActualizados)

            estado.copy(menu = menuActualizado, vueltas = vueltasActualizadas, sectores = sectoresActualizados)
        }
    }

    private val climaApi = ClimaApi()

    fun alActualizarEstadoGps(tieneFijacion: Boolean, precisionMetros: Float, latitud: Double?, longitud: Double?) {
        _estadoUi.update { estado ->
            estado.copy(
                gps = estado.gps.copy(
                    tieneFijacion = tieneFijacion,
                    precisionMetros = precisionMetros,
                    latitud = latitud,
                    longitud = longitud
                ),
                clima = estado.clima.copy(
                    ubicacion = if (tieneFijacion) estado.clima.ubicacion else "Ubicación no disponible",
                    condicion = if (tieneFijacion) estado.clima.condicion else "Esperando señal GPS",
                    temperatura = if (tieneFijacion) estado.clima.temperatura else "--",
                    sensacionTermica = if (tieneFijacion) estado.clima.sensacionTermica else "--",
                    direccionViento = if (tieneFijacion) estado.clima.direccionViento else "--",
                    velocidadViento = if (tieneFijacion) estado.clima.velocidadViento else "--",
                    rafagaViento = if (tieneFijacion) estado.clima.rafagaViento else "--",
                    presion = if (tieneFijacion) estado.clima.presion else "--",
                    visibilidad = if (tieneFijacion) estado.clima.visibilidad else "--",
                    nubosidad = if (tieneFijacion) estado.clima.nubosidad else "--",
                    lluvia = if (tieneFijacion) estado.clima.lluvia else "--"
                )
            )
        }
        if (tieneFijacion && latitud != null && longitud != null) {
            cargarClima(latitud, longitud)
        }
    }

    private fun cargarClima(latitud: Double, longitud: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val resultado = climaApi.obtenerClima(latitud, longitud, lang = "es")) {
                is ResultadoClima.Ok -> {
                    val datos = resultado.datos
                    val viento = datos.vientoDireccion?.let { convertirAGradosCardinal(it) } ?: "--"
                    val vientoVelocidad = datos.vientoVelocidadMs?.let { String.format(Locale.US, "%.1f km/h", it * 3.6f) } ?: "--"
                    val rafaga = datos.vientoRafagaMs?.let { String.format(Locale.US, "%.1f km/h", it * 3.6f) } ?: "--"
                    val presion = datos.presion?.let { "$it hPa" } ?: "--"
                    val visibilidad = datos.visibilidad?.let { String.format(Locale.US, "%.1f km", it / 1000f) } ?: "--"
                    val nubosidad = datos.nubosidad?.let { "$it%" } ?: "--"
                    val lluvia = datos.lluviaMmHora?.let { String.format(Locale.US, "%.1f mm/h", it) } ?: "--"

                    _estadoUi.update { estado ->
                        estado.copy(
                            clima = estado.clima.copy(
                                ubicacion = datos.ubicacion.ifBlank { "Ubicación actual" },
                                condicion = datos.descripcion.ifBlank { "Sin datos" },
                                temperatura = if (datos.temperaturaC.isFinite()) {
                                    String.format(Locale.US, "%.1f°C", datos.temperaturaC)
                                } else "--",
                                sensacionTermica = datos.sensacionTermicaC?.let {
                                    String.format(Locale.US, "%.1f°C", it)
                                } ?: "--",
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

    private fun convertirAGradosCardinal(grados: Int): String {
        val direcciones = listOf("N", "NE", "E", "SE", "S", "SO", "O", "NO")
        val indice = ((grados % 360) / 45.0).toInt()
        return direcciones[indice.coerceIn(0, direcciones.lastIndex)]
    }
}
