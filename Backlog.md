# Backlog.md

Historias del TP Archivo Digital (decisiones del 23/11/2026, con correcciones de revisión). Integrantes: Nico, Demian, Gaby y Lucas. Nombre del grupo: a definir.

Issues: https://github.com/lucassfrann/tp-ddso-unsam/issues

- https://github.com/lucassfrann/tp-ddso-unsam/issues/1 — US-01 Alta de una pieza
- https://github.com/lucassfrann/tp-ddso-unsam/issues/2 — US-02 Asociar una digitalización
- https://github.com/lucassfrann/tp-ddso-unsam/issues/3 — US-03 Administrar el conjunto de tags
- https://github.com/lucassfrann/tp-ddso-unsam/issues/4 — US-04 Asociar tags a una digitalización
- https://github.com/lucassfrann/tp-ddso-unsam/issues/5 — US-05 Buscar piezas
- https://github.com/lucassfrann/tp-ddso-unsam/issues/6 — US-06 Bot de Telegram
- https://github.com/lucassfrann/tp-ddso-unsam/issues/7 — T-01 Base del repo y esqueleto de capas
- https://github.com/lucassfrann/tp-ddso-unsam/issues/8 — T-02 README, /docs/ y recorrida feliz

Cada integrante tiene que tener un commit de código (no solo docs) y un test unitario de un Service, listo para explicar. Tests: JUnit 5 + Mockito, sin Spring, repositorio mockeado, `Given` / `When` / `Then`. Controller y Handler solo hablan en DTOs; el Service traduce a dominio.

## Prioridad y reparto

**Prioridad 1 — T-01 (esqueleto de capas): hoy o mañana.** Después: US-01 y US-03 en paralelo → US-02 y US-04 → US-05 y US-06 → T-02.

Los roles A/B/C/D quedan **a asignar**. Si el rol D (búsqueda + bot) queda cargado, el `TelegramSearchHandler` se puede pasar a otra persona; el test obligatorio de quien haga la búsqueda sigue siendo el de `SearchService`.

| Rol | Alcance | Código | Test unitario de Service |
|---|---|---|---|
| A | Alta de pieza | `Piece`, repo, `PieceService`, `PieceController` | crear una pieza válida; rechazar una sin nombre |
| B | Asociar digitalizaciones | `Digitalization`, repo, `DigitalizationService`, controller | `addDigitalization` feliz; 404 si la pieza no existe; rechazar URL o descripción inválidas |
| C | Catalogación | `Tag`, `TagService`, asociación, controller | crear/renombrar; `assertCanBeDeleted` cuando está en uso (409); `tagWith` sin duplicar |
| D | Búsqueda + bot | `SearchService`, `SearchController`, Handler | criterios acordados, `DISTINCT` y paginación; el resultado son piezas |

---

## Historias

### T-01 — Base del repo y esqueleto de capas
**Área:** Base. **Prioridad 1 (hoy o mañana).**

- El repo ya parte del template. `./mvnw test` y `./mvnw spring-boot:run` funcionan en local (perfil `local`, H2).
- Paquetes `controller`, `dto`, `service`, `model`, `repository` y `exception`, según `AGENTS.md`.
- `@RestControllerAdvice` para 400 (invariante), 404 (no encontrado) y 409 (tag en uso). Ningún Controller llama a un Repository ni devuelve una entidad.

### US-01 — Alta de una pieza
**Área:** Digitalización. **Rol:** A (a asignar).

Como operador, quiero dar de alta una pieza por API, para después asociarle digitalizaciones.

**Criterios**
- `POST /pieces` (no pantalla). Responde DTO, no entidad JPA. El nombre es obligatorio (400 si falta). La fecha es la de carga, la asigna el sistema.
- La pieza puede tener descripción (la búsqueda la mira, junto con la de sus digitalizaciones).
- Invariante rota → no se guarda (transacción en el Service).

**Test de Service:** crear una pieza válida; rechazar una sin nombre (repositorio mockeado).

### US-02 — Asociar una digitalización
**Área:** Digitalización. **Rol:** B (a asignar).

Como operador, quiero asociar a una pieza existente una digitalización (URL + descripción).

