package com.lapmaster.ui.viewmodels

import android.os.SystemClock
import com.lapmaster.ui.model.EstadoAplicacionUi
import com.lapmaster.ui.model.VueltaSectoresUi

class SectoresActions(
    private val updateEstado: ((EstadoAplicacionUi) -> EstadoAplicacionUi) -> Unit
) {
    private companion object {
        const val MAX_HISTORIAL_SECTORES = 8
    }

    fun alSeleccionarPiloto(pilotoId: Int) {
        updateEstado { estado ->
            val piloto = estado.menu.pilotos.firstOrNull { it.id == pilotoId }
            estado.copy(sectores = estado.sectores.copy(piloto = piloto))
        }
    }

    fun alIniciarCronometro() {
        updateEstado { estado ->
            if (estado.sectores.inicioSistemaMs != null ||
                estado.sectores.sectores.none { it.tiempoMs == 0L }) {
                estado
            } else {
                estado.copy(
                    sectores = estado.sectores.copy(
                        inicioSistemaMs = SystemClock.elapsedRealtime(),
                        enPausa = false,
                        tiempoActualMs = 0L
                    )
                )
            }
        }
    }

    fun alMarcarSector(indiceSector: Int) {
        updateEstado { estado ->
            val sectoresActuales = estado.sectores.sectores
            val siguienteIndice = sectoresActuales.indexOfFirst { it.tiempoMs == 0L }
            if (estado.sectores.enPausa ||
                indiceSector !in sectoresActuales.indices ||
                siguienteIndice != indiceSector) {
                estado
            } else {
                val ahora = SystemClock.elapsedRealtime()
                val inicio = estado.sectores.inicioSistemaMs ?: ahora
                val acumuladoMs = sectoresActuales.sumOf { it.tiempoMs }
                val deltaMs = (ahora - inicio - acumuladoMs).coerceAtLeast(0L)
                val sectoresMarcados = sectoresActuales.mapIndexed { indice, sector ->
                    if (indice == indiceSector) sector.copy(tiempoMs = deltaMs) else sector
                }

                val hayPendientes = sectoresMarcados.any { it.tiempoMs == 0L }
                val totalMs = sectoresMarcados.sumOf { it.tiempoMs }
                if (hayPendientes) {
                    estado.copy(
                        sectores = estado.sectores.copy(
                            sectores = sectoresMarcados,
                            inicioSistemaMs = inicio,
                            enPausa = false,
                            tiempoActualMs = ahora - inicio,
                            ultimoTiempoMs = estado.sectores.ultimoTiempoMs
                        )
                    )
                } else {
                    val sectoresReiniciados = sectoresMarcados.map { sector -> sector.copy(tiempoMs = 0L) }
                    val nuevaEntrada = estado.sectores.historial.size + 1
                    val historialActualizado = (listOf(
                        VueltaSectoresUi(
                            numero = nuevaEntrada,
                            tiemposMs = sectoresMarcados.map { it.tiempoMs },
                            totalMs = totalMs
                        )
                    ) + estado.sectores.historial).take(MAX_HISTORIAL_SECTORES)
                    estado.copy(
                        sectores = estado.sectores.copy(
                            sectores = sectoresReiniciados,
                            inicioSistemaMs = ahora,
                            enPausa = false,
                            tiempoActualMs = 0L,
                            ultimoTiempoMs = totalMs,
                            historial = historialActualizado
                        )
                    )
                }
            }
        }
    }

    fun alAlternarPausa() {
        updateEstado { estado ->
            val sectores = estado.sectores
            val inicio = sectores.inicioSistemaMs
            val hayPendientes = sectores.sectores.any { it.tiempoMs == 0L }
            if (inicio == null || !hayPendientes) {
                estado
            } else {
                val ahora = SystemClock.elapsedRealtime()
                if (sectores.enPausa) {
                    val inicioAjustado = (ahora - sectores.tiempoActualMs).coerceAtLeast(0L)
                    estado.copy(
                        sectores = sectores.copy(
                            enPausa = false,
                            inicioSistemaMs = inicioAjustado
                        )
                    )
                } else {
                    val tiempoActual = (ahora - inicio).coerceAtLeast(0L)
                    estado.copy(
                        sectores = sectores.copy(
                            enPausa = true,
                            tiempoActualMs = tiempoActual
                        )
                    )
                }
            }
        }
    }

    fun alReiniciarSectores() {
        updateEstado { estado ->
            val sectoresReiniciados = estado.sectores.sectores.map { sector ->
                sector.copy(tiempoMs = 0L)
            }
            estado.copy(
                sectores = estado.sectores.copy(
                    sectores = sectoresReiniciados,
                    inicioSistemaMs = null,
                    enPausa = false,
                    tiempoActualMs = 0L,
                    ultimoTiempoMs = 0L,
                    historial = emptyList()
                )
            )
        }
    }
}

