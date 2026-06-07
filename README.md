# Lite Thinking

Sistema full-stack para administracion comercial y ventas. El proyecto incluye un backend REST con Spring Boot y un frontend con Vue 3 + TypeScript para gestionar catalogo, inventario, empresas, categorias, usuarios, roles, ordenes y resumen de dashboard.

## Tecnologias

### Backend

| Tecnologia | Detalle |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.6 |
| Seguridad | Spring Security + JWT |
| Persistencia | Spring Data JPA / Hibernate |
| Base de datos | PostgreSQL 17 |
| Build | Maven Wrapper |
| Documentacion API | Springdoc OpenAPI / Swagger UI |

### Frontend

| Tecnologia | Detalle |
|---|---|
| Framework | Vue 3 |
| Lenguaje | TypeScript |
| Build | Vite |
| Estado | Pinia |
| HTTP | Axios |
| Reportes | jsPDF + AutoTable |
| Iconos | Lucide Vue |
| Pruebas | Vitest |

## Estructura

```text
LiteThinking/
├── backend/
│   └── lite-thinking/
│       ├── Dockerfile
│       ├── docker-compose.yml
│       ├── pom.xml
│       └── src/
│           ├── main/java/com/lite/thinking/app/
│           │   ├── application/       # DTOs, mappers y casos de uso
│           │   ├── domain/            # Modelos, repositorios y excepciones
│           │   ├── infrastructure/    # JPA, seguridad y configuracion
│           │   └── presentation/      # Controladores REST y errores
│           └── main/resources/
│               ├── application.properties
│               └── import.sql
└── frontend/
    └── lite-thinking-web/
        ├── Dockerfile
        ├── nginx.conf
        ├── package.json
        └── src/
            ├── api/                  # Cliente HTTP y recursos
            ├── components/           # Componentes reutilizables
            ├── stores/               # Estado de autenticacion
            ├── utils/                # Formato y PDF
            ├── views/                # Pantallas principales
            ├── router.ts
            └── resourceConfig.ts
```

## Funcionalidades

- Autenticacion con JWT.
- Catalogo publico de productos.
- Dashboard autenticado con navegacion por roles.
- CRUD de empresas, categorias, productos, inventarios, usuarios y roles.
- Creacion y consulta de ordenes.
- Resumen de dashboard en `/api/v1/dashboard/summary`.
- Reporte PDF de inventario desde el frontend.
- Datos iniciales cargados con `import.sql`.
- Pruebas unitarias para servicios del backend y modulos principales del frontend.

## Usuarios de prueba

| Rol | Identificacion | Contrasena |
|---|---|---|
| Admin | `admin` | `12345678` |
| Externo / Visitor | `121236237` | `12345678` |
| Cliente | `cliente` | `12345678` |

## Requisitos

- Java 25 o superior.
- Node.js 18 o superior.
- Docker, si quieres levantar PostgreSQL en contenedor.
- Maven no es obligatorio porque el backend incluye `./mvnw`.

## Configuracion de base de datos

El backend usa PostgreSQL y toma la configuracion desde variables de entorno:

| Variable | Valor por defecto |
|---|---|
| `SERVER_PORT` | `8686` |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:15432/litethinkingdb` |
| `SPRING_DATASOURCE_USERNAME` | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | `12345678` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `create-drop` |

Para levantar solo PostgreSQL en local:

```bash
docker run --name postgres-db \
  -e POSTGRES_PASSWORD=12345678 \
  -e POSTGRES_DB=litethinkingdb \
  -p 15432:5432 \
  -d postgres:17.4
```

## Ejecucion local

### Backend

```bash
cd backend/lite-thinking
./mvnw spring-boot:run
```

El backend queda disponible en:

- API: `http://localhost:8686/api/v1`
- Swagger UI: `http://localhost:8686/swagger-ui.html`
- OpenAPI: `http://localhost:8686/api-docs`
- Health: `http://localhost:8686/actuator/health`

### Frontend

```bash
cd frontend/lite-thinking-web
npm install
npm run dev
```

La aplicacion queda disponible en `http://localhost:5173`.

Por defecto el frontend consume `http://localhost:8686/api/v1`. Puedes cambiarlo con:

```bash
VITE_API_BASE_URL=http://localhost:8686/api/v1 npm run dev
```

## Ejecucion con Docker Compose

Desde el directorio del backend:

```bash
cd backend/lite-thinking
docker compose up -d --build
```

Esto levanta:

| Servicio | URL / Puerto |
|---|---|
| Backend | `http://localhost:8686` |
| PostgreSQL | `localhost:5432` |

Para detener los servicios:

```bash
docker compose down
```

## Comandos utiles

### Backend

```bash
cd backend/lite-thinking
./mvnw test
./mvnw clean package
```

### Frontend

```bash
cd frontend/lite-thinking-web
npm run test
npm run build
npm run preview
```

### Imagenes Docker

```bash
docker build -t lite-thinking-backend backend/lite-thinking
docker build -t lite-thinking-frontend frontend/lite-thinking-web
```

## API principal

| Modulo | Endpoint base | Acceso |
|---|---|---|
| Auth | `POST /api/v1/auth/login` | Publico |
| Catalogo | `GET /api/v1/catalog/products` | Publico |
| Empresas | `/api/v1/companies` | GET publico, cambios solo admin |
| Categorias | `/api/v1/categories` | Lectura autenticada, cambios admin |
| Productos | `/api/v1/products` | Lectura autenticada, cambios admin |
| Inventarios | `/api/v1/inventories` | Lectura autenticada, cambios admin |
| Ordenes | `/api/v1/orders` | Crear/consultar autenticado, gestion admin |
| Usuarios | `/api/v1/users` | Admin |
| Roles | `/api/v1/roles` | Admin |
| Dashboard | `/api/v1/dashboard/summary` | Autenticado |

Las rutas protegidas requieren el header:

```text
Authorization: Bearer <token>
```

## Roles y permisos

| Rol | Permisos principales |
|---|---|
| `admin` | Acceso completo a modulos administrativos, dashboard, CRUD y ordenes. |
| `client` | Acceso a tienda, catalogo autenticado, creacion de ordenes y consulta de sus ordenes. |
| `externo` / `visitor` | Acceso limitado a empresas y vistas permitidas por la aplicacion. |

## Datos iniciales

El archivo `backend/lite-thinking/src/main/resources/import.sql` carga datos de ejemplo para:

- Categorias.
- Empresas.
- Productos con imagenes y precios.
- Inventarios.
- Roles.
- Usuarios.
- Ordenes e items.

Con `SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop`, la base se recrea al iniciar la aplicacion y vuelve a cargar estos datos.

## Notas de desarrollo

- El backend corre en el puerto `8686`.
- El frontend de desarrollo corre en el puerto `5173`.
- CORS esta habilitado para facilitar el desarrollo local.
- Swagger permite probar los endpoints directamente desde el navegador.
- El build de frontend genera archivos estaticos en `frontend/lite-thinking-web/dist`.