**Criterios**
- `POST /pieces/{id}/digitalizations`. Una pieza puede tener varias. Se crea con `piece.addDigitalization(url, descripcion)`.
- URL válida y descripción obligatoria (400 si no). Sin tags al crearla: lista vacía.
- Si la pieza no existe: 404 (lo resuelve el Service, no es regla de dominio).

**Test de Service:** asociación feliz; 404 si la pieza no está; invariante de URL o descripción.

### US-03 — Administrar el conjunto de tags
**Área:** Catalogación. **Rol:** C (a asignar).

Como administrador, quiero crear, renombrar y borrar tags en runtime. La API de tags es la de administración; no hay autenticación.

**Criterios**
- `POST /tags`, `PUT /tags/{id}`, `DELETE /tags/{id}`. El cambio es dato persistido. Renombrar no rompe asociaciones (son por id). `tag.rename(nombre)` rechaza un nombre vacío (400).
- Nombre único: si está repetido, el Service consulta al Repository y responde 409.
- No se puede borrar un tag en uso: el Service pregunta al Repository si está en uso y llama `tag.assertCanBeDeleted(enUso)`. Si está en uso → excepción de dominio y **409**. Si el tag no existe → **404**.

**Test de Service:** crear y renombrar; rechazar el borrado de un tag en uso (409); 404 si no está.

### US-04 — Asociar tags a una digitalización
**Área:** Catalogación. **Rol:** C (a asignar).

Como operador, quiero catalogar una digitalización con tags del conjunto actual.

**Criterios**
- `POST /digitalizations/{id}/tags`. Se asocia con `digitalization.tagWith(tag)`, sin duplicar.
- Digitalización o tag inexistente → 404 (Service). No se inventa un tag libre.
- Se puede asociar más de un tag a la misma digitalización.

**Test de Service:** `tagWith` con un tag existente; no duplicar; 404 si falta la digitalización o el tag.

### US-05 — Buscar piezas
**Área:** Disponibilización. **Rol:** D (a asignar).

Como consultante o cliente de la API, quiero buscar piezas por fecha, nombre, descripción y tags.

**Criterios**
- `GET /pieces/search?name=&description=&from=&to=&tags=&page=&size=`. Devuelve DTOs de **piezas**.
- `from`/`to`: rango sobre la fecha de carga. `name` y `description`: *contains* case-insensitive. La descripción se matchea en la pieza y en sus digitalizaciones. Varios tags se combinan con **AND**.
- La consulta vive en el Repository: filtros, `DISTINCT` y paginación.

**Test de Service:** un criterio devuelve la pieza esperada; tags con AND; descripción encuentra por la pieza o por una digitalización; sin match → vacío; se respeta la página.

### US-06 — Buscar y ver resultados en Telegram
**Área:** Visualización. **Rol:** D (a asignar). El Handler puede pasarse a otra persona si D queda cargado.

Como consultante, quiero buscar en un bot de Telegram y ver los resultados en el chat.

**Criterios**
- Comandos: `/buscar texto`, `/tag nombre`, y botón «ver más» (página siguiente). El Handler arma un `SearchRequestDTO` y llama al mismo `SearchService` que la API. Solo DTOs; no habla con el Repository.
- Long polling en local. Webhook solo con el extra de Render.
- Token en `TELEGRAM_BOT_TOKEN` (ejemplo en el README). Nunca commiteado.

**Test obligatorio:** el de `SearchService` (US-05).

### T-02 — README, `/docs/` y recorrida feliz
**Área:** Docs.

- README: nombre del grupo (a definir), integrantes, cómo levantar la app / tests, y el ejemplo de `TELEGRAM_BOT_TOKEN`.
- `/docs/`: diagramas de dominio, arquitectura y base; secuencias con capa (búsqueda por el bot y borrar tag en uso); `api.md`; `test.md`.
- Recorrida feliz: crear un tag, crear una pieza, crear una digitalización, catalogarla y buscarla (API y bot).

### Extras (optativos)

- EX-01 Render + Postgres (ahí puede entrar el webhook).
- EX-02 OCR o etiquetado de terceros (cliente HTTP en Fuente de datos).
- EX-03 Batch CSV. EX-04 = EX-02 + EX-03.
