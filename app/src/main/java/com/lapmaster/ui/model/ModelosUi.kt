package com.lapmaster.ui.model

// Guardamos colores como ARGB Long para no acoplar el ViewModel a Compose.
data class PilotoUi(
    var id: Int,
    val nombre: String,
    var numero: String,
    val color: Long,
    val confirmado: Boolean = false
)

data class VueltaPilotoUi(
    val piloto: PilotoUi,
    val tiempoMs: Long = 0L,
    val corriendo: Boolean = false,
    val inicioSistemaMs: Long? = null,
    val vueltas: Int = 0
)

data class AccionMenuUi(val titulo: String, val color: Long)

data class SectorUi(val etiqueta: String, val tiempoMs: Long, val color: Long)

data class SerieGraficaUi(val nombre: String, val color: Long, val valores: List<Float>)

data class EntradaHistorialUi(
    val etiquetaDia: String,
    val mejorVuelta: String,
    val vueltas: Int
)

data class ClimaUi(
    val ubicacion: String = "",
    val condicion: String = "",
    val temperatura: String = "",
    val sensacionTermica: String = "",
    val direccionViento: String = "",
    val direccionVientoGrados: Int? = null,
    val velocidadViento: String = "",
    val rafagaViento: String = "",
    val presion: String = "",
    val visibilidad: String = "",
    val nubosidad: String = "",
    val lluvia: String = "",
    val humedad: Int = 0
)

data class GpsUi(
    val tieneFijacion: Boolean,
    val precisionMetros: Float,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val rumboGrados: Float? = null
)

data class ResumenUi(
    val mejorVuelta: String,
    val promedioVuelta: String,
    val vueltasSesion: Int,
    val mejorDelDia: String,
    val promedioDia: String
)

enum class PreferenciaMano { DIESTRO, ZURDO }
enum class PreferenciaOrientacion { AUTO, VERTICAL, HORIZONTAL }

data class EstadoConfiguracionUi(
    val temaOscuro: Boolean = false,
    val preferenciaMano: PreferenciaMano = PreferenciaMano.DIESTRO,
    val preferenciaOrientacion: PreferenciaOrientacion = PreferenciaOrientacion.AUTO
)

data class EstadoVueltasUi(
    val pilotos: List<VueltaPilotoUi> = emptyList()
)

data class EstadoMenuUi(
    val acciones: List<AccionMenuUi> = emptyList(),
    val pilotos: List<PilotoUi> = emptyList()
)

data class EstadoSectoresUi(
    val piloto: PilotoUi? = null,
    val sectores: List<SectorUi> = emptyList(),
    val inicioSistemaMs: Long? = null,
    val tiempoActualMs: Long = 0L,
    val ultimoTiempoMs: Long = 0L
)

data class EstadoGraficasUi(
    val tanda: List<String> = emptyList(),
    val tandaSeleccionada: String = "",
    val series: List<SerieGraficaUi> = emptyList(),
    val historial: List<EntradaHistorialUi> = emptyList()
)

data class EstadoAplicacionUi(
    val pantallaSeleccionada: Pantalla = Pantalla.VUELTAS,
    val vueltas: EstadoVueltasUi = EstadoVueltasUi(),
    val menu: EstadoMenuUi = EstadoMenuUi(),
    val sectores: EstadoSectoresUi = EstadoSectoresUi(),
    val graficas: EstadoGraficasUi = EstadoGraficasUi(),
    val clima: ClimaUi = ClimaUi(),
    val gps: GpsUi = GpsUi(false, 0f),
    val resumen: ResumenUi = ResumenUi("-", "-", 0, "-", "-"),
    val configuraciones: EstadoConfiguracionUi = EstadoConfiguracionUi()
)

enum class Pantalla { VUELTAS, MENU, SECTORES, CLIMA, GRAFICAS }
