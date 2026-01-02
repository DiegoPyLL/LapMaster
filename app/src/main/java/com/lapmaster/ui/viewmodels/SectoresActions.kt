package com.lapmaster.ui.viewmodels

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
}
