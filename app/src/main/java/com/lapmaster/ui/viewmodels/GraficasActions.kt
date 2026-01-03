package com.lapmaster.ui.viewmodels

import com.lapmaster.ui.model.EstadoAplicacionUi

class GraficasActions(
    private val updateEstado: ((EstadoAplicacionUi) -> EstadoAplicacionUi) -> Unit
) {
    fun alSeleccionarAnio(tanda: String) {
        updateEstado { estado ->
            if (tanda in estado.graficas.tanda) {
                estado.copy(graficas = estado.graficas.copy(tandaSeleccionada = tanda))
            } else estado
        }
    }
}
