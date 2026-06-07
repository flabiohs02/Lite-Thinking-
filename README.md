# 🛒 Lite Thinking — Sistema de Ventas y Administración (E-commerce)

Aplicación **full-stack** de gestión comercial compuesta por un **backend** construido con **Spring Boot (Java 25)** y un **frontend** moderno desarrollado en **Vue 3 + TypeScript**.

El sistema permite administrar el catálogo de productos, el control de inventarios, la gestión de empresas y categorías, la creación de órdenes de compra, y el manejo de usuarios con roles diferenciados (Administrador, Cliente y Visitante).

---

## Contenerización y Despliegue

El proyecto ya incluye:

- `backend/lite-thinking/Dockerfile`
- `frontend/lite-thinking-web/Dockerfile`
- `backend/lite-thinking/docker-compose.yml`
- Manifiestos de Kubernetes para GKE en `deploy/gke/`

La API del frontend se publica en la misma ruta del dominio usando `/api/v1`, mientras que el backend expone Swagger en `/swagger-ui.html` y OpenAPI en `/api-docs`.
Para desarrollo local, `docker compose` levanta el backend junto con PostgreSQL.

---

## 🛠️ Tecnologías Utilizadas

### Backend
| Tecnología | Detalle |
|---|---|
| **Lenguaje** | Java 25 |
| **Framework** | Spring Boot 4.x |
| **Seguridad** | Spring Security + JWT (JSON Web Tokens) |
| **Persistencia** | Spring Data JPA / Hibernate |
| **Base de Datos** | PostgreSQL 17 (contenedor Docker para local; servicio en GKE para despliegue) |
| **Gestor de Dependencias** | Maven 4.x (wrapper `./mvnw` incluido) |
| **Documentación API** | Springdoc OpenAPI / Swagger UI |

### Frontend
| Tecnología | Detalle |
|---|---|
| **Framework** | Vue 3 (Composition API / `<script setup>`) |
| **Herramienta de Construcción** | Vite |
| **Lenguaje** | TypeScript |
| **Manejo de Estado** | Pinia |
| **Estilos** | Vanilla CSS (diseño premium responsivo, gradientes, animaciones) |
| **Iconografía** | Lucide Vue |

---

## 📁 Estructura del Proyecto

```text
LiteThinking/
├── README.md
├── backend/
│   └── lite-thinking/                  # Proyecto Spring Boot (Maven)
│       ├── src/main/java/...
│       │   ├── application/            # DTOs y Casos de uso (Services)
│       │   ├── domain/                 # Modelos de dominio y contratos de repositorio
│       │   │   ├── model/              # Entidades: Company, Product, Category,
│       │   │   │                       #   Inventory, Order, OrderItem, Role, User
│       │   │   ├── repository/         # Interfaces de repositorio (puertos)
│       │   │   └── service/            # Contratos de servicio de dominio
│       │   ├── infrastructure/         # Adaptadores: JPA, Seguridad, Configuración
│       │   │   ├── persistence/entity/ # Entidades JPA (BaseEntity, etc.)
│       │   │   └── security/           # JwtService, filtros, configuración de seguridad
│       │   └── presentation/           # Controladores REST y manejo de excepciones
│       ├── src/main/resources/
│       │   ├── application.properties  # Configuración del servidor y base de datos
│       │   └── import.sql              # Datos iniciales (categorías, empresas, productos,
│       │                               #   inventarios, usuarios, órdenes)
│       └── pom.xml
└── frontend/
    └── lite-thinking-web/              # Aplicación Vue 3 (Vite)
        ├── src/
        │   ├── api/                    # Clientes HTTP (Axios)
        │   ├── components/             # Componentes reutilizables
        │   ├── stores/                 # Estado global con Pinia (auth.ts)
        │   ├── views/                  # Vistas principales:
        │   │   ├── HomeView.vue        #   Panel de bienvenida
        │   │   ├── LoginView.vue       #   Login / acceso visitante
        │   │   ├── StoreView.vue       #   Tienda pública y autenticada
        │   │   ├── ResourceView.vue    #   CRUD genérico (admin)
        │   │   ├── MyOrdersView.vue    #   Órdenes del cliente
        │   │   └── DashboardLayout.vue #   Estructura de navegación con sidebar
        │   ├── router.ts               # Definición de rutas y guardas de navegación
        │   ├── types.ts                # Tipos TypeScript globales
        │   └── styles.css              # Estilos CSS globales premium
        ├── index.html
        └── package.json
```

