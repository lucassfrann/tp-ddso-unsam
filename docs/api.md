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
| `GET` | `/pieces/search?name=&description=&from=&to=&tags=&page=&size=` | Busca **piezas**. `from`/`to`: rango sobre la fecha de carga. `name` y `description`: *contains* case-insensitive (la descripción mira la pieza y sus digitalizaciones). `tags`: varios ids o nombres, combinados con **AND**. El Repository aplica `DISTINCT` y pagina (`page`, `size`). |

El bot no es un endpoint más de esta tabla: el `TelegramSearchHandler` llama al mismo `SearchService` que `GET /pieces/search`.
