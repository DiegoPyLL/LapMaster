package com.lapmaster

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.lapmaster.ui.model.GpsUi

/**
 * Proveedor sencillo basado en [LocationManager] que expone lecturas del GPS nativo.
 */
class ProveedorGpsNativo(
    context: Context,
    owner: LifecycleOwner,
    private val onGpsUpdate: (GpsUi) -> Unit
) : DefaultLifecycleObserver {

    private val locationManager = context.getSystemService(LocationManager::class.java)
    private val appContext = context.applicationContext
    private var escuchando = false

    private val listener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            onGpsUpdate(
                GpsUi(
                    tieneFijacion = true,
                    precisionMetros = location.accuracy,
                    latitud = location.latitude,
                    longitud = location.longitude,
                    altitudMetros = location.altitude
                )
            )
        }

        override fun onProviderEnabled(provider: String) {
            if (!escuchando) {
                iniciarSiPermiso()
            }
        }

        override fun onProviderDisabled(provider: String) {
            val gpsActivo = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
            val redActiva = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
            if (!gpsActivo && !redActiva) {
                onGpsUpdate(
                    GpsUi(
                        tieneFijacion = false,
                        precisionMetros = Float.MAX_VALUE,
                        latitud = null,
                        longitud = null
                    )
                )
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            // Metodo deprecado, se mantiene vacío por compatibilidad.
        }
    }

    init {
        owner.lifecycle.addObserver(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        iniciarSiPermiso()
    }

    override fun onPause(owner: LifecycleOwner) {
        detener()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        detener()
        owner.lifecycle.removeObserver(this)
    }

    fun tienePermisoUbicacion(): Boolean {
        val fine = ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    fun iniciarSiPermiso() {
        if (escuchando || !tienePermisoUbicacion()) return
        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            INTERVALO_MS,
            DISTANCIA_MIN_METROS,
            listener,
            Looper.getMainLooper()
        )
        locationManager?.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            INTERVALO_MS,
            DISTANCIA_MIN_METROS,
            listener,
            Looper.getMainLooper()
        )
        escuchando = true
        locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let(::propagarLocation)
            ?: locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.let(::propagarLocation)
    }

    fun detener() {
        if (!escuchando) return
        locationManager?.removeUpdates(listener)
        escuchando = false
    }

    private fun propagarLocation(location: Location) {
        onGpsUpdate(
            GpsUi(
                tieneFijacion = true,
                precisionMetros = location.accuracy,
                latitud = location.latitude,
                longitud = location.longitude,
                altitudMetros = location.altitude
            )
        )
    }

    companion object {
        private const val INTERVALO_MS = 1_500L
        private const val DISTANCIA_MIN_METROS = 1f
    }
}
