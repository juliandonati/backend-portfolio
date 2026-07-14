# ⚙️ Backend API - Plataforma de Portafolios Web Multiusuario

Esta es la API RESTful que funciona como núcleo lógico y gestor de datos para la Plataforma de Portafolios Web. El sistema permite el registro de usuarios, autenticación, y la administración completa de perfiles profesionales (habilidades, experiencia, proyectos y enlaces).

> 💻 **Frontend Client:** [Enlace al repositorio del Frontend en ReactTS](https://github.com/juliandonati/frontend-portfolio-reactts)

## 🛠️ Stack Tecnológico

El proyecto está construido bajo una arquitectura en capas, priorizando la escalabilidad y las buenas prácticas:

*   **Core:** Java 21+ y Spring Boot.
*   **Persistencia de Datos:** Spring Data JPA, Hibernate y PostgreSQL (implementado en la nube mediante Neon.tech)
*   **Mapeo de Objetos:** MapStruct (para la transformación eficiente entre Entidades y DTOs).
*   **Almacenamiento en la Nube:** Cloudinary API (para la gestión y optimización de imágenes de perfil/proyectos).
*   **Documentación:** Swagger UI / OpenAPI 3.
*   **Testing:** JUnit 5 y Mockito para pruebas unitarias.
*   **Seguridad:** Spring Security y encriptación de contraseñas.

## ✨ Características Principales

*   **Autenticación y Autorización:** Registro de usuarios, encriptación de contraseñas y gestión de roles.
*   **Gestión de Perfil:** Endpoints para crear y actualizar información personal, sección "Sobre mí" y redes sociales.
*   **Módulos del Portafolio:** ABM (Alta, Baja y Modificación) de Habilidades, Experiencia Laboral y Proyectos.
*   **Gestión Multimedia:** Integración con Cloudinary para la subida segura de imágenes, devolviendo URLs optimizadas al frontend.
*   **Arquitectura Desacoplada:** Uso estricto de DTOs para no exponer la estructura de la base de datos al cliente.

## 🚀 Instalación y Configuración Local

Si querés clonar el proyecto y correrlo en tu entorno local, seguí estos pasos:

### Prerrequisitos
*   Java Development Kit (JDK) 21 o superior.
*   Maven.
*   Una instancia de base de datos PostgreSQL (local o en la nube)
*   Cuenta en Cloudinary (para las credenciales de API).

### Pasos

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/juliandonati/frontend-portfolio-reactts
   
2. Configurar las variables de entorno: El proyecto lee credenciales sensibles desde el entorno, por lo que antes de levantar la aplicación, tenés
que asegurarte de configurar las siguientes variables de entorno en tu sistema operativo, IDE, o archivo .env:
   ACTIVE_PROFILE: Perfil de Spring Boot activo (ej: dev, prod).

* ALLOWED_CORS_URL: Origen permitido para realizar peticiones a la API (ej: http://localhost:5173 para el cliente de React en desarrollo).

* CLOUDINARY_URL: URL completa de conexión a tu entorno de Cloudinary para la gestión de imágenes.

* DEFAULT_ADMIN_PASSWORD: Contraseña inicial asignada al rol de administrador por defecto al inicializar la base de datos.

* JWT_SECRET: Clave secreta (cadena de texto compleja o Base64) utilizada para firmar los tokens de autenticación.

* JWT_EXPIRATION: Tiempo de validez del token en milisegundos (ej: 86400000 para 24 horas).

* DB_URL: URL de conexión a la base de datos PostgreSQL (ej: jdbc:postgresql://<host>.neon.tech/<db>?sslmode=require).

* DB_USERNAME: Usuario de la base de datos.

* DB_PASSWORD: Contraseña de la base de datos.

3. Correr el proyecto