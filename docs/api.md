# api.md

Borrador de la API web (sin pantallas). Los bodies son DTOs, nunca entidades JPA.
Errores: **400** invariante de dominio, **404** recurso no encontrado, **409** tag en uso (o nombre de tag repetido).

| Método | Ruta | Qué hace |
|---|---|---|
| `POST` | `/pieces` | Alta de una pieza. Fecha de carga la asigna el sistema. Nombre obligatorio. |
| `POST` | `/pieces/{id}/digitalizations` | Asocia una digitalización (URL `http`/`https` válida + descripción) a la pieza. 201 con la digitalización creada (`DigitalizationResponseDTO`, `tags` vacía). 400 si la URL es inválida o falta la descripción. 404 si la pieza no existe. |
| `GET` | `/tags` | Lista el conjunto de tags (`TagResponseDTO`), para ver de cuáles se puede elegir. 200. |
| `POST` | `/tags` | Crea un tag. Nombre único y no vacío. 201. |
| `PUT` | `/tags/{id}` | Renombra el tag (la asociación sigue por id). 200. 404 si no existe; 409 si el nombre ya está usado. |
| `DELETE` | `/tags/{id}` | Borra el tag. 204. 404 si no existe; **409** si está en uso. |
| `POST` | `/digitalizations/{id}/tags` | Asocia un tag existente a la digitalización (`tagWith`, sin duplicar). 404 si falta la digitalización o el tag. |
| `GET` | `/pieces/search?name=&description=&from=&to=&tags=&page=&size=` | Busca **piezas** (ver detalle abajo). 200 con la lista de `PieceResponseDTO`. 400 si el rango de fechas o la página son inválidos. |

El bot no es un endpoint más de esta tabla: el `TelegramSearchHandler` llama al mismo `SearchService` que `GET /pieces/search`.

## Búsqueda de piezas (`GET /pieces/search`)

Todos los parámetros son opcionales; sin ninguno devuelve la primera página de todas las piezas.

| Parámetro | Formato | Criterio |
|---|---|---|
| `name` | texto | *contains*, sin distinguir mayúsculas, sobre el nombre de la pieza |
| `description` | texto | *contains*, sin distinguir mayúsculas, sobre la descripción de la pieza **o** la de alguna de sus digitalizaciones |
| `from`, `to` | `yyyy-MM-dd` | rango **inclusivo** sobre la fecha de carga. `from` no puede ser posterior a `to` (400) |
| `tags` | **nombres** de tag, repetidos o separados por coma (`tags=foto&tags=familia` o `tags=foto,familia`) | AND: la pieza debe tener todos (a través de sus digitalizaciones). Sin distinguir mayúsculas. Un tag que no existe no devuelve resultados |
| `page` | entero ≥ 0, por defecto `0` | número de página |
| `size` | entero de 1 a 50, por defecto `20` | tamaño de página (400 si está fuera de rango) |

Los filtros se combinan con AND. El orden es fecha de carga descendente y, a igual fecha, id descendente. El filtrado, el `DISTINCT` y la paginación los hace el Repository.

Ejemplo: `GET /pieces/search?description=carta&from=2026-01-01&tags=foto,familia&page=0&size=10`
