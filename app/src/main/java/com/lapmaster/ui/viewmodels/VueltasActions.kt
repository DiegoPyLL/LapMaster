package com.lapmaster.ui.viewmodels

import android.os.SystemClock
import com.lapmaster.ui.model.EstadoAplicacionUi

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
            estado.copy(
                vueltas = estado.vueltas.copy(
                    pilotos = estado.vueltas.pilotos.map { vuelta ->
                        if (vuelta.piloto.id != pilotoId) return@map vuelta
                        if (!vuelta.corriendo) {
                            vuelta.copy(corriendo = true, inicioSistemaMs = ahora)
                        } else {
                            vuelta.copy(
                                tiempoMs = 0L,
                                corriendo = true,
                                inicioSistemaMs = ahora,
                                vueltas = vuelta.vueltas + 1
                            )
                        }
                    }
                )
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
                )
            )
        }
    }
}
