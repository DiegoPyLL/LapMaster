# LapMaster

App móvil Android (Jetpack Compose) para tomar tiempos de pista de varios pilotos desde una sola pantalla, incluyendo laps y sectores, con clima/GPS y modo claro/oscuro.

## Funcionalidades actuales
- Tiempos por piloto (hasta 4) con botón “Marcar vuelta” y colores por piloto.
- Pantalla de sectores para piloto único con barra apilada y tiempos parciales.
- Banner de clima + estado GPS (mock) y resumen rápido de sesión/día.
- Historial por día + gráfico de vueltas con colores de piloto.
- Configuración rápida: tema claro/oscuro, layout zurdo/diestro, orientación (info).
- Navegación superior en pestañas.

## Estructura y stack
- Namespace/appId: `com.lapmaster`.
- Compose + Material3, ViewModel + StateFlow (MVVM).
- Temas claro/oscuro (`LapMasterTheme`), colores de alto contraste.

## Cómo correr
1) Configura `JAVA_HOME` a un JDK 17.
2) Sincroniza/compila: `./gradlew assembleDebug` (descarga dependencias la primera vez).
3) Ejecuta desde Android Studio abriendo el módulo `app/`.

## Próximos pasos (pendientes)
- Conectar datos reales de clima/GPS utilizando una Api de clima chilena y libre de restricciones.
Esta debe ser rápida y gratis. https://openweathermap.org/api/one-call-3#how
- Cronometraje.
- Persistir pilotos/sesiones (Room) y servicio en primer plano.
- Edición de piloto (nombre/número/color) y selección de piloto para sectores.
- Animaciones
- Implementar API de Ubicación Nativa
- API de traducción

Diseño de referencia (Figma):  
https://www.figma.com/design/hHHBmt2PhofI3f03KKJ0MD/Multicron%C3%B3metro-Racing?node-id=1-2&p=f&t=9yh1ywg0UXCzVq6u-0
