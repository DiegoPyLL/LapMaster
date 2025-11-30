package com.lapmaster.apis

import com.lapmaster.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

data class RespuestaClima(
    val ubicacion: String,
    val descripcion: String,
    val temperaturaC: Float,
    val sensacionTermicaC: Float?,
    val vientoDireccion: Int?,
    val vientoVelocidadMs: Float?,
    val vientoRafagaMs: Float?,
    val humedad: Int?,
    val presion: Int?,
    val visibilidad: Int?,
    val nubosidad: Int?,
    val lluviaMmHora: Float?
)

class ClimaApi(
    private val client: OkHttpClient = OkHttpClient()
) {
    fun obtenerClima(lat: Double, lon: Double, lang: String = "es"): ResultadoClima {
        val apiKey = BuildConfig.OPEN_WEATHER_API_KEY
        if (apiKey.isEmpty()) {
            return ResultadoClima.Error("Falta OPEN_WEATHER_API_KEY en local.properties")
        }
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$apiKey&units=metric&lang=$lang"
        val request = Request.Builder().url(url).build()
        return try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: return ResultadoClima.Error("Respuesta vacía")
                if (!response.isSuccessful) {
                    val msg = try {
                        JSONObject(body).optString("message").takeIf { it.isNotBlank() }
                    } catch (_: Exception) {
                        null
                    }
                    return ResultadoClima.Error("HTTP ${response.code}${msg?.let { " - $it" } ?: ""}")
                }
                val json = JSONObject(body)
                val main = json.getJSONObject("main")
                val weatherArray = json.getJSONArray("weather")
                val weatherObj = if (weatherArray.length() > 0) weatherArray.getJSONObject(0) else null
                val wind = json.optJSONObject("wind")
                val rain = json.optJSONObject("rain")
                val clouds = json.optJSONObject("clouds")

                val ciudad = json.optString("name", "Ubicación")
                val descripcion = weatherObj?.optString("description", "")?.replaceFirstChar { it.uppercase() } ?: ""
                val tempC = main.optDouble("temp", Double.NaN).toFloat()
                val sensacion = main.optDouble("feels_like", Double.NaN).toFloat()
                val humedad = main.optInt("humidity", -1).takeIf { it >= 0 }
                val presion = main.optInt("pressure", -1).takeIf { it >= 0 }
                val visibilidad = json.optInt("visibility", -1).takeIf { it >= 0 }
                val nubosidad = clouds?.optInt("all", -1).takeIf { (clouds?.optInt("all", -1)
                    ?: -1) >= 0 }
                val lluvia = rain?.optDouble("1h", Double.NaN)?.toFloat()
                val vientoDir = wind?.optInt("deg")
                val vientoVel = wind?.optDouble("speed")?.toFloat()
                val vientoRafaga = wind?.optDouble("gust")?.toFloat()

                ResultadoClima.Ok(
                    RespuestaClima(
                        ubicacion = ciudad,
                        descripcion = descripcion,
                        temperaturaC = tempC,
                        sensacionTermicaC = sensacion.takeIf { it.isFinite() },
                        vientoDireccion = vientoDir,
                        vientoVelocidadMs = vientoVel,
                        vientoRafagaMs = vientoRafaga,
                        humedad = humedad,
                        presion = presion,
                        visibilidad = visibilidad,
                        nubosidad = nubosidad,
                        lluviaMmHora = lluvia
                    )
                )
            }
        } catch (e: Exception) {
            ResultadoClima.Error("Error de red: ${e.message}")
        }
    }
}

sealed class ResultadoClima {
    data class Ok(val datos: RespuestaClima) : ResultadoClima()
    data class Error(val mensaje: String) : ResultadoClima()
}