---

## 🗄️ Base de Datos

La aplicación usa **PostgreSQL** en contenedor Docker para desarrollo local. Hay dos formas soportadas:

- `docker run`: expone PostgreSQL en el puerto `15432`
- `docker compose` dentro de `backend/lite-thinking`: expone PostgreSQL en el puerto `5432`

| Parámetro | Valor |
|---|---|
| Host | `localhost` |
| Puerto `docker run` | `15432` |
| Puerto `docker compose` | `5432` |
| Base de datos | `litethinkingdb` |
| Usuario | `postgres` |
| Contraseña | `12345678` |

> [!NOTE]
> La configuración del backend toma la conexión a base de datos desde variables de entorno:
> `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.
> Por defecto mantiene valores locales para ejecutar el proyecto sin Docker.

### Datos Iniciales (`import.sql`)
El archivo `src/main/resources/import.sql` carga automáticamente:
- **4 Categorías** (Material Plástico, Equipo Portátil, Acer, Celulares)
- **2 Empresas** (Ktronix, Linio)
- **4 Productos** (Disco Duro ADATA, MacBook Neo, Portátil ACER, Xiaomi Poco X7 Pro) con imágenes en Base64
- **4 Inventarios** con stock
- **4 Roles** (admin, manager, externo, client)
- **3 Usuarios** de prueba
- **5 Órdenes** de ejemplo con sus ítems
- **Precios** en COP y USD por producto

---

## 👤 Usuarios de Prueba

| Rol | Identificación | Contraseña |
|---|---|---|
| **Admin** | `admin` | `12345678` |
| **Externo / Visitor** | `121236237` | `12345678` |
| **Cliente** | `cliente` | `12345678` |

---

## 🚀 Instrucciones de Configuración y Ejecución

### 1. Prerequisitos

- Java 25 o superior
- Maven 4.x (opcional — se incluye `./mvnw`)
- Node.js v18.x o superior
- Docker (para levantar PostgreSQL si no tienes una instancia local)

### 2. Levantar la Base de Datos (Docker)

Si usas Docker, puedes iniciar PostgreSQL con:
```bash
docker run --name postgres-db \
  -e POSTGRES_PASSWORD=12345678 \
  -e POSTGRES_DB=litethinkingdb \
  -p 15432:5432 \
  -d postgres:17.4
```

### 2.1 Levantar Backend + PostgreSQL Con Docker Compose

Desde `backend/lite-thinking`:

```bash
docker compose up -d --build
```

Esto levanta el backend en `http://localhost:8686` y PostgreSQL en `localhost:5432`.

### 2.2 Construir Imágenes Docker

```bash
docker build -t lite-thinking-backend backend/lite-thinking
docker build -t lite-thinking-frontend frontend/lite-thinking-web
```

### 3. Backend (Spring Boot)

El backend expone una API REST en el puerto **`8686`**.

```bash
# Navega al directorio del backend
cd backend/lite-thinking

# Ejecuta el servidor
./mvnw spring-boot:run
```

El servidor estará disponible en: **`http://localhost:8686`**

La documentación interactiva de la API (Swagger UI) estará en: **`http://localhost:8686/swagger-ui.html`**

