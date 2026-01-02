package com.lapmaster.ui.viewmodels

import com.lapmaster.ui.model.EstadoAplicacionUi
import com.lapmaster.ui.model.Pantalla

class NavegacionActions(
    private val updateEstado: ((EstadoAplicacionUi) -> EstadoAplicacionUi) -> Unit
) {
    fun alSeleccionarPantalla(pantalla: Pantalla) {
        updateEstado { it.copy(pantallaSeleccionada = pantalla) }
    }
}
