# Chronix — migración sincronizada v1.0.0

Esta carpeta transforma la Chronix de escritorio en una arquitectura híbrida equivalente a Nexit:

- **Web/PWA:** interfaz responsive, offline shell, instalación en PC y sincronización por Supabase.
- **Datos:** tabla `public.chronix_records`, con tombstones para eliminaciones y copia local en navegador.
- **Android:** WebView compartiendo la misma web + `AlarmManager`, permisos de notificación/alarma exacta, restauración tras reinicio y barra de estado reservada.
- **Windows:** `ChronixAlarmBridge.exe` residente, puerto `127.0.0.1:51339`, endpoints `/health`, `/state`, `/test`, `/snooze` y arranque automático en el perfil del usuario.

## Funciones preservadas

- Cumpleaños anuales y búsqueda/gestión.
- Eventos con fecha, hora, lugar, comentario y kit.
- Recordatorios únicos, anuales y mensuales (cada 1–6 meses con mes de referencia).
- Consulta por fecha.
- Rango global de 1–365 días y accesos rápidos 5/7/14/30/45/60/90/120/180.
- Tema claro/oscuro.
- Programación de alerta independiente para cada día de la semana.
- Posponer alerta.
- Importación/exportación JSON y exportación Word.
- Datos originales incluidos en `migration/`; se importan automáticamente la primera vez que la cuenta entra si Chronix está vacía.

## Supabase

La migración SQL ya fue aplicada al proyecto `yerekyfrrkxioaniyasx`. El archivo se conserva en `supabase/schema.sql` para referencia/repetibilidad.

## Publicación recomendada

Crear repositorio público `yoandarz/chronix`, subir **el contenido de esta carpeta a la raíz** y activar GitHub Pages desde `main` / root. La app Android y el Bridge ya apuntan a `https://yoandarz.github.io/chronix/`.

## Android

El workflow `.github/workflows/build-android.yml` compila el APK desde GitHub Actions. Tras publicar la web, ejecutar el workflow y descargar `Chronix-Android-1.0.0`.

## Windows

Ejecutar `windows/ChronixAlarmBridge.exe` una vez. Después, Chronix > Ajustes > **Activar alarmas** y **Probar 1 min**. El Bridge queda residente y registra su arranque con Windows.

## Regla arquitectónica

Supabase **sincroniza**. Android AlarmManager y Chronix Alarm Bridge **disparan las alarmas localmente**. La PWA sola no se usa como reloj de alarma fiable con la aplicación cerrada.
