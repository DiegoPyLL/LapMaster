package com.lapmaster.ui.viewmodels

import com.lapmaster.ui.model.EstadoAplicacionUi

class GraficasActions(
    private val updateEstado: ((EstadoAplicacionUi) -> EstadoAplicacionUi) -> Unit
) {
    fun alSeleccionarTanda(tandaId: Int) {
        updateEstado { estado ->
            if (estado.graficas.tandas.any { it.id == tandaId }) {
                estado.copy(graficas = estado.graficas.copy(tandaSeleccionadaId = tandaId))
            } else estado
        }
    }
}
