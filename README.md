# login.invent

API de autenticacion construida con Spring Boot, Spring Security, Spring Data JPA, MySQL y JWT.

## Requisitos

- Java JDK 25.
- MySQL Server ejecutandose en `localhost:3306`.
- Una terminal con permisos para ejecutar el Maven Wrapper incluido (`mvnw.cmd` en Windows o `./mvnw` en Linux/macOS).

El proyecto usa Spring Boot `4.1.1` y genera la base de datos `multiservicespsidb` automaticamente si el usuario de MySQL tiene permisos para crearla.

## Configuracion de MySQL

La aplicacion se conecta con estos valores definidos en `src/main/resources/application.properties`:

```text
Host: localhost
Puerto: 3306
Base de datos: multiservicespsidb
Usuario: variable de entorno userMySQL
Contrasena: variable de entorno passMySQL
```

No es necesario crear la base de datos manualmente porque la URL incluye `createDatabaseIfNotExist=true`. Si el servidor MySQL no esta en el puerto 3306, hay que actualizar `spring.datasource.url`.

## Variables de entorno

Antes de iniciar la aplicacion, define estas tres variables:

| Variable | Uso |
| --- | --- |
| `userMySQL` | Usuario de MySQL |
| `passMySQL` | Contrasena de MySQL |
| `claveJWT` | Clave secreta JWT codificada en Base64 |

La clave JWT debe tener suficiente longitud para HMAC-SHA y ser una cadena Base64 valida. Ejemplo para una sesion de PowerShell:

```powershell
$env:userMySQL = "root"
$env:passMySQL = "tu-contrasena"
$env:claveJWT = "VGhpc0lzQVN1ZmZpY2llbnRseUxvbmdCYXNlNjRTZWNyZXRLZXlGb3JKV1Q="
```

En CMD:

```bat
set userMySQL=root
set passMySQL=tu-contrasena
set claveJWT=VGhpc0lzQVN1ZmZpY2llbnRseUxvbmdCYXNlNjRTZWNyZXRLZXlGb3JKV1Q=
```

En Linux/macOS:

```bash
export userMySQL=root
export passMySQL='tu-contrasena'
export claveJWT='VGhpc0lzQVN1ZmZpY2llbnRseUxvbmdCYXNlNjRTZWNyZXRLZXlGb3JKV1Q='
```

Para un entorno real, usa una clave generada de forma segura y no la guardes en el repositorio.

## Ejecutar el proyecto

Desde la carpeta raiz de `login.invent`:

### Windows PowerShell

```powershell
./mvnw.cmd spring-boot:run
```

### Linux/macOS

```bash
./mvnw spring-boot:run
```

La API quedara disponible en `http://localhost:8080`.

Para generar el JAR y ejecutarlo:

```powershell
./mvnw.cmd clean package
java -jar target/login.invent-0.0.1-SNAPSHOT.jar
```

En Linux/macOS, sustituye `./mvnw.cmd` por `./mvnw`.

## Ejecutar las pruebas

```powershell
./mvnw.cmd test
```

La prueba de contexto necesita poder resolver la configuracion de MySQL y JWT. Si se ejecuta sin las variables de entorno anteriores, Spring no podra crear todos los beans de la aplicacion.

## API de autenticacion

Todas las peticiones usan JSON y la URL base es `http://localhost:8080`.

### Registrar un usuario

`POST /auth/register`

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"ana","email":"ana@example.com","password":"Secret123!"}'
```

Respuesta `200` con un token JWT:

```json
{
  "token": "eyJ..."
}
```

El registro devuelve error si el nombre de usuario o el correo ya existen.

### Iniciar sesion con usuario

`POST /auth/login`

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"ana","password":"Secret123!"}'
```

Tambien se puede iniciar sesion usando el correo:

```json
{
  "email": "ana@example.com",
  "password": "Secret123!"
}
```

### Autenticacion de rutas protegidas

Las rutas distintas de `/auth/**` requieren el token recibido en el registro o login:

```text
Authorization: Bearer <token>
```

La aplicacion usa sesiones sin estado. Los tokens expiran aproximadamente 24 minutos despues de su emision.

## Estructura principal

```text
src/main/java/promo67/login/invent/
├── Application.java       # Punto de entrada
├── auth/                  # Controlador, DTO y servicio de autenticacion
├── config/                # Beans y reglas de Spring Security
├── jwt/                   # Creacion y validacion de tokens JWT
└── models/                # Entidad User, rol y repositorio
```

## Notas de desarrollo

- `spring.jpa.hibernate.ddl-auto=update` actualiza el esquema al iniciar; no debe considerarse una estrategia de migraciones para produccion.
- `spring.jpa.show-sql=true` imprime las consultas SQL en la consola.
- Las contrasenas se almacenan usando BCrypt.
- No incluyas credenciales de MySQL ni la clave JWT en archivos versionados.