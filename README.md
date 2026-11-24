# A definir

Integrantes

- Nico
- Demian
- Gaby
- Lucas

# Forma de uso

```
src/main/java/ar/edu/unsam/ddso/tp/
└── TpApplication.java          # entry point
src/main/resources/
├── application.properties        # nombre de la app + perfil activo
├── application-local.properties  # H2 + JPA para desarrollo local
└── application-prod.properties   # H2 + JPA para "prod"
src/test/java/ar/edu/unsam/ddso/tp/
└── TpApplicationTests.java       # smoke test (contextLoads)
```

## Cómo correr los tests

```bash
./mvnw test
```

## Cómo ejecutar la app en local

```bash
./mvnw spring-boot:run
```

Perfil `local` por defecto (H2 in-memory, consola en `http://localhost:8080/h2-console`).

## Bot de Telegram

El token se lee de una variable de entorno. **Nunca lo subas al repo** (ni en `application-*.properties` commiteados).

```bash
export TELEGRAM_BOT_TOKEN="pegá-acá-el-token"
./mvnw spring-boot:run
```

Comandos mínimos: `/buscar texto`, `/tag nombre`, y el botón «ver más» para la página siguiente. El bot usa long polling en local.
