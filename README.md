# SmartShip Logistics Tracker

SmartShip Logistics Tracker es una kata fullstack para gestionar paquetes y su avance operativo en una empresa de logistica. El sistema permite autenticar usuarios con JWT, crear paquetes como administrador y mover paquetes entre estados mediante un tablero Kanban con drag and drop.

La solucion esta pensada como un MVP tecnico: prioriza reglas de negocio claras, separacion de responsabilidades, pruebas automatizadas y una experiencia frontend funcional sin sobrecargar la arquitectura.

## Stack

Backend:
- Java 21
- Spring Boot 3.2
- Spring Web
- Spring Security
- Spring Data JPA
- Bean Validation
- JWT con `jjwt`
- H2 en memoria
- OpenAPI / Swagger UI
- JUnit, Spring Security Test y JaCoCo

Frontend:
- Angular 17
- Standalone Components
- Signals
- Reactive Forms
- Angular Router Guards
- HTTP Interceptor
- Angular CDK Drag Drop
- Karma / Jasmine

## Arquitectura

El proyecto separa el dominio de negocio, la aplicacion, la infraestructura y la capa web. La intencion es que las reglas centrales, como las transiciones de estado de un paquete, no dependan de controladores HTTP ni de detalles de persistencia.

En frontend se sigue una estructura por responsabilidades: `core` para modelos, autenticacion e infraestructura HTTP; `features` para pantallas funcionales; y componentes pequenos para vistas reutilizables cuando aportan claridad.

## Backend Layers

`domain`
- Modelos del negocio: `Paquete`, `Usuario`, `Rol`, `EstadoPaquete`.
- Excepciones de dominio.
- Implementacion del State Pattern para controlar transiciones de estado.

`application`
- DTOs de entrada y salida.
- Servicios de aplicacion.
- Mappers entre entidades y respuestas HTTP.
- Orquestacion de casos de uso como login, creacion de paquetes y actualizacion de estado.

`infrastructure`
- Configuracion de seguridad.
- JWT service y filtro de autenticacion.
- Repositorios JPA.
- Seeder de usuarios demo.
- Configuracion OpenAPI.

`web`
- Controladores REST.
- Manejo centralizado de errores.
- Codigos HTTP para validaciones, autenticacion, autorizacion y reglas de negocio.

## Frontend Structure

`src/app/core/auth`
- `AuthService` con estado de sesion usando Signals.
- Guards de autenticacion y rol.

`src/app/core/http`
- Interceptor que agrega `Authorization: Bearer <token>`.

`src/app/core/models`
- Modelos tipados para autenticacion y paquetes.

`src/app/features/auth/login`
- Pantalla de login con Reactive Forms.

`src/app/features/board`
- Tablero de paquetes.
- Servicio de paquetes con Signals.
- Agrupacion por estado con `computed()`.
- Drag and drop con Angular CDK.

`src/app/features/packages/create-package`
- Formulario de creacion de paquetes.
- Validaciones reactivas.
- Acceso protegido por `adminGuard`.

## Base De Datos

Se usa H2 en memoria porque la kata representa un MVP y una prueba tecnica time-boxed. Esta eleccion reduce friccion de instalacion, evita dependencias externas y permite levantar backend, ejecutar pruebas y revisar la funcionalidad rapidamente.

Para un entorno productivo, la decision natural seria migrar a PostgreSQL o MySQL, agregar migraciones con Flyway o Liquibase y configurar persistencia durable por ambiente.

## Business Rules

- Todo paquete inicia en estado `RECIBIDO`.
- Transicion valida: `RECIBIDO -> EN_TRANSITO`.
- Transicion valida: `EN_TRANSITO -> ENTREGADO`.
- `ENTREGADO` es estado terminal.
- No se permite permanecer en el mismo estado mediante PATCH.
- No se permite saltar de `RECIBIDO` a `ENTREGADO`.
- No se permite volver de `EN_TRANSITO` a `RECIBIDO`.
- Las transiciones invalidas devuelven HTTP `422`.
- `peso` debe ser mayor a 0.
- `largo`, `ancho` y `alto` deben ser mayores a 0.
- `destinatario` es obligatorio.

## State Pattern

El backend usa State Pattern para encapsular las reglas de transicion de paquetes. Cada estado tiene un handler responsable de decidir si una transicion destino es valida:

- `RecibidoHandler`: permite avanzar a `EN_TRANSITO`.
- `EnTransitoHandler`: permite avanzar a `ENTREGADO`.
- `EntregadoHandler`: rechaza cualquier transicion porque es estado terminal.

Esta decision evita que las reglas queden dispersas en controladores o servicios, facilita agregar nuevos estados y hace que las pruebas de negocio sean directas.

## JWT Roles

`ADMINISTRADOR`
- Puede iniciar sesion.
- Puede listar paquetes.
- Puede crear paquetes.
- Puede cambiar estado de paquetes.

