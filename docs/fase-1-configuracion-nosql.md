# Fase 1: Configuracion del Sistema y Conexion NoSQL

Documento de configuracion del entorno para operaciones CRUD sobre MongoDB.

## 1. Componentes necesarios

| Componente | Rol |
|------------|-----|
| Spring Boot 3.3.4 | Framework base de la aplicacion |
| Spring Data MongoDB (`spring-boot-starter-data-mongodb`) | Integracion con MongoDB (autoconfigura `MongoTemplate`) |
| MongoDB | Base de datos NoSQL orientada a documentos |
| Flapdoodle Embedded Mongo (`de.flapdoodle.embed.mongo.spring3x`) | MongoDB embebido en memoria para pruebas, sin instalar servidor |

## 2. Conexion en ejecucion (perfil por defecto)

La conexion se define en `src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/userservice
```

- **Host/puerto:** `localhost:27017` (instalacion estandar de MongoDB).
- **Base de datos:** `userservice`.
- Spring Boot detecta esta propiedad y crea automaticamente el bean `MongoTemplate`,
  que `UserRepositoryImpl` inyecta para ejecutar las operaciones.

Para arrancar la aplicacion se requiere un MongoDB accesible en esa URI (local o Docker):

```bash
# Opcion Docker
docker run -d -p 27017:27017 --name mongo-userservice mongo:6.0
```

## 3. Conexion en pruebas (MongoDB embebido)

Los tests no requieren un MongoDB instalado. Flapdoodle levanta una instancia embebida
al iniciar el contexto de test.

`src/test/resources/application.properties`:

```properties
spring.data.mongodb.database=userservice-test
de.flapdoodle.mongodb.embedded.version=6.0.8
```

- La anotacion `@AutoConfigureDataMongo` en la clase de test activa la autoconfiguracion
  del Mongo embebido.
- Se descarga (la primera vez) y arranca un binario de MongoDB 6.0.8 en memoria; al terminar
  las pruebas se detiene automaticamente.

## 4. Como se establece la conexion (flujo)

```
application.properties (URI)
        |
        v
Spring Boot autoconfig  ->  MongoClient  ->  MongoTemplate (bean)
        |
        v
UserRepositoryImpl (@Repository)  ->  operaciones CRUD sobre la coleccion "user"
```

## 5. Verificacion

- Ejecucion de pruebas con Mongo embebido: `mvn clean test`.
- Arranque real contra MongoDB local: `mvn spring-boot:run` (requiere Mongo en `localhost:27017`).
