package com.lapmaster.ui.viewmodels

import android.os.SystemClock
import com.lapmaster.ui.model.EstadoAplicacionUi

class SectoresActions(
    private val updateEstado: ((EstadoAplicacionUi) -> EstadoAplicacionUi) -> Unit
) {
    fun alSeleccionarPiloto(pilotoId: Int) {
        updateEstado { estado ->
            val piloto = estado.menu.pilotos.firstOrNull { it.id == pilotoId }
            estado.copy(sectores = estado.sectores.copy(piloto = piloto))
        }
    }

    fun alIniciarCronometro() {
        updateEstado { estado ->
            if (estado.sectores.inicioSistemaMs != null || estado.sectores.sectores.none { it.tiempoMs == 0L }) {
                estado
            } else {
                estado.copy(
                    sectores = estado.sectores.copy(
                        inicioSistemaMs = SystemClock.elapsedRealtime(),
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
            if (indiceSector !in sectoresActuales.indices || siguienteIndice != indiceSector) {
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
                estado.copy(
                    sectores = estado.sectores.copy(
                        sectores = sectoresMarcados,
                        inicioSistemaMs = inicio,
                        tiempoActualMs = if (hayPendientes) ahora - inicio else estado.sectores.tiempoActualMs
                    )
                )

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
                    tiempoActualMs = 0L
                )
            )
        }
    }
    }
}