`REPARTIDOR`
- Puede iniciar sesion.
- Puede listar paquetes.
- Puede cambiar estado de paquetes.
- No puede crear paquetes.

En frontend:
- `/board` requiere autenticacion.
- `/packages/create` requiere autenticacion y rol `ADMINISTRADOR`.
- Usuarios no autenticados son redirigidos a `/login`.
- Usuarios sin permisos para crear paquetes son redirigidos a `/board`.

## Endpoints

Auth:

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Respuesta:

```json
{
  "token": "jwt-token",
  "rol": "ADMINISTRADOR"
}
```

Paquetes:

```http
GET /api/paquetes
Authorization: Bearer <token>
```

```http
POST /api/paquetes
Authorization: Bearer <token>
Content-Type: application/json

{
  "peso": 3.5,
  "dimensiones": {
    "largo": 20,
    "ancho": 12,
    "alto": 8
  },
  "destinatario": "Cliente Uno"
}
```

```http
PATCH /api/paquetes/{id}/estado
Authorization: Bearer <token>
Content-Type: application/json

{
  "nuevoEstado": "EN_TRANSITO"
}
```

Estados permitidos:
- `RECIBIDO`
- `EN_TRANSITO`
- `ENTREGADO`

OpenAPI:
- `http://localhost:8080/swagger-ui.html`

## Usuarios Demo

| Usuario | Password | Rol |
| --- | --- | --- |
| `admin` | `admin123` | `ADMINISTRADOR` |
| `repartidor` | `rep123` | `REPARTIDOR` |

## Como Ejecutar Backend

Desde la carpeta `backend`:

```bash
mvn spring-boot:run
```

El backend queda disponible en:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

H2 Console:

```text
http://localhost:8080/h2-console
```

Datos H2:
- JDBC URL: `jdbc:h2:mem:smartshipdb`
- User: `sa`
- Password: vacio

## Como Ejecutar Frontend

Desde la carpeta `frontend`:

```bash
npm install
npm start
```

La aplicacion queda disponible en:

```text
http://localhost:4200
```

## Tests Y Cobertura

Backend:

```bash
cd backend
mvn test
```

Cobertura backend con JaCoCo:

```bash
cd backend
mvn verify
```

Reporte de cobertura:

```text
backend/target/site/jacoco/index.html
```

Resultado verificado:
- Comando: `mvn clean verify`
- Tests: 32 OK
- JaCoCo: todos los checks de cobertura cumplidos

Frontend:

```bash
cd frontend
npm test -- --watch=false
```

Tests frontend con cobertura:

```bash
cd frontend
npm run test -- --watch=false --code-coverage
```

Resultado verificado:
- Tests: 14 OK
- Statements: 90.32%
- Branches: 86.66%
- Functions: 88.88%
- Lines: 90.99%

Build frontend:

```bash
cd frontend
npm run build
```

En Windows, si PowerShell bloquea `npm.ps1`, usar:

```powershell
npm.cmd run build
npm.cmd run test -- --watch=false
```

## AI Skill Log

Prompts usados para arquitectura:
- "Implementar autenticacion frontend con Angular 17+, Standalone Components, Signals, JWT, guards por rol e interceptor HTTP."
- "Implementar solo el tablero de paquetes con Angular CDK Drag Drop, Signals, computed para agrupar por estado y rollback ante error 422."
- "Implementar solo el formulario de creacion de paquetes con Reactive Forms, validaciones y navegacion al tablero."

Prompts usados para pruebas:
- "Agregar pruebas minimas para AuthService, authGuard e interceptor."
- "Probar carga de paquetes, actualizacion de estado, agrupacion por estado y rollback ante error."
- "Probar que el formulario invalido no envia, el formulario valido hace POST, el exito navega a /board y el error backend muestra mensaje en espanol."

Human refactors aplicados:
- Se limito el alcance por iteracion para evitar implementar funcionalidades fuera de la tarea.
- Se separo estado/API en servicios y se mantuvieron componentes delgados.
- Se mantuvo el tablero independiente del formulario de creacion.
- Se agregaron placeholders solo cuando eran necesarios para rutas protegidas.
- Se ajustaron pruebas para evitar navegaciones reales innecesarias durante Karma.
- Se priorizo texto de UI en espanol y estructura Angular standalone.

## Final Delivery Notes

- Backend completo en `http://localhost:8080`.
- Frontend completo en `http://localhost:4200`.
- Autenticacion JWT implementada.
- Roles `ADMINISTRADOR` y `REPARTIDOR` implementados.
- Tablero de paquetes con drag and drop implementado.
- Rollback frontend ante error de transicion implementado.
- Formulario de creacion de paquetes implementado para administradores.
- OpenAPI disponible en `http://localhost:8080/swagger-ui.html`.
- H2 fue elegido deliberadamente por velocidad de entrega, reproducibilidad local y simplicidad para una prueba tecnica.
