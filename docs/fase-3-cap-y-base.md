# Fase 3: Aplicacion de los Principios CAP y BASE

Analisis de como el diseno del sistema de gestion de usuarios (MongoDB) se relaciona con
el Teorema CAP y el modelo BASE.

## 1. Teorema CAP

El Teorema CAP establece que un sistema distribuido no puede garantizar simultaneamente las
tres propiedades siguientes; ante una particion de red debe sacrificar una:

| Propiedad | Significado |
|-----------|-------------|
| **C - Consistencia** | Toda lectura recibe el dato mas reciente o un error. |
| **A - Disponibilidad** | Toda peticion recibe respuesta (sin garantia de que sea la mas reciente). |
| **P - Tolerancia al particionado** | El sistema sigue operando aunque se pierdan mensajes entre nodos. |

En un sistema distribuido real la particion (P) es inevitable, por lo que la eleccion practica
es entre **CP** y **AP**.

## 2. Donde se ubica MongoDB en CAP

MongoDB, en una configuracion de replica set, es por defecto un sistema **CP**
(Consistencia + Tolerancia al particionado):

- Cada replica set tiene un nodo **primary** que recibe las escrituras.
- Si el primary queda aislado por una particion, el sistema promueve un nuevo primary
  y el antiguo deja de aceptar escrituras: se prioriza consistencia sobre disponibilidad
  de escritura.
- Las lecturas y escrituras se pueden ajustar con **read/write concerns**.

### Relacion con este proyecto

- El proyecto usa una unica base `userservice` sobre MongoDB. En despliegue productivo con
  replica set, hereda el comportamiento **CP** de MongoDB.
- Las operaciones CRUD (`save`, `findById`, `findAll`, `deleteById`) se ejecutan contra el
  primary a traves de `MongoTemplate`, obteniendo consistencia de lectura tras escritura
  en la configuracion por defecto.

## 3. Modelo BASE

BASE es el enfoque tipico de las bases NoSQL, contrapuesto a ACID de las relacionales:

| Sigla | Significado | Aplicacion en el diseno |
|-------|-------------|-------------------------|
| **BA - Basically Available** | El sistema responde siempre, aunque parte de los datos este degradada. | MongoDB con replica set mantiene disponibilidad de lectura ante caida de nodos secundarios. |
| **S - Soft state** | El estado puede cambiar con el tiempo sin nuevas escrituras (por replicacion). | Los datos escritos en el primary se propagan a los secundarios de forma asincrona. |
| **E - Eventually consistent** | Con el tiempo, todos los nodos convergen al mismo valor. | Una lectura desde un secundario puede devolver un valor levemente atrasado hasta que la replicacion termina. |

## 4. Decisiones de diseno implicadas

| Decision | Opcion tomada | Justificacion |
|----------|---------------|---------------|
| Tipo de base | NoSQL documental (MongoDB) | Modelo flexible de usuario (id, name, email), escalable horizontalmente. |
| CP vs AP | CP (default de MongoDB) | En fintech la consistencia de datos de usuario es prioritaria sobre disponibilidad total de escritura. |
| Consistencia de lectura | Lectura desde primary (default) | Evita leer datos desactualizados en operaciones criticas. |
| Escalabilidad | Sharding/replica set (a nivel infra) | Permite tolerancia al particionado y crecimiento horizontal. |

## 5. Errores comunes al aplicar CAP y BASE

- **Creer que se pueden tener C, A y P a la vez:** ante particion siempre se sacrifica una.
- **Asumir consistencia fuerte por defecto en lecturas desde secundarios:** si se habilita
  lectura desde secundarios, se acepta consistencia eventual.
- **Ignorar los write concerns:** un `w=1` confirma en el primary pero no garantiza que la
  escritura se haya replicado; un fallo inmediato del primary podria perder el dato.
- **Tratar una base BASE como si fuera ACID:** esperar transacciones fuertes multi-documento
  sin configurarlas explicitamente.

## 6. Resumen

El sistema implementa CRUD sobre MongoDB, una base NoSQL documental que sigue el modelo BASE
y se comporta como CP dentro del Teorema CAP en su configuracion por defecto. Las decisiones
de consistencia (lectura desde primary, write concerns) priorizan la integridad de los datos
de usuario, adecuado para un contexto fintech, aceptando consistencia eventual en la
replicacion hacia nodos secundarios.
