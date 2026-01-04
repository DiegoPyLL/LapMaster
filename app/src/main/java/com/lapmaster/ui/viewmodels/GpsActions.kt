package com.lapmaster.ui.viewmodels

import android.os.SystemClock
import com.lapmaster.ui.model.EstadoAplicacionUi
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class GpsActions(
    private val updateEstado: ((EstadoAplicacionUi) -> EstadoAplicacionUi) -> Unit,
    private val cargarClima: (Double, Double) -> Unit
) {
    private var ultimaConsultaMs = 0L
    private var ultimaLatitud: Double? = null
    private var ultimaLongitud: Double? = null
    private val intervaloMinimoMs = 120_000L
    private val distanciaMinimaMetros = 200.0

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
            if (debeActualizarClima(latitud, longitud)) {
                ultimaConsultaMs = SystemClock.elapsedRealtime()
                ultimaLatitud = latitud
                ultimaLongitud = longitud
                cargarClima(latitud, longitud)
            }
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

    private fun debeActualizarClima(latitud: Double, longitud: Double): Boolean {
        val ahora = SystemClock.elapsedRealtime()
        val ultimaLat = ultimaLatitud
        val ultimaLon = ultimaLongitud
        if (ultimaLat == null || ultimaLon == null) return true
        if (ahora - ultimaConsultaMs >= intervaloMinimoMs) return true
        val distancia = distanciaMetros(ultimaLat, ultimaLon, latitud, longitud)
        return distancia >= distanciaMinimaMetros
    }

    private fun distanciaMetros(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radio = 6_371_000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return radio * c
    }
}
