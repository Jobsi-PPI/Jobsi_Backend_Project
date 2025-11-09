# Consulta sobre los Métodos HTTP

## 1. Listado de métodos HTTP

Los métodos HTTP (HyperText Transfer Protocol) son acciones que indican qué tipo de operación desea realizar un cliente sobre un recurso del servidor. Entre los más utilizados se encuentran:

| Método | Descripción breve |
|--------|--------------------|
| **GET** | Solicita datos del servidor sin modificarlos. |
| **POST** | Envía datos al servidor para crear nuevos recursos. |
| **PUT** | Actualiza completamente un recurso existente. |
| **PATCH** | Actualiza parcialmente un recurso. |
| **DELETE** | Elimina un recurso del servidor. |
| **HEAD** | Solicita solo los encabezados de la respuesta sin el cuerpo. |
| **OPTIONS** | Devuelve los métodos HTTP permitidos para un recurso. |
| **CONNECT** | Crea un túnel de comunicación con el servidor (por ejemplo, para HTTPS). |
| **TRACE** | Devuelve la misma solicitud enviada, utilizada para pruebas y diagnóstico. |

---

## 2. Aplicabilidad de cada método

A continuación, se explica en qué casos se utiliza cada método y por qué:

- **GET:**  
  Se usa para obtener información sin alterar el estado del servidor. Es idempotente, lo que significa que varias solicitudes idénticas producirán el mismo resultado.  
  Ejemplo: visualizar una lista de usuarios o los detalles de un producto.

- **POST:**  
  Se utiliza para enviar datos al servidor con el fin de crear nuevos recursos. No es idempotente, ya que puede generar múltiples resultados distintos si se repite.  
  Ejemplo: registrar un nuevo usuario o enviar un formulario.

- **PUT:**  
  Sirve para actualizar completamente un recurso existente, reemplazando todos sus datos. Es idempotente.  
  Ejemplo: actualizar todos los campos del perfil de un usuario.

- **PATCH:**  
  Se aplica cuando se desea modificar solo una parte de un recurso, sin reemplazarlo por completo.  
  Ejemplo: cambiar únicamente la dirección de correo de un usuario.

- **DELETE:**  
  Permite eliminar un recurso identificado por una URL específica. Es idempotente.  
  Ejemplo: eliminar una cuenta o un producto del inventario.

- **HEAD:**  
  Se utiliza para obtener únicamente los encabezados de una respuesta, útil para verificar la existencia o el tamaño de un recurso sin descargarlo.  
  Ejemplo: comprobar si un archivo está disponible antes de descargarlo.

- **OPTIONS:**  
  Permite conocer qué métodos HTTP admite un recurso. Es muy usado en contextos de CORS (Cross-Origin Resource Sharing).  
  Ejemplo: verificar si un servidor acepta solicitudes `POST` desde otro dominio.

- **CONNECT:**  
  Establece una conexión segura con el servidor mediante un túnel, generalmente utilizado para tráfico cifrado HTTPS.

- **TRACE:**  
  Se usa para pruebas y diagnóstico, ya que devuelve la misma solicitud que el cliente envió. Su uso en producción es poco común por motivos de seguridad.

---

## 3. Relación con la arquitectura web

Los métodos HTTP son esenciales en la **arquitectura REST (Representational State Transfer)**, donde cada recurso (por ejemplo, un usuario o un producto) se manipula a través de una URL utilizando uno de estos métodos.

- En **REST**, los métodos HTTP representan las operaciones CRUD:
  
- | Operación CRUD | Método HTTP |
  |----------------|--------------|
  | **Create** | POST |
  | **Read** | GET |
  | **Update** | PUT / PATCH |
  | **Delete** | DELETE |

En arquitecturas **SOAP**, las operaciones se realizan mediante un único método `POST`, pero el contenido del mensaje (en XML) especifica la acción deseada.  
En cambio, REST aprovecha la semántica de los métodos HTTP para mantener una comunicación más ligera y estandarizada.

---
## 4. Ejemplos formas de uso peticiones HTTP

Los siguientes archivos `.http` se encuentran dentro de la carpeta de pruebas (`/src/test/http`) y muestran ejemplos de las peticiones realizadas a los endpoints de la API.

- [Public Endpoints](./src/test/http/public.http)
- [User Endpoints](./src/test/http/user.http)

---
## 5. Ejemplos prácticos de uso de métodos HTTP (Controladores)

A continuación, se presentan ejemplos prácticos de cómo utilizar cada método HTTP en una API RESTful con Spring Boot.

- [UsuarioController.java](./src/main/java/com/escaes/jobsy/infraestructure/rest/controller/UsuarioController.java)
- [AuthController.java](./src/main/java/com/escaes/jobsy/infraestructure/rest/controller/AuthController.java)
- [TrabajoController.java](./src/main/java/com/escaes/jobsy/infraestructure/rest/controller/TrabajoController.java)
