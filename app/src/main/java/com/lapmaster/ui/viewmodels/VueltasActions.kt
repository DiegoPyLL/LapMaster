package com.lapmaster.ui.viewmodels

import android.os.SystemClock
import com.lapmaster.ui.model.EntradaHistorialUi
import com.lapmaster.ui.model.EstadoAplicacionUi
import com.lapmaster.ui.model.EstadoGraficasUi
import com.lapmaster.ui.model.PilotoUi
import com.lapmaster.ui.model.SerieGraficaUi
import com.lapmaster.ui.model.TandaUi
import com.lapmaster.ui.model.crearTandaUi
import com.lapmaster.ui.model.nombreParaGrafica
import kotlin.math.roundToInt

class VueltasActions(
    private val updateEstado: ((EstadoAplicacionUi) -> EstadoAplicacionUi) -> Unit
) {
    fun alAlternarCronometro(pilotoId: Int) {
        val ahora = SystemClock.elapsedRealtime()
        updateEstado { estado ->
            estado.copy(
                vueltas = estado.vueltas.copy(
                    pilotos = estado.vueltas.pilotos.map { vuelta ->
                        if (vuelta.piloto.id != pilotoId) return@map vuelta
                        if (vuelta.corriendo) {
                            val inicio = vuelta.inicioSistemaMs ?: ahora
                            val acumulado = vuelta.tiempoMs + (ahora - inicio)
                            vuelta.copy(
                                corriendo = false,
                                tiempoMs = acumulado,
                                inicioSistemaMs = null
                            )
                        } else {
                            vuelta.copy(
                                corriendo = true,
                                inicioSistemaMs = ahora
                            )
                        }
                    }
                )
            )
        }
    }

    fun alMarcarVuelta(pilotoId: Int) {
        val ahora = SystemClock.elapsedRealtime()
        updateEstado { estado ->
            var tiempoVueltaMs: Long? = null
            estado.copy(
                vueltas = estado.vueltas.copy(
                    pilotos = estado.vueltas.pilotos.map { vuelta ->
                        if (vuelta.piloto.id != pilotoId) return@map vuelta
                        if (!vuelta.corriendo) {
                            vuelta.copy(corriendo = true, inicioSistemaMs = ahora)
                        } else {
                            val inicio = vuelta.inicioSistemaMs ?: ahora
                            tiempoVueltaMs = vuelta.tiempoMs + (ahora - inicio)
                            vuelta.copy(
                                tiempoMs = 0L,
                                corriendo = true,
                                inicioSistemaMs = ahora,
                                vueltas = vuelta.vueltas + 1
                            )
                        }
                    }
                ),
                graficas = tiempoVueltaMs?.let { lapMs ->
                    actualizarTandaConVuelta(
                        graficas = estado.graficas,
                        pilotos = estado.menu.pilotos,
                        pilotoId = pilotoId,
                        tiempoVueltaMs = lapMs
                    )
                } ?: estado.graficas
            )
        }
    }

    fun alResetearCronometro(pilotoId: Int) {
        updateEstado { estado ->
            estado.copy(
                vueltas = estado.vueltas.copy(
                    pilotos = estado.vueltas.pilotos.map { vuelta ->
                        if (vuelta.piloto.id == pilotoId) {
                            vuelta.copy(
                                tiempoMs = 0L,
                                corriendo = false,
                                inicioSistemaMs = null,
                                vueltas = 0
                            )
                        } else vuelta
                    }
                ),
                graficas = limpiarVueltasPiloto(estado.graficas, pilotoId)
            )
        }
    }

    fun alTerminarTanda() {
        updateEstado { estado ->
            val graficas = estado.graficas
            val activaId = graficas.tandaActivaId ?: return@updateEstado estado
            val indiceActiva = graficas.tandas.indexOfFirst { it.id == activaId }
            if (indiceActiva == -1) return@updateEstado estado

            val tandaActiva = graficas.tandas[indiceActiva]
            val tieneVueltas = tandaActiva.series.any { it.valores.isNotEmpty() }
            val tandasActualizadas = if (tieneVueltas) {
                val tandaFinalizada = tandaActiva.copy(finalizada = true)
                val nuevoId = (graficas.tandas.maxOfOrNull { it.id } ?: activaId) + 1
                val nuevaTanda = crearTandaUi(id = nuevoId, pilotos = estado.menu.pilotos)
                graficas.tandas.toMutableList().apply {
                    set(indiceActiva, tandaFinalizada)
                    add(nuevaTanda)
                }
            } else {
                graficas.tandas
            }

            val historialActualizado = if (tieneVueltas) {
                val tandaFinalizada = tandasActualizadas.getOrNull(indiceActiva) ?: tandaActiva
                val entrada = construirEntradaHistorial(tandaFinalizada)
                if (entrada != null) listOf(entrada) + graficas.historial else graficas.historial
            } else {
                graficas.historial
            }

            val nuevoActivaId = if (tieneVueltas) tandasActualizadas.last().id else activaId
            val nuevaSeleccionId = if (tieneVueltas) tandaActiva.id else graficas.tandaSeleccionadaId
            val graficasActualizadas = graficas.copy(
                tandas = tandasActualizadas,
                tandaActivaId = nuevoActivaId,
                tandaSeleccionadaId = nuevaSeleccionId,
                historial = historialActualizado
            )

            val pilotosReset = estado.vueltas.pilotos.map { vuelta ->
                vuelta.copy(
                    tiempoMs = 0L,
                    corriendo = false,
                    inicioSistemaMs = null,
                    vueltas = 0
                )
            }
            estado.copy(
                vueltas = estado.vueltas.copy(pilotos = pilotosReset),
                graficas = graficasActualizadas
            )
        }
    }

    private fun actualizarTandaConVuelta(
        graficas: EstadoGraficasUi,
        pilotos: List<PilotoUi>,
        pilotoId: Int,
        tiempoVueltaMs: Long
    ): EstadoGraficasUi {
        val activaId = graficas.tandaActivaId ?: return graficas
        val pilotosPorId = pilotos.associateBy { it.id }
        val tandaActualizada = graficas.tandas.map { tanda ->
            if (tanda.id != activaId || tanda.finalizada) return@map tanda
            val seriesActualizadas = actualizarSeriePiloto(
                series = tanda.series,
                piloto = pilotosPorId[pilotoId],
                tiempoVueltaMs = tiempoVueltaMs
            )
            tanda.copy(series = seriesActualizadas)
        }
        return graficas.copy(tandas = tandaActualizada)
    }

    private fun actualizarSeriePiloto(
        series: List<SerieGraficaUi>,
        piloto: PilotoUi?,
        tiempoVueltaMs: Long
    ): List<SerieGraficaUi> {
        if (piloto == null) return series
        val tiempoSegundos = tiempoVueltaMs / 1000f
        var encontrado = false
        val actualizadas = series.map { serie ->
            if (serie.pilotoId == piloto.id) {
                encontrado = true
                serie.copy(valores = serie.valores + tiempoSegundos)
            } else serie
        }
        if (encontrado) return actualizadas
        val nombre = piloto.nombreParaGrafica()
        return actualizadas + SerieGraficaUi(
            pilotoId = piloto.id,
            nombre = nombre,
            color = piloto.color,
            valores = listOf(tiempoSegundos)
        )
    }

    private fun limpiarVueltasPiloto(
        graficas: EstadoGraficasUi,
        pilotoId: Int
    ): EstadoGraficasUi {
        val activaId = graficas.tandaActivaId ?: return graficas
        val tandasActualizadas = graficas.tandas.map { tanda ->
            if (tanda.id != activaId || tanda.finalizada) return@map tanda
            val seriesActualizadas = tanda.series.map { serie ->
                if (serie.pilotoId == pilotoId) serie.copy(valores = emptyList()) else serie
            }
            tanda.copy(series = seriesActualizadas)
        }
        return graficas.copy(tandas = tandasActualizadas)
    }

    private fun construirEntradaHistorial(tanda: TandaUi): EntradaHistorialUi? {
        val valores = tanda.series.flatMap { it.valores }
        if (valores.isEmpty()) return null
        val mejorSegundos = valores.minOrNull() ?: return null
        val mejorMs = (mejorSegundos * 1000f).roundToInt().coerceAtLeast(0).toLong()
        val totalVueltas = tanda.series.sumOf { it.valores.size }
        return EntradaHistorialUi(
            etiquetaDia = tanda.nombre,
            mejorVuelta = formatearTiempoMs(mejorMs),
            vueltas = totalVueltas
        )
    }

    private fun formatearTiempoMs(ms: Long): String {
        if (ms <= 0L) return "--:--.---"
        val totalSegundos = ms / 1000
        val minutos = totalSegundos / 60
        val segundos = totalSegundos % 60
        val milisegundos = ms % 1000
        return String.format("%d:%02d.%03d", minutos, segundos, milisegundos)
    }
}
