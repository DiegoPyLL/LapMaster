package com.lapmaster.ui.viewmodels

import com.lapmaster.ui.model.EstadoAplicacionUi

class GpsActions(
    private val updateEstado: ((EstadoAplicacionUi) -> EstadoAplicacionUi) -> Unit,
    private val cargarClima: (Double, Double) -> Unit
) {
    fun alActualizarEstadoGps(tieneFijacion: Boolean, precisionMetros: Float, latitud: Double?, longitud: Double?) {
        updateEstado { estado ->
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
                    direccionVientoGrados = if (tieneFijacion) estado.clima.direccionVientoGrados else null,
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

    fun alActualizarRumbo(grados: Float) {
        val normalizado = ((grados % 360f) + 360f) % 360f
        updateEstado { estado ->
            estado.copy(
                gps = estado.gps.copy(rumboGrados = normalizado)
            )
        }
    }
}
