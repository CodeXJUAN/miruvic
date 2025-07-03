# Miruvic

Miruvic es una aplicación modular para la gestión de reservas, estudiantes, habitaciones, servicios y pagos, diseñada con una arquitectura flexible que soporta múltiples implementaciones de persistencia (JDBC y JPA).

## Estructura del Proyecto

```plaintext
    miruvic/
├── app/                # Aplicación principal
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
- **app**: Módulo de aplicación principal (puede contener lógica de negocio y/o interfaz de usuario).

### Prerrequisitos

- Java 17+
- Gradle 7+
- MySQL (para pruebas de integración)

### Configuración de Base de Datos

Asegúrate de tener una base de datos MySQL llamada `miruvic_test` y un usuario con permisos. Puedes modificar las credenciales en los archivos de configuración de cada módulo (`datasoruce.properties`, `persistence.xml`, etc.).

### Compilar el Proyecto

```bash
./gradlew build
```

### Ejecutar Pruebas

```bash
./gradlew test
```

## Pruebas de Integración

Cada módulo incluye pruebas de integración para sus respectivos repositorios. Por ejemplo:

- `jdbc/src/test/java/.../JdbcStudentRepositoryIT.java`
- `jpa/src/test/java/.../JpaStudentIT.java`

Estas pruebas crean y limpian las tablas necesarias automáticamente y validan las operaciones CRUD y consultas específicas.

## Dependencias Principales

- JUnit Jupiter (pruebas)
- Hibernate (JPA)
- MySQL Connector/J
- H2 Database (para pruebas en memoria)
- Lombok

## Contribución

1. Haz un fork del repositorio
2. Crea una rama (`git checkout -b feature/nueva-funcionalidad`)
3. Realiza tus cambios y haz commit (`git commit -am 'Añade nueva funcionalidad'`)
4. Haz push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

## Licencia

Este proyecto está licenciado bajo la Licencia Apache 2.0.

---

¿Tienes dudas o sugerencias? ¡No dudes en abrir un issue o contribuir!