> [!NOTE]
> Las peticiones `GET` públicas para el módulo de Empresas (`/api/v1/companies/**`) no requieren autenticación. Todas las demás operaciones requieren un token JWT válido en el header `Authorization: Bearer <token>`.

### 4. Frontend (Vue 3 + TypeScript)

```bash
# Navega al directorio del frontend
cd frontend/lite-thinking-web

# Instala las dependencias
npm install

# Inicia el servidor de desarrollo
npm run dev
```

La aplicación estará disponible en: **`http://localhost:5173`**

#### Construcción para producción
```bash
npm run build
```

### 5. Despliegue En GKE

Los manifiestos están en `deploy/gke/`. El orden recomendado es:

```bash
kubectl apply -f deploy/gke/namespace.yaml
kubectl apply -f deploy/gke/secret.yaml
kubectl apply -f deploy/gke/configmap.yaml
kubectl apply -f deploy/gke/postgres.yaml
kubectl apply -f deploy/gke/backend.yaml
kubectl apply -f deploy/gke/frontend.yaml
kubectl apply -f deploy/gke/ingress.yaml
```

---

## 🔌 API REST — Endpoints Principales

| Módulo | Endpoint Base | Descripción |
|---|---|---|
| **Auth** | `POST /api/v1/auth/login` | Autenticación y obtención de JWT |
| **Empresas** | `/api/v1/companies` | CRUD de empresas |
| **Categorías** | `/api/v1/categories` | CRUD de categorías |
| **Productos** | `/api/v1/products` | CRUD de productos con precios multi-moneda |
| **Inventarios** | `/api/v1/inventories` | Gestión de inventario por empresa |
| **Órdenes** | `/api/v1/orders` | Creación y consulta de órdenes de compra |
| **Usuarios** | `/api/v1/users` | Gestión de usuarios (solo admin) |
| **Roles** | `/api/v1/roles` | Gestión de roles (solo admin) |
| **Catálogo público** | `GET /api/v1/catalog` | Catálogo de productos sin autenticación |

> [!TIP]
> Consulta la documentación completa y prueba los endpoints directamente en Swagger UI: **`http://localhost:8686/swagger-ui.html`**

---

## 🔐 Roles y Permisos

| Rol | Acceso |
|---|---|
| **admin** | Acceso completo al dashboard: empresas, productos, categorías, inventarios, usuarios, roles y órdenes |
| **client** | Solo accede a la Tienda (`/dashboard/store`) y a sus propias Órdenes (`/dashboard/my-orders`) |
| **externo / visitor** | Solo accede al módulo de Empresas (`/dashboard/companies`); redirigido automáticamente si intenta acceder a otro módulo |

---

## ✨ Características Principales de la Interfaz (UI/UX)

1. **Tienda Virtual con Diseño Premium:**
   - Cuadrícula de productos en tarjetas modernas con efectos de elevación (`translateY`) y zoom de imagen en hover.
   - **Filtro Horizontal Avanzado:** Barra de búsqueda compacta con iconos interactivos (`Search`, `Tag`, `DollarSign`) e integrada por encima de la grilla de productos.

2. **Modal de Creación de Orden:**
   - Al hacer clic en **"Agregar"** en una tarjeta de producto, se abre un modal interactivo que calcula el total en tiempo real, valida la cantidad contra el stock del backend y genera la orden en un solo paso.

3. **Acceso para Visitantes:**
   - La pantalla de login incluye la opción de ingresar como **visitante externo** (`VISITOR`). Esta sesión especial permite revisar la lista de empresas activas sin necesidad de registrarse.

4. **Control de Rutas con Guardas de Navegación:**
   - Redirecciones automáticas garantizan que cada rol solo acceda a los módulos autorizados.
   - Al cerrar sesión, el usuario es redirigido al catálogo público (`/`).

5. **Página de Inicio Pública:**
   - La ruta `/` muestra la tienda en modo público (sin autenticación) con un acceso directo visible al Login.
