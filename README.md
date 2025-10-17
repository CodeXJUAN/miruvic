# Miruvic

Miruvic es una aplicación modular para la gestión de reservas, estudiantes, habitaciones, servicios y pagos, diseñada con una arquitectura cliente-servidor que soporta múltiples implementaciones de persistencia (JDBC y JPA).

## Estructura del Proyecto

```plaintext
    miruvic/
├── client/             # Cliente de consola
├── server/             # Servidor de aplicación
├── jdbc/               # Implementación JDBC de los repositorios
├── jpa/                # Implementación JPA de los repositorios
├── model/              # Modelos de dominio y sus implementaciones
├── repositories/       # Interfaces de repositorios
├── gradle/             # Configuración de Gradle
├── buildSrc/           # Scripts y plugins de construcción
├── README.md           # Este archivo
└── 
```

## Módulos Principales

- **model**: Define las entidades principales (`Student`, `Address`, `Room`, `Service`, `Reservation`, `Payment`, etc.) y sus implementaciones.
- **repositories**: Contiene las interfaces de los repositorios para acceso a datos.
- **jdbc**: Implementación de los repositorios usando JDBC.
- **jpa**: Implementación de los repositorios usando JPA/Hibernate.
- **server**: Servidor de aplicación que expone servicios REST para la gestión de entidades.
- **client**: Cliente de consola que se comunica con el servidor para realizar operaciones.

### Prerrequisitos

- Java 17+
- Gradle 7+
- MySQL (base de datos principal)

### Configuración de Base de Datos

Asegúrate de tener una base de datos MySQL llamada `RUVIC` y un usuario con permisos. Puedes modificar las credenciales en los archivos de configuración:

- **server**: `server/src/main/resources/datasource.properties`
- **jdbc**: `jdbc/src/main/resources/datasource.properties`
- **jpa**: `jpa/src/main/resources/persistence.xml`

### Compilar el Proyecto

```bash
./gradlew build
```

### Ejecutar el Servidor

```bash
./gradlew :server:run
```

El servidor se iniciará en el puerto predeterminado y estará listo para recibir conexiones del cliente.

### Ejecutar el Cliente de Consola

```bash
./gradlew :client:run
```

El cliente de consola se conectará al servidor y permitirá realizar operaciones de gestión a través de una interfaz de línea de comandos.

### Ejecutar Pruebas

```bash
./gradlew test
```

## Pruebas de Integración

Cada módulo incluye pruebas de integración para sus respectivos repositorios. Por ejemplo:

- `jdbc/src/test/java/.../JdbcStudentRepositoryIT.java`
- `jpa/src/test/java/.../JpaStudentIT.java`

Estas pruebas crean y limpian las tablas necesarias automáticamente y validan las operaciones CRUD y consultas específicas.

## Arquitectura Cliente-Servidor

El proyecto utiliza una arquitectura cliente-servidor:

- **Servidor**: Implementado con Spark Java, expone endpoints REST para la gestión de entidades y utiliza HikariCP para la gestión eficiente de conexiones a la base de datos.
- **Cliente**: Aplicación de consola que se comunica con el servidor mediante peticiones HTTP y proporciona una interfaz de usuario para realizar operaciones de gestión.

## Dependencias Principales

- JUnit Jupiter (pruebas)
- Hibernate (JPA)
- MySQL Connector/J
- HikariCP (pool de conexiones)
- Lombok
- Spark Java (servidor web)
- Gson (serialización JSON)

## Contribución

1. Haz un fork del repositorio
2. Crea una rama (`git checkout -b feature/nueva-funcionalidad`)
3. Realiza tus cambios y haz commit (`git commit -am 'Añade nueva funcionalidad'`)
4. Haz push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

## Licencia

Este proyecto está licenciado bajo la Licencia del MIT.

---

¿Tienes dudas o sugerencias? ¡No dudes en abrir un issue o contribuir!
