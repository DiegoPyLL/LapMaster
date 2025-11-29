# Multicronómetro Racing

App móvil para Android que permite manejar varios cronómetros a la vez desde una sola pantalla. Pensada para prácticas de pista, karting o cualquier situación donde necesites seguir múltiples tiempos en paralelo sin perder precisión.

## Qué hace

- Crea y gestiona varios cronómetros simultáneos con inicio/pausa/reset independientes.
- Muestra tiempos en vivo con una referencia de reloj común para evitar desfases.
- Permite etiquetar cada cronómetro para identificar pilotos, vueltas o pruebas.
- Opción de registrar laps parciales (en futuro cercano).
- Preparada para correr en primer plano con notificación persistente si se requiere mantener los tiempos activos al salir de la app.

## Pila y arquitectura

- UI en Jetpack Compose.
- Gestión de estado con ViewModel y casos de uso sencillos (start/stop/reset/lap).
- Persistencia planificada con Room para recuperar cronómetros si la app se reinicia.
- Uso de `SystemClock.elapsedRealtime()` para calcular tiempos con precisión y bajo consumo.

## Flujo de uso (a grandes rasgos)

1) Abrir la app y crear cronómetros con el botón de “Nuevo”.
2) Asignar un nombre o etiqueta a cada cronómetro.
3) Iniciar, pausar o reiniciar cada uno según se requiera.
4) Consultar los tiempos en pantalla o en la notificación (modo servicio en primer plano).

## Estado del proyecto

- Versión inicial en desarrollo. Se implementarán laps, almacenamiento local y servicio en primer plano en iteraciones siguientes.




https://www.figma.com/design/hHHBmt2PhofI3f03KKJ0MD/Multicron%C3%B3metro-Racing?node-id=1-2&p=f&t=9yh1ywg0UXCzVq6u-0
