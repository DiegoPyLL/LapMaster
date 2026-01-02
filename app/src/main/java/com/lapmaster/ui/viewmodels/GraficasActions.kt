package com.lapmaster.ui.viewmodels

import com.lapmaster.ui.model.EstadoAplicacionUi

class GraficasActions(
    private val updateEstado: ((EstadoAplicacionUi) -> EstadoAplicacionUi) -> Unit
) {
    fun alSeleccionarAnio(anio: String) {
        updateEstado { estado ->
            if (anio in estado.graficas.anios) {
                estado.copy(graficas = estado.graficas.copy(anioSeleccionado = anio))
            } else estado
        }
    }
}
