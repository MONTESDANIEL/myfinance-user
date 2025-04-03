# 👤 MyFinance User

## 📌 Descripción

Este repositorio contiene el **microservicio de usuarios** de **MyFinance**, encargado de gestionar la autenticación, autorización y administración de usuarios en la plataforma.  
Además de manejar la seguridad mediante **JWT**, permite gestionar perfiles, roles y datos personales de los usuarios.

Este microservicio está desarrollado con **Spring Boot**, proporcionando una API REST robusta para gestionar la identidad y seguridad de los usuarios.

---

## ✨ Características Principales

- ✅ **Autenticación con JWT** – Inicio de sesión seguro con tokens.
- ✅ **Gestión de Usuarios** – Creación, actualización y eliminación de usuarios.
- ✅ **Perfiles de Usuario** – Manejo de información personal y configuraciones.
- ✅ **Roles y Permisos** – Control de acceso basado en roles (USER, ADMIN).
- ✅ **Recuperación de Contraseña** – Proceso de recuperación y restablecimiento seguro.
- ✅ **Verificación de Email** – Activación de cuenta a través de correo electrónico.
- ✅ **Protección de Endpoints** – Seguridad en API mediante Spring Security.

---

## 🛠 Tecnologías Utilizadas

- **Spring Boot** – Framework para el desarrollo del backend.
- **Spring Security & JWT** – Manejo de autenticación segura.
- **Spring Data JPA** – Interacción con la base de datos.
- **MySQL** – Base de datos relacional para almacenamiento.
- **Docker** – Contenedorización del microservicio.

---

## 🚀 Instalación y Ejecución

### 📌 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- **JDK 17 o superior**
- **Maven**
- **Docker** (opcional)
- **Base de datos MySQL**

### 📥 Clonar el Repositorio

```sh
git clone https://github.com/MONTESDANIEL/myfinance-user.git
cd myfinance-user
```

### 🗃️ Configurar la base de datos

```sh
Utilizar el archivo .sql del proyecto para generar la base.
```

### ⚙️ Configurar el application.properties

Ajustar el application.properties de la siguiente forma según la base de datos:

```sh
spring.datasource.url=           # Url de acceso a la base de datos.
spring.datasource.username=      # Usuario de la base de datos
spring.datasource.password=      # Contraseña de la base de datos
```

### 📦 Construir y Ejecutar el Proyecto

Para compilar y ejecutar el proyecto:

```sh
mvn clean install
mvn spring-boot:run
```

---

## 📂 Estructura del Proyecto

```sh
myfinance-user/
│── src/main/java/com/myfinance/backend/user/
│   ├── config/             # Configuración de autenticación y configuración
│   ├── controllers/        # Controladores REST
│   ├── entities/           # Entidades
│   ├── exceptions/         # Control de excepciones
│   ├── repositories/       # Acceso a la base de datos
│   ├── services/           # Lógica de negocio
│── src/main/java/com/myfinance/backend/user/resources/
│   ├── application.properties      # Configuración del microservicio
│── Dockerfile              # Configuración para contenedorización
│── user_db.sql         # Archivo de creación de la base de datos
│── README.md               # Documentación del repositorio
```

## 📜 Licencia

Este proyecto está bajo la licencia MIT, por lo que puedes usarlo y modificarlo libremente.

## ⛓️Relacionado

🔗 Repositorio Principal: [MyFinance](https://github.com/MONTESDANIEL/myfinance)
