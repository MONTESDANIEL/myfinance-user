# Microservicio de Autenticación y Gestión de Usuarios

Este microservicio está encargado de gestionar la autenticación de usuarios y la manipulación de sus datos personales a través de dos controladores principales:

1. **Controlador de Autorización**: Maneja el inicio de sesión, registro, recuperación de contraseña, restablecimiento de contraseña, cierre de sesión y la generación y validación de tokens de acceso.
2. **Controlador de Usuario**: Proporciona acceso y gestión de la información personal del usuario, incluyendo la visualización, actualización y eliminación de la cuenta.

## Endpoints del Microservicio

### 1. Controlador de Autorización (Authentication Controller)

#### `POST /auth/login`
- **Descripción**: Inicia sesión de un usuario y devuelve un token de acceso.
- **Parametros**: 
  - `email`: Correo electrónico del usuario.
  - `password`: Contraseña del usuario.
- **Respuesta**: Token JWT válido para la sesión y mensaje de exito o error.

#### `POST /auth/register`
- **Descripción**: Registra un nuevo usuario.
- **Parametros**:
  - `id`: Identificación del usuario.
  - `idType` : Tipo de identificacion del usuario.
  - `name`: Nombre del usuario.
  - `email`: Email del usuario.
  - `phoneNumber`: Telefono del usuario.
  - `birthDate`: Correo electrónico del usuario.
  - `password`: Contraseña del usuario.
- **Respuesta**: Mensaje de éxito o error.

#### `POST /auth/password-recovery`
- **Descripción**: Inicia el proceso de recuperación de contraseña, enviando un enlace de recuperación al correo electrónico del usuario.
- **Parametros**:
  - `email`: Correo electrónico del usuario.
- **Respuesta**: Token JWT válido para la actualización de contraseña y mensaje de éxito o error.

#### `POST /auth/reset-password`
- **Descripción**: Restablece la contraseña del usuario con el enlace de recuperación enviado previamente.
- **Parametros**:
  - `token`: Token de recuperación enviado por correo electrónico.
  - `newPassword`: Nueva contraseña del usuario.
  - `confirmPassword`: Confirmación de contraseña del usuario.
- **Respuesta**: Mensaje de éxito o error.

#### `POST /auth/logout`
- **Descripción**: Cierra la sesión del usuario invalidando el token de acceso.
- **Parametros**:
  - `token`: Token de sesión del usuario para desloguear.
- **Respuesta**: Mensaje de éxito.

### 2. Controlador de Usuario (User Controller)

#### `GET /user/profile`
- **Descripción**: Obtiene la información personal del usuario basado en el token de acceso.
- **Respuesta**: Datos del usuario (nombre, correo electrónico, fecha de nacimiento, etc.).

#### `PUT /user/update`
- **Descripción**: Actualiza los datos personales del usuario.
- **Parametros**:
  - `id`: Identificación del usuario.
  - `idType` : Tipo de identificacion del usuario.
  - `name`: Nombre del usuario.
  - `email`: Email del usuario.
  - `phoneNumber`: Telefono del usuario.
  - `birthDate`: Correo electrónico del usuario.
  - `password`: Contraseña del usuario.
- **Respuesta**: Actualiza los datos que se pueden actualizar (name, email, phoneNumber) y mensaje de éxito o error.

#### `PUT /user/update-password`
- **Descripción**: Actualiza la contraseña del usuario.
- **Parametros**:
  - `currentPassword`: Contraseña actual del usuario.
  - `newPassword`: Nueva contraseña del usuario.
  - `confirmPassword`: Confirmación de contraseña del usuario.
- **Respuesta**: Mensaje de éxito o error.

#### `DELETE /user/delete`
- **Descripción**: Elimina la cuenta del usuario.
- **Respuesta**: Mensaje de éxito o error.

## Tecnologías Utilizadas

- **Spring Boot**: Framework utilizado para construir el microservicio.
- **JWT (JSON Web Tokens)**: Utilizado para la autenticación y autorización segura.
- **Spring Security**: Para la gestión de la seguridad, como la protección de rutas y validación de contraseñas.
- **MySQL**: Base de datos para almacenar la información del usuario.

## Cómo Ejecutar el Microservicio

### Prerequisitos

- Java 11 o superior.
- MySQL Server en ejecución.

### Pasos para Ejecutar

1. **Clonar el repositorio**:

    ```bash
    git clone https://github.com/tu-usuario/nombre-del-repositorio.git
    cd nombre-del-repositorio
    ```

2. **Configurar la base de datos**:
    - Crea una base de datos en MySQL (si aún no tienes una):
      ```sql
        CREATE DATABASE user_service_db;

        USE user_service_db;

        CREATE TABLE users (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- Campo id, auto incremental
            id_type VARCHAR(255) NOT NULL,            -- Tipo de ID (por ejemplo, 'CC', 'CE', 'NIT', 'PAS', 'NIE', 'RUT', 'CEX')
            name VARCHAR(255) NOT NULL,              -- Nombre del usuario
            email VARCHAR(255) UNIQUE NOT NULL,      -- Correo electrónico único
            phone_number BIGINT,                    -- Número de teléfono
            birth_date DATE,                         -- Fecha de nacimiento
            password VARCHAR(255) NOT NULL           -- Contraseña del usuario
        );
      ```

3. **Configurar las credenciales de la base de datos**:
    - Actualiza las credenciales en el archivo `application.properties`:

      ```properties
      spring.datasource.url=jdbc:mysql://localhost:3306/user_service_db
      spring.datasource.username=usuario
      spring.datasource.password=contraseña
      ```

4. **Ejecutar la aplicación**:
    - En el directorio raíz del proyecto, ejecuta el siguiente comando para iniciar el microservicio:
    
      ```bash
      ./mvnw spring-boot:run
      ```