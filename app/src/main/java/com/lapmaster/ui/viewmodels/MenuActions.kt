package com.lapmaster.ui.viewmodels

import com.lapmaster.ui.model.EstadoAplicacionUi
import com.lapmaster.ui.model.PilotoUi
import com.lapmaster.ui.model.PreferenciaMano
import com.lapmaster.ui.model.VueltaPilotoUi

class MenuActions(
    private val updateEstado: ((EstadoAplicacionUi) -> EstadoAplicacionUi) -> Unit,
    private val siguienteId: () -> Int,
    private val paletaPilotos: List<Long>
) {
    fun alAlternarTema() {
        updateEstado { estado ->
            estado.copy(
                configuraciones = estado.configuraciones.copy(temaOscuro = !estado.configuraciones.temaOscuro)
            )
        }
    }

    fun alAlternarPreferenciaMano() {
        updateEstado { estado ->
            val siguiente = if (estado.configuraciones.preferenciaMano == PreferenciaMano.DIESTRO) {
                PreferenciaMano.ZURDO
            } else PreferenciaMano.DIESTRO
            estado.copy(configuraciones = estado.configuraciones.copy(preferenciaMano = siguiente))
        }
    }

    fun alAgregarPiloto() {
        updateEstado update@{ estado ->
            if (estado.menu.pilotos.size >= 4) return@update estado

            val id = siguienteId()
            val color = paletaPilotos[estado.menu.pilotos.size % paletaPilotos.size]
            val nuevoPiloto = PilotoUi(
                id = id,
                nombre = "Piloto N°$id",
                numero = "${80 + id}",
                color = color,
                confirmado = false
            )
            val pilotosActualizados = (estado.menu.pilotos + nuevoPiloto).take(4)
            actualizarPilotosEnEstado(estado, pilotosActualizados)
        }
    }

    fun alActualizarPilotoNombre(pilotoId: Int, nombre: String) {
        actualizarPiloto(pilotoId) { piloto ->
            if (piloto.nombre == nombre) piloto else piloto.copy(nombre = nombre, confirmado = false)
        }
    }

    fun alActualizarPilotoNumero(pilotoId: Int, numero: String) {
        actualizarPiloto(pilotoId) { it.copy(numero = numero) }
    }

    fun alActualizarPilotoColor(pilotoId: Int, color: Long) {
        actualizarPiloto(pilotoId) { it.copy(color = color) }
    }

    fun alConfirmarPiloto(pilotoId: Int) {
        actualizarPiloto(pilotoId) { piloto ->
            if (piloto.nombre.isBlank()) piloto else piloto.copy(confirmado = true)
        }
    }

    fun alEliminarPiloto(pilotoId: Int) {
        updateEstado { estado ->
            val pilotosActualizados = estado.menu.pilotos.filterNot { it.id == pilotoId }
            actualizarPilotosEnEstado(estado, pilotosActualizados)
        }
    }

    private fun actualizarPiloto(pilotoId: Int, transform: (PilotoUi) -> PilotoUi) {
        updateEstado { estado ->
            val pilotosActualizados = estado.menu.pilotos.map { piloto ->
                if (piloto.id == pilotoId) transform(piloto) else piloto
            }
            actualizarPilotosEnEstado(estado, pilotosActualizados)
        }
    }

    private fun actualizarPilotosEnEstado(
        estado: EstadoAplicacionUi,
        nuevosPilotos: List<PilotoUi>
    ): EstadoAplicacionUi {
        val pilotosLimitados = nuevosPilotos.take(4)
        val mapaPilotos = pilotosLimitados.associateBy { it.id }
        val vueltasExistentes = estado.vueltas.pilotos
            .filter { mapaPilotos.containsKey(it.piloto.id) }
            .map { vuelta -> vuelta.copy(piloto = mapaPilotos.getValue(vuelta.piloto.id)) }
        val vueltasNuevas = pilotosLimitados
            .filter { piloto -> vueltasExistentes.none { it.piloto.id == piloto.id } }
            .map { piloto -> VueltaPilotoUi(piloto) }
        val vueltasActualizadas = estado.vueltas.copy(
            pilotos = (vueltasExistentes + vueltasNuevas).take(4)
        )
        val pilotoSectores = estado.sectores.piloto?.let { mapaPilotos[it.id] } ?: pilotosLimitados.firstOrNull()
        val sectoresActualizados = estado.sectores.copy(piloto = pilotoSectores)
        val menuActualizado = estado.menu.copy(pilotos = pilotosLimitados)

        return estado.copy(menu = menuActualizado, vueltas = vueltasActualizadas, sectores = sectoresActualizados)
    }
}
