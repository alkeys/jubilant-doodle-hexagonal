# Probando

API REST , desarrollada con Spring Boot y organizada con arquitectura hexagonal (también conocida como Ports and Adapters).

El código de este proyecto es propietario. Consulta [LICENSE](LICENSE) para conocer las condiciones de uso.

todavia falta mucho para darle una horientacion completa pero lo are en estos dias 
## Tecnologías

- Java 27
- Spring Boot 4.1.1
- Gradle
- Spring Web MVC
- Spring Data JPA e Hibernate
- PostgreSQL
- Bean Validation
- Lombok
- OpenAPI/Swagger UI
- Docker Compose
- Testcontainers

## Arquitectura

El proyecto separa las reglas de negocio de los detalles externos mediante puertos y adaptadores:

```mermaid
flowchart LR
    Client[Cliente HTTP] --> Web[Adaptador de entrada REST]
    Web --> InPort[Puerto de entrada]
    InPort --> Service[Servicio de aplicación]
    Service --> Domain[Dominio Usuario]
    Service --> OutPort[Puerto de salida]
    OutPort --> Jpa[Adaptador JPA]
    Jpa --> PostgreSQL[(PostgreSQL)]
```

### Capas

- `domain`: contiene el modelo `Usuario` y sus reglas de negocio, como la validación de edad, apellido y email.
- `application/port/in`: define los casos de uso disponibles para la aplicación, actualmente la creación de usuarios.
- `application/service`: implementa los casos de uso y coordina el dominio con los puertos de salida.
- `application/port/out`: define la interfaz que necesita la aplicación para persistir usuarios.
- `infra/adapter/in/web`: expone la API REST, sus DTOs, mapeadores, validaciones y el manejador global de errores.
- `infra/adapter/out/persistence/jpa`: implementa la persistencia mediante Spring Data JPA, entidades y mapeadores.
- `shared/config`: contiene configuración compartida, como OpenAPI.

La dependencia principal apunta hacia el centro: el dominio y la aplicación no dependen directamente de Spring Data ni de PostgreSQL. Los adaptadores implementan los puertos definidos por la aplicación.

## Estructura principal

```text
src/
├── main/
│   ├── java/alex/probando/
│   │   ├── shared/config/
│   │   └── usuarios/
│   │       ├── application/
│   │       │   ├── port/in/
│   │       │   ├── port/out/
│   │       │   └── service/
│   │       ├── domain/
│   │       └── infra/adapter/
│   │           ├── in/web/
│   │           └── out/persistence/jpa/
│   └── resources/
├── test/java/
└── ...
```

## Requisitos

- JDK 27
- Docker y Docker Compose
- Gradle Wrapper incluido (`./gradlew`)

## Configuración y ejecución

La aplicación usa PostgreSQL en `localhost:5432` con la configuración definida en `src/main/resources/application.properties`. Para iniciar las dependencias:

```bash
docker compose up -d
```

Después, inicia la aplicación con:

```bash
./gradlew bootRun
```

En sistemas Windows:

```bat
gradlew.bat bootRun
```

La API estará disponible en `http://localhost:8080`.

## API

### Crear usuario

`POST /api/v1/usuarios`

Ejemplo de solicitud:

```json
{
  "nombre": "Juan Carlos",
  "apellido": "Bodoque",
  "email": "juan.bodoque@example.com",
  "edad": 30
}
```

Respuestas principales:

- `201 Created`: usuario creado correctamente.
- `400 Bad Request`: datos inválidos; la respuesta identifica el campo y el motivo.
- `409 Conflict`: ya existe un usuario con el mismo email.

El email se almacena como único en la tabla `usuarios`.

## Documentación OpenAPI

Con la aplicación ejecutándose, Swagger UI está disponible en:

`http://localhost:8080/swagger-ui/index.html`

## Pruebas

Ejecuta la suite con:

```bash
./gradlew test
```

El proyecto incluye soporte para pruebas con Testcontainers y servicios PostgreSQL y MongoDB. PostgreSQL es la base de datos usada actualmente por el adaptador JPA de usuarios.

## Licencia

Este proyecto es de uso propietario y no se distribuye bajo una licencia open source. Consulta [LICENSE](LICENSE) antes de copiar, modificar o redistribuir el código.
